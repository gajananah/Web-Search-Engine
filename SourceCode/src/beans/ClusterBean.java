package beans;

import java.util.List;

public class ClusterBean {
	
	public String clusterNum;
	public String clusterURLs;
	public String numURLs;
	public List<String> arrayListofURLsPerCluster;

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

	public String getNumURLs() {
		return numURLs;
	}

	public void setNumURLs(String numURLs) {
		this.numURLs = numURLs;
	}

	public List<String> getArrayListofURLsPerCluster() {
		return arrayListofURLsPerCluster;
	}

	public void setArrayListofURLsPerCluster(List<String> arrayListofURLsPerCluster) {
		this.arrayListofURLsPerCluster = arrayListofURLsPerCluster;
	}
	
	

}
