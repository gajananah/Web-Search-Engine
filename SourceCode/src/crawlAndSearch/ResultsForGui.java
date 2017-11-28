package crawlAndSearch;

import java.util.List;


public class ResultsForGui {
   public String url;
   public String path;
   public float score;
   public String title;
   public int imagePerURL;
   public List<String> urlsPerCluster;
   
   //extra for clustering
   public String clusterNum;
   public String clusterURLs;
   public int clusterNumOfURLs;
   
   public int getImagePerURL() {
	return imagePerURL;
}
public void setImagePerURL(int imagePerURL) {
	this.imagePerURL = imagePerURL;
}
public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}
public String getPath() {
	return path;
}
public void setPath(String path) {
	this.path = path;
}
public float getScore() {
	return score;
}
public void setScore(float score) {
	this.score = score;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getClusterNum() {
	return clusterNum;
}
public void setClusterNum(String clusterNum) {
	this.clusterNum = clusterNum;
}
public String getClusterURLs() {
	return clusterURLs;
}
public void setClusterURLs(String clusterURLs) {
	this.clusterURLs = clusterURLs;
}
public int getClusterNumOfURLs() {
	return clusterNumOfURLs;
}
public void setClusterNumOfURLs(int clusterNumOfURLs) {
	this.clusterNumOfURLs = clusterNumOfURLs;
}
public List<String> getUrlsPerCluster() {
	return urlsPerCluster;
}
public void setUrlsPerCluster(List<String> urlsPerCluster) {
	this.urlsPerCluster = urlsPerCluster;
}
   
   
   
}