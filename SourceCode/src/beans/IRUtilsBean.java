package beans;

import java.util.List;
import java.util.Map;

public class IRUtilsBean {
	
	
	public IRUtilsBean(){
		
	}
	public IRUtilsBean(String url,int depth){
		this.urlStr = url;
		this.depthVal = depth;
	}
	
	//index
	private String urlStr;
	private int depthVal;
	private String btn;
	private String indexSummary;
	private int maxURLs;
	
	
	//search
	private String searchQry;
	private String searchResultSummary;
	
	private Map<Integer, String> searchResultsMap;

	
	public int getMaxURLs() {
		return maxURLs;
	}
	public void setMaxURLs(int maxURLs) {
		this.maxURLs = maxURLs;
	}
	public String getUrlStr() {
		return urlStr;
	}

	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}

	public int getDepthVal() {
		return depthVal;
	}

	public void setDepthVal(int depthVal) {
		this.depthVal = depthVal;
	}
	
	public void startCrawlingAndCollectImages(String url, int depth) {
		
	}

	public String getBtn() {
		return btn;
	}

	public void setBtn(String btn) {
		this.btn = btn;
	}

	public String getIndexSummary() {
		return indexSummary;
	}

	public void setIndexSummary(String indexSummary) {
		this.indexSummary = indexSummary;
	}
	
	
	

	
}

