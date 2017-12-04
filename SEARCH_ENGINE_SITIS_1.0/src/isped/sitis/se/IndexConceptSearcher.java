package isped.sitis.se;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

public class IndexConceptSearcher extends Parametre {
	static String Query;
	static ArrayList<DocScored> queryDocList = new ArrayList<DocScored>();
	static ArrayList<VocabTerm> queryVocabList = new ArrayList<VocabTerm>();
	static ArrayList<Concept> queryConcepts = new ArrayList<Concept>();
	static IndexConceptAnalyser analyser = new IndexConceptAnalyser(INDEX_DIR);

	public IndexConceptSearcher(String query, String lang) throws Exception {

		analyser.makeDocList();
		// analyser.afficheDocList(IndexConceptAnalyser.vocabDocList);
		makeQueryVocab(QueryParser(query));
		makeQueryConceptScore(queryVocabList);
		// analyser.afficheVocabList(queryVocabList);
		// analyser.afficheConcept(queryConcepts);
	}

	// public static ArrayList<VocabTerm> vocabTermList = new
	// ArrayList<VocabTerm>();
	public static void main(String[] args) throws Exception {

		queryDocList = search("cancer lymphoma patient", "EN");

		// System.out.println(ConceptRslt.conceptName);

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
				position=i;
			}
		i++;
		}
		return position;
	}

	public static ArrayList<DocScored> search(String query, String Lang) throws Exception {
		// ArrayList<DocScored> queryDocList = null;

		IndexConceptSearcher searcher = new IndexConceptSearcher(query, Lang);
		analyser.afficheDocList(IndexConceptAnalyser.vocabDocList);
		analyser.afficheConcept(queryConcepts);
		Concept ConceptQuery = getMostPertinentConcept(queryConcepts);
		Stream<DocScored> FilteredConcepts = IndexConceptAnalyser.vocabDocList.stream()
				.filter(o -> o.docConcept.equals(ConceptQuery.conceptName));
		Iterator<DocScored> itr = FilteredConcepts.iterator();
		//IndexConceptAnalyser.vocabDocList.get(0)
		for (int i = 0; i < FilteredConcepts.count(); i++) {
			Stream<DocScored> FilteredConcepts2 =  FilteredConcepts.skip(i);
			//DocScored docQuery = getMostPertinentDoc(FilteredConcepts2);
			//queryDocList.add(docQuery);
			
		}
		for(int i = 0; i < IndexConceptAnalyser.vocabDocList.size(); i++) {
		//DocScored docQuery = getMostPertinentDoc(getMostPertinentDoc(FilteredConcepts));
		DocScored temp = IndexConceptAnalyser.vocabDocList.get(i);
		//IndexConceptAnalyser.vocabDocList.set(i, IndexConceptAnalyser.vocabDocList());
		//IndexConceptAnalyser.vocabDocList.set(docQuery.);
			
	}
		
		//analyser.afficheDocList(IndexConceptAnalyser.vocabDocList);
		analyser.afficheDocList(queryDocList);
		return queryDocList;
	}

	public static String[] QueryParser(String Query) {
		String[] QueryTerms;
		QueryTerms = Query.toLowerCase().split(" ");
		return QueryTerms;

	}

	public static void makeQueryConceptScore(ArrayList<VocabTerm> queryVocabList) {
		Iterator<VocabTerm> itr = queryVocabList.iterator();
		while (itr.hasNext()) {
			VocabTerm term = (VocabTerm) itr.next();
			if (queryConcepts.stream().anyMatch(o -> o.conceptName.equals(term.concept))) {
				Stream<Concept> FilteredConcepts = queryConcepts.stream()
						.filter(o -> o.conceptName.equals(term.concept));
				Iterator<Concept> itr2 = FilteredConcepts.iterator();
				while (itr2.hasNext()) {
					Concept concept = itr2.next();
					concept.totalScore = concept.totalScore + term.tfIdf;
				}

			}

		}
	}

	public static void makeQueryVocab(String[] Query) throws Exception {
		queryConcepts = analyser.getConcepts(VOCAB_FILE);
		analyser.initVocab();
		analyser.afficheConcept(queryConcepts);
		if (Query.length != 0) {
			Concept concept = null;
			for (int j = 0; j < Query.length; j++) {
				Iterator<Concept> itr1 = queryConcepts.iterator();
				while (itr1.hasNext()) {
					concept = itr1.next();
					String[] VocabTerms = analyser.extractVocabTerms(VOCAB_FILE, concept.conceptName);
					for (int k = 0; k < IndexConceptAnalyser.reader.getDocCount("contents"); k++) {
						for (int i = 0; i < VocabTerms.length; i++) {
							if (Query[j].equals(VocabTerms[i])) {
								analyser.addVocab(Query[j], k, concept.conceptName, queryVocabList);
							}
						}
					}
				}

			}
		}
	}
}
/*
 * 
 * for (int j = 0; j < Query.length; j++) { VocabTerm qv = new
 * VocabTerm(Query[j], 0, 0, 0, 0, "", 0); QueryVocabList.add(qv); }
 * Analyser.AfficheVocabList(QueryVocabList);
 * 
 * 
 * float scoreDocConcept= 0; String docPath= ""; String Concept = ""; for (int i
 * = 0; i < Analyser.reader.getDocCount("contents"); i++) {
 * 
 * for (int j = 0; j < Query.length; j++) { int idDoc = i; // if
 * (Query[j].equals(term.concept)) { Document doc = Analyser.reader.document(i);
 * if (Analyser.vocabTermList.stream().anyMatch(o -> o.idDoc == idDoc)) {
 * scoreDocConcept = 0; Stream<VocabTerm> FilteredDocList =
 * Analyser.vocabTermList.stream().filter(o -> o.idDoc == idDoc);
 * Iterator<VocabTerm> itr2 = FilteredDocList.iterator(); while (itr2.hasNext())
 * { VocabTerm term = itr2.next(); docPath = doc.get("path"); Concept =
 * term.concept; scoreDocConcept = scoreDocConcept + term.tfIdf; } }
 * 
 * } DocScored qd = new DocScored(docPath, i, "", scoreDocConcept);
 * QueryDocList.add(qd); } Analyser.afficheDocList(QueryDocList); // DocScored
 * qd= new DocScored(docPath, docId, Concept, scoreDocConcept);
 * 
 * // QueryTermList.add(qd);
 */