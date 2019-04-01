# News-Crawler
A news website crawler using Crawler4j

Configured and compiled Crawler4j web crawler to crawl www.yahoo.com/news

The crawling depth was set to 16 and the limit was 20000 webpages.

## Repository file descriptions:

- MyCrawler.java - a customized crawler class in java to crawl intended webpages. <br />

- MyController.java - a customized crawler controller class in java to manage and control the crawling process <br />

- fetch_yahoo.csv - contains the URLs attempted to fetch and the corresponding HTTP status codes received. <br />

- visit_yahoo.csv - contains the URLs successfully downloaded, the size of the downloaded files, the # of outlinks found, and the resulting content-types. <br />

- urls_yahoo.csv - contains the encountered URLs and an indication of whether the URLs resides in the website (OK), or points outside of the website (N_OK). <br />

- CrawlReport_yahoo.txt - the crawling statistics report.
