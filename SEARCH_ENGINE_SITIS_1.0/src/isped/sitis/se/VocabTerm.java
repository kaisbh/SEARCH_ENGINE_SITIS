package isped.sitis.se;

public class VocabTerm {
int termFrq =0;
int docFrq=0;
int totalTermFrq=0;
String termText="";
float tfIdf=0;
String concept ="";
int idDoc;
VocabTerm(String termText,int termFrq, int totalTermFrq,int docFrq,float tfIdf,String concept,int idDoc){
	this.termFrq=termFrq;
	this.docFrq=docFrq;
	this.totalTermFrq=totalTermFrq;
	this.termText=termText;
	this.tfIdf=tfIdf;
	this.concept=concept;
	this.idDoc=idDoc;
}
}
