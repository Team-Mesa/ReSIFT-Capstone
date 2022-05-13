**ReSIFT**

Team Mesa:

- Andrew Wong
- Daniel Miau
- Kayvon Tari
- Tom Nguyen

In collaboration with the University of Washington iSchool and
Center for an Informed Public. 

Project Handoff Documentation can be found [here](https://docs.google.com/document/d/1mwxMiBEVt8hNRyhVtQixWu8GPqNpJpTJScY277Nhuts/edit?usp=sharing)

**Project Summary**

Misinformation has always been present, but social media has significantly
increased its scope, spread, and reach in our world today.

Working with the University of Washington's Center for an Informed Public
(CIP), our project places the SIFT fact-checking strategy in the hands of
Android mobile device users.

From our research we found the following:

- Current tech solutions in this problem space are typically
not mobile-friendly or convenient.
- Many people trust that others in their social network fact-check
before sharing information onto social mediums.
- People have short attention spans in the digital age, a few seconds is
a significant barrier for people to engage in information validation.

The ReSIFT mobile application prompts users to fact-check articles and
provides an information extraction tool to expedite the fact-checking process.
The intent is to reduce the amount of time needed to fact-check and educate
users in information verification best practices to create a community of
information skeptics and critical thinkers.


**ReSIFT Solution**

To combat this problem we created a tool that helps users quickly find
information about news articles that they read. We will help users apply
the SIFT method to critically analyze information and ascertain the accuracy
of information presented in social media and news articles. SIFT is a four-step
strategy used to fact-check and cross-reference information.

- S: STOP and think before sharing a post or article
- I: INVESTIGATE the source. Check their reputation and authority.
- F: FIND better coverage. Can you find similar claims elsewhere?
- T: TRACE claims, quotes, and media to its original context.

The ReSIFT mobile application prompts users to fact-check articles and provides
an information extraction tool to expedite the fact-checking process.
The intent is to reduce the amount of time needed to fact-check and for users
to consider the credibility and bias of their news sources in order to create
a community of information skeptics and critical thinkers.

**Key Features**

The ReSIFT process begins once the users inputs a news article URL into the s
earch bar on the landing page of the ReSIFT app. Users can also directly
share the news article link with ReSIFT from their mobile web browser.

ReSIFT will automatically "SIFT" through the desired news article. The ReSIFT
organizes information from the article through three different sections:

- Publisher Overview
- Article Summary
- Related Articles

The "Publisher Overview" section provides users with background information
about the media source, along with a media source credibility rating and media
bias rating based of off data from
MediaBiasFactCheck.com (MBFC).
Rating methodology can be found
[here](https://dmiau88.github.io/ReSIFT-Website/)

The "Article Summary" section provides users with a shortened version of the
original article for convenient viewing. This section provides important
information of who the author is and when the article was published.

The "Related Articles" section includes a list of articles that exist across
the Internet that discusses the same topic as the chosen article.
This section helps the user understand whether or not the news topic
is talked about often.

At the bottom of the ReSIFT interface, users can click on the "Share"
button to directly share the URL of the news article to their social media
applications through the Android share menu.


**Technologies**

- Android Kotlin
- Python
- GNews API (free version, rate limited to 1 request per second)

**Setup**
- Scrape mbfc with ScrapeMBFC.py
- Host app.py and constants.py on the cloud. We used Heroku, it is up 
to you to which cloud provider you use.
- It is recommended to test endpoints with Postman or similar.
