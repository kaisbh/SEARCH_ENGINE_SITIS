package isped.sitis.owlApiexamples;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asUnorderedSet;

import javax.annotation.Nonnull;

import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;



public class Ontologie {

	
	public static void main(String [ ] args) throws OWLOntologyCreationException{

		try {
		 OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		 File fichierOnto = new File("./resources/ontologies/SEARCH_ENGINE.owl");
		 OWLOntology Onto = manager.loadOntologyFromOntologyDocument(fichierOnto);
		 //IRI iri = IRI.create("http://www.pizza.com/ontologies.pizza.owl");
		  
		 
		 File OntologieAEnregistrer = new File("./resources/ontologies/CREATED_SE.owl");
			try {
				manager.saveOntology(Onto,IRI.create(OntologieAEnregistrer.toURI()));
			} catch (OWLOntologyStorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
         
		 OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		 OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(Onto);
		 reasoner.precomputeInferences();
		 boolean consistent = reasoner.isConsistent();
		 System.out.println("Consistent: " + consistent);

		 ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
	            // Create the DLQueryPrinter helper class. This will manage the
	            // parsing of input and printing of results
		 DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(new DLQueryEngine(reasoner, shortFormProvider),shortFormProvider);
	            // Enter the query loop. A user is expected to enter class
	            // expression on the command line.
	            doQueryLoop(dlQueryPrinter);
	        } catch (OWLOntologyCreationException e) {
	            System.out.println("Could not load ontology: " + e.getMessage());
	        } catch (IOException ioEx) {
	            System.out.println(ioEx.getMessage());
	        }
	}
	

	 private static void doQueryLoop(DLQueryPrinter dlQueryPrinter) throws IOException {
	        while (true) {
	            // Prompt the user to enter a class expression
	            System.out.println("Please type a class expression in Manchester Syntax and press Enter (or press x to exit):");
	            System.out.println("");
	            String classExpression = readInput();
	            // Check for exit condition
	            if (classExpression.equalsIgnoreCase("x")) {
	                break;
	            }
	            dlQueryPrinter.askQuery(classExpression.trim());
	            System.out.println();
	            System.out.println();
	        }
	    }

	 
	    private static String readInput() throws IOException {
	        InputStream is = System.in;
	        InputStreamReader reader;
	        reader = new InputStreamReader(is, StandardCharsets.UTF_8);
	        BufferedReader br = new BufferedReader(reader);
	        return br.readLine();
	    }

	
	}

	class DLQueryEngine {

	    private final OWLReasoner reasoner;
	    private final DLQueryParser parser;


	    DLQueryEngine(OWLReasoner reasoner, ShortFormProvider shortFormProvider) {
	        this.reasoner = reasoner;
	        OWLOntology rootOntology = reasoner.getRootOntology();
	        parser = new DLQueryParser(rootOntology, shortFormProvider);
	    }

	    /**
	     * Gets the superclasses of a class expression parsed from a string.
	     *
	     * @param classExpressionString The string from which the class expression will be parsed.
	     * @param direct Specifies whether direct superclasses should be returned or not.
	     * @return The superclasses of the specified class expression If there was a problem parsing the
	     * class expression.
	     */
	    public Set<OWLClass> getSuperClasses(String classExpressionString, boolean direct) {
	        if (classExpressionString.trim().isEmpty()) {
	            return Collections.emptySet();
	        }
	        OWLClassExpression classExpression = parser.parseClassExpression(classExpressionString);
	        NodeSet<OWLClass> superClasses = reasoner.getSuperClasses(classExpression, direct);
	        return asUnorderedSet(superClasses.entities());
	    }

	    /**
	     * Gets the equivalent classes of a class expression parsed from a string.
	     *
	     * @param classExpressionString The string from which the class expression will be parsed.
	     * @return The equivalent classes of the specified class expression If there was a problem
	     * parsing the class expression.
	     */
	    public Set<OWLClass> getEquivalentClasses(String classExpressionString) {
	        if (classExpressionString.trim().isEmpty()) {
	            return Collections.emptySet();
	        }
	        OWLClassExpression classExpression = parser.parseClassExpression(classExpressionString);
	        Node<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(classExpression);
	        return asUnorderedSet(equivalentClasses.entities().filter(c -> !c.equals(classExpression)));
	    }

	    /**
	     * Gets the subclasses of a class expression parsed from a string.
	     *
	     * @param classExpressionString The string from which the class expression will be parsed.
	     * @param direct Specifies whether direct subclasses should be returned or not.
	     * @return The subclasses of the specified class expression If there was a problem parsing the
	     * class expression.
	     */
	    public Set<OWLClass> getSubClasses(String classExpressionString, boolean direct) {
	        if (classExpressionString.trim().isEmpty()) {
	            return Collections.emptySet();
	        }
	        OWLClassExpression classExpression = parser.parseClassExpression(classExpressionString);
	        NodeSet<OWLClass> subClasses = reasoner.getSubClasses(classExpression, direct);
	        return asUnorderedSet(subClasses.entities());
	    }

