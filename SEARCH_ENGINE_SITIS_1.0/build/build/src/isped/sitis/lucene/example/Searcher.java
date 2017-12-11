package isped.sitis.lucene.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {


	//=========================================================
	// Now search
	//=========================================================

	static String indexLocation = null;
	private static CharArraySet CharArraySetSWFR = new CharArraySet(
			getStopWord("D:\\Projet_Gayo\\RIIFT\\stop-words-collection-2011.11.21\\stop-words\\stop-words-french.txt"),
			true);
	private static CharArraySet CharArraySetSWEN = new CharArraySet(
			getStopWord(
					"D:\\Projet_Gayo\\RIIFT\\stop-words-collection-2011.11.21\\stop-words\\stop-words-english1.txt"),
			true);
	private static EnglishAnalyzer analyzer = new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
	private static FrenchAnalyzer analyzerFR = new FrenchAnalyzer(CharArraySetSWFR);
	private static EnglishAnalyzer analyzerEN = new EnglishAnalyzer(CharArraySetSWEN);
	private static StandardAnalyzer analyzerStd = new StandardAnalyzer();


	static IndexReader reader ;
	static  IndexSearcher searcher =null;
	//static TopScoreDocCollector collector = TopScoreDocCollector.create(5, true);
	static TopScoreDocCollector collector = TopScoreDocCollector.create(5);
	
	
	
	
	public static void main(String[] args) throws IOException {	
		System.out.println("Enter the path where the index will be read");
		BufferedReader br = new BufferedReader(
	            new InputStreamReader(System.in));
		String s ="";
	    try{
	    	 s = br.readLine();
	    	indexLocation=s;
	    } catch (Exception ex) {
	        System.out.println("Cannot read index..." + ex.getMessage());
	        System.exit(-1);
	      }
		 //s = "";
		try{
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
			
			searcher= new IndexSearcher(reader);

			 br = new BufferedReader(
					new InputStreamReader(System.in));
			s = br.readLine();



			while (!s.equalsIgnoreCase("q")) {

				System.out.println("Enter the search query (q=quit):");
				s = br.readLine();
				if (s.equalsIgnoreCase("q")) {
					break;
				}
				Query q = new QueryParser("contents", analyzer).parse(s);
				searcher.search(q, collector);
				ScoreDoc[] hits = collector.topDocs().scoreDocs;

				// 4. display results
				System.out.println("Found " + hits.length + " hits.");
				for(int i=0;i<hits.length;++i) {
					int docId = hits[i].doc;
					Document d = searcher.doc(docId);
					System.out.println((i + 1) + ". " + d.get("path") + " score=" + hits[i].score);
				}

			}  

		}catch (Exception e) {
			System.out.println("Error searching " + s + " : " + e.getMessage());
		} 
	}
	
	public static ArrayList<String> getStopWord(String FileName) {
		ArrayList<String> stop_words = new ArrayList<String>();
		try {
			BufferedReader is = new BufferedReader(new FileReader(FileName));
			String inputLine;
			while ((inputLine = is.readLine()) != null) {
				// stop_words.add(inputLine);
				// System.out.println(inputLine);
			}
		} catch (IOException io) {

		}
		return stop_words;
	}


}
