package uk.ac.ebi.fgpt.populous.service;

import org.apache.commons.lang3.StringUtils;
import org.coode.oppl.Variable;
import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
import org.coode.oppl.variabletypes.*;
import org.coode.parsers.BidirectionalShortFormProviderAdapter;
import org.coode.parsers.common.SystemErrorEcho;
import org.coode.patterns.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.populous.entity.CustomOWLEntityFactory;
import uk.ac.ebi.fgpt.populous.exception.OWLEntityCreationException;
import uk.ac.ebi.fgpt.populous.model.*;

import java.text.SimpleDateFormat;
import java.util.*;

/* THIS IS JUPP'S MAGIC CLASS*/

/*
 * The PopulousPatternExecutorService takes a data collection, eg a spreadsheet, a PopulousModel, with OPPL patterns and variable bindings, and a entity creation strategy
 * and turns the data into ontology axioms based on the specified patterns.
  *
 */


public class PopulousPatternExecutionService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private DataCollection dataCollection;

    private PopulousModel populousModel;
    private EntityCreation entityCreation;
    private OWLOntologyManager ontologyManager;

    private Set<OWLOntology> ontologies;
    private OWLOntology activeOntology;

    private ParserFactory pf;
    private OPPLPatternParser parser;
//    private PatternModel patternModel;

    private HashMap<Integer, Map<String, IRI>> shortFromMapper;
    private BidirectionalShortFormProviderAdapter shortFormProvider;

    private Random randomGenerator;

    private List<OWLAnnotationProperty> props;

    CustomOWLEntityFactory cf;


    public PopulousPatternExecutionService(DataCollection collection, OWLOntologyManager man, PopulousModel populousModel, EntityCreation newEntities) throws OWLOntologyCreationException {
        logger.debug("Starting Pattern Executor");
        this.dataCollection = collection;
        this.populousModel = populousModel;
        this.entityCreation = newEntities;
        randomGenerator = new Random();

        ontologyManager = man;

//make sure the source ontology is loaded in the ontology manager and acquire it
        if (!ontologyManager.contains(populousModel.getSourceOntologyIRI())) {
            logger.debug("creating new ontology " + populousModel.getSourceOntologyIRI().toString());
            activeOntology = ontologyManager.createOntology(populousModel.getSourceOntologyIRI());
        }
        else {
            activeOntology = ontologyManager.getOntology(populousModel.getSourceOntologyIRI());
        }

//set up an OWLEntityFactory
        this.cf = new CustomOWLEntityFactory(ontologyManager, activeOntology, entityCreation);

//set up an OPPL ParserFactory
        this.pf = new ParserFactory(activeOntology, ontologyManager);
        this.parser = pf.build(new SystemErrorEcho());

// set up short form provider
        IRI iri = OWLRDFVocabulary.RDFS_LABEL.getIRI();
        OWLAnnotationProperty prop = ontologyManager.getOWLDataFactory().getOWLAnnotationProperty(iri);
        props = new ArrayList<OWLAnnotationProperty>();
        props.add(prop);
        ShortFormProvider provider = new AnnotationValueShortFormProvider(props, new HashMap<OWLAnnotationProperty, List<String>>(), ontologyManager);
        shortFormProvider = new BidirectionalShortFormProviderAdapter(ontologyManager, ontologyManager.getOntologies(), provider);
        shortFromMapper = createShortFormMapper();


    }

    public void executeOPPLPatterns() {

        QuickFailRuntimeExceptionHandler handler = new QuickFailRuntimeExceptionHandler();

        for (PopulousPattern pattern : populousModel.getPopulousPatterns()) {
            System.err.println("Got pattern: " + pattern.getPatternName() + "\n" + pattern.getPatternValue());

//pass the OPPL pattern string to the parser for processing
            PatternModel patternModel = parser.parse(pattern.getPatternValue());

            List<OWLAxiomChange> changes = new ArrayList<OWLAxiomChange>();

 //if the resulting patternModel has input variables, do this
            if (!patternModel.getInputVariables().isEmpty()) {

//create a map of the input variable names, eg "?disease" and the actual variable
                Map<String, Variable> opplVariableMap = createOPPLVariableMap(patternModel);

// for each column, create a new short form mapper for the values that are allowed in that column
//                shortFromMapper = createShortFormMapper();

                logger.debug("Processing model");

                int done = 0;

//process each row in the DataCollection, one by one
                for (DataObject row : dataCollection.getDataObjects()) {
                    logger.debug("Reading row: " + done+1);

//create an instantiated pattern model based on the data in the row
                    InstantiatedPatternModel ipm =  processDataObject(row, opplVariableMap, handler,patternModel);

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


    public Map<String, Variable> createOPPLVariableMap(PatternModel patternModel){
        Map<String, Variable> opplVariableMap = new HashMap<String, Variable>();
        for (Variable v : patternModel.getInputVariables()) {
            System.err.println("Loading input variables:" + v.getName());
            opplVariableMap.put(v.getName(), v);
        }
        return opplVariableMap;
    }

    public HashMap<Integer, Map<String, IRI>> createShortFormMapper(){
        HashMap<Integer, Map<String, IRI>> sfMapper = new HashMap<Integer, Map<String, IRI>>();
        // for each column, create a new short form mapper for the values that are allowed in that column
        for(DataAttribute dataAttribute : dataCollection.getDataAttributes()){
            if(!dataAttribute.getPermissibleTerms().isEmpty()){
                sfMapper.put(dataAttribute.getIndex(), getTermToURIMapping(dataAttribute.getPermissibleTerms()));

            }
            //if no term restrictions exist for a given column, create an empty HashMap
            else{
                sfMapper.put(dataAttribute.getIndex(), new HashMap<String, IRI>());
            }
        }

       return sfMapper;
    }


    public InstantiatedPatternModel processDataObject(DataObject row, Map<String, Variable> opplVariableMap, QuickFailRuntimeExceptionHandler handler, PatternModel patternModel){

        InstantiatedPatternModel ipm = pf.getPatternFactory().createInstantiatedPatternModel(patternModel, handler);

//        System.out.println("Processing a row with " + row.getDataFieldCount() + " fields");


        for (Integer column : populousModel.getVariableMapper().keySet()) {

            if (column != null) {
//                System.out.println("Processing column " + column);

                DataField dataField = null;
                //go through each data field in the current row until you find the one that matches the column
                for(DataField field : row.getDataFields()){
                    if (field.getIndex() == column){
                        dataField = field;
//                        System.out.println("My data field is " + dataField.getValue());
                    }
                }

                if (dataField != null) {
//                                System.err.println("cell value: " + dataField.getValue());

//get the variable associated with this column
                    String variable = populousModel.getVariableMapper().get(column);

                    if (StringUtils.isNotBlank(variable)) {

//check that the variable matches an OPPL pattern input variable
                        if (opplVariableMap.keySet().contains(variable)) {
                            Variable v = opplVariableMap.get(variable);
                            VariableType type = v.getType();

//determine the type of the input variable: OWLClass, OWLIndividual or constant, then instantiate as appropriate
                            if (type.accept(variableVisitor).equals(5)) {
                                if (StringUtils.isNotBlank(dataField.getValue())) {
//                                                System.err.println("instantiating variable:" + opplVariableMap.get(variable).getName() + " to " + dataField.getValue());
                                    String [] values = dataField.getValue().split("\\s*\\|\\s*");
                                    for (String s : values) {
                                        s = s.trim();
                                        ipm.instantiate(opplVariableMap.get(variable), ontologyManager.getOWLDataFactory().getOWLLiteral(s));
                                    }
                                }
                            }
                            else if (type.accept(variableVisitor).equals(1)) {
                                for (OWLEntity entity : getOWLEntities(dataField, 1)) {
                                    if (entity.isOWLClass()) {
                                        logger.debug("instantiating variable:" + opplVariableMap.get(variable).getName() + " to " + entity.getIRI().toString());
//                                                    System.err.println("instantiating variable:" + opplVariableMap.get(variable).getName() + " to " + entity.getIRI().toString());
                                        ipm.instantiate(opplVariableMap.get(variable), entity);
                                    }

                                }
                            }
                            else if (type.accept(variableVisitor).equals(4)) {
                                for (OWLEntity entity : getOWLEntities(dataField, 4)) {
                                    if (entity.isOWLNamedIndividual()) {
                                        logger.debug("instantiating variable:" + opplVariableMap.get(variable).getName() + " to " + entity.getIRI().toString());
//                                                    System.err.println("instantiating variable:" + opplVariableMap.get(variable).getName() + " to " + entity.getIRI().toString());
                                        ipm.instantiate(opplVariableMap.get(variable), entity);
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        return ipm;
    }

//transform a collection of Terms into a String to IRI map
    private Map<String, IRI> getTermToURIMapping(Collection<Term> terms) {
        Map<String, IRI> result = new HashMap<String, IRI> ();
        for (Term t : terms) {
            result.put(t.getName(), t.getIRI());
        }
        return result;
    }

//save the ontology to the designated location
    public void saveOntology(IRI file) throws OWLOntologyStorageException {
                ontologyManager.saveOntology(activeOntology, file);
    }

//save the ontology based on the output location specified in the properties file
    public void saveOntology() throws OWLOntologyStorageException {

        String location = populousModel.getSourceOntologyOutputLocation();


        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;

        String outputFileName = location.concat(dateFormat.format(date)).concat(".owl");

        ontologyManager.saveOntology(activeOntology, IRI.create(outputFileName));
    }

//acquire all the OWLEntities associated with this data field
    private Set<OWLEntity> getOWLEntities(DataField field, Integer type) {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();

        String value = field.getValue();
        if (value != null && !value.equals("")) {
            String [] values = value.split("\\s*\\|\\s*");
            for (String s : values) {
                s = s.trim();
                logger.debug("Looking up:" + s);
                entities.addAll(getEntityShortForms(s, type, field.getIndex(), field.getRestrictionIndex()));
            }
        }

        return entities;
    }

//get the OWLEntities for data value shortForm, looking first in the list of valid ontology terms for this column, then in all ontologies, then if not found, create a new OWLEntity
    private Set<OWLEntity> getEntityShortForms(String shortForm, Integer type, Integer index, Integer restrictionIndex) {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();

        // first look in the validation list
        for (String s : shortFromMapper.get(index).keySet()) {
            if (s.toLowerCase().equals(shortForm.toLowerCase())) {
                logger.debug("Entity found:" + s.toLowerCase());

                if (type == 1) {

                    entities.add(ontologyManager.getOWLDataFactory().getOWLClass(shortFromMapper.get(index).get(s)));

                }
                else if (type == 4) {

                    entities.add(ontologyManager.getOWLDataFactory().getOWLNamedIndividual(shortFromMapper.get(index).get(s)));

                }
            }
        }

        // then look in the all the ontologies
        if (entities.isEmpty()) {
            for (String s : shortFormProvider.getShortForms()) {
                if (s.toLowerCase().equals(shortForm.toLowerCase())) {
                    logger.debug("Entity found:" + s.toLowerCase());
                    entities.addAll(shortFormProvider.getEntities(s));
                }
                else if (s.toLowerCase().equals(shortForm.toLowerCase().replaceAll(" ", "_")) ) {
                    logger.debug("Entity found:" + s.toLowerCase());
                    entities.addAll(shortFormProvider.getEntities(s));
                }
            }
        }

        // finally create a new entity
        if (entities.isEmpty()) {
            OWLEntity e = getNewEntities(shortForm, type, index, restrictionIndex);
            entities.add(e);
        }
        return entities;
    }


//create a new OWLEntity (class or individual) for the term newTerm
    private OWLEntity getNewEntities(String newTerm, Integer type, Integer index, Integer restrictionIndex) {

        logger.debug("Creating new term:" + newTerm);

        if (type == 1) {
            boolean hasRestriction = false;
            String parent = null;

            if(restrictionIndex != null){

                for(DataAttribute attribute : dataCollection.getDataAttributes()){
                    if(!hasRestriction){
                        if(attribute.getIndex() == index){
                            System.out.println("Attribute index is " + index);
                            if(attribute.getDataRestrictions() != null){
                                for(PopulousDataRestriction restriction : attribute.getDataRestrictions()){
                                    if(restriction != null && restriction.getRestrictionIndex() == restrictionIndex){
                                        parent = restriction.getRestrictionParentURI();
                                        hasRestriction = true;
                                    }
                                }
                            }
                            else {
                                System.out.println("Attribute " + index + " has no restricitons");

                            }

                        }
                    }
                }
            }


            OWLEntityCreationSet<OWLClass> ecs = null;
            try {
                if(hasRestriction){
                    ecs = cf.createOWLClass(newTerm, entityCreation.getDefaultBaseURI(), ontologyManager.getOWLDataFactory().getOWLClass(IRI.create(parent)));
                }
                else{
                    ecs = cf.createOWLClass(newTerm, entityCreation.getDefaultBaseURI());
                }
            } catch (OWLEntityCreationException e) {
                e.printStackTrace();
            }
            if (ecs.getOntologyChanges() != null) {
                ontologyManager.applyChanges(ecs.getOntologyChanges());
            }
            shortFormProvider.add(ecs.getOWLEntity());
            return ecs.getOWLEntity();

        }
        else if (type == 4) {
            OWLEntityCreationSet<OWLNamedIndividual> ecs = null;
            try {
                ecs = cf.createOWLIndividual(newTerm, entityCreation.getDefaultBaseURI());
            } catch (OWLEntityCreationException e) {
                e.printStackTrace();
            }
            if (ecs.getOntologyChanges() != null) {
                ontologyManager.applyChanges(ecs.getOntologyChanges());
            }
            shortFormProvider.add(ecs.getOWLEntity());
            return ecs.getOWLEntity();

        }
        return null;

    }
}
