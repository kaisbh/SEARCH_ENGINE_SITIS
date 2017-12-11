package isped.sitis.se;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import isped.sitis.se.model.DocScored;

public class Searcher extends Parametre{
	//private static EnglishAnalyzer analyzer = new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
	//static IndexReader reader;
	//static IndexSearcher searcher = null;
	//static TopScoreDocCollector collector = TopScoreDocCollector.create(5);
	public static ArrayList<String> resultat = new ArrayList<String>();
	public static Query query ;
	static String indexLocation = null;
	//public static indexLocation;
	Searcher(String analyzerLang, String q) throws ParseException  {
		CharArraySet CharArraySetSW;
		
		switch (analyzerLang) {
		//Choix de l'Analyse de la reqête
		case "FR":
			indexLocation = INDEX_DIR_FR;
			CharArraySetSW = new CharArraySet(getStopWord(STOP_WORD_FR_FILE),true);
			FrenchAnalyzer analyzerFR = new FrenchAnalyzer(CharArraySetSW);
			query = new QueryParser("contents", analyzerFR).parse(q);
			break;
		case "EN":
			indexLocation = INDEX_DIR_EN;
			CharArraySetSW = new CharArraySet(getStopWord(STOP_WORD_EN_FILE),true);
		    EnglishAnalyzer analyzerEN = new EnglishAnalyzer(CharArraySetSW);
		    //EnglishAnalyzer analyzerEN = new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
		    query = new QueryParser("contents", analyzerEN).parse(q);
			break;
		default:
			indexLocation = INDEX_DIR;
			StandardAnalyzer analyzerStd = new StandardAnalyzer();
			query = new QueryParser("contents", analyzerStd).parse(q);
			break;
		}

	}
	// static TopScoreDocCollector collector = TopScoreDocCollector.create(5, true);
	public static ArrayList<String> Search(String s, int topDoc, String analyzerLang)
			throws IOException, ParseException {
		new Searcher(analyzerLang,s);
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
			resultLign = (i + 1) + ";" + d.get("path") + ";" + hits[i].score+";"+getDocSubject(docId);
			result.add(resultLign);

		}
		return result;
		
	}
	public static ArrayList<String> fuzzySearch(String s, int topDoc, String analyzerLang)
			throws IOException, ParseException {
		new Searcher(analyzerLang,s);
		ArrayList<String> result = new ArrayList<String>();
	
		TopScoreDocCollector collector = TopScoreDocCollector.create(5);
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
		IndexSearcher searcher = new IndexSearcher(reader);
		//searcher.search(query, collector);
		//******************************************************************
		Builder booleanQuery = new BooleanQuery.Builder();
		String[] QueryTerms;
		QueryTerms = s.toLowerCase().split(" ");
		for(int i= 0; i<QueryTerms.length;i++) {
			FuzzyQuery query = new FuzzyQuery(new Term("content", QueryTerms[i].trim()));
			booleanQuery.add(query, Occur.SHOULD);
		}
		
		reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
		searcher = new IndexSearcher(reader);
		//searcher.search(booleanQuery, collector);


		searcher.search(booleanQuery.build(), collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		String resultLign;
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
		
			resultLign = (i + 1) + ";" + d.get("path") + ";" + hits[i].score+";"+getDocSubject(docId);
			result.add(resultLign);
				
		}
		
		
		//*********************************************************************

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
				
				resultat = Search( q, 10, "EN");
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
	public static String getDocSubject(int docId) {
		String docConcept = null;
		ArrayList<DocScored> docs = loadIndexConcept(CONCEPT_INDEX_FILE);
		Stream<DocScored> Filtereddocs = docs.stream()
				.filter(o -> o.docId==docId);
		Iterator<DocScored> itr = Filtereddocs.iterator();
		DocScored doc = itr.next();
		docConcept = doc.docConcept;
		return docConcept;
		
	}
	public static ArrayList<DocScored> loadIndexConcept(String CONCEPT_INDEX_FILE) {
		ArrayList<DocScored> DocList = new ArrayList<DocScored>();;
		try {
			BufferedReader br = new BufferedReader(new FileReader(CONCEPT_INDEX_FILE));
			String line; 
			
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				String[] splittedLigne = line.toLowerCase().split(";");
				int numOrder = Integer.parseInt(splittedLigne[0]);
				String docPath = splittedLigne[1];
				int docId = Integer.parseInt(splittedLigne[2]);
				String docConcept = splittedLigne[3];
				float scoreDocConcept = Float.parseFloat(splittedLigne[4]);
			
				
				DocScored vt = new DocScored(numOrder, docPath, docId, docConcept,scoreDocConcept);
				DocList.add(vt);
				
			}
		} catch (Exception e) {
		}

		return DocList;
	}
}
