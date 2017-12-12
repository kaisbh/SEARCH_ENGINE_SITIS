package isped.sitis.se.model;

public class VocabTerm {
public int termFrq =0;
public int docFrq=0;
public int totalTermFrq=0;
public String termText="";
public float weight=0;
public String concept ="";
public int idDoc;
public VocabTerm(String termText,int termFrq, int totalTermFrq,int docFrq,float weight,String concept,int idDoc){
	this.termFrq=termFrq;
	this.docFrq=docFrq;
	this.totalTermFrq=totalTermFrq;
	this.termText=termText;
	this.weight=weight;
	this.concept=concept;
	this.idDoc=idDoc;
}
}
