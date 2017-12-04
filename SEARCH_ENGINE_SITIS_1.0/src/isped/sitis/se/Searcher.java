package isped.sitis.se;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

public class Searcher {
	//private static EnglishAnalyzer analyzer = new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
	//static IndexReader reader;
	//static IndexSearcher searcher = null;
	//static TopScoreDocCollector collector = TopScoreDocCollector.create(5);
	public static ArrayList<String> resultat = new ArrayList<String>();
	public static Query query ;
	static String indexLocation = null;
	//public static indexLocation;
	Searcher(String indexDir, String analyzerLang, String q) throws ParseException  {
		CharArraySet CharArraySetSW;
		
		switch (analyzerLang) {
		//Choix de l'Analyse de la reqête
		case "FR":
			CharArraySetSW = new CharArraySet(getStopWord("./resources/StopWord/stop-words-french.txt"),true);
			FrenchAnalyzer analyzerFR = new FrenchAnalyzer(CharArraySetSW);
			query = new QueryParser("contents", analyzerFR).parse(q);
			break;
		case "EN":
			CharArraySetSW = new CharArraySet(getStopWord("./resources/StopWord/stop-words-english1.txt"),true);
		    EnglishAnalyzer analyzerEN = new EnglishAnalyzer(CharArraySetSW);
		    //EnglishAnalyzer analyzerEN = new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
		    query = new QueryParser("contents", analyzerEN).parse(q);
			break;
		default:
			StandardAnalyzer analyzerStd = new StandardAnalyzer();
			query = new QueryParser("contents", analyzerStd).parse(q);
			break;
		}

	}
	// static TopScoreDocCollector collector = TopScoreDocCollector.create(5, true);
	public static ArrayList<String> Search(String indexLocation, String s, int topDoc, String analyzerLang)
			throws IOException, ParseException {
		new Searcher(indexLocation,"EN",s);
		ArrayList<String> result = new ArrayList<String>();
		IndexReader reader;
		IndexSearcher searcher = null;
		TopScoreDocCollector collector = TopScoreDocCollector.create(5);
		reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
		searcher = new IndexSearcher(reader);
		searcher.search(query, collector);
		System.out.println("OK!");
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		String resultLign;
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			resultLign = (i + 1) + ";" + d.get("path") + ";" + hits[i].score;
			result.add(resultLign);

		}
		return result;
		
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Enter the path where the index will be read");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String s = "";
		String q = "";
		try {
			s = br.readLine();
			indexLocation = s;
		} catch (Exception ex) {
			System.out.println("Cannot read index..." + ex.getMessage());
			System.exit(-1);
		}
		while (!s.equalsIgnoreCase("q")) {
			br = new BufferedReader(new InputStreamReader(System.in));
			q = br.readLine();
			try {
				System.out.println("Enter the search query (q=quit):");
				q = br.readLine();
				if (q.equalsIgnoreCase("q")) {
					break;
				}
				//Fonction Search
				
				resultat = Search(indexLocation, q, 7, "EN");
				Iterator<String> iterator = resultat.iterator();
				while (iterator.hasNext()) {
					System.out.println(iterator.next());
				}
		} catch (Exception e) {
			System.out.println("Error searching " + s + " : " + e.getMessage());
		}
			
		}

	}
	public static ArrayList<String> getStopWord(String FileName) {
		ArrayList<String> stop_words = new ArrayList<String>();
		try {
			BufferedReader is = new BufferedReader(new FileReader(FileName));
			String inputLine;
			while ((inputLine = is.readLine()) != null) {
				 stop_words.add(inputLine);
			 //System.out.println(inputLine);
			}
		} catch (IOException io) {

		}
		return stop_words;
	}
}
