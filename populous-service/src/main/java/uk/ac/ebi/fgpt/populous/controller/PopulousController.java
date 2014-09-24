package uk.ac.ebi.fgpt.populous.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.fgpt.populous.model.DataCollection;
import uk.ac.ebi.fgpt.populous.model.PopulousModel;
import uk.ac.ebi.fgpt.populous.service.DefaultPopulousService;
import uk.ac.ebi.fgpt.populous.service.WebulousDataConversionService;
import uk.ac.ebi.fgpt.populous.utils.OntologyConfiguration;

import java.io.IOException;
import java.util.*;

/**
 * Created by dwelter on 09/07/14.
 *
 *
 * The controller receives data from webulous, converts it into the appropriate DataCollection model, instantiates config stuff, then passes it all on to
 * a DefaultPopulousService to process
 *
 * required API calls
 * - a POST call that takes all the data in one big chunk. See JSONtemplate.txt for what the incoming JSON should look like
 *
 * - a validation call that gets passed a single OPPL pattern and associated source ontology info in order to validate the actual pattern. Return valid or invalid message
 * this should still return a status report while the server is processing, then a success/failure message
 *
 * - in theory, we could also have a GET method that gets one or more rows of data as per JSON template and returns something, either a full ontology file or a location of a file
 *
 *
 */

@Controller
//@RequestMapping("/webulous")
public class PopulousController {

    private OntologyConfiguration ontologyConfiguration;


    @RequestMapping(value = "/test")
    public @ResponseBody String helloWorld(){
        return ontologyConfiguration.getOntologyManager().toString();
    }

    @RequestMapping(value = "/source")
    public @ResponseBody String helloSource(){
        return "You want a source";
    }

    @RequestMapping(value = "/sourcetest/{ontology}")
    public @ResponseBody String helloSpecificSource(@PathVariable String ontology){
        return "You want the source " + ontology;
    }



    @RequestMapping(value = "/source/{ontology}")
    public @ResponseBody Map<String, Object> getSourceOntologyParameters(@PathVariable String ontology){
        String ontologyID = ontology.toLowerCase();
        Properties ontologyConfig = new Properties();
        boolean status = true;

        try {
            ontologyConfig.load(getClass().getClassLoader().getResource(ontologyID +".properties").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            System.out.println("There is no config file for ontology " + ontologyID);
            status = false;
        }

        Map<String,Object> sourceParameters;
        if(status){
            sourceParameters = processConfig(ontologyConfig);
        }
        else{
            sourceParameters = new HashMap<String, Object>();
            sourceParameters.put("status", "No config parameters available for " + ontology);
        }

        return sourceParameters;
    }


    @RequestMapping(value = "/data/all", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody String processDataSubmission(@RequestBody String data){

        if(data == ""){
            return "Data, I need data!";
        }


        System.out.println(data);


        WebulousDataConversionService converter = new WebulousDataConversionService(data, getOntologyConfiguration());

        converter.processInput();

        PopulousModel populousModel = converter.getConfigInformation();
        DataCollection processedData = converter.getData();


        DefaultPopulousService populousService = new DefaultPopulousService(processedData, populousModel);
        populousService.setConfiguration(getOntologyConfiguration());
        populousService.setUpPatternExecutor(populousModel, processedData);

/**TO DO: do something useful with this status message!!!!**/
        String status = converter.getStatusMessage();

        return status;
    }



//    @RequestMapping(value = "/validate/{opplPattern}")
//    public @ResponseBody String validateOPPL(@PathVariable String opplPattern){
//
//
//        return "false";
//    }


    public Map<String, Object> processConfig(Properties ontologyConfig){

        Map<String,Object> sourceParameters = new HashMap<String, Object>();
        sourceParameters.put("status", "OK");

        Map<String, String> sourceOntology = new HashMap<String, String>();
        sourceOntology.put("acronym", ontologyConfig.getProperty("sourceOntology.acronym"));
        sourceOntology.put("name", ontologyConfig.getProperty("sourceOntology.name"));
        sourceParameters.put("sourceOntology", sourceOntology);

        List<Object> imports = new ArrayList<Object>();
        int importCount = 1;
        String acronym = ontologyConfig.getProperty("importOntology.acronym."+importCount);

        while(acronym != null){
            Map<String, String> importOntology = new HashMap<String, String>();
            importOntology.put("acronym", acronym);
            importOntology.put("name", ontologyConfig.getProperty("importOntology.name."+importCount));
            imports.add(importOntology);

            importCount++;
            acronym = ontologyConfig.getProperty("importOntology.acronym."+importCount);
        }

        sourceParameters.put("importOntologies", imports);

        List<Object> patterns = new ArrayList<Object>();

        int patternCount = 1;
        String name = ontologyConfig.getProperty("opplPattern.name."+patternCount);

        while(name != null){
            Map<String, Object> pattern = new HashMap<String, Object>();
            pattern.put("name", name);
            pattern.put("pattern", ontologyConfig.getProperty("opplPattern.pattern."+patternCount));

            int varCount = 1;
            List<String> variables = new ArrayList<String>();
            String variable = ontologyConfig.getProperty("opplPattern.variable."+patternCount+"."+varCount);

            while (variable != null){
                variables.add(variable);
                varCount++;
                variable = ontologyConfig.getProperty("opplPattern.variable."+patternCount+"."+varCount);
            }

            pattern.put("variables", variables);

            patterns.add(pattern);
            patternCount++;
            name = ontologyConfig.getProperty("opplPattern.name."+patternCount);
        }

        sourceParameters.put("opplPatterns", patterns);

        return sourceParameters;
    }

    public OntologyConfiguration getOntologyConfiguration() {
        return ontologyConfiguration;
    }

    @Autowired
    public void setOntologyConfiguration(OntologyConfiguration ontologyConfiguration) {
        this.ontologyConfiguration = ontologyConfiguration;
    }
}