	    /**
	     * Gets the instances of a class expression parsed from a string.
	     *
	     * @param classExpressionString The string from which the class expression will be parsed.
	     * @param direct Specifies whether direct instances should be returned or not.
	     * @return The instances of the specified class expression If there was a problem parsing the
	     * class expression.
	     */
	    public Set<OWLNamedIndividual> getInstances(String classExpressionString, boolean direct) {
	        if (classExpressionString.trim().isEmpty()) {
	            return Collections.emptySet();
	        }
	        OWLClassExpression classExpression = parser.parseClassExpression(classExpressionString);
	        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression, direct);
	        return asUnorderedSet(individuals.entities());
	    }
	}

	class DLQueryParser {

	    private final OWLOntology rootOntology;
	    private final BidirectionalShortFormProvider bidiShortFormProvider;

	    /**
	     * Constructs a DLQueryParser using the specified ontology and short form
	     * provider to map entity IRIs to short names.
	     *
	     * @param rootOntology The root ontology. This essentially provides the domain vocabulary for
	     * the query.
	     * @param shortFormProvider A short form provider to be used for mapping back and forth between
	     * entities and their short names (renderings).
	     */
	    DLQueryParser(OWLOntology rootOntology, ShortFormProvider shortFormProvider) {
	        this.rootOntology = rootOntology;
	        OWLOntologyManager manager = rootOntology.getOWLOntologyManager();
	        List<OWLOntology> importsClosure = asList(rootOntology.importsClosure());
	        // Create a bidirectional short form provider to do the actual mapping.
	        // It will generate names using the input
	        // short form provider.
	        bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager, importsClosure,
	            shortFormProvider);
	    }

	    /**
	     * Parses a class expression string to obtain a class expression.
	     *
	     * @param classExpressionString The class expression string
	     * @return The corresponding class expression if the class expression string is malformed or
	     * contains unknown entity names.
	     */
	    public OWLClassExpression parseClassExpression(String classExpressionString) {
	        // Set up the real parser
	        ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
	        parser.setStringToParse(classExpressionString);
	        parser.setDefaultOntology(rootOntology);
	        // Specify an entity checker that wil be used to check a class
	        // expression contains the correct names.
	        OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
	        parser.setOWLEntityChecker(entityChecker);
	        // Do the actual parsing
	        return parser.parseClassExpression();
	    }
	}

	class DLQueryPrinter {

	    private final DLQueryEngine dlQueryEngine;
	    private final ShortFormProvider shortFormProvider;

	    /**
	     * @param engine the engine
	     * @param shortFormProvider the short form provider
	     */
	    DLQueryPrinter(DLQueryEngine engine, ShortFormProvider shortFormProvider) {
	        this.shortFormProvider = shortFormProvider;
	        dlQueryEngine = engine;
	    }

	    /**
	     * @param classExpression the class expression to use for interrogation
	     */
	    public void askQuery(String classExpression) {
	        if (classExpression.isEmpty()) {
	            System.out.println("No class expression specified");
	        } else {
	            StringBuilder sb = new StringBuilder();
	            sb.append(
	                "\n--------------------------------------------------------------------------------\n");
	            sb.append("QUERY:   ");
	            sb.append(classExpression);
	            sb.append('\n');
	            sb.append(
	                "--------------------------------------------------------------------------------\n\n");
	            // Ask for the subclasses, superclasses etc. of the specified
	            // class expression. Print out the results.
	            Set<OWLClass> superClasses = dlQueryEngine.getSuperClasses(classExpression, true);
	            printEntities("SuperClasses", superClasses, sb);
	            Set<OWLClass> equivalentClasses = dlQueryEngine.getEquivalentClasses(classExpression);
	            printEntities("EquivalentClasses", equivalentClasses, sb);
	            Set<OWLClass> subClasses = dlQueryEngine.getSubClasses(classExpression, true);
	            printEntities("SubClasses", subClasses, sb);
	            Set<OWLNamedIndividual> individuals = dlQueryEngine.getInstances(classExpression, true);
	            printEntities("Instances", individuals, sb);
	            
	            System.out.println(sb);
	        }
	    }

	    private void printEntities(String name, Set<? extends OWLEntity> entities, StringBuilder sb) {
	        sb.append(name);
	        int length = 50 - name.length();
	        for (int i = 0; i < length; i++) {
	            sb.append('.');
	        }
	        sb.append("\n\n");
	        if (!entities.isEmpty()) {
	            for (OWLEntity entity : entities) {
	                sb.append('\t');
	                sb.append(shortFormProvider.getShortForm(entity));
	                sb.append('\n');
	            }
	        } else {
	            sb.append("\t[NONE]\n");
	        }
	        sb.append('\n');
	    }
	}
