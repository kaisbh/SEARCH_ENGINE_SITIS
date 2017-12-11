package isped.sitis.se.model;

public class DocScored {
	public int numOrder =0;
	public String docPath = "";
	public int docId = 0;
	public String docConcept = "";
	public float scoreDocConcept = 0;
	public String term;
	float scoreDocTerm =0;

	public DocScored(int numOrder,String docPath, int docId, String docConcept, float scoreDocConcept) {
		this.numOrder = numOrder;
		this.docPath = docPath;
		this.docId = docId;
		this.docConcept = docConcept;
		this.scoreDocConcept = scoreDocConcept;

	}
}
