package uk.ac.ebi.fgpt.populous.service;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import uk.ac.ebi.fgpt.populous.model.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by dwelter on 16/09/14.
 */
public class WebulousDataConverter {

    private String statusMessage;
    private JsonNode allData;
    private PopulousInput populousInput;
    private SimpleDataCollection dataCollection;
    private OntologyTermValidationService validationService;

    public WebulousDataConverter(String inputData){

        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(jsonFactory);
        try {
            allData = mapper.readTree(inputData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        populousInput = new PopulousInput();
        validationService = new OntologyTermValidationService(allData.get("configuration").get("sourceOntology").toString().toLowerCase());
    }

    public String getStatusMessage(){
         return statusMessage;
    }


    public void processInput(){

        JsonNode configuration = allData.get("configuration");
        processConfiguration(configuration);

        JsonNode data = allData.get("data");
        processData(data);

    }

    public void processConfiguration(JsonNode configuration){

        String sourceOntology = configuration.get("sourceOntology").toString().toLowerCase();
        Properties ontologyConfig = new Properties();

        try {
            ontologyConfig.load(getClass().getClassLoader().getResource(sourceOntology+".properties").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            System.out.println("There is no config file for this ontology");
        }

        populousInput.setSourceOntology(ontologyConfig.getProperty("sourceOntology.iri"));

        List<String> imports = new ArrayList<String>();
        int importCount = 1;
        String iri = ontologyConfig.getProperty("importOntology.iri."+importCount);

        while(iri != null){
             imports.add(iri);

            importCount++;
            iri = ontologyConfig.getProperty("importOntology.iri."+importCount);
        }

        populousInput.setImportedOntologies(imports);

        JsonNode patterns = configuration.get("opplPatterns");

        List<String> patternNames = new ArrayList<String>();
        Map<String, Integer> variableMap = new HashMap<String, Integer>();


        for(JsonNode pattern : patterns){
            String name = pattern.get("name").toString();
            patternNames.add(name);

            JsonNode vars = pattern.get("variables");


            for(JsonNode var : vars){
                 String variable = var.get("variable").toString();

                if(var.get("columnIndex").isInt()){
                  int columnIndex = var.get("columnIndex").getIntValue();
                  if(variableMap.get(variable) == null){
                    variableMap.put(variable, columnIndex);
                  }
                  else if(variableMap.get(variable) != columnIndex){
                      System.out.println("One variable can't be assigned to more than one column");
                  }
                }
            }
        }

        populousInput.setOpplVariables(variableMap);

        int patternCount = 1;
        String patName = ontologyConfig.getProperty("opplPattern.name."+patternCount);
        List<String> opplPatterns = new ArrayList<String>();

        while(patName != null){
            if(patternNames.contains(patName)){
                String pattern = ontologyConfig.getProperty("opplPattern.fullpattern."+patternCount);
                opplPatterns.add(pattern);
            }

            patternCount++;
            patName = ontologyConfig.getProperty("opplPattern.name."+patternCount);
        }

        populousInput.setOpplPatterns(opplPatterns);


        JsonNode restrictions = configuration.get("dataRestrictions");
        Map<Integer, PopulousDataRestriction> dataRestrictions = new HashMap<Integer, PopulousDataRestriction>();

        for(JsonNode restriction : restrictions){
            PopulousDataRestriction dataRestriction = new PopulousDataRestriction();
            int index = restriction.get("restrictionIndex").getIntValue();
            dataRestriction.setRestrictionIndex(index);

            String ontology = restriction.get("restrictionOntology").toString();
            dataRestriction.setRestrictionOntology(ontology);

            String type = restriction.get("restrictionType").toString();
            dataRestriction.setRestrictionType(type);

            String name = restriction.get("restrictionName").toString();
            dataRestriction.setRestrictionParentName(name);

            String uri = restriction.get("restrictionURI").toString();
            dataRestriction.setRestrictionParentURI(uri);

            dataRestrictions.put(index, dataRestriction);
        }

        populousInput.setRestrictions(dataRestrictions);

    }


    public void processData(JsonNode data){

        int rowNum = data.size();
        int colNum = data.get(0).size();

        dataCollection = new SimpleDataCollection(rowNum, colNum);
        HashMap<String, Integer> variables = (HashMap<String, Integer>) populousInput.getOpplVariables();


        for(JsonNode row : data){
            SimpleDataObject dataRow = new SimpleDataObject(row.size());
            int ind = 0;
            for(JsonNode cell : row){
                SimpleDataAttribute column;
                if(dataCollection.getDataAttributes().get(ind) == null){
                    column = new SimpleDataAttribute(ind);
                    dataCollection.addDataAttribute(column);

                    if(variables.containsValue(ind)){
                        for(String key : variables.keySet()){
                            if(variables.get(key) == ind){
                                column.addVariable(key);
                            }
                        }
                    }
                }
                else {
                    column = (SimpleDataAttribute) dataCollection.getDataAttributes().get(ind);
                }
                String value = cell.get("value").toString();
                PopulousDataRestriction restriction = null;
                String restrictionName = null;
                Integer restrictIndex = null;
                if(cell.get("restrictionIndex").isInt()){
                    restrictIndex = cell.get("restrictionIndex").getIntValue();
                    restriction = populousInput.getRestrictions().get(restrictIndex);
                    restrictionName = restriction.getRestrictionParentName();

                    if(restriction != null && !column.getDataRestrictions().contains(restriction)){
                        column.addDataRestriction(restriction);

                        List<Term> allTerms = validationService.getPermissibleTerms(restriction.getRestrictionParentURI(), restriction.getRestrictionType(), restriction.getRestrictionOntology());

                        for(Term term : allTerms){
                            if(!column.getPermissibleTerms().contains(term)){
                                column.addTerm(term);
                            }
                        }
                    }
                }
                int cellIndex = ind;
                ind++;

                SimpleDataField field = new SimpleDataField(value, restrictionName, restrictIndex, cellIndex);
                dataRow.addDataField(field);

            }
            dataCollection.addDataObject(dataRow);
        }




    }
}


