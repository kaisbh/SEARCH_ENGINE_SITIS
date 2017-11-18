package isped.sitis.jena.example;

//Imports
///////////////
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
* <p>Base class for cheese-ontology based examples. Declares
* common namespaces and provides some basic utilities.</p>
*/
public abstract class OntoBase extends Base
{
/***********************************/
/* Constants                       */
/***********************************/

public static final String ONTO_SCHEMA = "http://www.w3.org/2002/07/owl";
public static final String ONTO_DATA = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl";

public static final String ONTO_SCHEMA_FILE = ONTOLOGIES_DIR + "nci_small_fma.owl";
//public static final String ONTO_SCHEMA_FILE = ONTOLOGIES_DIR + "pizza.owl";
//public static final String ONTO_DATA_FILE = DATA_DIR + "nci_small_fma.owl";
//public static final String ONTO_DATA_FILE = DATA_DIR + "SNOMED_small_overlapping_nci.owl";
//

//public static final String NCI_DATA_FILE = DATA_DIR + "cheeses-0.1.ttl";

/***********************************/
/* Static variables                */
/***********************************/

@SuppressWarnings( value = "unused" )
private static final Logger log = LoggerFactory.getLogger( OntoBase.class );

/***********************************/
/* Instance variables              */
/***********************************/

/***********************************/
/* Constructors                    */
/***********************************/

/***********************************/
/* External signature methods      */
/***********************************/

/***********************************/
/* Internal implementation methods */
/***********************************/

/***********************************/
/* Inner class definitions         */
/***********************************/

}

