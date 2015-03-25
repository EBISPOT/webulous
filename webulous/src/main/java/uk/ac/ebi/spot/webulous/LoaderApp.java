package uk.ac.ebi.spot.webulous;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.ebi.spot.webulous.model.*;
import uk.ac.ebi.spot.webulous.repository.PopulousTemplateRepository;

import java.util.*;

/**
 * @author Simon Jupp
 * @date 17/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@SpringBootApplication
public class LoaderApp  implements CommandLineRunner {

    @Autowired
    private PopulousTemplateRepository repository;

    @Override
    public void run(String... strings) throws Exception {

        System.out.println("creating a new teamplate");
        PopulousTemplateDocument document = new PopulousTemplateDocument();
        document.setDescription("Pizza template");
        document.setActive(true);
        document.setAdminEmailAddresses(Collections.singleton("simon.jupp@gmail.com"));
        document.setActiveOntology("http://www.test.com/test.owl");
        Set<String> ontologyImports= new HashSet<String>();
        ontologyImports.add("file:/Users/jupp/Dropbox/dev/webpopulous/webulous/src/main/resources/pizza.owl");
//        ontologyImports.add("file:/Users/jupp/Dropbox/dev/ols/ontology-tools/src/test/resources/test-import-2.owl");

        document.setOntologyImports(ontologyImports);
//        document.setLastUpdated(new Date());
//        document.setStatus(Status.OK);



        PopulousDataRestriction dr1 = new PopulousDataRestriction(1, "Pizza");
//        dr1.setClassExpression("A");
        dr1.setVariableName("?pizza");
        dr1.setRestrictionType(RestrictionType.UNRESTRICTED);

        PopulousDataRestriction dr2 = new PopulousDataRestriction(2, "Fish toppings");
        dr2.setVariableName("?fishTopping");
        dr2.setRestrictionType(RestrictionType.DESCENDANTS);
        dr2.setClassExpression("CoberturaDePeixe");

        PopulousDataRestriction dr3 = new PopulousDataRestriction(3, "Meat toppings");
        dr3.setVariableName("?meatTopping");
        dr3.setClassExpression("CoberturaDeCarne");
        dr3.setRestrictionType(RestrictionType.DESCENDANTS);

        List<PopulousDataRestriction> restrictionSet = new ArrayList<PopulousDataRestriction>();
        restrictionSet.add(dr1);
        restrictionSet.add(dr2);
        restrictionSet.add(dr3);
        document.setDataRestrictions(restrictionSet);

        SimplePopulousPattern populousPattern1 = new SimplePopulousPattern();
        populousPattern1.setPatternName("Pattern 1");
        populousPattern1.setPatternValue("?pizza:CLASS, ?meatTopping:CLASS \n BEGIN ADD ?pizza subClassOf hasTopping some ?meatTopping END;");

        SimplePopulousPattern populousPattern2 = new SimplePopulousPattern();
        populousPattern2.setPatternName("Pattern 1");
        populousPattern2.setPatternValue("?pizza:CLASS, ?fishTopping:CLASS \n BEGIN ADD ?pizza subClassOf hasTopping some ?fishTopping END;");

        List<PopulousPattern> patterns = new ArrayList<PopulousPattern>();
        patterns.add(populousPattern1);
        patterns.add(populousPattern2);
        document.setPatterns(patterns);

        //        Map<String, Integer> variableToColumnMap = new HashMap<String, Integer>();
        //        variableToColumnMap.put("?a", 1);
        //        variableToColumnMap.put("?b", 2);
        //        document.setVariableToColumnMap(variableToColumnMap);

        System.out.println("template created about to save");

        repository.deleteAll();
        repository.save(document);

        System.out.println("saved!");




    }
    public static void main(String[] args) throws Exception {
        SpringApplication.run(LoaderApp.class, args);
    }
}
