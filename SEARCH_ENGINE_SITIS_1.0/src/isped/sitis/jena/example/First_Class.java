package isped.sitis.jena.example;

import org.apache.jena.ontology.OntClass;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.omg.CORBA.portable.InputStream;
import java.io.*;

public abstract class First_Class extends OntoBase {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		String inputFileName = ONTO_SCHEMA_FILE;
		java.io.InputStream in = FileManager.get().open(inputFileName);
		model.read(in, null);
		
		ExtendedIterator<OntClass> iter = model.listClasses();
		//model.write(System.out);
		System.out.println("Nombre de classes :"+ model.size());
		while (iter.hasNext()) {
		
			OntClass maClass = iter.next();
			Document document = new Document();
		        
			System.out.println(maClass.getNameSpace()+"=>"+maClass.getLocalName());
			ExtendedIterator<RDFNode> label = maClass.listLabels(null);
			while (label.hasNext()) {
				System.out.println(":"+ label.next().toString());
				document.add(new StringField("id",maClass.getURI(), Field.Store.YES));
				document.add(new StringField("LocalName",maClass.getLocalName(), Field.Store.YES));
				//document.add(new StringField("label",label.next().toString(), Field.Store.YES));
			}
		}
		
	}

}
