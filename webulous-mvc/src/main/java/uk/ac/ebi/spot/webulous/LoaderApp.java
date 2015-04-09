package uk.ac.ebi.spot.webulous;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.ebi.spot.webulous.model.PopulousDataRestriction;
import uk.ac.ebi.spot.webulous.model.PopulousPattern;
import uk.ac.ebi.spot.webulous.model.PopulousTemplateDocument;
import uk.ac.ebi.spot.webulous.model.RestrictionType;
import uk.ac.ebi.spot.webulous.repository.DataConversionRunRepository;
import uk.ac.ebi.spot.webulous.repository.PopulousTemplateRepository;
import uk.ac.ebi.spot.webulous.repository.RestrictionRunRepository;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Simon Jupp
 * @date 17/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@SpringBootApplication
public class LoaderApp  implements CommandLineRunner {

    @Autowired
    private PopulousTemplateRepository templateRepository;
    @Autowired
    private DataConversionRunRepository dataRunRepository;
    @Autowired
    private RestrictionRunRepository restrictionRunRepository;

    @Override
    public void run(String... strings) throws Exception {
        restrictionRunRepository.deleteAll();
        dataRunRepository.deleteAll();
        templateRepository.deleteAll();

        createPizza();
        createEFOCellLine();





    }

    private void createEFOCellLine() {
        System.out.println("creating a new template for EFO");
        PopulousTemplateDocument document = new PopulousTemplateDocument();
        document.setDescription("EFO Cell Line");
        document.setActive(true);
        document.setAdminEmailAddresses("jupp@ebi.ac.uk");
        document.setActiveOntology("http://www.ebi.ac.uk/efo");
        Set<String> ontologyImports= new HashSet<String>();
        ontologyImports.add("http://www.ebi.ac.uk/efo/efo.owl");

        document.setOntologyImports(ontologyImports);



        PopulousDataRestriction dr1 = new PopulousDataRestriction(1, "Cell line");
        dr1.setVariableName("?cellLine");
        dr1.setRestrictionType(RestrictionType.DESCENDANTS);
        dr1.setClassExpression("'cell line'");

        PopulousDataRestriction dr2 = new PopulousDataRestriction(2, "cell type/line");
        dr2.setVariableName("?cellType");
        dr2.setRestrictionType(RestrictionType.DESCENDANTS);
        dr2.setClassExpression("'cell type' or 'cell line'");

        PopulousDataRestriction dr3 = new PopulousDataRestriction(3, "organism part");
        dr3.setVariableName("?tissue");
        dr3.setRestrictionType(RestrictionType.DESCENDANTS);
        dr3.setClassExpression("'organism part'");

        PopulousDataRestriction dr4 = new PopulousDataRestriction(4, "organism");
        dr4.setVariableName("?species");
        dr4.setClassExpression("organism");
        dr4.setRestrictionType(RestrictionType.DESCENDANTS);

        PopulousDataRestriction dr5 = new PopulousDataRestriction(5, "sex");
        dr5.setVariableName("?sex");
        dr5.setClassExpression("sex");
        dr5.setRestrictionType(RestrictionType.DESCENDANTS);

        PopulousDataRestriction dr6 = new PopulousDataRestriction(6, "disease");
        dr6.setVariableName("?disease");
        dr6.setClassExpression("disposition");
        dr6.setRestrictionType(RestrictionType.DESCENDANTS);

        PopulousDataRestriction dr7 = new PopulousDataRestriction(7, "definition");
        dr7.setVariableName("?definition");
        dr7.setRestrictionType(RestrictionType.UNRESTRICTED);

        PopulousDataRestriction dr8 = new PopulousDataRestriction(8, "synonyms");
        dr8.setVariableName("?synonym");
        dr8.setMultivalueField(true);
        dr8.setRestrictionType(RestrictionType.UNRESTRICTED);

        PopulousDataRestriction dr9 = new PopulousDataRestriction(9, "definition citation");
        dr9.setVariableName("?definitionCitation");
        dr9.setRestrictionType(RestrictionType.UNRESTRICTED);

        List<PopulousDataRestriction> restrictionSet = new ArrayList<PopulousDataRestriction>();
        restrictionSet.add(dr1);
        restrictionSet.add(dr2);
        restrictionSet.add(dr3);
        restrictionSet.add(dr4);
        restrictionSet.add(dr5);
        restrictionSet.add(dr6);
        restrictionSet.add(dr7);
        restrictionSet.add(dr8);
        restrictionSet.add(dr9);
        document.setDataRestrictions(restrictionSet);

        PopulousPattern populousPattern1 = new PopulousPattern();
        populousPattern1.setPatternName("Cell type, species, tissue");
        populousPattern1.setPatternValue("?cellLine:CLASS, \n" +
                "?cellType:CLASS, \n" +
                "?species:CLASS, \n" +
                "?tissue:CLASS \n" +
                "BEGIN \n" +
                "ADD ?cellLine subClassOf derives_from some (?cellType and BFO_0000050 some (?tissue and BFO_0000050 some ?species))\n" +
                "END;");

        PopulousPattern populousPattern2 = new PopulousPattern();
        populousPattern2.setPatternName("Bearer of some disease");
        populousPattern2.setPatternValue("?cellLine:CLASS, \n" +
                "?disease:CLASS \n" +
                "BEGIN \n" +
                "ADD ?cellLine subClassOf bearer_of some ?disease \n" +
                "END;");

        PopulousPattern populousPattern3 = new PopulousPattern();
        populousPattern3.setPatternName("Has quality some sex");
        populousPattern3.setPatternValue("?cellLine:CLASS, \n" +
                "?sex:CLASS \n" +
                "BEGIN \n" +
                "ADD ?cellLine subClassOf OBI_0000298 some ?sex \n" +
                "END;\n");

        PopulousPattern populousPattern4 = new PopulousPattern();
        populousPattern4.setPatternName("Definition");
        populousPattern4.setPatternValue("?cellLine:CLASS,\n" +
                "?definition:CONSTANT\n" +
                "BEGIN\n" +
                "ADD ?cellLine.IRI definition \"?definition\"\n" +
                "END;");

        PopulousPattern populousPattern5 = new PopulousPattern();
        populousPattern5.setPatternName("Synonyms");
        populousPattern5.setPatternValue("?cellLine:CLASS,\n" +
                "?synonym:CONSTANT\n" +
                "BEGIN\n" +
                "ADD ?cellLine.IRI alternative_term \"?synonym\"\n" +
                "END;");

        PopulousPattern populousPattern6 = new PopulousPattern();
        populousPattern6.setPatternName("Definition citation");
        populousPattern6.setPatternValue("?cellLine:CLASS,\n" +
                "?definitionCitation:CONSTANT\n" +
                "BEGIN\n" +
                "ADD ?cellLine.IRI definition_citation \"?definitionCitation\"\n" +
                "END;");

        PopulousPattern populousPattern7 = new PopulousPattern();
        populousPattern7.setPatternName("Cell line");
        populousPattern7.setPatternValue("?cellLine:CLASS\n" +
                "BEGIN\n" +
                "ADD ?cellLine subClassOf EFO_0000322\n" +
                "END;");

        List<PopulousPattern> patterns = new ArrayList<PopulousPattern>();
        patterns.add(populousPattern1);
        patterns.add(populousPattern2);
        patterns.add(populousPattern3);
        patterns.add(populousPattern4);
        patterns.add(populousPattern5);
        patterns.add(populousPattern6);
        patterns.add(populousPattern7);
        document.setPatterns(patterns);

        System.out.println("template created about to save");

        templateRepository.save(document);

        System.out.println("saved!");
    }

