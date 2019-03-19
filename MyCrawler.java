/*package quickstart;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.io.Files;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.TextParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {
	
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|mp3|mp4|zip|gz|txt))$");
	private final static Pattern filterXML = Pattern.compile(".*(format=xml|feed).*");
	private final static Pattern filterJSON = Pattern.compile(".*(\\wp-json).*");
	private final static Pattern filterCSS = Pattern.compile(".*(\\.css).*");
	
	private static final Pattern docPatterns = Pattern.compile(".*(\\.(doc|docx|pdf|jpg|gif|ico|jpeg|png|html|htm?))$");
		
	private static File storageFolder;
	private static File fetch, visit, urls;
	
	public static void configure(String storageFolderName) {

	    storageFolder = new File(storageFolderName);
	    if (!storageFolder.exists()) {
	      storageFolder.mkdirs();
	    }
	    
	    fetch = new File(storageFolder.getAbsolutePath() + "/result/fetch.csv");
	    visit = new File(storageFolder.getAbsolutePath() + "/result/visit.csv");
	    urls = new File(storageFolder.getAbsolutePath() + "/result/urls.csv");
	  }
	
			 
			 @Override
			 public boolean shouldVisit(Page referringPage, WebURL url) {
			 String href = url.getURL().toLowerCase();
			 
			 try
				{
				    FileWriter writer = new FileWriter(urls, true);
					 
				    writer.append(url.getURL());
				    writer.append(',');
				    if (href.startsWith("https://www.yahoo.com/news"))
				    	writer.append("OK");
				    else
				    	writer.append("N_OK");
				    writer.append('\n');
				    
				    writer.flush();
				    writer.close();
				}
				catch(IOException e)
				{
				     e.printStackTrace();
				} 
			 
			 return !FILTERS.matcher(href).matches() && !filterCSS.matcher(href).matches() &&
					 !filterXML.matcher(href).matches() && !filterJSON.matcher(href).matches() &&
					 href.startsWith("https://www.yahoo.com/news");
			 }
			 
		
			  @Override
			  public void visit(Page page) {
			  String url = page.getWebURL().getURL();
			  String contentType = new String();
			  
			  try {
			  URL url1 = new URL(url);
			  HttpURLConnection connection = (HttpURLConnection)  url1.openConnection();
			  connection.setRequestMethod("HEAD");
			  connection.connect();
			  contentType = connection.getContentType();
			  }
			  catch (MalformedURLException e) {
		            e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			  
			  if (page.getParseData() instanceof HtmlParseData) {
			  HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			  String html = htmlParseData.getHtml();
			  Set<WebURL> links = htmlParseData.getOutgoingUrls();

			  try
              {
				  String hashedName = URLEncoder.encode(url, "UTF-8");
				  File outputfile = new File(storageFolder.getAbsolutePath() + "/" + hashedName + ".html");
				  //If file doesnt exists, then create it
                   if(!outputfile.exists()){
                       outputfile.createNewFile();
                   }

                  FileWriter fw = new FileWriter(outputfile); 
                    BufferedWriter bufferWritter = new BufferedWriter(fw);
                    bufferWritter.write(html);
                    fw.write(html);
                    bufferWritter.close();
                    fw.close();
                  
                  FileWriter writer = new FileWriter(visit, true);
				    writer.append(url);
				    writer.append(',');
				    Long size = (Long) outputfile.length()/1024; 
				    writer.append(size.toString());
				    writer.append(',');
				    Integer outlinks = (Integer) links.size(); 
				    writer.append(outlinks.toString());
				    writer.append(',');
				    writer.append(contentType);
				    writer.append('\n');
				    writer.flush();
				    writer.close();
                  
              }catch(IOException e)
              {
                  System.out.println("IOException : " + e.getMessage() );
                  e.printStackTrace();
              }
			  }
			  
			  else if (docPatterns.matcher(url).matches()) {

				    // get a unique name for storing this document
				    String filename = "";
				    
				    try {
				    	String hashedName = URLEncoder.encode(url, "UTF-8");

					    // store document
					    filename = storageFolder.getAbsolutePath() + "/" + hashedName;
					    File outputfile = new File(filename);
					    Files.write(page.getContentData(), outputfile);
				      
					    FileWriter writer = new FileWriter(visit, true);
						 
					    writer.append(url);
					    writer.append(',');
					    Long size = (Long) outputfile.length()/1024;
					    writer.append(size.toString());
					    writer.append(',');
					    if (page.getParseData() instanceof BinaryParseData){
					    	BinaryParseData binaryParseData = (BinaryParseData) page.getParseData();
					    	Set<WebURL> links = binaryParseData.getOutgoingUrls();
					    	Integer outlinks = (Integer) links.size();
					    	writer.append(outlinks.toString());
					    }
					    else if (page.getParseData() instanceof TextParseData){
					    	TextParseData textParseData = (TextParseData) page.getParseData();
					    	Set<WebURL> links = textParseData.getOutgoingUrls();
					    	Integer outlinks = (Integer) links.size();
					    	writer.append(outlinks.toString());
					    }

					    else
					    	writer.append(hashedName);
					    writer.append(',');
					    writer.append(contentType);
					    writer.append('\n');
					    
					    writer.flush();
					    writer.close();
				    } catch (IOException iox) {
				      logger.error("Failed to write file: " + filename, iox);
					}
			  }  		  
  	}
			  
			  @Override
				protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
					
					try
					{
					    FileWriter writer = new FileWriter(fetch, true);
						 
					    writer.append(webUrl.getURL());
					    writer.append(',');
					    Integer status = (Integer)statusCode;
					    writer.append(status.toString());
					    writer.append('\n');
					    
					    writer.flush();
					    writer.close();
					}
					catch(IOException e)
					{
					     e.printStackTrace();
					} 
					
				}
			  
			  @Override
			  protected void onUnhandledException(WebURL webUrl, Throwable e) {
				      String urlStr = (webUrl == null ? "NULL" : webUrl.getURL());
				      logger.warn("Unhandled exception while fetching {}: {}", urlStr, e.getMessage());
}
}
*/

