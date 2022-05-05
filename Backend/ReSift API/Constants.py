from newspaper import Config
import psycopg2
import os 

G_NEWS_API_KEY = "35c21e410e8afd371d297ec742b21a5c"
DATABASE_URL = os.environ.get("DATABASE_URL")
CONNECTION = psycopg2.connect(DATABASE_URL)

# User agent for newspaper scraping to try to avoid bot-blocking websites
USER_AGENT = 'Mozilla/5.0 (X11; OpenBSD i386) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36'# 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36'
CONFIG = Config()
CONFIG.browser_user_agent = USER_AGENT

JSON_INDENT_LEVEL = 4

STATUS_CODE = {
    "OK": 200,
    "NOT_FOUND": 404,
    "INTERNAL_SERVER_ERROR": 500
}