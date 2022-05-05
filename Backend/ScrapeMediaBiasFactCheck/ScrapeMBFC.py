from operator import contains
from sqlite3 import ProgrammingError
from bs4 import BeautifulSoup
import traceback
import requests
import pandas as pd
import ftfy
from icecream import ic

websiteInfo = dict() 
FACTUAL_RATINGS = set(["VERY HIGH", "HIGH", "MOSTLY FACTUAL", "MIXED", "LOW", "VERY LOW", 
                       "VERY-HIGH", "MOSTLY-FACTUAL", "VERY-LOW",
                       "VERY\xa0HIGH", "MOSTLY\xa0FACTUAL", "VERY\xa0LOW",
                       "N/A"])

FACTUAL_RATINGS_TO_NUM = {
    "VERY HIGH": 5,
    "HIGH": 4,
    "MOSTLY FACTUAL": 3,
    "MIXED": 2,
    "LOW": 1,
    "VERY LOW": 0,
    "N/A": -1
}

'''
Scrapes a MBFC website (ex. https://mediabiasfactcheck.com/leftcenter/)
'''
def scrape(mbfcSection): 
    url, sectionType = mbfcSection

    # to get the html content for a single page
    page = requests.get(url)
    soup = BeautifulSoup(page.content, "html.parser")

    table = soup.find("table", class_ = "sort", id = "mbfc-table")
    rows = table.find_all("tr") 

    # scrape each website in the table
    for row in rows: 
        aTag = row.find("a") 
        if aTag is None:
            continue

        mbfcWebsiteUrl = aTag["href"]
        scrapeMBFCReview(mbfcWebsiteUrl, sectionType)

'''
Scrapes a single MBFC review of a news source (ex. https://mediabiasfactcheck.com/7news/)
Provides the following information:
- news agency       : from the <h1> of the mbfc review
- news agency url   : from the "Source: <website url>" at the bottom of the page, with leading ["https://", etc] and trailing [".com", etc]
                      removed
- mbfc mwebsite url : from the url of the webpage 
- factualRating     : from the "Factual Rating" under "Detailed Report" 
''' 
def scrapeMBFCReview(mbfcWebsiteUrl, sectionType):
    try:
        page = requests.get(mbfcWebsiteUrl) 
        soup = BeautifulSoup(page.content, "html.parser")

        newsAgency = soup.find("h1", class_= ["entry-title", "page-title"]).text.strip()
        newsAgency = newsAgency.replace("\u2013", "-") # remove unicode en-dash

        newsAgencyUrl = getNewsUrlFromSourceContent(soup)
        factualRating = getFactualRating(soup)
        history = getHistory(soup)
        
        if factualRating != "" and factualRating != None and history != "" and history != None: 
            websiteInfo[newsAgencyUrl] = {
                "newsAgency": newsAgency,
                "mbfcUrl": mbfcWebsiteUrl,
                "factualRating": factualRating,
                "history": history,
                "type": sectionType
            }

            #print("Completed: " + mbfcWebsiteUrl)
        else: 
            print(mbfcWebsiteUrl)
    except (TypeError, AttributeError) as e: # if info is not found (page not formatted as expected)
        # print(traceback.format_exc())
        print(mbfcWebsiteUrl)

'''
Returns <website url> from "Source: <website url>" at the bottom of the page, with leading ["https://", etc] and trailing [".com", etc] and "Source:"
removed. 
'''
def getNewsUrlFromSourceContent(soup): 
    newsAgencyTag = soup.find(lambda tag: tag.findChild("a") != None and tag.name == "p" and ("Source:" in tag.text or "Sources:" in tag.text)) 
    newsAgencyUrl = newsAgencyTag.find("a")["href"]
    newsAgencyUrl = parseNewsUrl(newsAgencyUrl)
    return newsAgencyUrl

