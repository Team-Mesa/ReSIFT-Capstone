# pip install -U pip setuptools wheel
# pip install -U spacy
# python -m spacy download en_core_web_sm

from os import read
from typing import OrderedDict
from newspaper import Article, ArticleException
from sqlite3 import ProgrammingError
from flask import Flask, request, Response
import urllib3
import Constants
import requests
import nltk
import json
import favicon
import base64
import PIL
from PIL import Image
from io import BytesIO
from bs4 import BeautifulSoup

'''
TODO: Replace newspaper3k nlp

issues with newspaper3k nlp
- keywords are returned in random order = words are not ranked? + difficult to use gnews api properly (can only pick a few keywords)
    ex. 
    ['experts', 'cases', 'getty', 'rates', 'masks', 'ill', 'low', 'cdc', 'unmask', 'health', 'community', 'mask', 'university', 'places', 'safe', 'arent', 'ready', 'quite']
    ['quite', 'getty', 'mask', 'university', 'health', 'arent', 'safe', 'rates', 'cdc', 'experts', 'community', 'cases', 'unmask', 'masks', 'places', 'ready', 'ill', 'low']

- generated summary is quite poor 

'''

app = Flask(__name__)
app.config["DEBUG"] = True

nltk.download('punkt')

urlToInfo = dict()

def jsonStatusCodeOk(info): 
    response = json.dumps(info, indent = Constants.JSON_INDENT_LEVEL)
    return Response(response = response, status = Constants.STATUS_CODE["OK"], headers = { "Content-Type": "application/json" })

def jsonStatusCodeNotFound(): 
    response = json.dumps({}, indent = Constants.JSON_INDENT_LEVEL)
    return Response(response = response, status = Constants.STATUS_CODE["NOT_FOUND"], headers = { "Content-Type": "application/json" })

def jsonStatusCodeInternalError(e):
    response = json.dumps({ "error": e }, indent = Constants.JSON_INDENT_LEVEL)
    return Response(response = response, status = Constants.STATUS_CODE["INTERNAL_SERVER_ERROR"], headers = { "Content-Type": "application/json" })

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

import io

def getFaviconAsBase64Str(url): 
    try: 
        # Get favicon url from domain name
        thirdSlashIndex = [index for index, character in enumerate(url) if character == '/'][2]
        domainUrl = url[:thirdSlashIndex]
        faviconIcons = favicon.get(domainUrl)
        faviconUrl = faviconIcons[0].url

        # Download favicon 
        request = requests.get(faviconUrl, headers={ 'user-agent': Constants.USER_AGENT })
        icon = Image.open(io.BytesIO(request.content))

        # Upscale favicon
        upscaled_icon = icon.resize((180, 180), Image.ANTIALIAS)
        buffer = BytesIO()

        # Save favicon data as base64
        upscaled_icon.save(buffer, "PNG", optimize=True)
        return str(base64.b64encode(buffer.getvalue()))        
    except (PIL.UnidentifiedImageError, urllib3.exceptions.HTTPError) as e: # image cannot be found OR 403 forbidden error
        print(e)
        return None 

'''
Parses and adds a news article to urlToInfo in the format
{
    "title": string?,
    "favicon": string?, // base 64 encoding, 250x250 pixels
    "publishedDate": string?, // Standard ISO Format YYYY-MM-DD
    "authors": [Author object]?, // Array of author object
    "summary": string?,
    "url": string
}
'''
def addNewsArticle(url):
    if url in urlToInfo: 
        return

    article = Article(url, config=Constants.CONFIG) 
    article.download()
    article.parse()    
    article.nlp() 

    title = article.title
    faviconBase64Str = getFaviconAsBase64Str(url)
    publishedDate = None if article.publish_date is None else article.publish_date.strftime('%Y-%m-%d')
    
    summary = None if article.summary is None else article.summary
    imageCredit = "" 
    if summary is not None: 
        page = requests.get(url)
        soup = BeautifulSoup(page.content, "html.parser")
        imageCreditTag = soup.find("span", { "class": "credit", "aria-label": "Image credit" })
        imageCredit = imageCreditTag.contents[0].strip() if imageCreditTag is not None else ""

    # ("  ", " ") is listed twice to account for groups of 4 spaces
    toReplace = [("\n", " "), ("Enlarge this image", ""), ("toggle caption", ""), (imageCredit, ""), ("  ", " "), ("  ", " ")] 
    if summary is not None:
        for tuple in toReplace:
            print(tuple, tuple[0], tuple[1])
            summary = summary.replace(tuple[0], tuple[1])

    keywords = [""] if article.keywords is None else article.keywords

    authors = []
    if article.authors is not None: 
        for author in article.authors: 
            authors.append({ "name": author })

    urlToInfo[url] = {
        "title": title,
        "favicon": faviconBase64Str,
        "publishedDate": publishedDate,
        "authors": authors,
        "summary": summary,
        "keywords": keywords
    }

'''
Returns a JSON representation of the parsed article. 
If article cannot be scraped, an ArticleException is raised.
The JSON is formatted as such: 
{
    "title": string?,
    "favicon": string?, // base 64 encoding, 250x250 pixels
    "publishedDate": string?, // Standard ISO Format YYYY-MM-DD
    "authors": [Author object]?, // Array of author object
    "summary": string?,
    "url": string
}
'''
def getArticleInfo(url): 
    try: 
        addNewsArticle(url)
    except ArticleException: # article could not be scraped (captcha, etc blocking scraper)
        raise ArticleException
    else:
        articleInfo = {
            "title": urlToInfo[url]["title"],
            "favicon": urlToInfo[url]["favicon"],
            "publishedDate": urlToInfo[url]["publishedDate"],
            "authors": urlToInfo[url]["authors"],
            "summary": urlToInfo[url]["summary"],
            "url": url
        }

        return articleInfo

