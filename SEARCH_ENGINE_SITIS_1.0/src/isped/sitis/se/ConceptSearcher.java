package isped.sitis.se;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import isped.sitis.se.model.Concept;
import isped.sitis.se.model.DocScored;
import isped.sitis.se.model.VocabTerm;

public class ConceptSearcher extends Parametre {
	private static final int MaxPosition = 0;
	static String Query;
	// static ArrayList<DocScored> queryDocList = new ArrayList<DocScored>();

	static IndexReader reader;
	static IndexSearcher searcher;
	static IndexReaderContext context;

	public ConceptSearcher(String query) throws Exception {
		reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_DIR)));
		searcher = new IndexSearcher(reader);
		context = searcher.getTopReaderContext();

	}

	// public static ArrayList<VocabTerm> vocabTermList = new
	// ArrayList<VocabTerm>();
	public static void main(String[] args) throws Exception {

		ArrayList<DocScored> queryDocList = search("aortic");
		


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

	public static Concept getMostPertinentConcept(ArrayList<Concept> queryConcepts) throws Exception {
		// getDocConceptScores(docId);
		Iterator<Concept> itr = queryConcepts.iterator();
		Concept ConceptRslt = null;
		float MaxConceptScore = 0;
		while (itr.hasNext()) {
			Concept concept = itr.next();
			if (concept.totalScore > MaxConceptScore) {
				ConceptRslt = concept;
				MaxConceptScore = concept.totalScore;
			}
		}
		return ConceptRslt;
	}

	public static int getMostPertinentDoc(Stream<DocScored> scoredDocList) throws Exception {
		// getDocConceptScores(docId);
		Iterator<DocScored> itr = scoredDocList.iterator();
		DocScored DocRslt = null;
		float MaxDocScore = 0;
		int position = 0;
		int i = 0;
		while (itr.hasNext()) {
			DocScored doc = itr.next();
			if (doc.scoreDocConcept > MaxDocScore) {
				DocRslt = doc;
				MaxDocScore = doc.scoreDocConcept;
				position = i;
			}
			i++;
		}
		return position;
	}

	public static ArrayList<DocScored> search(String query) throws Exception {
		
		ArrayList<DocScored> queryDocList = new ArrayList<DocScored>();
	
		ArrayList<VocabTerm> queryVocabList = new ArrayList<VocabTerm>();
		ArrayList<Concept> queryConcepts = new ArrayList<Concept>();
		ArrayList<DocScored> vocabDocList = new ArrayList<DocScored>();
		new ConceptSearcher(query);

		System.out.println(
				"----------------------------Scored documents wich contain term's query---------------------------------");
		queryVocabList = makeQueryVocab(QueryParser(query), ConceptSearcher.reader, ConceptSearcher.searcher);
		ConceptAnalyser.afficheVocabList(queryVocabList);
		System.out.println("----------------------------Calculating concepts scores---------------------------------");
		queryConcepts = makeQueryConceptScore(queryVocabList, getConcepts(VOCAB_FILE));
		ConceptAnalyser.afficheConcept(queryConcepts);
		System.out.println("----------------------------Most pertinent concept in the query-------------------------");
		Concept ConceptQuery = getMostPertinentConcept(queryConcepts);
		System.out.println(ConceptQuery.conceptName);
		System.out.println("----------------------------Most pertinent documents-------------------------");
		vocabDocList = loadIndexConcept(CONCEPT_INDEX_FILE);
		//IndexConceptAnalyser.afficheDocList(vocabDocList);
		Stream<DocScored> FilteredConcepts = vocabDocList.stream()
				.filter(o -> o.docConcept.equals(ConceptQuery.conceptName));
		Iterator<DocScored> itr = FilteredConcepts.iterator();
		while (itr.hasNext()) {
			DocScored doc = itr.next();
			queryDocList.add(doc);
		}
		ConceptAnalyser.afficheDocList(queryDocList);
		System.out.println("----------------------------Make some order-------------------------");
		for (int i = 0; i < queryDocList.size(); i++) {
			DocScored docQueryMax = queryDocList.get(i);
			docQueryMax.numOrder = i+1;
			int MaxPosition = i;
			for (int j = i + 1; j < queryDocList.size(); j++) {
				DocScored docQuery = queryDocList.get(j);
				if (docQuery.scoreDocConcept > docQueryMax.scoreDocConcept) {
					docQueryMax = docQuery;
					docQueryMax.numOrder = i+1;
					MaxPosition = j;
				}
			}
			if (i != MaxPosition) {
				DocScored temp = queryDocList.get(i);
				queryDocList.set(i, docQueryMax);
				queryDocList.set(MaxPosition, temp);
			}

		}

		ConceptAnalyser.afficheDocList(queryDocList);
		return queryDocList;
	}

	public static String[] QueryParser(String Query) {
		String[] QueryTerms;
		QueryTerms = Query.toLowerCase().split(" ");
		return QueryTerms;

	}

	public static ArrayList<Concept> makeQueryConceptScore(ArrayList<VocabTerm> queryVocabList,
			ArrayList<Concept> queryConcepts) {

		Iterator<VocabTerm> itr = queryVocabList.iterator();
		while (itr.hasNext()) {
			VocabTerm term = (VocabTerm) itr.next();
			if (queryConcepts.stream().anyMatch(o -> o.conceptName.equals(term.concept))) {
				Stream<Concept> FilteredConcepts = queryConcepts.stream()
						.filter(o -> o.conceptName.equals(term.concept));
				Iterator<Concept> itr2 = FilteredConcepts.iterator();
				while (itr2.hasNext()) {
					Concept concept = itr2.next();
					concept.totalScore = concept.totalScore + term.weight;
				}

			}

		}
		return queryConcepts;
	}

	public static ArrayList<VocabTerm> addVocab(String vocabTerm, int idDoc, String concept,
			ArrayList<VocabTerm> vocabTermList, IndexReader reader, IndexSearcher searcher) throws Exception {

		//Document doc = reader.document(idDoc);
		Terms terms = reader.getTermVector(idDoc, "contents");
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
				// int termFrq = tf(iter.utf8ToString(), doc.get("path"));
				int termFrq = tf(iter.utf8ToString(), idDoc, "contents", reader);
				int totalTermFrq = (int) termStats[i].totalTermFreq();
				int docFrq = (int) termStats[i].docFreq();
				float tfIdf = tfIdf(termFrq, docFrq, reader);
				VocabTerm vt = new VocabTerm(termText, termFrq, totalTermFrq, docFrq, tfIdf, concept, idDoc);

				vocabTermList.add(vt);
			}
			i++;
			iter = termsEnum.next();
		}
		return vocabTermList;
	}

	public static float tfIdf(int tf, int df, IndexReader reader) throws IOException {
		ClassicSimilarity sim = new ClassicSimilarity();
		int N = reader.getDocCount("contents");
		float TF = sim.tf(tf);
		float IDF = sim.idf(df, N);
		float tfIdf = TF * IDF;
		return tfIdf;
	}

	public static Integer tf(String term, int docID, String Field, IndexReader reader) throws IOException {
		Integer tf = null;
		TermsEnum termEnum = MultiFields.getTerms(reader, Field).iterator();
		while (termEnum.next() != null) {
			if (termEnum.term().utf8ToString().equals(term)) {
				PostingsEnum docEnum = MultiFields.getTermDocsEnum(reader, "contents", termEnum.term());
				//int doc = docEnum.NO_MORE_DOCS;
				//int count = 0;
				while (docEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
					if (docEnum.docID() == docID) {
						tf = docEnum.freq();
						// System.out.println(docEnum.docID() + " :" +
						// reader.document(docEnum.docID()).get("path") + ": "+
						// termEnum.term().utf8ToString() + ": " + docEnum.freq());
					}
				}
			}
		}

		return tf;
	}

	public static ArrayList<Concept> getConcepts(String vocabLocation) {
		ArrayList<Concept> concepts2 = new ArrayList<Concept>();
		BufferedReader br;
		String line;
		String[] splittedLigne = null;
		try {
			br = new BufferedReader(new FileReader(new File(vocabLocation)));
			//line = br.readLine();
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

	public static void initVocab() {
		//queryVocabList.removeAll(queryVocabList);
	}

	public static void initResultDoc() {
		// queryDocList.removeAll(queryDocList);
	}

	public static ArrayList<VocabTerm> makeQueryVocab0(String[] Query, IndexReader reader, IndexSearcher searcher) throws Exception {
		ArrayList<VocabTerm> queryVocabList = new ArrayList<VocabTerm>();
		// initVocab();
		ArrayList<Concept> queryConcepts = getConcepts(VOCAB_FILE);
		initVocab();
		// IndexConceptAnalyser.afficheConcept(queryConcepts);
		if (Query.length != 0) {
			Concept concept = null;
			for (int j = 0; j < Query.length; j++) {
				Iterator<Concept> itr1 = queryConcepts.iterator();
				while (itr1.hasNext()) {
					concept = itr1.next();
					ArrayList<VocabTerm> VocabTerms = extractVocabTerms(VOCAB_FILE, concept.conceptName);
					for (int k = 0; k < reader.getDocCount("contents"); k++) {
						//for (int i = 0; i < VocabTerms.size(); i++) {
							Iterator<VocabTerm> itr2 = VocabTerms.iterator();
							while (itr2.hasNext()) {
								VocabTerm VocabTerm = itr2.next();
							if (Query[j].equals(VocabTerm.termText)) {
								queryVocabList = addVocab(Query[j], k, concept.conceptName, queryVocabList, reader, searcher);
							}
						}
					}
				}

			}
		}
		return queryVocabList;
	}
	public static ArrayList<VocabTerm> makeQueryVocab(String[] Query, IndexReader reader, IndexSearcher searcher) throws Exception {
		ArrayList<VocabTerm> queryVocabList = new ArrayList<VocabTerm>();
		// initVocab();
		ArrayList<Concept> queryConcepts = getConcepts(VOCAB_FILE);
		initVocab();
		// IndexConceptAnalyser.afficheConcept(queryConcepts);
		if (Query.length != 0) {
			Concept concept = null;
			for (int j = 0; j < Query.length; j++) {
				ArrayList<VocabTerm> result = VocabSearcher.fuzzySearch(Query[j]);
				String fuzzyqueryTerm = VocabSearcher.getMostPertinentVocab(result);
				Iterator<Concept> itr1 = queryConcepts.iterator();
				String queryTerm = Query[j];
				if (fuzzyqueryTerm!="") {
					queryTerm=fuzzyqueryTerm;
				}
				while (itr1.hasNext()) {
					concept = itr1.next();
					ArrayList<VocabTerm> VocabTerms = extractVocabTerms(VOCAB_FILE, concept.conceptName);
					for (int k = 0; k < reader.getDocCount("contents"); k++) {
						//for (int i = 0; i < VocabTerms.size(); i++) {
							Iterator<VocabTerm> itr2 = VocabTerms.iterator();
							while (itr2.hasNext()) {
								VocabTerm VocabTerm = itr2.next();
								
							if (queryTerm.equals(VocabTerm.termText)) {
								queryVocabList = addVocab(queryTerm, k, concept.conceptName, queryVocabList, reader, searcher);
							}
						}
					}
				}

			}
		}
		return queryVocabList;
	}
	public static ArrayList<VocabTerm> extractVocabTerms(String pathVocab, String concept) throws IOException {
		ArrayList<VocabTerm> list = new ArrayList<VocabTerm>() ;
		RAMDirectory ramDir = new RAMDirectory();
		Analyzer analyzer = new StandardAnalyzer();
		VocabSearcher.writeIndex(ramDir, analyzer, VOCAB_FILE);

		IndexReader reader = DirectoryReader.open(ramDir);
		//System.out.println(reader.getDocCount("content"));
		for (int i = 0; i < reader.getDocCount("content"); i++) {
			Document doc = reader.document(i);
			//System.out.println("content: " + doc.getField("content") + "; " + doc.get("content") + "; concept:"+ doc.get("concept"));
			if (doc.get("concept").equals(concept)){
				VocabTerm vt = new VocabTerm(doc.get("content"), 0, 0, 0, 0, doc.get("concept"), 0);
				list.add(vt);
			}
			
		}
		reader.close();

		return list;
	}
	public static String[] extractVocabTerms0(String pathVocab, String concept) {
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
	
}