'''
Removes leading ["https://", etc] and trailing [".com", etc] from a url
'''
def parseNewsUrl(url): 
    removeFromFront = ["https://", "http://", "www."]
    for remove in removeFromFront: 
        idx = url.find(remove)
        if idx != -1:
            url = url[idx + len(remove):]

    removeFromBack = [".com", ".org", ".co", ".biz", ".info", ".mobi", ".tv", ".ws", ".name", ".eu", ".uk"]
    for remove in removeFromBack: 
        idx = url.find(remove)
        if idx != -1:
            url = url[:idx]

    if url[-1] == "/":
        url = url[:-1]
    
    return url

'''
Returns mbfc's factual rating review of the article.
'''
def getFactualRating(soup): 
    possibleTagNames = set(["p", "h5"])
    pTagFactual = soup.find(lambda tag: tag.name in possibleTagNames and "Factual Reporting" in tag.text)
    spanTags = pTagFactual.find_all("span")

    # there may be multiple non-distinct spans each representing a different category, so
    # brute force search for the first exact match with a possible factual rating (very high, high, ..., very low)
    for span in spanTags:
        text = span.text.strip().upper()
        if text in FACTUAL_RATINGS: 
            text = text.replace("-", " ").replace("\xa0", " ")
            return FACTUAL_RATINGS_TO_NUM[text]

    return None

'''
Returns mbfc's history review of the article
'''
def getHistory(soup): 
    historyTag = soup.find(lambda tag: (tag.name == "b" or tag.name == "strong") and "History" in tag.text)
    stopPhrases = set(["Funded by / Ownership", "Funded by /Ownership", "Analysis / Bias"])

    history = ""
    currTag = historyTag.find_next("p")
    #while "History" in currTag.text:
    #    currTag = currTag.next

    while not stopIfInTag(stopPhrases, currTag.text):
        if len(currTag.text) > 0: 
            phrase = currTag.text.strip()
            history += " " + phrase

        currTag = currTag.find_next(["p", "h4"])
    

    # find and remove starting with this text phrase
    history = removeStartingFromString(history, "Read our profile on")
    history = removeStartingFromString(history, "Read our media profile on")
    return ftfy.fix_text(history.strip())

def removeStartingFromString(s, toRemove): 
    idx = s.find(toRemove)
    if idx == -1:
        return s 
    return s[:idx]

'''
True if a phrase in stopPhrases exists in tagContents, False otherwise.
'''
def stopIfInTag(stopPhrases, tagContents): 
    for phrase in stopPhrases:
        if phrase in tagContents:
            return True

    return False

# cmd + / to mass comment / uncomment
mbfcSections = [
    ("https://mediabiasfactcheck.com/left/", "Left"),
    ("https://mediabiasfactcheck.com/leftcenter/", "Left Center"),
    ("https://mediabiasfactcheck.com/center/", "Center"),
    ("https://mediabiasfactcheck.com/right-center/", "Right Center"), 
    ("https://mediabiasfactcheck.com/right/", "Right"),
    ("https://mediabiasfactcheck.com/conspiracy/", "Conspiracy"),
    ("https://mediabiasfactcheck.com/fake-news/", "Fake News"),
    ("https://mediabiasfactcheck.com/pro-science/", "Pro Science"),
    ("https://mediabiasfactcheck.com/satire/", "Satire")
]

for section in mbfcSections:
    print("*****************************")
    print("*****************************")
    print("*****************************")
    print("*****************************")
    print("WORKING ON SECTION " + section[0])
    print("*****************************")
    print("*****************************")
    print("*****************************")
    print("*****************************")
    scrape(section)

'''
Transforms the dict to a dataframe before converting the dataframe to a CSV
'''
listOfDict = []
for newsAgencyUrl in websiteInfo.keys(): 
    listOfDict.append({
        "newsAgencyUrl": newsAgencyUrl,
        "newsAgency": websiteInfo[newsAgencyUrl]["newsAgency"],
        "mbfcUrl": websiteInfo[newsAgencyUrl]["mbfcUrl"],
        "factualRating": websiteInfo[newsAgencyUrl]["factualRating"],
        "history": websiteInfo[newsAgencyUrl]["history"],
        "type": websiteInfo[newsAgencyUrl]["type"]
    })

df = pd.DataFrame.from_dict(listOfDict) 
df.to_csv("mbfcScraped.csv", index = False, header = True)