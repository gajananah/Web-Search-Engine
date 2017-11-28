package clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.LatentSemanticAnalysis;
import weka.attributeSelection.Ranker;
import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.unsupervised.instance.Normalize;

import crawlAndSearch.FileSearch;
import crawlAndSearch.Indexing;
import crawlAndSearch.Repository;
import crawlAndSearch.ResultsForGui;

public class DocumentClustering {
public String clusteringType="";
public Map<Integer, ResultsForGui> clusteringResults =null;
public String splChrs = "-/@#$%^&_+=():.| " ;

public String [] title = null;
public String [] URLs = null;
 

public void createClusters(Map<Integer, ResultsForGui> guiMap, String clusteringType,String kClusters, String LSAThreshold) throws Exception{
	
	 Instances tf_IncidenceWithoutClass = null;
	 //This method populates the tf-incidence matrix!!
	 tf_IncidenceWithoutClass = loadFromSearchedAndCreateFiles(guiMap);
	 Map<Integer, ResultsForGui> cluster_res_map = new HashMap<Integer, ResultsForGui>();
	 
	if(clusteringType.equals("kMeans")){
		 System.out.println("inside Kmeans");
		 int noOfClusters=3;
		 if(kClusters!=null || kClusters!="")
			 noOfClusters = Integer.parseInt(kClusters);
			 
		Map<Integer, ResultsForGui> kMeans = new HashMap<Integer, ResultsForGui>();
		Map<String, String> tempMap = new HashMap<String, String>();
		//Noramlize instances (not atrbs) only for kMenas
		Normalize fl = new Normalize();
	    fl.setInputFormat(tf_IncidenceWithoutClass);
	    Instances datasetNor = Filter.useFilter(tf_IncidenceWithoutClass, fl);
	    
	    SimpleKMeans km = new SimpleKMeans();
	    km.setSeed(10);
	    km.setPreserveInstancesOrder(true);
	    km.setNumClusters(noOfClusters);//3
	    km.buildClusterer(datasetNor);
	    
	    int[] assignments = km.getAssignments();
	    int i=0;
	    for(int clusterNum : assignments){
	    	//this loop will run for all number of URLs/docs to be clustered
	    	
	    	//kMeans.put(String.valueOf("Doc/URL Num "+i), String.valueOf("Cluster Num "+clusterNum)); //key = doc, value = clusnum
	    	System.out.printf("Docs %d -> Cluster %d \n",i,clusterNum);
	    	tempMap.put(URLs[i],"Cluster : "+clusterNum); // keys = URLs[i] ==> unique for each 
	    	i++;
	    }
	    String cluster_num =null;
	    String running_URL = null;
	    int numOfURLs=0;
	    ResultsForGui clusterResultsGui = null;
	    for(int j=0;j<noOfClusters;j++){
	    	 cluster_num = "Cluster : "+String.valueOf(j);
	    	 running_URL = "";
	    	 numOfURLs=0;
		    	for(Map.Entry<String, String> k2: tempMap.entrySet()){
			    		if(cluster_num.equals(k2.getValue())){
			    			running_URL = running_URL + k2.getKey()+" AND ";
			    			numOfURLs++;
			    		}
		    	}
		    	clusterResultsGui = 	new ResultsForGui();
		    	clusterResultsGui.setClusterNum(cluster_num);
		    	clusterResultsGui.setClusterURLs(running_URL);
		    	clusterResultsGui.setClusterNumOfURLs(numOfURLs);
	    	kMeans.put(j+1, clusterResultsGui);// C1uster 1 : d1 || d5 || d6    Cluster 2: d3 || d7 || d8 
	    }
	    
	    
	    cluster_res_map= kMeans;
	    
	}
	else{
		System.out.println("inside LSA");
		 double threshold= 0.15;
		 if(LSAThreshold!=null || LSAThreshold!="")
			 threshold =  Float.parseFloat(LSAThreshold);
		 
		Map<String, String> LSAMap = new HashMap<String, String>();
		
		AttributeSelection filter1 = new AttributeSelection();
	    LatentSemanticAnalysis lsa = new LatentSemanticAnalysis();
	    Ranker rank = new Ranker();
	    rank.setThreshold(threshold);//threshold between 0.1 to 0.5- possibly frobenius norm
	    filter1.setEvaluator(lsa);
	    filter1.setSearch(rank);
	    filter1.setRanking(true);

	    filter1.SelectAttributes(tf_IncidenceWithoutClass);
	    Instances outputData = filter1.reduceDimensionality(tf_IncidenceWithoutClass);
	    Enumeration<Object> e = outputData.enumerateAttributes();
	    
	    while(e.hasMoreElements()){
	    	System.out.println(" LSA Attributes, enum ="+e.nextElement().toString());
	    }
	    System.out.println(" num of atribs in LSA ="+outputData.numAttributes());
	    System.out.println(" ");
	    
	    System.out.println("LSA "+outputData.toSummaryString());
	    for(int i=0;i<outputData.numInstances();i++){
	    	//this loop will have docs in the rows and columns as the reduced space
	    	//System.out.println("For i = "+i+"Instance value="+outputData.instance(i).toString());
	    	LSAMap.put(String.valueOf("Doc/URL Num "+i), String.valueOf("Cluster Num "+outputData.instance(i).toString())); //key = doc, value = clusnum
	    }
	    
	    //cluster_res_map= LSAMap;
		
	}
	setClusteringResults(cluster_res_map);
	//Delete Files
	
}




public Instances loadFromSearchedAndCreateFiles(Map<Integer, ResultsForGui> guiMap) throws Exception{
	
	List<String> urlh=new ArrayList<String>();

	Indexing stem = new Indexing();
	
	for (Map.Entry<Integer, ResultsForGui> k : guiMap.entrySet())
	  {
	     
		  ResultsForGui childgui= k.getValue();
		  urlh.add(childgui.getUrl());
	  }
	
	String [] doc = new String [urlh.size()];
	this.title = new String [urlh.size()]; // will be needed later
	this.URLs = new String [urlh.size()]; //will be needed later
	  for(int i=0;i<urlh.size();i++){
			 String url=urlh.get(i).toString();
			 Document jsDocument = Jsoup.connect(url).get();//making http call, vvry bad! 
			 doc[i] = jsDocument.body().text();
			 title [i] =jsDocument.title().trim().toLowerCase();
			 title [i] = title [i].replaceAll(splChrs, "");
			 title [i] = title [i].replaceAll("[\\-\\+\\.\\^:,|\"\\?$><'?!:]", "");
			 doc[i] =stem.porterStem(doc[i]);// this is my d1, d2, d3 of the search result in Phase 2
			 URLs[i] = url.trim();
		  }
	  
	  
	  
	  //global directory
	  Repository repository;
	  Path global_doc_path =null;
		try {
			repository = new Repository();
			global_doc_path = Paths.get(repository.searchedDocsFolder); 
			// creates global directory
			Files.createDirectories(global_doc_path);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		String single_docFolderName =null;
		String single_docFileName = null;
		Path p1= null;
		Path p2= null;
	  for(int i=0;i<doc.length;i++){
		  
		  //Create as many folders- as there are doucments
		  //for each folder create a text file with contents of doc[i]
		  		//urlFolder=String.valueOf(i);
					
				
				single_docFolderName = Paths.get(global_doc_path.toString(), String.valueOf(i)).toString();//name of sub directory -per url
				p1= Paths.get(single_docFolderName);
				Files.createDirectories(p1);//url1 created
				
				
				single_docFileName = Paths.get(single_docFolderName.toString(), "doc.txt").toString();//give me child path of url1
				p2= Paths.get(single_docFileName);
				Files.createFile(p2);
				
				List<String> lines = Arrays.asList(doc[i]);
				Files.write(p2, lines, Charset.forName("UTF-8"));
			
	  }
	  
	    TextDirectoryLoader loader = new TextDirectoryLoader();
	    loader.setDirectory(new File(String.valueOf(global_doc_path)));
	    Instances dataRaw = loader.getDataSet();

	    //TF-Incidence Matrix Created
	    StringToWordVector filter = new StringToWordVector();
	    filter.setInputFormat(dataRaw);
	    Instances dataFiltered = Filter.useFilter(dataRaw, filter);

	  
	    //remove Class labels/Class colm
	    Remove remove= new Remove();
	    remove.setAttributeIndices(""+(dataFiltered.classIndex())+1);
	    remove.setInputFormat(dataFiltered);
	    Instances datanew;
	    datanew=Filter.useFilter(dataFiltered, remove);
	    
	    //Delete all files !!!!!!
	    for(int i=0;i<doc.length;i++){
			  		single_docFolderName = Paths.get(global_doc_path.toString(), String.valueOf(i)).toString();//name of sub directory -per url
					p1= Paths.get(single_docFolderName);
					
					single_docFileName = Paths.get(single_docFolderName.toString(), "doc.txt").toString();//give me child path of url1
					p2= Paths.get(single_docFileName);
					Files.deleteIfExists(p2);//first delete file
					
					Files.deleteIfExists(p1);//second dlete folder
		  }
	    //finally delete global folder
	    Files.deleteIfExists(global_doc_path);
	    
	    
	    return datanew;
	
}



public String getClusteringType() {
	return clusteringType;
}






public String[] getTitle() {
	return title;
}


public void setTitle(String[] title) {
	this.title = title;
}


public Map<Integer, ResultsForGui> getClusteringResults() {
	return clusteringResults;
}


public void setClusteringResults(Map<Integer, ResultsForGui> clusteringResults) {
	this.clusteringResults = clusteringResults;
}






 
 
	
}
