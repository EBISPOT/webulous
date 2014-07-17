package uk.ac.ebi.fgpt.populous.service;

import junit.framework.TestCase;
import org.coode.oppl.Variable;
import org.coode.parsers.common.SystemErrorEcho;
import org.coode.patterns.OPPLPatternParser;
import org.coode.patterns.ParserFactory;
import org.coode.patterns.PatternModel;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import uk.ac.ebi.fgpt.populous.entity.CustomOWLEntityFactory;
import uk.ac.ebi.fgpt.populous.model.*;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by dwelter on 11/07/14.
 */
public class TestPopulousPatternExecutor extends TestCase {

    private PopulousModel populousModel;
    private OWLOntology activeOntology;
    private DataCollection collection;
    private PatternModel patternModel;

    private ParserFactory pf;
    private OPPLPatternParser parser;
    CustomOWLEntityFactory cf;

    private PopulousPatternExecutionService service;


    @Before
    public void setUp(){

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        populousModel = mock(SimplePopulousModel.class);


        MockPopulousPattern mockPattern = new MockPopulousPattern();

        Set<PopulousPattern> mockPatterns = new HashSet<PopulousPattern>();
        mockPatterns.add(mockPattern);


        when(populousModel.getSourceOntologyIRI()).thenReturn(IRI.create("http://www.pizza.com/ontologies/pizza.owl"));
        when(populousModel.getSourceOntologyPhysicalIRI()).thenReturn(IRI.create("../../resources/pizza.owl"));
        when(populousModel.getPopulousPatterns()).thenReturn((Set<PopulousPattern>) mockPatterns);

        manager.addIRIMapper(new SimpleIRIMapper(populousModel.getSourceOntologyIRI(), populousModel.getSourceOntologyPhysicalIRI()));

       collection = new MockDataCollection();



        try {
            activeOntology = manager.createOntology(populousModel.getSourceOntologyIRI());

            this.pf = new ParserFactory(activeOntology, manager);
            this.parser = pf.build(new SystemErrorEcho());

//            Variable var = mock(Variable.class);

//            List<InputVariable<?>> foo = parser.parse(populousModel.getPopulousPatterns().iterator().next().getPatternValue());
            patternModel = parser.parse(mockPattern.getPatternValue());
//            patternModel = mock(PatternModel.class);
//
//            when(patternModel.getInputVariables()).thenReturn(parser.parse(mockPattern.getPatternValue()).getInputVariables());



            service = new PopulousPatternExecutionService(collection, manager, populousModel, populousModel.getEntity());


        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testOPPLscriptGeneration(){

        Map<String, Variable> map = service.createOPPLVariableMap(patternModel);

        assertTrue(map.keySet().contains("?namedPizza"));
        assertTrue(map.keySet().contains("?pizzaBase"));



//        service.createShortFormMapper();
//        service.processDataObject();

//        service.executeOPPLPatterns();


    }


//    @Test
//    public void testNewEntities(){
//
//    }


    private class MockDataCollection implements DataCollection{

        @Override
        public int getDataObjectCount() {
            return 2;
        }

        @Override
        public int getDataAttributeCount() {
            return 2;
        }

        @Override
        public Collection<DataAttribute> getDataAttributes() {
            Collection<DataAttribute> attributes = new ArrayList<DataAttribute>();
            MockDataAttribute attr1 = new MockDataAttribute("NamedPizza", 0);
            attr1.setPermissableTerm(IRI.create("http://www.pizza.com/ontologies/pizza.owl#NewYorkPizza"), "NewYorkPizza");
            attr1.setPermissableTerm(IRI.create("http://www.pizza.com/ontologies/pizza.owl#ChicagoPizza"), "ChicagoPizza");
            MockDataAttribute attr2 = new MockDataAttribute("PizzaBase", 1);
            attr2.setPermissableTerm(IRI.create("http://www.pizza.com/ontologies/pizza.owl#DeepPanBase"), "DeePanBase");
            attr2.setPermissableTerm(IRI.create("http://www.pizza.com/ontologies/pizza.owl#ThinAndCrispyBase"), "ThinAndCrispyBase");
            attributes.add(attr1);
            attributes.add(attr2);
            return attributes;
        }

        @Override
        public Collection<DataObject> getDataObjects() {
            Collection<DataObject> objects = new ArrayList<DataObject>();
            MockDataObject obj1 = new MockDataObject();
            obj1.setDataField("ChicagoPizza", 0);
            obj1.setDataField("DeepPanBase", 1);
            MockDataObject obj2 = new MockDataObject();
            obj2.setDataField("NewYorkPizza", 0);
            obj2.setDataField("ThinAndCrispyBase", 1);
            objects.add(obj1);
            objects.add(obj2);
            return objects;
        }


    }

    private class MockDataAttribute implements DataAttribute {
        private String label;
        private int index;
        private ArrayList<Term> permissableTerms;

        public MockDataAttribute(String lab, int ind){
            label = lab;
            index = ind;
            permissableTerms = new ArrayList<Term>();
        }

        public void setPermissableTerm(IRI iri, String name){
            permissableTerms.add(new Term(iri, name));
        }

        @Override
        public Collection<Term> getPermissibleTerms() {
            return null;
        }

        @Override
        public String getTypeLabel() {
            return label;
        }

        @Override
        public String getTypeURI() {
            return null;
        }

        @Override
        public String getTypeRestriction() {
            return null;
        }

        @Override
        public int getIndex() {
            return index;
        }
    }


    private class MockPopulousPattern implements PopulousPattern {

        @Override
        public String getPatternName() {
            return "PizzaBase";
        }

        @Override
        public String getPatternValue() {
            return "?namedPizza:CLASS, \n" +
                    "?pizzaBase:CLASS \n " +
                    "BEGIN \n" +
                    "ADD ?namedPizza hasBase some ?pizzaBase \n" +
                    "END;";
        }

        @Override
        public String getPatternID() {
            return null;
        }
    }

    private class MockDataObject implements DataObject {
        private Collection<DataField> fields; 
        
        public void setDataField(String value, int index){
            fields.add(new MockDataField(value, index));

        }
        @Override
        public Collection<DataField> getDataFields() {
            
            return fields;
        }

        @Override
        public int getDataFieldCount() {
            return 2;
        }
    }

    private class MockDataField implements DataField {
        private String value;
        private int index;
        public MockDataField(String val, int ind) {
            value = val;
            index = ind;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getType() {
            return null;
        }

        @Override
        public String getIRI() {
            return null;
        }

        @Override
        public int getIndex(){
            return index;
        }
    }
}



