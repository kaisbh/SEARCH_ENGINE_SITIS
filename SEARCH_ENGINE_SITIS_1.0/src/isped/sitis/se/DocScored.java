package isped.sitis.se;

public class DocScored {
	String docPath="";
	int docId=0;
	String docConcept="";
	float scoreDocConcept=0;
		

	DocScored(String docPath,int docId, String docConcept,float scoreDocConcept){
			this.docPath=docPath;
			this.docId=docId;
			this.docConcept=docConcept;
			this.scoreDocConcept=scoreDocConcept;
			
		}
}