    private void createPizza() {
        System.out.println("creating a new template");
        PopulousTemplateDocument document = new PopulousTemplateDocument();
        document.setDescription("Pizza template");
        document.setActive(true);
        document.setAdminEmailAddresses("jupp@ebi.ac.uk");
        document.setActiveOntology("http://www.test.com/pizza.owl");
        Set<String> ontologyImports= new HashSet<String>();
        ontologyImports.add("http://www.ebi.ac.uk/~jupp/downloads/pizza.owl");
        document.setOntologyImports(ontologyImports);



        PopulousDataRestriction dr1 = new PopulousDataRestriction(1, "Pizza");
        dr1.setVariableName("?pizza");
        dr1.setRestrictionType(RestrictionType.UNRESTRICTED);

        PopulousDataRestriction dr2 = new PopulousDataRestriction(2, "Fish toppings");
        dr2.setVariableName("?fishTopping");
        dr2.setRestrictionType(RestrictionType.DESCENDANTS);
        dr2.setClassExpression("SeafoodTopping");
        String[][] fishValues = new String[2][2];
        fishValues[0][0] = "AnchovyTopping";
        fishValues[0][1] = "http://www.pizza.com/ontologies/pizza.owl#AnchovyTopping";
        fishValues[1][0] = "PrawnTopping";
        fishValues[1][1] = "http://www.pizza.com/ontologies/pizza.owl#PrawnTopping";
        dr2.setValues(fishValues);

        PopulousDataRestriction dr3 = new PopulousDataRestriction(3, "Meat toppings");
        dr3.setVariableName("?meatTopping");
        dr3.setClassExpression("MeatTopping");
        dr3.setRestrictionType(RestrictionType.DESCENDANTS);
        String[][] meatValues = new String[2][2];
        meatValues[0][0] = "HamTopping";
        meatValues[0][1] = "http://www.pizza.com/ontologies/pizza.owl#HamTopping";
        meatValues[1][0] = "SalamiTopping";
        meatValues[1][1] = "http://www.pizza.com/ontologies/pizza.owl#SalamiTopping";
        dr3.setValues(meatValues);

        List<PopulousDataRestriction> restrictionSet = new ArrayList<PopulousDataRestriction>();
        restrictionSet.add(dr1);
        restrictionSet.add(dr2);
        restrictionSet.add(dr3);
        document.setDataRestrictions(restrictionSet);

        PopulousPattern populousPattern1 = new PopulousPattern();
        populousPattern1.setPatternName("Meat topping pattern");
        populousPattern1.setPatternValue("?pizza:CLASS,\n?meatTopping:CLASS\nBEGIN\nADD ?pizza subClassOf hasTopping some ?meatTopping\nEND;\n");

        PopulousPattern populousPattern2 = new PopulousPattern();
        populousPattern2.setPatternName("Fish topping pattern");
        populousPattern2.setPatternValue("?pizza:CLASS,\n?fishTopping:CLASS\nBEGIN\nADD ?pizza subClassOf hasTopping some ?fishTopping\nEND;\n");

        List<PopulousPattern> patterns = new ArrayList<PopulousPattern>();
        patterns.add(populousPattern1);
        patterns.add(populousPattern2);
        document.setPatterns(patterns);

        System.out.println("template created about to save");

        templateRepository.save(document);

        System.out.println("saved pizza!");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LoaderApp.class, args);
    }
}