'''
Returns a JSON representation of the article publisher's information by fetching data from the database.
If there is a database error, a ProgrammingError is raised.
The JSON is formatted as such: 
{
    "id": string, // the unique id used to query this article's publisher from the database
    "name": string,
    "mbfcUrl": string?,
    "factualRating": int?, // 0 to 5 (Lowest to highest credibility )
    "bias": string?, // one of [Left, Left Center, Center, Right, Right Center, Conspiracy, Fake News, Pro Science, Satire]
    "history": string?
}

'''
def getPublisherInfo(url):
    url = parseNewsUrl(url)
    queryRes = None

    try:
        with Constants.CONNECTION.cursor() as cursor:
            cursor.execute("SELECT news_agency_url, news_agency, mbfc_url, rating, history, type FROM mbfc WHERE news_agency_url = '" + url + "'")
            queryRes = cursor.fetchone()
    except ProgrammingError: # SQL error
        raise ProgrammingError
    else: 
        if queryRes is None:
            return None
        else:
            publisherInfo = {
                "id": queryRes[0],
                "name": queryRes[1],
                "mbfcUrl": queryRes[2],
                "factualRating": int(queryRes[3]),
                "bias": queryRes[5],
                "history": queryRes[4]
            }

            return publisherInfo

'''
Returns an articleInfo formatted as: 
{ 
    "publisherInfo": {
        "id": string, // the unique id used to query this article's publisher from the database
        "name": string,
        "mbfcUrl": string? 
        "factualRating": int?, // 0 to 5 (Lowest to highest credibility )
        "bias": string?, // one of [Left, Left Center, Center, Right, Right Center, Conspiracy, Fake News, Pro Science, Satire]
        "history": string?
    },
    "articleInfo": {
        "title": string?,
        "favicon": string?, // base64 encoding, 250x250 pixels
        "publishedDate": string?, // Standard ISO Format YYYY-MM-DD
        "authors": [Author object]?, // Array of author object
        "summary": string?,
        "url": string
    }
}
'''
@app.route('/articleInfo', methods=['GET'])
def getInfo(): 
    url = request.args.get("url")
    try: 
        articleInfo = getArticleInfo(url)
        publisherInfo = getPublisherInfo(url)
    except (ProgrammingError, ArticleException) as e: 
        return jsonStatusCodeInternalError(e)
    else:
        if articleInfo is None and publisherInfo is None: 
            return jsonStatusCodeNotFound()
        else:
            info = {} 
            if publisherInfo is not None:
                info["publisher"] = publisherInfo 
            if articleInfo is not None: 
                info["article"] = articleInfo

            return jsonStatusCodeOk(info)
    
'''
Returns a GNews request url that returns articles similar to the given url based on that article's keywords
'''
def getGNewsRequestUrl(url): 
    maxKeywords = 2 # Choose low # of keywords to get results consistently
    if len(urlToInfo[url]["keywords"]) > maxKeywords: 
        urlToInfo[url]["keywords"] = urlToInfo[url]["keywords"][:maxKeywords]
    
    keywordParams = " AND ".join(urlToInfo[url]["keywords"])
    return "https://gnews.io/api/v4/search?q=" + keywordParams + "&token=" + Constants.G_NEWS_API_KEY

'''
Returns a set of similar articles whose keywords match the keywords of this article. Returned articles are formatted as a set of ArticleInfo:
[
    {Article object},
    {Article object},
    {Article object}
]
'''
@app.route('/similarArticles', methods=['GET'])
def getSimilarArticles(): 
    url = request.args.get("url")

    try: 
        addNewsArticle(url)
    except ArticleException as e: # article could not be scraped (captcha, etc blocking scraper)
        return jsonStatusCodeInternalError(e)
    else:
        requestUrl = getGNewsRequestUrl(url)
        response = requests.get(requestUrl).json()

        if int(response["totalArticles"]) == 0: 
            return jsonStatusCodeNotFound()
        else: 
            distinctArticleUrls = set() # prevent duplicate urls
            for article in response["articles"]: 
                similarUrl = article["url"]
                if similarUrl != url:
                    addNewsArticle(similarUrl)
                    distinctArticleUrls.add(similarUrl)

            distinctArticleTitles = dict() # prevent duplicate article title names
            for article in distinctArticleUrls: 
                title = getArticleInfo(article)["title"]
                distinctArticleTitles[title] = article

            distinctArticles = [distinctArticleTitles[title] for title in distinctArticleTitles]
            formattedArticles = []
            for distinctUrl in distinctArticles: 
                try: 
                    articleInfo = getArticleInfo(distinctUrl)
                    articleInfo["url"] = distinctUrl
                    formattedArticles.append(articleInfo)
                except ArticleException: # article could not be scraped (captcha, etc blocking scraper)
                    pass

            if len(formattedArticles) == 0: 
                return jsonStatusCodeNotFound()
            else:
                return jsonStatusCodeOk(formattedArticles)

if __name__ == "__main__":
    app.run(threaded=True, debug=False)








