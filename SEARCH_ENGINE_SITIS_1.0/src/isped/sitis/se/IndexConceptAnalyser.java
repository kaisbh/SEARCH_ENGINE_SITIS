package isped.sitis.se;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.lucene.search.similarities.*;

public class IndexConceptAnalyser extends Parametre{
	
	static CharArraySet CharArraySetSW = new CharArraySet(getStopWord("./resources/StopWord/stop-words-english1.txt"),
			true);
	static EnglishAnalyzer analyzerEN = new EnglishAnalyzer(CharArraySetSW);

	//public static String indexLocation = "D:\\Projet_Gayo\\Index";
	//public static String vocabLocation = "./resources/vocab.txt";
	static IndexReader reader;
	static IndexSearcher searcher;
	static Terms contentsTermVector;
	static Terms pathTermVector;
	static Terms filenameTermVector;
	static IndexReaderContext context;
	static String[] VocabTerms = null;
	static ArrayList<VocabTerm> vocabTermList = new ArrayList<VocabTerm>();
	static ArrayList<Concept> concepts = new ArrayList<Concept>();
	static ArrayList<DocScored> vocabDocList = new ArrayList<DocScored>();
	static Concept docConcept;
	
	public IndexConceptAnalyser(String indexLocation) {
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
			searcher = new IndexSearcher(reader);
			context = searcher.getTopReaderContext();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		IndexConceptAnalyser analyser = new IndexConceptAnalyser(INDEX_DIR);
		analyser.makeDocList();
		analyser.afficheDocList(vocabDocList);
		//analyser.afficheVocabList(vocabTermList);
		//System.out.println(getMaxDocConceptScores(1));
		//getDocConceptScores(1);
		//afficheConcept();
		

	}

	
	public  void makeDocList() throws Exception {
		concepts = getConcepts(VOCAB_FILE);
		for (int i = 0; i < reader.getDocCount("contents"); i++) {
			makeDocVocab(VOCAB_FILE,i);
			Document doc = reader.document(i);
			String docPath = doc.get("path");
			int docId = i;
			makeDocConceptScores(i);
			System.out.println("#################################################################################################################################################");
			System.out.println(doc.get("path"));
			System.out.println("***********************************");
			System.out.println("Vocabulaire:");
			afficheVocabList(vocabTermList);
			System.out.println("***********************************");
			System.out.println("Concepts:");
			afficheConcept(concepts) ;
			System.out.println("#################################################################################################################################################");
			float scoreDocConcept = getMaxDocConceptScores();
			String docConcept = IndexConceptAnalyser.docConcept.conceptName;
			DocScored vd = new DocScored( i+1,docPath, docId, docConcept, scoreDocConcept);
			vocabDocList.add(vd);
		}
		
	}
	public  void addVocab(String vocabTerm, int idDoc, String concept, ArrayList<VocabTerm> vocabTermList) throws Exception {
		Document doc = reader.document(idDoc);
		Terms terms = getContentsTermVector(idDoc, IndexConceptAnalyser.reader);
		TermsEnum termsEnum = terms.iterator();
		int n = (int) terms.size();
		BytesRef iter = termsEnum.next();
		TermContext[] states = new TermContext[n];
		TermStatistics termStats[] = new TermStatistics[n];
		int i = 0;
		while (iter != null) {
			if (iter.utf8ToString().equals(vocabTerm.toLowerCase())) {
				Term term = new Term("contents", iter.utf8ToString());
				states[i] = TermContext.build(context, term);
				termStats[i] = searcher.termStatistics(term, states[i]);
				String termText = iter.utf8ToString();
				int termFrq = tf(iter.utf8ToString(), doc.get("path"));
				int totalTermFrq = (int) termStats[i].totalTermFreq();
				int docFrq = (int) termStats[i].docFreq();
				float tfIdf = tfIdf(termFrq, docFrq);
				VocabTerm vt = new VocabTerm(termText, termFrq, totalTermFrq, docFrq, tfIdf, concept,idDoc);
				vocabTermList.add(vt);
			}
			i++;
			iter = termsEnum.next();
		}

	}
	public  void initVocab() {
		//int i =0;
		//Iterator<VocabTerm> itr1 = vocabTermList.iterator();
		//while (itr1.hasNext()) {
		//	VocabTerm term = itr1.next();
			vocabTermList.removeAll(vocabTermList);
		//	i++;
		//}
	}
	public  void makeDocVocab(String vocabLocation, int docId) throws Exception {
		initVocab();
		Iterator<Concept> itr2 = concepts.iterator();
		while (itr2.hasNext()) {
			Concept concept = itr2.next();
			// addVocab(concept.conceptName, docId,concept.conceptName);
			String[] VocabTerms = extractVocabTerms(vocabLocation, concept.conceptName);
			for (int j = 0; j < VocabTerms.length; j++) {
				addVocab(VocabTerms[j], docId, concept.conceptName, vocabTermList);
			}

		}
		
	}
	public  ArrayList<Concept> getConcepts(String vocabLocation) {
		ArrayList<Concept> concepts2 = new ArrayList<Concept>();
		BufferedReader br;
		String line;
		String[] splittedLigne = null;
		try {
			br = new BufferedReader(new FileReader(new File(vocabLocation)));
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				// System.out.println(line.toLowerCase());
				splittedLigne = line.toLowerCase().split(":");
				if (!splittedLigne.equals(null)) {
					// System.out.println(splittedLigne[0]);
					Concept concept = new Concept(splittedLigne[0].toLowerCase(), 0);
					concepts2.add(concept);
		
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return concepts2;
	}
	public  float getMaxDocConceptScores() throws Exception {
		//getDocConceptScores(docId);
		Iterator<Concept> itr = concepts.iterator();
		float MaxConceptScore = 0;
		while (itr.hasNext()) {
			Concept concept = itr.next();
			if (concept.totalScore > MaxConceptScore) {
				MaxConceptScore = concept.totalScore;
				docConcept  = concept;
			}
		}
		return MaxConceptScore;
	}

	public  void makeDocConceptScores(int docId) throws Exception {
		Iterator<Concept> itr1 = concepts.iterator();
		while (itr1.hasNext()) {
			Concept concept = itr1.next();
			concept.totalScore = 0;
			// addVocab(concept.conceptName, docId,concept);
			Iterator<VocabTerm> itr2 = vocabTermList.iterator();
			while (itr2.hasNext()) {
				VocabTerm Term = itr2.next();
				if (Term.concept == concept.conceptName) {
					concept.totalScore = concept.totalScore + Term.tfIdf;
					
				}
			}

		}
	}
	public  float tfIdf(int tf, int df) throws IOException {
		ClassicSimilarity sim = new ClassicSimilarity();
		int N = reader.getDocCount("contents");
		float TF = sim.tf(tf);
		float IDF = sim.idf(df, N);
		float tfIdf = TF * IDF;
		return tfIdf;
	}
	
	public  String[] extractVocabTerms(String pathVocab, String concept) {
		BufferedReader br;
		String line;
		String[] splittedLigne = null;
		String[] splittedVocabTerms = null;
		try {
			br = new BufferedReader(new FileReader(new File(pathVocab)));
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				splittedLigne = line.toLowerCase().split(":");
				if (splittedLigne[0].toLowerCase().equals(concept.toLowerCase())) {
					splittedVocabTerms = splittedLigne[1].split(",");
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return splittedVocabTerms;
	}

	public  Integer tf(String term, String FilePath) {
		BufferedReader br;
		Integer tf = null;
		String line;
		String txt = null;
		String[] splittedText = null;
		try {
			File f = new File(FilePath);
			if (f.getName().toLowerCase().endsWith(".pdf")) {
				PDDocument docPdf = null;
				docPdf = PDDocument.load(f);
				PDFTextStripper stripper = new PDFTextStripper();
				stripper.setLineSeparator("\n");
				stripper.setStartPage(1);
				// stripper.setEndPage(5);// this mean that it will index the first 5 pages only
				txt = stripper.getText(docPdf);
			} else {
				br = new BufferedReader(new FileReader(f));
				while ((line = br.readLine()) != null) {
					txt = txt + " " + line;
				}
			}
			// System.out.println(txt);
			// System.out.println(splittedText[0]);
			splittedText = txt.toLowerCase().split(term);
			tf = splittedText.length - 1;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tf;
	}

	public  void afficheConcept( ArrayList<Concept> concepts) {
		Iterator<Concept> itr = concepts.iterator();
		while (itr.hasNext()) {
			Concept concept = itr.next();
			System.out.println("Concept :" + concept.conceptName + "  Score:" + concept.totalScore);
		}
	}
	public  void afficheDocList( ArrayList<DocScored> vocabDocList) {
		Iterator<DocScored> itr = vocabDocList.iterator();
		while (itr.hasNext()) {
			DocScored doc = itr.next();
			System.out.println("Oder:"+doc.numOrder+"; Path :" + doc.docPath + "; Concept :" + doc.docConcept +"; Score Concept:" + doc.scoreDocConcept);
		}
	}
	public  void afficheVocabList( ArrayList<VocabTerm> vocabTermList) throws Exception {
		Iterator<VocabTerm> itr = vocabTermList.iterator();
		while (itr.hasNext()) {
			VocabTerm vt = (VocabTerm) itr.next();
			System.out.println(vt.termText + "; tf:" + vt.termFrq + "; totalTf:" + vt.totalTermFrq + "; df:" + vt.docFrq
					+ "; tfIdf score:" + vt.tfIdf + "; Concept:" + vt.concept+"; docID:"+ vt.idDoc );

		}

	}
	/*
	 * public static boolean vocabExist(String vocabTerm, int idDoc) throws
	 * Exception { Document doc = reader.document(idDoc); Terms terms =
	 * getContentsTermVector(idDoc, IndexParser.reader); TermsEnum termsEnum =
	 * terms.iterator(); int n = (int) terms.size(); BytesRef iter =
	 * termsEnum.next(); TermContext[] states = new TermContext[n]; TermStatistics
	 * termStats[] = new TermStatistics[n]; int i = 0; boolean vocabExist = false;
	 * while (iter != null) { if
	 * (iter.utf8ToString().equals(vocabTerm.toLowerCase())) { Term term = new
	 * Term("contents", iter.utf8ToString()); states[i] = TermContext.build(context,
	 * term); termStats[i] = searcher.termStatistics(term, states[i]); String
	 * termText = iter.utf8ToString(); int termFrq = tf(iter.utf8ToString(),
	 * doc.get("path")); int totalTermFrq = (int) termStats[i].totalTermFreq(); int
	 * docFrq = (int) termStats[i].docFreq(); float tfIdf = tfIdf(termFrq, docFrq);
	 * VocabTerm vt = new VocabTerm(termText, termFrq, totalTermFrq, docFrq, tfIdf);
	 * vocabList.add(vt); vocabExist = true; break; } i++; iter = termsEnum.next();
	 * } return vocabExist; }
	 */
	
	public  Terms getContentsTermVector(Integer docId, IndexReader reader) throws IOException {
		contentsTermVector = reader.getTermVector(docId, "contents");
		return contentsTermVector;
	}

	public  Terms getPathTermVector(Integer docId, IndexReader reader) throws IOException {
		contentsTermVector = reader.getTermVector(docId, "contents");
		return contentsTermVector;
	}

	public  Terms getFilenameTermVector(Integer docId, IndexReader reader) throws IOException {
		contentsTermVector = reader.getTermVector(docId, "contents");
		return contentsTermVector;
	}


	public static ArrayList<String> getStopWord(String FileName) {
		ArrayList<String> stop_words = new ArrayList<String>();
		try {
			BufferedReader is = new BufferedReader(new FileReader(FileName));
			String inputLine;
			while ((inputLine = is.readLine()) != null) {
				 stop_words.add(inputLine);
				// System.out.println(inputLine);
			}
		} catch (IOException io) {

		}
		return stop_words;
	}
}
