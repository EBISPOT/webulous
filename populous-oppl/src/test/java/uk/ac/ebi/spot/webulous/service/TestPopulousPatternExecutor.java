//package uk.ac.ebi.spot.webulous.service;
//
//import junit.framework.TestCase;
//import org.coode.oppl.Variable;
//import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
//import org.coode.parsers.common.SystemErrorEcho;
//import org.coode.patterns.*;
//import org.junit.Before;
//import org.junit.Test;
//import org.semanticweb.owlapi.apibinding.OWLManager;
//import org.semanticweb.owlapi.model.*;
//import org.semanticweb.owlapi.util.SimpleIRIMapper;
//import uk.ac.ebi.spot.webulous.entity.CustomOWLEntityFactory;
//import uk.ac.ebi.spot.webulous.model.*;
//
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.util.*;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
///**
// * Created by dwelter on 11/07/14.
// */
//public class TestPopulousPatternExecutor extends TestCase {
//
//    private PopulousModel populousModel;
//    private OWLOntology activeOntology;
//    private DataCollection collection;
//    private PatternModel patternModel;
//
//    private ParserFactory pf;
//    private OPPLPatternParser parser;
//    CustomOWLEntityFactory cf;
//
//    private PopulousPatternExecutionService service;
//
//    private String output;
//
//    private OWLOntologyManager manager;
//
//
//    @Before
//    public void setUp(){
//
//        manager = OWLManager.createOWLOntologyManager();
//
//
//        MockPopulousPattern mockPattern = new MockPopulousPattern();
//
//
//        Set<PopulousPattern> mockPatterns = new HashSet<PopulousPattern>();
//        mockPatterns.add(mockPattern);
//
//        populousModel = mock(PropertyBasedPopulousModel.class);
//
//        LinkedHashMap<Integer, String> variableMap = new LinkedHashMap<Integer, String>();
//
//        variableMap.put(0, "?namedPizza");
//        variableMap.put(1, "?pizzaBase");
//
//        URL ontologyURL = getClass().getClassLoader().getResource("pizza.owl");
//
//        when(populousModel.getSourceOntologyIRI()).thenReturn(IRI.create("http://www.pizza.com/ontologies/pizza.owl"));
//        try {
//            when(populousModel.getSourceOntologyPhysicalIRI()).thenReturn(IRI.create(ontologyURL));
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            fail();
//        }
//        when(populousModel.getPopulousPatterns()).thenReturn((Set<PopulousPattern>) mockPatterns);
//        when(populousModel.getVariableMapper()).thenReturn(variableMap);
//
//        manager.addIRIMapper(new SimpleIRIMapper(populousModel.getSourceOntologyIRI(), populousModel.getSourceOntologyPhysicalIRI()));
//
//       collection = new MockDataCollection();
//
//
//
//        try {
//            activeOntology = manager.loadOntology(populousModel.getSourceOntologyIRI());
//
//            this.pf = new ParserFactory(activeOntology, manager);
//            this.parser = pf.build(new SystemErrorEcho());
//            this.patternModel = parser.parse(mockPattern.getPatternValue());
//
//            service = new PopulousPatternExecutionService(collection, manager, populousModel, populousModel.getEntity());
//
//
//        } catch (OWLOntologyCreationException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    @Test
//    public void testOPPLscriptGeneration(){
//
//        Map<String, Variable> map = service.createOPPLVariableMap(patternModel);
//
//
//        assertTrue(map.keySet().contains("?namedPizza"));
//        assertTrue(map.keySet().contains("?pizzaBase"));
//
//
//        HashMap<Integer, Map<String, IRI>> sfMapper = service.createShortFormMapper();
//
//        assertEquals(sfMapper.get(0).get("ChicagoPizza"), IRI.create("http://www.pizza.com/ontologies/pizza.owl#ChicagoPizza"));
//        assertEquals(sfMapper.get(1).get("ThinAndCrispyBase"), IRI.create("http://www.pizza.com/ontologies/pizza.owl#ThinAndCrispyBase"));
//
//
//        QuickFailRuntimeExceptionHandler handler = mock((QuickFailRuntimeExceptionHandler.class));
//
//        for(DataObject row : collection.getDataObjects()){
//            InstantiatedPatternModel pm = service.processDataObject(row, map, handler, patternModel);
//
//            assertNotNull(pm);
//
//            NonClassPatternExecutor patternExecutor = new NonClassPatternExecutor(pm, activeOntology, manager, IRI.create("http://e-lico.eu/populous#OPPL_pattern"), handler);
//
//
//            for (OWLAxiomChange owlAxiomChange : patternExecutor.visit(patternModel)) {
//                assertNotNull(owlAxiomChange);
//            }
//
//
//        }
//
//        service.executeOPPLPatterns();
//
//        try {
//            service.saveOntology(populousModel.getSourceOntologyPhysicalIRI());
//
//        } catch (OWLOntologyStorageException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//
//
//    private class MockDataCollection implements DataCollection{
//
//        @Override
//        public int getDataObjectCount() {
//            return 2;
//        }
//
//        @Override
//        public int getDataAttributeCount() {
//            return 2;
//        }
//
//        @Override
//        public List<DataAttribute> getDataAttributes() {
//            List<DataAttribute> attributes = new ArrayList<DataAttribute>();
//            MockDataAttribute attr1 = new MockDataAttribute("NamedPizza", 0);
//            attr1.setPermissableTerm(IRI.create("http://www.pizza.com/ontologies/pizza.owl#NewYorkPizza"), "NewYorkPizza");
//            attr1.setPermissableTerm(IRI.create("http://www.pizza.com/ontologies/pizza.owl#ChicagoPizza"), "ChicagoPizza");
//            MockDataAttribute attr2 = new MockDataAttribute("PizzaBase", 1);
//            attr2.setPermissableTerm(IRI.create("http://www.pizza.com/ontologies/pizza.owl#DeepPanBase"), "DeePanBase");
//            attr2.setPermissableTerm(IRI.create("http://www.pizza.com/ontologies/pizza.owl#ThinAndCrispyBase"), "ThinAndCrispyBase");
//            attributes.add(attr1);
//            attributes.add(attr2);
//            return attributes;
//        }
//
//        @Override
//        public List<DataObject> getDataObjects() {
//            List<DataObject> objects = new ArrayList<DataObject>();
//            MockDataObject obj1 = new MockDataObject();
//            obj1.setDataField("ChicagoPizza", 0);
//            obj1.setDataField("DeepPanBase", 1);
//            MockDataObject obj2 = new MockDataObject();
//            obj2.setDataField("NewYorkPizza", 0);
//            obj2.setDataField("ThinAndCrispyBase", 1);
//            objects.add(obj1);
//            objects.add(obj2);
//            return objects;
//        }
//
//
//    }
//
//    private class MockDataAttribute implements DataAttribute {
//        private String label;
//        private int index;
//        private ArrayList<Term> permissableTerms;
//
//        public MockDataAttribute(String lab, int ind){
//            label = lab;
//            index = ind;
//            permissableTerms = new ArrayList<Term>();
//        }
//
//        public void setPermissableTerm(IRI iri, String name){
//            permissableTerms.add(new Term(iri, name));
//        }
//
//        @Override
//        public List<Term> getPermissibleTerms() {
//            return permissableTerms;
//        }
//
////        @Override
////        public String getTypeLabel() {
////            return label;
////        }
////
////        @Override
////        public String getTypeURI() {
////            return null;
////        }
////
////        @Override
////        public String getTypeRestriction() {
////            return null;
////        }
//
//        @Override
//        public int getIndex() {
//            return index;
//        }
//
//        @Override
//        public List<PopulousDataRestriction> getDataRestrictions() {
//            return null;
//        }
//    }
//
//
//    private class MockPopulousPattern implements PopulousPattern {
//
//        @Override
//        public String getPatternName() {
//            return "PizzaBase";
//        }
//
//        @Override
//        public String getPatternValue() {
//            return "?namedPizza:CLASS, \n" +
//                    "?pizzaBase:CLASS \n" +
//                    "BEGIN \n" +
//                    "ADD ?namedPizza subClassOf hasBase some ?pizzaBase \n" +
//                    "END;";
//        }
//
//    }
//
//    private class MockDataObject implements DataObject {
//        private List<DataField> fields;
//
//        public MockDataObject(){
//            fields = new ArrayList<DataField>();
//        }
//
//        public void setDataField(String value, int index){
//            fields.add(new MockDataField(value, index));
//
//        }
//        @Override
//        public List<DataField> getDataFields() {
//
//            return fields;
//        }
//
//        @Override
//        public int getDataFieldCount() {
//            return 2;
//        }
//    }
//
//    private class MockDataField implements DataField {
//        private String value;
//        private int index;
//        public MockDataField(String val, int ind) {
//            value = val;
//            index = ind;
//        }
//
//        @Override
//        public String getValue() {
//            return value;
//        }
//
//        @Override
//        public String getType() {
//            return null;
//        }
//
////        @Override
////        public String getIRI() {
////            return null;
////        }
//
//        @Override
//        public int getIndex(){
//            return index;
//        }
//
//        @Override
//        public Integer getRestrictionIndex() {
//            return null;
//        }
//    }
//}
//
//
//
