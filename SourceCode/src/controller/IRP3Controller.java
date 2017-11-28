package controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.text.DefaultEditorKit.CutAction;

import org.apache.commons.io.FileUtils;

import clustering.DocumentClustering;

import crawlAndSearch.Crawler;
import crawlAndSearch.FileSearch;
import crawlAndSearch.ResultsForGui;

import beans.ClusterBean;
import beans.IRUtilsBean;
import beans.SearchResults;

/**
 * Servlet implementation class IRController
 */
public class IRP3Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CRAWL_AND_INDEX = "Start Crawling";
	private static final String SEARCH = "Search";
	private static final String CLUSTER = "Cluster";
	private static Map<Integer, ResultsForGui> guiMap = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IRP3Controller() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String urlValue= request.getParameter("urlStr");
		HttpSession session = request.getSession();
		session.setAttribute("urlStr", "");
		session.setAttribute("depthVal", "");
		session.setAttribute("indexSummary","");
		session.setAttribute("searchQry","");
		session.setAttribute("searchSummary","");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		long startTime = 0;
		long elapsedTime=0;
		long stopTime=0;
		int el = 0;
		double tm1=0.0;
		String urlValue = request.getParameter("urlStr");
		String buttonVal= request.getParameter("btn");
		String searchQry= request.getParameter("searchQry");
		String indexSummary= request.getParameter("indexSummary");
		String searchSummary= request.getParameter("searchSummary");
		String clusterSummary= request.getParameter("clusterSummary");
		String searchReslToDisplay= request.getParameter("searchReslToDisplay");
		
		String clusterRadioSelect= request.getParameter("clusterRadio");
		String kClusters = request.getParameter("kClusters");
		String LSAThreshold= request.getParameter("LSAthreshold");
		LSAThreshold= "";
		
		int maxURLs= Integer.parseInt(request.getParameter("maxURLs"));
		int depth = Integer.parseInt(request.getParameter("depthVal"));
		
		HttpSession session = request.getSession();
		session.setAttribute("urlStr", urlValue);
		session.setAttribute("depthVal", depth);
		session.setAttribute("maxURLs", maxURLs);
		session.setAttribute("searchQry", searchQry);
		session.setAttribute("indexSummary", indexSummary);
		session.setAttribute("searchSummary", searchSummary);

		ServletContext context = getServletContext();
		String contextPath = context.getRealPath("/").toString();
	
		if(CRAWL_AND_INDEX.equals(buttonVal)){
		System.out.println("inside crawl"); 

		// delete all folders with contents (Index, pics, TempCrawl
		Path index = Paths.get(contextPath,"Index");
		File indexFile = new File(index.toString());
		FileUtils.deleteDirectory(indexFile);
		
		Path pics = Paths.get(contextPath,"pics");
		File picsFile = new File(pics.toString());
		FileUtils.deleteDirectory(picsFile);

		Crawler crawler = new Crawler(contextPath);
		startTime = System.currentTimeMillis();
		crawler.initialize(urlValue,depth,maxURLs);
		stopTime = System.currentTimeMillis();
		elapsedTime = (stopTime - startTime);
		el = (int) elapsedTime;//in ms
		tm1 = (double) el/60000;//in mins	
		tm1 = (double) (Math.ceil(tm1*1000)/1000);
		session.setAttribute("indexSummary", "Crawling and Indexing Completed in "+tm1+" mins. Total URLs(incl. seed) visited: "+crawler.getNoOfURLS());
		
        }
		else if(SEARCH.equals(buttonVal)){
		// Perform Lucene Search - call the correct method for searching
		// Get the populated map
			System.out.println("inside search"); 
			
			Path pics = Paths.get(contextPath,"pics");
			File picsFile = new File(pics.toString());
			FileUtils.deleteDirectory(picsFile);
			
			Path pics_crawl = Paths.get(contextPath,"pics","TempCrawl");
			Files.createDirectories(pics_crawl);
			
			FileSearch fs = new FileSearch();
			
			int displayRes=0;
			if(searchReslToDisplay!="" || searchReslToDisplay!=null){
				displayRes = Integer.parseInt(searchReslToDisplay);
			}
			try {
				if(!searchQry.equals(""))
				{
				startTime = System.currentTimeMillis();
				fs.searchInitializations(searchQry,displayRes,contextPath);
				stopTime = System.currentTimeMillis();
				elapsedTime = (stopTime - startTime);//in ms
				el = (int) elapsedTime;
				tm1 = (double) el/60000;//in mins	
				tm1 = (double) (Math.ceil(tm1*1000)/1000);
				guiMap = fs.getMapForGui();
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			List<Map<String, String>> totalResults = new ArrayList<Map<String,String>>();

			if(guiMap!=null){
			for(Map.Entry<Integer, ResultsForGui> k : guiMap.entrySet()){
				ResultsForGui rs = k.getValue();
				String imagePath_Actual = String.valueOf(rs.getPath());
				Path pics1 = Paths.get(contextPath,"pics","TempCrawl");
				int len = pics1.toString().length();
				String imagePath_mod = imagePath_Actual.subSequence(len, imagePath_Actual.length()).toString();

				

				Map<String, String> uiMap1 = new HashMap<String, String>();
				uiMap1.put("rank", String.valueOf(k.getKey()));//rank
				uiMap1.put("score", String.valueOf(rs.getScore()));//score
				uiMap1.put("url", String.valueOf(rs.getUrl()));//url path
				uiMap1.put("imagepath", imagePath_mod);//image path
				

				totalResults.add(uiMap1);
			}
			}
			System.out.println("totalResults size ="+totalResults.size());
			session.setAttribute("listofMaps", totalResults); 
			session.setAttribute("searchSummary", "Search Completed in "+tm1+" mins. Displaying :"+totalResults.size()+" of Available Results: "+fs.getTotalnumDocs());
			session.setAttribute("searchReslToDisplay", searchReslToDisplay);
			
		}
		else if (CLUSTER.equals(buttonVal)){

             FileSearch fs1 = new FileSearch();
						

			if(guiMap!=null){ // guiMap = Static Map, that holds all results from search button action
				DocumentClustering clus = new DocumentClustering();

				try {
					startTime = System.currentTimeMillis();
					clus.createClusters(guiMap,clusterRadioSelect,kClusters,LSAThreshold);
					stopTime = System.currentTimeMillis();
					elapsedTime = (stopTime - startTime);//in ms
					el = (int) elapsedTime;
					tm1 = (double) el/60000;//in mins	
					tm1 = (double) (Math.ceil(tm1*1000)/1000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				Map<Integer, ResultsForGui> guiClusterMap = null;
				guiClusterMap = clus.getClusteringResults();
				if(guiClusterMap!=null){
				System.out.println("result of clustering ="+guiClusterMap.size());
				session.setAttribute("clusterSummary", "Clustering Completed in "+tm1+" mins. With "+guiClusterMap.size()+" Clusters");


				
				
				List<Map<String, ClusterBean>> globalListofClusters = new ArrayList<Map<String,ClusterBean>>();

				
				String each_url=null;
				for(Map.Entry<Integer, ResultsForGui> kc : guiClusterMap.entrySet()){
					ResultsForGui clusGui = kc.getValue();
                    
                    //Steps to retrieve the Reprs. image paths of each URL
                    String [] all_urls_of_a_cluster = null;
                    if(clusGui.getClusterURLs()!=null || !clusGui.getClusterURLs().equals(""))
                    all_urls_of_a_cluster = clusGui.getClusterURLs().toString().split("AND"); // getClusterURLs ==> running URLs concatenated for display
                    
                    List<String> fixedImagesPerClutser = new ArrayList<String>();
    				
    				
                    each_url=null;
                    for(int k=0;k<all_urls_of_a_cluster.length;k++){
                    	each_url = all_urls_of_a_cluster[k].trim();
                    	    
                    	//take a URL and iterate the original GUI map , after "search" button to get the actual Rep image path of this url
                    	   for(Map.Entry<Integer, ResultsForGui> k5: guiMap.entrySet()){
                    		   ResultsForGui r = k5.getValue();
                    		   if(each_url.equals(r.getUrl().trim())){
                    			   
                    			   String imagePath_Actual = String.valueOf(r.getPath());
                   				   Path pics1 = Paths.get(contextPath,"pics","TempCrawl");
                   				   int len = pics1.toString().length();
                   				   String imagePath_mod = imagePath_Actual.subSequence(len, imagePath_Actual.length()).toString();
                   				   
                    			   fixedImagesPerClutser.add(imagePath_mod);// Hence this list will have all the paths to each image of a cluster
                    		   }
                    	   }
                    	
                    	
                    }
                    
                    each_url = clusGui.getClusterURLs().replace("AND", " ~ ");
                    
					Map<String, ClusterBean> uiClustMap1 = new HashMap<String, ClusterBean>();
					ClusterBean clusBean = new ClusterBean();
					clusBean.setClusterNum(clusGui.getClusterNum());
					clusBean.setClusterURLs(each_url);//clusGui.getClusterURLs()
					clusBean.setNumURLs(String.valueOf(clusGui.getClusterNumOfURLs()));
					clusBean.setArrayListofURLsPerCluster(fixedImagesPerClutser);
					
					uiClustMap1.put("clusterNum", clusBean);//cluster Num
					uiClustMap1.put("clusterURLs",clusBean);//URL1 || URL 2 || ....
					uiClustMap1.put("numURLs", clusBean);//URL1 || URL 2 || ....
					//Arralist of Images
					uiClustMap1.put("arrayListofURLsPerCluster", clusBean);//URL1 || URL 2 || ....
					globalListofClusters.add(uiClustMap1);
				}
				
				session.setAttribute("listofClusters", globalListofClusters);
				}
				else
					session.setAttribute("clusterSummary", "Clustering not done. Please search and then proceed to cluster");
				
			}
			
		}//end of all button-if conditions
		
		request.getRequestDispatcher("/irIndex.jsp").forward(request, response);	    
		
	}

}
