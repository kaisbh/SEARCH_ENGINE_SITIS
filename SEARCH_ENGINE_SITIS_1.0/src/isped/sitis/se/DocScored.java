package isped.sitis.se;

public class DocScored {
	int numOrder =0;
	String docPath = "";
	int docId = 0;
	String docConcept = "";
	float scoreDocConcept = 0;
	String term;
	float scoreDocTerm =0;

	DocScored(int numOrder,String docPath, int docId, String docConcept, float scoreDocConcept) {
		this.numOrder = numOrder;
		this.docPath = docPath;
		this.docId = docId;
		this.docConcept = docConcept;
		this.scoreDocConcept = scoreDocConcept;

	}
}
