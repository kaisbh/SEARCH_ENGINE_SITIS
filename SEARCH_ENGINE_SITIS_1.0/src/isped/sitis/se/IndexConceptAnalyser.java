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
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.DFISimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.lucene.search.similarities.*;

public class IndexConceptAnalyser {
	private static final float MaxConceptScore = 0;
	static CharArraySet CharArraySetSW = new CharArraySet(getStopWord("./resources/StopWord/stop-words-english1.txt"),
			true);
	static EnglishAnalyzer analyzerEN = new EnglishAnalyzer(CharArraySetSW);

	public static String indexLocation = "D:\\Projet_Gayo\\Index";
	public static String vocabLocation = "./resources/vocab.txt";
	public static IndexReader reader;
	public static IndexSearcher searcher;
	public static Terms contentsTermVector;
	public static Terms pathTermVector;
	public static Terms filenameTermVector;
	public static IndexReaderContext context;
	public static String[] VocabTerms = null;
	public static ArrayList<VocabTerm> vocabTermList = new ArrayList<VocabTerm>();
	private static ArrayList<Concept> concepts = new ArrayList<Concept>();
	public static ArrayList<DocScored> vocabDocList = new ArrayList<DocScored>();
	public static Concept docConcept;
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
		IndexConceptAnalyser IndexParser = new IndexConceptAnalyser(indexLocation);
		MakeDocList();
		afficheDocList();
		//AfficheVocabList(vocabTermList);
		//System.out.println(getMaxDocConceptScores(1));
		//getDocConceptScores(1);
		//afficheConcept();
		

	}

	
	public static void MakeDocList() throws Exception {
		MakeConcepts(vocabLocation);
		for (int i = 0; i < reader.getDocCount("contents"); i++) {
			MakeDocVocab(vocabLocation,i);
			Document doc = reader.document(i);
			String docPath = doc.get("path");
			int docId = i;
			getDocConceptScores(i);
			System.out.println("#################################################################################################################################################");
			System.out.println(doc.get("path"));
			System.out.println("***********************************");
			System.out.println("Vocabulaire:");
			AfficheVocabList();
			System.out.println("***********************************");
			System.out.println("Concepts:");
			afficheConcept() ;
			System.out.println("#################################################################################################################################################");
			float scoreDocConcept = getMaxDocConceptScores(i);
			String docConcept = IndexConceptAnalyser.docConcept.conceptName;
			DocScored vd = new DocScored(docPath, docId, docConcept, scoreDocConcept);
			vocabDocList.add(vd);
		}
		
	}
	public static void addVocab(String vocabTerm, int idDoc, String concept) throws Exception {
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
				VocabTerm vt = new VocabTerm(termText, termFrq, totalTermFrq, docFrq, tfIdf, concept);
				vocabTermList.add(vt);
			}
			i++;
			iter = termsEnum.next();
		}

	}
	public static void InitVocab() {
		//int i =0;
		//Iterator<VocabTerm> itr1 = vocabTermList.iterator();
		//while (itr1.hasNext()) {
		//	VocabTerm term = itr1.next();
			vocabTermList.removeAll(vocabTermList);
		//	i++;
		//}
	}
	public static void MakeDocVocab(String vocabLocation, int docId) throws Exception {
		InitVocab();
		String docConcept = null;
		ArrayList<Term> vocabList;
		Iterator<Concept> itr2 = concepts.iterator();
		while (itr2.hasNext()) {
			Concept concept = itr2.next();
			// addVocab(concept.conceptName, docId,concept.conceptName);
			String[] VocabTerms = extractVocabTerms(vocabLocation, concept.conceptName);
			for (int j = 0; j < VocabTerms.length; j++) {
				addVocab(VocabTerms[j], docId, concept.conceptName);
			}

		}
		
	}
	public static void MakeConcepts(String vocabLocation) {
		// ArrayList<String> concepts = new ArrayList<>();
		/*
		int i =0;
		Iterator<Concept> itr = concepts.iterator();
		while (itr.hasNext()) {
			Concept concept = itr.next();
			concepts.remove(i);
			i++;
		}*/
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
					concepts.add(concept);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static float getMaxDocConceptScores(int docId) throws Exception {
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

	public static void getDocConceptScores(int docId) throws Exception {
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

	public static void afficheConcept() {
		Iterator<Concept> itr = concepts.iterator();
		while (itr.hasNext()) {
			Concept concept = itr.next();
			System.out.println("Concept :" + concept.conceptName + "  Score:" + concept.totalScore);
		}
	}
	public static void afficheDocList() {
		Iterator<DocScored> itr = vocabDocList.iterator();
		while (itr.hasNext()) {
			DocScored doc = itr.next();
			System.out.println("Path :" + doc.docPath + "; Concept :" + doc.docConcept +"; Score Concept:" + doc.scoreDocConcept);
		}
	}
	

	public static void AfficheVocabList() throws Exception {
		Iterator<VocabTerm> itr = vocabTermList.iterator();
		while (itr.hasNext()) {
			VocabTerm vt = (VocabTerm) itr.next();
			System.out.println(vt.termText + "; tf:" + vt.termFrq + "; totalTf:" + vt.totalTermFrq + "; df:" + vt.docFrq
					+ "; tfIdf score:" + vt.tfIdf + "; Concept:" + vt.concept);

		}

	}

	public static float tfIdf(int tf, int df) throws IOException {
		ClassicSimilarity sim = new ClassicSimilarity();
		int N = reader.getDocCount("contents");
		float TF = sim.tf(tf);
		float IDF = sim.idf(df, N);
		float tfIdf = TF * IDF;
		return tfIdf;
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
	public static String[] extractVocabTerms(String pathVocab, String concept) {
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

	public static Integer tf(String term, String FilePath) {
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

	public static Terms getContentsTermVector(Integer docId, IndexReader reader) throws IOException {
		contentsTermVector = reader.getTermVector(docId, "contents");
		return contentsTermVector;
	}

	public static Terms getPathTermVector(Integer docId, IndexReader reader) throws IOException {
		contentsTermVector = reader.getTermVector(docId, "contents");
		return contentsTermVector;
	}

	public static Terms getFilenameTermVector(Integer docId, IndexReader reader) throws IOException {
		contentsTermVector = reader.getTermVector(docId, "contents");
		return contentsTermVector;
	}

	public IndexConceptAnalyser() {
		super();
		// TODO Auto-generated constructor stub
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
