package isped.sitis.owlApiexamples;

import java.io.File;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

public class OwlApiExample {

	public OwlApiExample() throws OWLOntologyCreationException {
		
	}
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//IRI documentIRI = IRI.create("http://owl.cs.manchester.ac.uk/co-ode-files/ontologies/pizza.owl");
		//OWLOntology pizzaOntology = manager.loadOntologyFromOntologyDocument(documentIRI);
		File fichierOntologie = new File("P:\\helminthiases.owl");
		OWLOntology Onto = manager.loadOntologyFromOntologyDocument(fichierOntologie);
		System.out.println("Loading");
		File OntologieAenregistrer = new File("P:\\jnDossier\\fichierOwl\\snomedct_owl.owl");
		manager.saveOntology(Onto,IRI.create(OntologieAenregistrer.toURI()));
		System.out.println("save");
		OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat();
		RDFXMLOntologyFormat rdfxmlFormat = new RDFXMLOntologyFormat();
		manager.saveOntology(Onto,rdfxmlFormat,IRI.create(OntologieAenregistrer.toURI()));
		System.out.println("Changing format");
		IRI iri = IRI.create("");
		PrefixManager prefixe = new DefaultPrefixManager("");
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLClass classCree1 = factory.getOWLClass(iri);
		OWLClass classCree2 = factory.getOWLClass(":epoux",prefixe);
		System.out.println("creating owl class");
		OWLDeclarationAxiom declarationAxiom1 = factory.getOWLDeclarationAxiom(classCree1);
		OWLDeclarationAxiom declarationAxiom2 = factory.getOWLDeclarationAxiom(classCree2);
		System.out.println("creating owl axiom");
		manager.addAxiom(Onto, declarationAxiom1);
		OWLDatatype integerDatatype = factory.getIntegerOWLDatatype();
		OWLDatatype floatDatatype = factory.getFloatOWLDatatype();
		OWLDatatype doubleDatatype =factory.getDoubleOWLDatatype();
		OWLDatatype booleanDatatype =factory.getBooleanOWLDatatype();
		OWLLiteral literal = factory.getOWLLiteral("51",integerDatatype);
		OWLObjectProperty hasWife = factory.getOWLObjectProperty(":hasWife",prefixe);
		OWLNamedIndividual john = factory.getOWLNamedIndividual(":Jean",prefixe);
		OWLNamedIndividual mary = factory.getOWLNamedIndividual(":Marie",prefixe);
		System.out.println("creating owl literal");
		OWLObjectPropertyAssertionAxiom propertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(hasWife, john, mary);
		manager.addAxiom(Onto, propertyAssertion);
		System.out.println("creating owl axiom between instances");
		OWLObjectProperty hasPart = factory.getOWLObjectProperty(IRI.create(prefixe + "#hasPart"));
		OWLClass noise = factory.getOWLClass(IRI.create(prefixe + "#bruit"));
		OWLClassExpression hasPartSomeNoise = factory.getOWLObjectAllValuesFrom(hasPart, noise);
		OWLObjectExactCardinality hasGenderRestriction = factory.getOWLObjectExactCardinality(1, hasWife);
		OWLClass head = factory.getOWLClass(IRI.create(prefixe + "#Tête"));
		OWLSubClassOfAxiom axiome = factory.getOWLSubClassOfAxiom(head, hasPartSomeNoise);
		
		
	//chargement raisonneur
		ElkReasonerFactory reasonerInfered = new ElkReasonerFactory();
		OWLReasoner reasoner = reasonerInfered.createReasoner(Onto);
		//reasoner.getDisjointClasses(hasGenderRestriction);
		
	}

}
