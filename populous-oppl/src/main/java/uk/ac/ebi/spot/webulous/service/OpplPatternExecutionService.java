package uk.ac.ebi.spot.webulous.service;

import org.apache.commons.lang3.StringUtils;
import org.coode.oppl.Variable;
import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
import org.coode.oppl.variabletypes.*;
import org.coode.parsers.BidirectionalShortFormProviderAdapter;
import org.coode.parsers.ErrorListener;
import org.coode.parsers.common.ErrorCollector;
import org.coode.parsers.common.QuickFailErrorListener;
import org.coode.parsers.common.SystemErrorEcho;
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

    private static final String SEPERATOR = "||";
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


    public OWLOntology executeOPPLPatterns(String[][] data, PopulousTemplate populousTemplate, EntityCreation entityCreation, List<String> errorCollector) throws OWLOntologyCreationException {

        logger.debug("Starting Pattern Executor");
        this.dataCollection = data;
        this.populousTemplate = populousTemplate;
        this.ontologyManager = OWLManager.createOWLOntologyManager();
        this.entityCreation = entityCreation;

        logger.debug("creating new ontology " + populousTemplate.getActiveOntology());
        this.activeOntology = ontologyManager.createOntology(IRI.create(populousTemplate.getActiveOntology()));

        for (String iri: populousTemplate.getOntologyImports())  {
            ontologyManager.loadOntology(IRI.create(iri));
        }
        //set up an OWLEntityFactory
        this.cf = new CustomOWLEntityFactory(ontologyManager, activeOntology, entityCreation);

        //set up an OPPL ParserFactory
        this.pf = new ParserFactory(activeOntology, ontologyManager);
        this.parser = pf.build(new QuickFailErrorListener());

        // set up short form provider
        IRI iri = OWLRDFVocabulary.RDFS_LABEL.getIRI();
        OWLAnnotationProperty prop = ontologyManager.getOWLDataFactory().getOWLAnnotationProperty(iri);
        props = new ArrayList<OWLAnnotationProperty>();
        props.add(prop);
        ShortFormProvider provider = new AnnotationValueShortFormProvider(props, new HashMap<OWLAnnotationProperty, List<String>>(), ontologyManager);
        shortFormProvider = new BidirectionalShortFormProviderAdapter(ontologyManager, ontologyManager.getOntologies(), provider);
        shortFromMapper = createShortFormMapper(populousTemplate.getDataRestrictions());

        QuickFailRuntimeExceptionHandler handler = new QuickFailRuntimeExceptionHandler();

        try {
            validateAllPatterns(parser, populousTemplate.getPatterns());


            for (PopulousPattern pattern : populousTemplate.getPatterns()) {
                logger.debug("Got pattern: " + pattern.getPatternName() + "\n" + pattern.getPatternValue());

                //pass the OPPL pattern string to the parser for processing
                PatternModel patternModel = parser.parse(pattern.getPatternValue());

                List<OWLAxiomChange> changes = new ArrayList<OWLAxiomChange>();

                // get the pattern variables
                if (!patternModel.getInputVariables().isEmpty()) {

                    //create a map of the input variable names, eg "?disease" and the actual variable
                    Map<String, Variable> opplVariableMap = createOPPLVariableMap(patternModel);

                    int done = 0;
                    logger.debug("About to read " + dataCollection.length + " rows");
                    //process each row in the DataCollection, one by one
                    for (int x =0 ; x < dataCollection.length; x ++) {
                        // for (DataObject row : dataCollection.getDataObjects()) {
                        logger.debug("Reading row: " + x);
                        try {
                            //create an instantiated pattern model based on the data in the row
                            InstantiatedPatternModel ipm =  processDataRow(dataCollection[x], opplVariableMap, handler, patternModel);

                            //pass the instantiated pattern model to a patternExecutor and add the changes to the list of all changes for this model
                            NonClassPatternExecutor patternExecutor = new NonClassPatternExecutor(ipm, activeOntology, ontologyManager, IRI.create("http://e-lico.eu/populous#OPPL_pattern"), handler);
                            changes.addAll(patternExecutor.visit(patternModel));
                            done++;
                        } catch (RuntimeException e) {
                            // todo collect this error
                            errorCollector.add(e.getMessage());
                            logger.error("Error processing row " + done + ": " + e.getMessage(), e);
                        }
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
                    logger.debug(change.toString());
                }
                ontologyManager.applyChanges(changes);
            }
        } catch (Exception e ) {
            errorCollector.add(e.getMessage());
        }

        return activeOntology;

    }

    private void validateAllPatterns(OPPLPatternParser parser, List<PopulousPattern> patterns) {
        for (PopulousPattern pattern : patterns) {
            try {
                parser.parse(pattern.getPatternValue());
            } catch (Exception e) {
                logger.error("Failed to validate pattern: " + pattern.getPatternName(), e);
                throw new RuntimeException("Failed to validate pattern: " + pattern.getPatternName() + ": " + e.getMessage());
            }
        }
    }

    public Map<String, Variable> createOPPLVariableMap(PatternModel patternModel){
        Map<String, Variable> opplVariableMap = new HashMap<String, Variable>();
        for (Variable v : patternModel.getInputVariables()) {
            logger.debug("Loading input variables:" + v.getName());
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

            int columnIndex = (restriction.getColumnIndex() - 1);
            sfMapper.put(columnIndex, labelToUriMap);
        }
        return sfMapper;
    }


    public InstantiatedPatternModel processDataRow(String[] row, Map<String, Variable> opplVariableMap, QuickFailRuntimeExceptionHandler handler, PatternModel patternModel) {

        InstantiatedPatternModel ipm = pf.getPatternFactory().createInstantiatedPatternModel(patternModel, handler);

        for (PopulousDataRestriction populousDataRestriction : populousTemplate.getDataRestrictions()) {

            int columnIndex = populousDataRestriction.getColumnIndex() - 1;

            logger.debug("reading col " + columnIndex);
            // see if the row has a value
            if (columnIndex < row.length) {
                String cellValue = row[columnIndex];
                logger.debug("Cell value: "  + cellValue);
                if (!StringUtils.isBlank(cellValue)) {
                    String variable = populousDataRestriction.getVariableName();

                    //check that the variable matches an OPPL pattern input variable
                    // if not we just ignore this column
                    if (opplVariableMap.keySet().contains(variable)) {
                        Variable v = opplVariableMap.get(variable);
                        VariableType type = v.getType();

                        //determine the type of the input variable: OWLClass, OWLIndividual or constant, then instantiate as appropriate
                        if (type.accept(variableVisitor).equals(5)) {
                            logger.debug("instantiating variable as constant:" + opplVariableMap.get(variable).getName() + " to " + cellValue);
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
                    else {
                        continue;
                    }
                }
                else if (populousDataRestriction.isRequired()) {
                    throw new RuntimeException("Missing value at " + columnIndex+ ", which is a required restriction");
                }
            }
            else {
                throw new RuntimeException("Failed to process row as the number of restricted column index " + columnIndex+ " is greater than the number of columns in the data " + row.length);
            }
        }
        return ipm;
    }

    private Set<OWLEntity> createOWLEntitiesFromValue(String value, int type, PopulousDataRestriction populousDataRestriction) {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        if (StringUtils.isNoneBlank(value)) {
            String[] values = value.split("\\s*\\|\\|\\s*");
            for (String s : values) {
                s = s.trim();
                logger.debug("Looking up:" + s);
                OWLEntity entity = getEntityForValue(s, type, populousDataRestriction);
                if (entity !=null) {
                    entities.add(entity) ;
                }
            }
        }
        else {
            if (!StringUtils.isBlank(populousDataRestriction.getDefaultValue())) {
                OWLEntity entity = getEntityForValue(populousDataRestriction.getDefaultValue(), type, populousDataRestriction);
                if (entity !=null) {
                    entities.add(entity) ;
                }
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

        // String location = populousTemplate.getSourceOntologyOutputLocation();


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

        String cleaned = shortForm.trim();
        cleaned = cleaned.toLowerCase();
        int columnIndex = (populousDataRestriction.getColumnIndex() - 1);
        if (shortFromMapper.get(columnIndex).containsKey(cleaned)) {

            if (type == 1) {
                return  ontologyManager.getOWLDataFactory().getOWLClass(shortFromMapper.get(columnIndex).get(cleaned));
            }
            else if (type == 4) {
                return ontologyManager.getOWLDataFactory().getOWLNamedIndividual(shortFromMapper.get(columnIndex).get(cleaned));
            }
        }

//        // then look in the all the ontologies
        for (String s : shortFormProvider.getShortForms()) {
            if (s.toLowerCase().equals(shortForm.toLowerCase())) {
                logger.debug("Entity found:" + s.toLowerCase());
                return shortFormProvider.getEntity(s);
            }
            else if (s.toLowerCase().equals(shortForm.toLowerCase().replaceAll(" ", "_")) ) {
                logger.debug("Entity found:" + s.toLowerCase());
                return shortFormProvider.getEntity(s);
            }
        }

        // finally create a new entity
        return createNewEntity(shortForm, type, populousDataRestriction);
    }


    //create a new OWLEntity (class or individual) for the term newTerm
    private OWLEntity createNewEntity(String shortForm, Integer type, PopulousDataRestriction populousDataRestriction) {

        logger.debug("Creating new term:" + shortForm);

        OWLEntity entity = null;
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
                    logger.info("creating owl class with base URI" + entityCreation.getDefaultBaseURI());
                    ecs = cf.createOWLClass(shortForm, entityCreation.getDefaultBaseURI());
                }
                if (ecs.getOntologyChanges() != null) {
                    ontologyManager.applyChanges(ecs.getOntologyChanges());
                    shortFormProvider.add(ecs.getOWLEntity());
                    entity = ecs.getOWLEntity();
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
                entity = ecs.getOWLEntity();
            } catch (OWLEntityCreationException e) {
                e.printStackTrace();
            }
        }
        logger.info("new term created with URI " + entity.getIRI());

        return entity;
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
