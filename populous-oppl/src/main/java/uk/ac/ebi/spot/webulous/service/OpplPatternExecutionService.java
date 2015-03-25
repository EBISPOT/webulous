package uk.ac.ebi.spot.webulous.service;

import org.apache.commons.lang3.StringUtils;
import org.coode.oppl.Variable;
import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
import org.coode.oppl.variabletypes.*;
import org.coode.parsers.BidirectionalShortFormProviderAdapter;
import org.coode.parsers.ErrorListener;
import org.coode.parsers.common.ErrorCollector;
import org.coode.patterns.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.webulous.entity.CustomOWLEntityFactory;
import uk.ac.ebi.spot.webulous.exception.OWLEntityCreationException;
import uk.ac.ebi.spot.webulous.model.*;

import java.text.SimpleDateFormat;
import java.util.*;

/* THIS IS JUPP'S MAGIC CLASS*/

/*
 * The PopulousPatternExecutorService takes a data collection, eg a spreadsheet, a PopulousModel, with OPPL patterns and variable bindings, and a entity creation strategy
 * and turns the data into ontology axioms based on the specified patterns.
  *
 */


public class OpplPatternExecutionService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String[][] dataCollection;

    private PopulousTemplate populousTemplate;
    private EntityCreation entityCreation;
    private OWLOntologyManager ontologyManager;

    private Set<OWLOntology> ontologies;
    private OWLOntology activeOntology;

    private ParserFactory pf;
    private OPPLPatternParser parser;

    private HashMap<Integer, Map<String, IRI>> shortFromMapper;
    private BidirectionalShortFormProviderAdapter shortFormProvider;

    private List<OWLAnnotationProperty> props;

    private CustomOWLEntityFactory cf;

    private ErrorCollector errorCollector;

    public OpplPatternExecutionService(String[][] data, PopulousTemplate populousTemplate, EntityCreation entityCreation, ErrorListener errorListener) throws OWLOntologyCreationException {
        logger.debug("Starting Pattern Executor");
        this.dataCollection = data;
        this.populousTemplate = populousTemplate;



        this.ontologyManager = OWLManager.createOWLOntologyManager();

        logger.debug("creating new ontology " + populousTemplate.getActiveOntology());
        this.activeOntology = ontologyManager.createOntology(IRI.create(populousTemplate.getActiveOntology()));

        //set up an OWLEntityFactory
        this.cf = new CustomOWLEntityFactory(ontologyManager, activeOntology, entityCreation);

        //set up an OPPL ParserFactory
//        this.errorCollector = new ErrorCollector();
        this.pf = new ParserFactory(activeOntology, ontologyManager);
        this.parser = pf.build(errorListener);

// set up short form provider
        IRI iri = OWLRDFVocabulary.RDFS_LABEL.getIRI();
        OWLAnnotationProperty prop = ontologyManager.getOWLDataFactory().getOWLAnnotationProperty(iri);
        props = new ArrayList<OWLAnnotationProperty>();
        props.add(prop);
        ShortFormProvider provider = new AnnotationValueShortFormProvider(props, new HashMap<OWLAnnotationProperty, List<String>>(), ontologyManager);
        shortFormProvider = new BidirectionalShortFormProviderAdapter(ontologyManager, ontologyManager.getOntologies(), provider);
        shortFromMapper = createShortFormMapper(populousTemplate.getDataRestrictions());


    }

    public void executeOPPLPatterns() {

        QuickFailRuntimeExceptionHandler handler = new QuickFailRuntimeExceptionHandler();

        for (PopulousPattern pattern : populousTemplate.getPatterns()) {
            System.err.println("Got pattern: " + pattern.getPatternName() + "\n" + pattern.getPatternValue());

            //pass the OPPL pattern string to the parser for processing
            PatternModel patternModel = parser.parse(pattern.getPatternValue());

            List<OWLAxiomChange> changes = new ArrayList<OWLAxiomChange>();

            // get the pattern variables
            if (!patternModel.getInputVariables().isEmpty()) {

                //create a map of the input variable names, eg "?disease" and the actual variable
                Map<String, Variable> opplVariableMap = createOPPLVariableMap(patternModel);

                // for each column, create a new short form mapper for the values that are allowed in that column
                // shortFromMapper = createShortFormMapper();

                logger.debug("Processing model");

                int done = 0;

//process each row in the DataCollection, one by one
                for (int x =0 ; x < dataCollection.length; x ++) {
//                for (DataObject row : dataCollection.getDataObjects()) {
                    logger.debug("Reading row: " + done+1);

//create an instantiated pattern model based on the data in the row
                    InstantiatedPatternModel ipm =  processDataObject(dataCollection[x], opplVariableMap, handler, patternModel);

//pass the instantiated pattern model to a patternExecutor and add the changes to the list of all changes for this model
                    NonClassPatternExecutor patternExecutor = new NonClassPatternExecutor(ipm, activeOntology, ontologyManager, IRI.create("http://e-lico.eu/populous#OPPL_pattern"), handler);
                    changes.addAll(patternExecutor.visit(patternModel));
                    done++;
                }

            }
//if there are no input variables in the OPPL pattern, create an instantiated pattern model without data
            else {
                InstantiatedPatternModel ipm = pf.getPatternFactory().createInstantiatedPatternModel(patternModel, handler);

                NonClassPatternExecutor patternExecutor = new NonClassPatternExecutor(ipm, activeOntology, ontologyManager, IRI.create("http://e-lico.eu/populous#OPPL_pattern"), handler);
                changes.addAll(patternExecutor.visit(ipm.getPatternModel().getOpplStatement()));


            }
//print a list of all the changes, then apply them to the ontology
            for (OWLAxiomChange change : changes) {
                System.err.println(change.toString());
            }
            ontologyManager.applyChanges(changes);
        }

    }

    public Map<String, Variable> createOPPLVariableMap(PatternModel patternModel){
        Map<String, Variable> opplVariableMap = new HashMap<String, Variable>();
        for (Variable v : patternModel.getInputVariables()) {
            System.err.println("Loading input variables:" + v.getName());
            opplVariableMap.put(v.getName(), v);
        }
        return opplVariableMap;
    }

    public HashMap<Integer, Map<String, IRI>> createShortFormMapper(List<PopulousDataRestriction> dataRestrictions){
        HashMap<Integer, Map<String, IRI>> sfMapper = new HashMap<Integer, Map<String, IRI>>();
        // for each column, create a new short form mapper for the values that are allowed in that column
        for(PopulousDataRestriction restriction : dataRestrictions){
            Map<String, IRI> labelToUriMap = new HashMap<String, IRI>();
            for (int x = 0; x <restriction.getValues().length; x++) {
                String uri = restriction.getValues()[x][0];
                String label = restriction.getValues()[x][1];
                if (!StringUtils.isBlank(label)) {
                    label = label.trim();
                    label = label.toLowerCase();
                    labelToUriMap.put(label, IRI.create(uri));
                }
            }

            sfMapper.put(restriction.getColumnIndex(), labelToUriMap);
        }
        return sfMapper;
    }


    public InstantiatedPatternModel processDataObject(String [] row, Map<String, Variable> opplVariableMap, QuickFailRuntimeExceptionHandler handler, PatternModel patternModel){

        InstantiatedPatternModel ipm = pf.getPatternFactory().createInstantiatedPatternModel(patternModel, handler);

        for (PopulousDataRestriction populousDataRestriction : populousTemplate.getDataRestrictions()) {

            int columnIndex = populousDataRestriction.getColumnIndex();

            // see if the row has a value
            if (columnIndex < row.length) {
                String cellValue = row[columnIndex];
                if (!StringUtils.isBlank(cellValue)) {
                    String variable = populousDataRestriction.getVariableName();

                    //check that the variable matches an OPPL pattern input variable
                    if (opplVariableMap.keySet().contains(variable)) {
                        Variable v = opplVariableMap.get(variable);
                        VariableType type = v.getType();

                        //determine the type of the input variable: OWLClass, OWLIndividual or constant, then instantiate as appropriate
                        if (type.accept(variableVisitor).equals(5)) {
                            logger.trace("instantiating variable:" + opplVariableMap.get(variable).getName() + " to " + cellValue);
                            String [] values = cellValue.split("\\s*\\||\\s*");
                            for (String s : values) {
                                s = s.trim();
                                ipm.instantiate(v, ontologyManager.getOWLDataFactory().getOWLLiteral(s));
                            }
                        }
                        else if (type.accept(variableVisitor).equals(1)) {
                            for (OWLEntity entity : createOWLEntitiesFromValue(cellValue, 1, populousDataRestriction)) {
                                logger.debug("instantiating variable as class:" + opplVariableMap.get(variable).getName() + " to " + entity.getIRI());
                                ipm.instantiate(opplVariableMap.get(variable), entity);
                            }
                        }
                        else if (type.accept(variableVisitor).equals(4)) {
                            for (OWLEntity entity : createOWLEntitiesFromValue(cellValue, 4, populousDataRestriction)) {
                                logger.debug("instantiating variable as class:" + opplVariableMap.get(variable).getName() + " to " + entity.getIRI());
                                ipm.instantiate(opplVariableMap.get(variable), entity);
                            }
                        }
                    }
                }
                else if (populousDataRestriction.isRequired()) {
                    // todo throw an exception here
                }

            }
        }
        return ipm;
    }

    private Set<OWLEntity> createOWLEntitiesFromValue(String value, int type, PopulousDataRestriction populousDataRestriction) {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();

        if (!StringUtils.isBlank(value)) {
            String[] values = value.split("\\s*\\||\\s*");
            for (String s : values) {
                s = s.trim();
                logger.debug("Looking up:" + s);
                entities.addAll(createOWLEntitiesFromValue(s, type, populousDataRestriction));


            }
        }
        return entities;
    }

    //save the ontology to the designated location
    public void saveOntology(IRI file) throws OWLOntologyStorageException {
        ontologyManager.saveOntology(activeOntology, file);
    }

    //save the ontology based on the output location specified in the properties file
    public void saveOntology() throws OWLOntologyStorageException {

//        String location = populousTemplate.getSourceOntologyOutputLocation();


        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;

//        String outputFileName = location.concat(dateFormat.format(date)).concat(".owl");

//        ontologyManager.saveOntology(activeOntology, IRI.create(outputFileName));
    }


    //acquire all the OWLEntities associated with this data field
