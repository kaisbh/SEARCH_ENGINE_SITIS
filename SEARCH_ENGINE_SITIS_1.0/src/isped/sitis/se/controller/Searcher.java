package isped.sitis.se.controller;

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
	static String indexLocation = null;

	//private static EnglishAnalyzer analyzer = new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());

	//static IndexReader reader;
	//static IndexSearcher searcher = null;
	//static TopScoreDocCollector collector = TopScoreDocCollector.create(5);

	// static TopScoreDocCollector collector = TopScoreDocCollector.create(5, true);
	public static ArrayList<String> Search(String indexLocation, String query, int topDoc, String analyzerLang)
			throws IOException, ParseException {
		CharArraySet CharArraySetSW;
		Query q;
		switch (analyzerLang) {
		//Choix de l'Analyse de la reqête
		case "FR":
			CharArraySetSW = new CharArraySet(getStopWord("./resources/StopWord/stop-words-french.txt"),true);
			FrenchAnalyzer analyzerFR = new FrenchAnalyzer(CharArraySetSW);
			q = new QueryParser("contents", analyzerFR).parse(query);
			break;
		case "EN":
			CharArraySetSW = new CharArraySet(getStopWord("./resources/StopWord/stop-words-english1.txt"),true);
		    EnglishAnalyzer analyzerEN = new EnglishAnalyzer(CharArraySetSW);
		    //EnglishAnalyzer analyzerEN = new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
		    q = new QueryParser("contents", analyzerEN).parse(query);
			break;
		default:
			StandardAnalyzer analyzerStd = new StandardAnalyzer();
			q = new QueryParser("contents", analyzerStd).parse(query);
			break;
		}

		ArrayList<String> result = new ArrayList<String>();
		IndexReader reader;
		IndexSearcher searcher = null;
		TopScoreDocCollector collector = TopScoreDocCollector.create(5);
		reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
		searcher = new IndexSearcher(reader);
		

		searcher.search(q, collector);
		System.out.println("OK!");
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		String resultLign = "Found " + hits.length + " hits.";
		result.add(resultLign);
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			resultLign = (i + 1) + ". " + d.get("path") + " score=" + hits[i].score;
			result.add(resultLign);

		}
		return result;
		
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Enter the path where the index will be read");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		ArrayList<String> resultat = new ArrayList<String>();
		String s = "";
		try {
			s = br.readLine();
			indexLocation = s;
		} catch (Exception ex) {
			System.out.println("Cannot read index..." + ex.getMessage());
			System.exit(-1);
		}
		while (!s.equalsIgnoreCase("q")) {
			br = new BufferedReader(new InputStreamReader(System.in));
			s = br.readLine();
			try {
				System.out.println("Enter the search query (q=quit):");
				s = br.readLine();
				if (s.equalsIgnoreCase("q")) {
					break;
				}
				//Fonction Search
				resultat = Search(indexLocation, s, 7, "EN");
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
				 //stop_words.add(inputLine);
			 //System.out.println(inputLine);
			}
		} catch (IOException io) {

		}
		return stop_words;
	}
}
