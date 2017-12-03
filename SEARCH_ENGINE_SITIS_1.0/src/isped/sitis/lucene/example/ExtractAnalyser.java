package isped.sitis.lucene.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.jena.shared.uuid.Bits;
import org.apache.lucene.util.Bits;

import org.apache.lucene.util.BytesRef;
import org.antlr.v4.runtime.TokenStream;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.store.FSDirectory;

public class ExtractAnalyser {

	static CharArraySet CharArraySetSW = new CharArraySet(getStopWord("./resources/StopWord/stop-words-english1.txt"),
			true);
	static EnglishAnalyzer analyzerEN = new EnglishAnalyzer(CharArraySetSW);

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

	public static void main(String[] args) throws Exception {
		String indexLocation = "D:\\Projet_Gayo\\Index";
		IndexReader Reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
		String[] fields = { "contents" };
		Document doc = Reader.document(4);
		System.out.println("Processing file:" + doc.get("filename"));
		System.out.println(tf("Lymphoma",1));
		test();

		Terms termVector = Reader.getTermVector(4, "contents");
		System.out.println(termVector.size());
		BytesRef termMax = termVector.getMax();
		System.out.println(termMax.utf8ToString());
		BytesRef termMin = termVector.getMin();
		System.out.println(termMin.utf8ToString());

		TermsEnum itr = termVector.iterator();
		BytesRef term = null;

		while ((term = itr.next()) != null) {
			try {

				String termText = term.utf8ToString();
				// String txt = BinaryDecoder.decodeTerm("contents", itr);
				Term termInstance = new Term("contents", term);
				long termFreq = Reader.totalTermFreq(termInstance);
				long docCount = Reader.docFreq(termInstance);
				long termFreqDoc = termVector.getSumDocFreq();

				System.out.println("term: " + termText + ", termFreq = " + termFreq + ", docCount = " + docCount);
				System.out.println("term: " + termText + ", termFreqDoc = " + termFreqDoc);
			} catch (Exception e) {
				System.out.println(e);
			}
		}

		IndexSearcher searcher = new IndexSearcher(Reader);
		Terms terms = searcher.getIndexReader().getTermVector(1, "contents");
		// TermsEnum termsEnum = terms.iterator();
		Query q = new QueryParser("contents", analyzerEN).parse("Lymphoma");
		System.out.println(searcher.count(q));
		// Term term = new Term("Lymphoma");
		IndexReaderContext context = searcher.getTopReaderContext();

		Terms terms1 = MultiFields.getTerms(Reader, "contents");
		int n = (int) terms1.size();
		TermContext[] states = new TermContext[n];
		TermStatistics termStats[] = new TermStatistics[n];
		// TermContext[] states = new TermContext[5];
		TermsEnum termsEnum = terms1.iterator();
		for (int i = 0; i < 5; i++) {
			// Term term1 = termsEnum.t ;
			// Term term = terms1[i];
			Term term1 = new Term("contents", termsEnum.next().utf8ToString());
			states[i] = TermContext.build(context, term1);
			termStats[i] = searcher.termStatistics(term1, states[i]);
			System.out.println(termsEnum.next().utf8ToString() + " " + termStats[i].totalTermFreq());
		}
		

		// termVectors.size();
		// Terms terms = MultiFields.getTerms(Reader, "contents"); //
		/*
		 * Term termText = new Term("Lymphoma"); if (terms != null) { TermsEnum
		 * termsEnum = terms.iterator(); BytesRef termText1 = new BytesRef("Lymphoma");
		 * if (termsEnum.seekExact(termText1)) { termsEnum.totalTermFreq(); //
		 * System.out.println(termsEnum.totalTermFreq()); }
		 * System.out.println(Reader.getSumTotalTermFreq("Content"));
		 * 
		 * }
		 */
		//

		Reader.close();
	}

	private static double getTf(String word, String[] docSplit, int docLen) {
		// number of occurences of this word in document
		int termFreq = 0;
		for (int k = 0; k < docSplit.length; k++) {
			if (word == docSplit[k]) {
				termFreq++;
			}
		}
		return (termFreq / (float) docSplit.length);
	}
	public static Integer tf (String term, Integer idDoc) throws IOException {
		String indexLocation = "D:\\Projet_Gayo\\Index";
		String corpusLocation ="D:\\Projet_Gayo\\MyCorpus";
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
		Integer tf = null;
		Terms pathVector = reader.getTermVector(1, "path");
		TermsEnum pathEnum = pathVector.iterator();
		String pathText = corpusLocation+"\\"+pathEnum.next().utf8ToString()+".txt";
		System.out.println(pathText);
		return tf;
	}

	public static void test() throws IOException {
		String indexLocation = "D:\\Projet_Gayo\\Index";
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
		// access indexed fields for an index segment
		// access term vector fields for a specified document
		Fields fields = reader.getTermVectors(1);

		for (String field : fields) {
			// access the terms for this field
			Terms terms = fields.terms(field);
			// metadata about the field
			System.out.println("positions? " + terms.hasPositions());
			System.out.println("offsets? " + terms.hasOffsets());
			System.out.println("payloads? " + terms.hasPayloads());
			// iterate through terms

			Terms pathVector = reader.getTermVector(1, "path");
			TermsEnum pathEnum = pathVector.iterator();
			String pathText = pathEnum.next().utf8ToString();
			System.out.println(pathText);
			
			TermsEnum termsEnum = terms.iterator();
			Terms termVector = reader.getTermVector(1, "contents");
			TermsEnum termsEnum1 = termVector.iterator();
			
			//PostingsEnum p = null;
			//p = termsEnum1.postings(p);
			/*
			while (termsEnum1.next() != null) {
				p = termsEnum1.postings(p, PostingsEnum.ALL);
				while (p.nextDoc() != PostingsEnum.NO_MORE_DOCS) {
					int freq = p.freq();
					for (int i = 0; i < freq; i++) {
						int pos = p.nextPosition(); // Always returns -1!!!
						BytesRef data = p.getPayload();
						System.out.println(freq + pos + data.utf8ToString()); // Fails miserably, of course.
					}
				}
			}*/
			// seek to a specific term
			boolean found = termsEnum1.seekExact(new BytesRef("Lymphoma"));
			if (found) {
				// get the document frequency
				System.out.println(termsEnum1.docFreq());
				// enumerate through documents
				// DocsEnum docs = termsEnum.docs(null, null);
				// enumerate through documents and positions
				// DocsAndPositionsEnum docsAndPositions = termsEnum.docsAndPositions(null,
				// null);
			}
			BytesRef term = null;
			while ((term = termsEnum.next()) != null) {
				System.out.println(termsEnum.term().utf8ToString());
			}
		}

	}

	public static long getTotalTermFreq(IndexReader reader, String field, BytesRef termtext) throws Exception {
		// BytesRef br = termtext;
		long totalTF = 0;
		try {
			Bits liveDocs = MultiFields.getLiveDocs(reader);
			totalTF = reader.getSumTotalTermFreq(field);
			return totalTF;
		} catch (Exception e) {
			return 0;
		}
	}
}