//    private Set<OWLEntity> getOWLEntities(String value, Integer type) {
//        Set<OWLEntity> entities = new HashSet<OWLEntity>();
//
//        if (!StringUtils.isBlank(value)) {
//            String [] values = value.split("\\s*\\||\\s*");
//            for (String s : values) {
//                s = s.trim();
//                logger.debug("Looking up:" + s);
//                entities.addAll(getEntityShortForms(s, type, field.getIndex(), field.getRestrictionIndex()));
//            }
//        }
//
//        return entities;
//    }

    //get the OWLEntities for data value shortForm, looking first in the list of valid ontology terms for this column, then in all ontologies, then if not found, create a new OWLEntity
    private OWLEntity getEntityForValue(String shortForm, Integer type, PopulousDataRestriction populousDataRestriction) {
        OWLEntity entity = null;

        String cleaned = shortForm.trim();
        cleaned = shortForm.toLowerCase();
        if (shortFromMapper.get(populousDataRestriction.getColumnIndex()).containsKey(cleaned)) {

            if (type == 1) {
                return  ontologyManager.getOWLDataFactory().getOWLClass(shortFromMapper.get(populousDataRestriction.getColumnIndex()).get(cleaned));
            }
            else if (type == 4) {
                return ontologyManager.getOWLDataFactory().getOWLNamedIndividual(shortFromMapper.get(populousDataRestriction.getColumnIndex()).get(cleaned));
            }
        }

//        // then look in the all the ontologies
//        if (entities.isEmpty()) {
//            for (String s : shortFormProvider.getShortForms()) {
//                if (s.toLowerCase().equals(shortForm.toLowerCase())) {
//                    logger.debug("Entity found:" + s.toLowerCase());
//                    entities.addAll(shortFormProvider.getEntities(s));
//                }
//                else if (s.toLowerCase().equals(shortForm.toLowerCase().replaceAll(" ", "_")) ) {
//                    logger.debug("Entity found:" + s.toLowerCase());
//                    entities.addAll(shortFormProvider.getEntities(s));
//                }
//            }
//        }

        // finally create a new entity
        return createNewEntity(shortForm, type, populousDataRestriction);
    }


    //create a new OWLEntity (class or individual) for the term newTerm
    private OWLEntity createNewEntity(String shortForm, Integer type, PopulousDataRestriction populousDataRestriction) {

        logger.debug("Creating new term:" + shortForm);

        if (type == 1) {

            boolean hasRestriction = false;
            OWLClass parent = null;
            if (!StringUtils.isBlank(populousDataRestriction.getClassExpression())) {
                // see if there is a parent term
                parent = shortFormProvider.getEntity(populousDataRestriction.getClassExpression()).asOWLClass();
            }

            OWLEntityCreationSet<OWLClass> ecs = null;
            try {
                if(parent != null){
                    ecs = cf.createOWLClass(shortForm, entityCreation.getDefaultBaseURI(), parent);
                }
                else{
                    ecs = cf.createOWLClass(shortForm, entityCreation.getDefaultBaseURI());
                }
                if (ecs.getOntologyChanges() != null) {
                    ontologyManager.applyChanges(ecs.getOntologyChanges());
                    shortFormProvider.add(ecs.getOWLEntity());
                    return ecs.getOWLEntity();
                }
            } catch (OWLEntityCreationException e) {
                e.printStackTrace();
            }
        }
        else if (type == 4) {
            OWLEntityCreationSet<OWLNamedIndividual> ecs = null;
            try {
                ecs = cf.createOWLIndividual(shortForm, entityCreation.getDefaultBaseURI());
                if (ecs.getOntologyChanges() != null) {
                    ontologyManager.applyChanges(ecs.getOntologyChanges());
                }
                shortFormProvider.add(ecs.getOWLEntity());
                return ecs.getOWLEntity();
            } catch (OWLEntityCreationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private VariableTypeVisitorEx variableVisitor = new VariableTypeVisitorEx ()
    {

        public Object visitCLASSVariableType(CLASSVariableType classVariableType) {
            return 1;
        }

        public Object visitOBJECTPROPERTYVariableType(OBJECTPROPERTYVariableType objectpropertyVariableType) {
            return 2;
        }

        public Object visitDATAPROPERTYVariableType(DATAPROPERTYVariableType datapropertyVariableType) {
            return 3;
        }

        public Object visitINDIVIDUALVariableType(INDIVIDUALVariableType individualVariableType) {
            return 4;
        }

        public Object visitCONSTANTVariableType(CONSTANTVariableType constantVariableType) {
            return 5;
        }

        public Object visitANNOTATIONPROPERTYVariableType(ANNOTATIONPROPERTYVariableType annotationpropertyVariableType) {
            return 6;
        }
    };

}
