package crawlAndSearch;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.net.ssl.SSLHandshakeException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sun.security.validator.ValidatorException;


/* Visit the url.
   Extract url,text.
   Store all urls to be visited in an ArrayList */
public class Crawler {

	public String urlFolder;
	public String urlFolderName;
	public int noOfURLS=0;
	//contains a ArrayList of urls
	private ArrayList<String> links;
	//pages visited should be unique. Hence Set. Sets wont add duplicate entries
	private Set<String> pagesVisited;
	String splChrs = "-/@#$%^&_+=():.| " ;
	private static final Pattern filters = Pattern.compile(
			".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|ppt|pptx|doc|docx|pdf" +
			"|rm|smil|wmv|swf|wma|zip|rar|gz|jar))$");

	Indexing indexing;
	Path crawlerPath;
	String href=null;
	public Crawler(){
	
	}

	public Crawler(String contextPath) throws IOException{
		
		crawlerPath = Paths.get(contextPath,"pics","TempCrawl"); //IRPEV01/TempCrawl - "TempCrawl"
		Files.createDirectories(crawlerPath); // creates both pics and temp at the same time
		indexing = new Indexing(contextPath);
		links = new ArrayList<String>();
		pagesVisited = new HashSet<String>();

	}
	
	public void initialize(String url,int crawlDepth,int maxURLs){

		try {
			crawlUrl(url,crawlDepth,maxURLs);
			setNoOfURLS(pagesVisited.size());
			indexing.indexWriter.close();
		}catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	public int getNoOfURLS() {
		return noOfURLS;
	}

	public void setNoOfURLS(int noOfURLS) {
		this.noOfURLS = noOfURLS;
	}

	public void crawlUrl(String url, int crawlDepth,int maxURLs) throws IOException{
		
		if(pagesVisited.size()<=maxURLs){
			pagesVisited.add(url.trim());
		if(crawlDepth==0){
			crawlingUrl(url);
		}else{
			while(crawlDepth > 0){
				crawlingUrl(url);
				crawlDepth--;
				Document jsDocumentTemp = Jsoup.connect(url).get();
				Elements linksOnPage = jsDocumentTemp.select("a[href]");
				for(Element link : linksOnPage){
					//Normalize the url.
					String href=link.absUrl("href").trim();
					//Check if link already exists in links object. If it does, dont add again. This is a way to normalize the url(removing duplicates)
					if(!pagesVisited.contains(link.absUrl("href").trim()) && (!this.links.contains(link.absUrl("href").trim())) && (!filters.matcher(href).matches())){
						this.links.add(link.absUrl("href").trim());
						crawlUrl(link.absUrl("href").trim(),crawlDepth,maxURLs);
					}
				}
			} 
		}
		}

	}
	public void crawlingUrl(String url){
		long totalExecutionTime = 8L;
		long startTime;

		try {
			//visit the url and get the contents from the webpage
			
			Document jsDocument = Jsoup.connect(url).get();
			//print the title or something
			String title = jsDocument.title().toLowerCase();
			
			//contents text
			String contents = jsDocument.body().text();

			//indexing the web page
			org.apache.lucene.document.Document luceneDocument = indexing.indexWebPage(contents,title,url);
			indexing.indexWriter.addDocument(luceneDocument);
			indexing.indexWriter.commit();
			
		}catch(SSLHandshakeException ss){
			System.err.println(ss);
		}catch(MalformedURLException mu){
			System.out.println("MalFormed URL");
		}catch(IllegalArgumentException ie){
			System.out.println("Illegal Argument");
		}catch(ConnectException c){ 
			System.err.println("Connection Issue");
		}catch(SocketTimeoutException s){
			System.err.println("SocketConnection Issue");
		}catch(HttpStatusException http){
			System.err.println("Http Connection Issue");
		}catch(NullPointerException ne){
			System.err.println("Null Pointer Exception");
		}catch (IOException e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}
}

