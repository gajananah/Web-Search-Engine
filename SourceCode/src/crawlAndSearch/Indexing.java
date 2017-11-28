package crawlAndSearch;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexing {
	public String indexFolderName;
	public String crawlerFolderName;
	Path indexPath;
	Directory directory;
	Analyzer analyzer;
	IndexWriterConfig indexWriterConfig;
	IndexWriter indexWriter;
	String inp;

	public Indexing(){
		//used by clustering
		analyzer=new StandardAnalyzer();
		
	}
	
	public Indexing(String contextPath){
		Repository repository;
		
		try {
			repository = new Repository();
			indexFolderName=repository.indexFolder;
			indexPath=Paths.get(indexFolderName);
			//Creates the lucene index directory/folder
			Path p1 = Paths.get(contextPath, indexPath.toString());
			Files.createDirectories(p1);
		    
			directory=FSDirectory.open(p1);
			//Build an analyzer with default stopwords
			analyzer=new StandardAnalyzer();
			//Create an index Writer, which would be used to create and maintain an index.
			indexWriterConfig=new IndexWriterConfig(analyzer);
			//Creates a new index or overwrites an existing one.
			indexWriterConfig.setOpenMode(OpenMode.CREATE);
			indexWriter=new IndexWriter(directory,indexWriterConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
/*
 * String Field - A field that is indexed but not tokenized: the entire String value is indexed as a single token	
 * A field that is indexed and tokenized, without term vectors
 */
	public Document indexWebPage(String contents,String title,String url) throws IOException{
		String stemmedContents="";
		Document document=new Document();
		//stem the contents
		stemmedContents = porterStem(contents);
		Field urlField = new StringField("url", url, Field.Store.YES);
		document.add(urlField);
		Field contentField = new TextField("text", stemmedContents, Field.Store.YES);
		document.add(contentField);
		Field titleField = new TextField("title", title, Field.Store.YES);
		document.add(titleField);
		return document;
	}
//implementing tokenizing and stemming 
	public String porterStem(String contents){
		TokenStream tokenStream = analyzer.tokenStream("contents", contents);
		//Transforms the token stream as per the Porter stemming algorithm
		tokenStream=new PorterStemFilter(tokenStream);
		/*passing in the actual type of the Attribute to the addAttribute(Class), which then checks if an
		instance of that type is already present.
		If yes, it returns the instance, otherwise it creates a new instance and returns it. */
				CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
				String stemmedTerms="";
				try {
					tokenStream.reset();
				} catch (IOException unableToResetException) {
					unableToResetException.printStackTrace();
				}
				try {
					while (tokenStream.incrementToken()) {
						String term = charTermAttribute.toString();
						stemmedTerms+=" "+term;
					}
					tokenStream.close();
				} catch (IOException fileOperationsException) {
					fileOperationsException.printStackTrace();
				}catch(Exception e){
					System.err.println(e);
					e.printStackTrace();
				}
			
		return stemmedTerms;
		
	}
}

