package isped.sitis.se;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import isped.sitis.se.model.DocScored;
import isped.sitis.se.model.VocabTerm;

public class VocabSearcher extends Parametre {

	public static void main(String[] args) throws IOException {
		ArrayList<VocabTerm> result = fuzzySearch("cacr");
		 getMostPertinentVocab(result);
	}

	public static ArrayList<VocabTerm> fuzzySearch(String s) throws IOException {
		ArrayList<VocabTerm> result = new ArrayList<VocabTerm>();
		Builder booleanQuery = new BooleanQuery.Builder();
		String[] QueryTerms;
		QueryTerms = s.toLowerCase().split(" ");
		for (int i = 0; i < QueryTerms.length; i++) {
			FuzzyQuery query = new FuzzyQuery(new Term("content", QueryTerms[i].trim()));
			booleanQuery.add(query, Occur.SHOULD);
		}

		TopScoreDocCollector collector = TopScoreDocCollector.create(10);
		RAMDirectory ramDir = new RAMDirectory();
		Analyzer analyzer = new StandardAnalyzer();
		writeIndex(ramDir, analyzer, VOCAB_FILE);

		IndexReader reader = DirectoryReader.open(ramDir);
		IndexSearcher searcher = new IndexSearcher(reader);
		// query1.createWeight(searcher, true);

		searcher.search(booleanQuery.build(), collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println("Vacab Concept : " + d.get("concept") + "; Content : " + d.get("content") + "; Score : "
					+ hits[i].score);
			VocabTerm vt= new VocabTerm(d.get("content"), 0, 0, 0, hits[i].score, d.get("concept"), docId);
			result.add(vt);
		}
		return result;
	}
	public static String getMostPertinentVocab(ArrayList<VocabTerm> result) {
		String Rslt;
		Iterator<VocabTerm> itr = result.iterator();
		VocabTerm VocabRslt = null;
		float MaxVocabScore = 0;

		while (itr.hasNext()) {
			VocabTerm vocab = itr.next();
			if (vocab.weight > MaxVocabScore) {
				VocabRslt = vocab;
				MaxVocabScore = vocab.weight;
			}
		}
		System.out.println("Most pertinent vocab term : " + VocabRslt.termText);
		Rslt = VocabRslt.termText;
		return Rslt;	
	}

	static void writeIndex(RAMDirectory ramDir, Analyzer analyzer, String VOCAB_FILE) {

		try {

			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE);

			IndexWriter writer = new IndexWriter(ramDir, iwc);

			BufferedReader br;
			String line;
			String[] splittedLigne = null;
			String[] splittedVocabTerms = null;
			try {
				br = new BufferedReader(new FileReader(new File(VOCAB_FILE)));
				line = br.readLine();
				while ((line = br.readLine()) != null) {
					splittedLigne = line.toLowerCase().split(":");
					String concept = splittedLigne[0];
					// if (splittedLigne[0].toLowerCase().equals(concept.toLowerCase())) {
					splittedVocabTerms = splittedLigne[1].split(",");
					for (int i = 0; i < splittedVocabTerms.length; i++) {
						Document doc = new Document();
						doc.add(new TextField("content", splittedVocabTerms[i].trim(), Store.YES));
						doc.add(new TextField("concept", concept, Store.YES));
						writer.addDocument(doc);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			writer.close();
		} catch (IOException e) {
			// Any error goes here
			e.printStackTrace();
		}

	}

	static void indexDoc(IndexWriter writer, String name, String content) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("name", name, Store.YES));
		doc.add(new TextField("content", content, Store.YES));
		writer.addDocument(doc);
	}

	static void analyseVocabIndex() throws IOException {
		RAMDirectory ramDir = new RAMDirectory();
		Analyzer analyzer = new StandardAnalyzer();
		writeIndex(ramDir, analyzer, VOCAB_FILE);

		IndexReader reader = DirectoryReader.open(ramDir);
		System.out.println(reader.getDocCount("content"));
		for (int i = 0; i < reader.getDocCount("content"); i++) {
			Document doc = reader.document(i);
			System.out.println("content: " + doc.getField("content") + "; " + doc.get("content") + "; concept:"
					+ doc.get("concept"));
		}

		reader.close();

	}

	static void searchVocabIndex(String term) {
		RAMDirectory ramDir = new RAMDirectory();
		Analyzer analyzer = new StandardAnalyzer();

		writeIndex(ramDir, analyzer, VOCAB_FILE);
		IndexReader reader = null;
		try {
			// Create Reader
			reader = DirectoryReader.open(ramDir);

			System.out.println(reader.document(0).get("content"));
			// Create index searcher
			IndexSearcher searcher = new IndexSearcher(reader);

			// Build query
			QueryParser qp = new QueryParser("content", analyzer);
			Query query = qp.parse(term);

			// Search the index
			TopDocs foundDocs = searcher.search(query, 10);

			// Total found documents
			System.out.println("Total Results :: " + foundDocs.totalHits);

			// Let's print found doc names and their content along with score
			for (ScoreDoc sd : foundDocs.scoreDocs) {
				Document d = searcher.doc(sd.doc);
				System.out.println("Vacab Concept : " + d.get("concept") + "  :: Content : " + d.get("content")
						+ "  :: Score : " + sd.score);
			}
			// don't forget to close the reader
			reader.close();
		} catch (IOException | ParseException e) {
			// Any error goes here
			e.printStackTrace();
		}

	}
}
