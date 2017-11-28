
package crawlAndSearch;

import java.io.File;
import java.io.IOException;
public class Repository {
	public final String indexFolder; // for lucene index
	public static String crawlerFolder; // for crawled images
	public static String searchedDocsFolder; // temp storage for weka files, clustering

	public Repository() throws IOException{
		indexFolder="Index";
		searchedDocsFolder="SearchedDocsFolder";
		
	}
}