package quickstart;

import java.util.Set;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.Header;

import com.google.common.io.Files;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;




public class MyCrawler extends WebCrawler {
	
	private static final Pattern FILTERS = Pattern.compile(".*\\.(css|js|mp3|zip|gz)$");
	
	CrawlState crawlState;

    public MyCrawler() {
        crawlState = new CrawlState();
    }
    
	private static File storageFolder;

    public static void configure(String storageFolderName) {
        storageFolder = new File(storageFolderName);
        if (!storageFolder.exists()) {
            storageFolder.mkdirs();
        }
    }
	
	@Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        String type="";
        if(href.contains("yahoo.com/news"))
        {
        	type="OK";
        }
        else
        {
        	type="N_OK";
        }
       
        crawlState.discoveredUrls.add(new UrlInfo(href, type));
        return !FILTERS.matcher(href).matches()&&href.startsWith("https://www.yahoo.com/news");
    }

	@Override
    public void visit(Page page) {
        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
        String domain = page.getWebURL().getDomain();
        String path = page.getWebURL().getPath();
        String subDomain = page.getWebURL().getSubDomain();
        String parentUrl = page.getWebURL().getParentUrl();
        String anchor = page.getWebURL().getAnchor();
        
        String contentType = page.getContentType().split(";")[0];
        ArrayList<String> outgoingUrls = new ArrayList<String>();

        logger.debug("Docid: {}", docid);
        logger.info("URL: {}", url);
        logger.debug("Domain: '{}'", domain);
        logger.debug("Sub-domain: '{}'", subDomain);
        logger.debug("Path: '{}'", path);
        logger.debug("Parent page: {}", parentUrl);
        logger.debug("Anchor text: {}", anchor);
        
        UrlInfo urlInfo;
        if (contentType.equals("text/html")) {
            if (page.getParseData() instanceof HtmlParseData) {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                Set<WebURL> links = htmlParseData.getOutgoingUrls();
                for (WebURL link : links) {
                    outgoingUrls.add(link.getURL());
                }
                urlInfo = new UrlInfo(url, page.getContentData().length, outgoingUrls, "text/html", ".html");
                crawlState.visitedUrls.add(urlInfo);
            }
            
            else {
                urlInfo = new UrlInfo(url, page.getContentData().length, outgoingUrls, "text/html", ".html");
                crawlState.visitedUrls.add(urlInfo);
            }
        } else if (contentType.equals("application/msword")) { // doc
            urlInfo = new UrlInfo(url, page.getContentData().length, outgoingUrls, "application/msword", ".doc");
            crawlState.visitedUrls.add(urlInfo);
        } else if (contentType.equals("application/pdf")) { // pdf
            urlInfo = new UrlInfo(url, page.getContentData().length, outgoingUrls, "application/pdf", ".pdf");
            crawlState.visitedUrls.add(urlInfo);
        }
        else if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            urlInfo = new UrlInfo(url, page.getContentData().length, outgoingUrls, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx");
            crawlState.visitedUrls.add(urlInfo);
        } 
        else if(contentType.contains("/image"))
        {
        	
        	urlInfo = new UrlInfo(url, page.getContentData().length, outgoingUrls, "image/jpg", ".jpg");
        	 crawlState.visitedUrls.add(urlInfo);
        }
        else if(contentType.contains("/image"))
        {
        	
        	urlInfo = new UrlInfo(url, page.getContentData().length, outgoingUrls, "image/png", ".png");
        	 crawlState.visitedUrls.add(urlInfo);
        }
        else if(contentType.contains("/image"))
        {
        	
        	urlInfo = new UrlInfo(url, page.getContentData().length, outgoingUrls, "image/jpeg", ".jpeg");
        	 crawlState.visitedUrls.add(urlInfo);
        }
        else if(contentType.contains("/image"))
        {
        	
        	urlInfo = new UrlInfo(url, page.getContentData().length, outgoingUrls, "image/gif", ".gif");
        	 crawlState.visitedUrls.add(urlInfo);
        }
        else if(contentType.contains("/image"))
        {
        	
        	urlInfo = new UrlInfo(url, page.getContentData().length, outgoingUrls, "image/x-icon", ".x-icon");
        	 crawlState.visitedUrls.add(urlInfo);
        }
        /*else {
            urlInfo = new UrlInfo(url, page.getContentData().length, outgoingUrls, "unknown", "");
            crawlState.visitedUrls.add(urlInfo);
        }
        if (!urlInfo.extension.equals("")) {
            String filename = storageFolder.getAbsolutePath() + "/" + urlInfo.hash + urlInfo.extension;
            try {
                Files.write(page.getContentData(), new File(filename));
            } catch (IOException iox) {
                System.out.println("Failed to write file: " + filename);
            }
        }*/
	}
        @Override
        protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
            crawlState.attemptUrls.add(new UrlInfo(webUrl.getURL(), statusCode));
        }

        @Override
        public Object getMyLocalData() {
            return crawlState;
        }
    }