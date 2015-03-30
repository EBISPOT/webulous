package uk.ac.ebi.spot.webulous.service;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.semanticweb.owlapi.model.IRI;
import uk.ac.ebi.spot.webulous.utils.OntologyConfiguration;
import uk.ac.ebi.spot.webulous.model.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by dwelter on 16/09/14.
 *
 * This class takes a string of JSON from the REST API and converts it into a PopulousModel and SimpleDataCollection
 *
 */
public class WebulousDataConversionService {

    private String statusMessage;
    private JsonNode allData;
    private PropertyBasedPopulousModel populousModel;
    private SimpleDataCollection dataCollection;
    private OntologyTermValidationService validationService;

    public WebulousDataConversionService(String inputData, OntologyConfiguration configuration){

        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(jsonFactory);
        try {
            allData = mapper.readTree(inputData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        populousModel = new PropertyBasedPopulousModel();
        validationService = new OntologyTermValidationService(allData.get("configuration").get("sourceOntology").getTextValue().toLowerCase(), configuration);

        statusMessage= "Thank you for the input, it has been processed successfully";
    }

    public String getStatusMessage(){
         return statusMessage;
    }


    public SimpleDataCollection getData(){
        return dataCollection;
    }

    public PopulousModel getConfigInformation(){
        return populousModel;
    }

    public void processInput(){

        JsonNode configuration = allData.get("configuration");

        System.out.println("Processing configuration data");

        processConfiguration(configuration);

        JsonNode data = allData.get("data");
        processData(data);

    }

/**
 * This method processes the various parts of the configuration section
 * */
    public void processConfiguration(JsonNode configuration){

        String sourceOntology = configuration.get("sourceOntology").getTextValue().toLowerCase();
        Properties ontologyConfig = new Properties();

        try {
            ontologyConfig.load(getClass().getClassLoader().getResource(sourceOntology+".properties").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            System.out.println("There is no config file for ontology " + sourceOntology);
        }

        setOntologyInformtaion(ontologyConfig);

        JsonNode patterns = configuration.get("opplPatterns");

        setOPPLPatterns(ontologyConfig, patterns);

        JsonNode restrictions = configuration.get("dataRestrictions");

        processRestrictions(restrictions);
    }

    public void processRestrictions(JsonNode restrictions) {

        Map<Integer, PopulousDataRestriction> dataRestrictions = new HashMap<Integer, PopulousDataRestriction>();

        if(restrictions.get(0).isTextual() && restrictions.get(0).getTextValue().equals("No restrictions available")){
            System.out.println("No data restrictions provided");
        }

        else{
            System.out.println("There are some data restrictions");
            for(JsonNode restriction : restrictions){
                PopulousDataRestriction dataRestriction = new PopulousDataRestriction();
                int index = restriction.get("restrictionIndex").getIntValue();
                dataRestriction.setColumnIndex(index);

                String ontology = restriction.get("restrictionOntology").getTextValue();
//                dataRestriction.setRestrictionOntology(ontology);

                String type = restriction.get("restrictionType").getTextValue();
//                dataRestriction.setRestrictionType(type);

                String name = restriction.get("restrictionName").getTextValue();
                dataRestriction.setRestrictionName(name);

                String uri = restriction.get("restrictionURI").getTextValue();
//                dataRestriction.setRestrictionParentURI(uri);

                dataRestrictions.put(index, dataRestriction);
            }
        }

        populousModel.setRestrictions(dataRestrictions);
    }

    public void setOPPLPatterns(Properties ontologyConfig, JsonNode patterns) {
        List<String> patternNames = new ArrayList<String>();
        LinkedHashMap<Integer, String> variableMap = new LinkedHashMap<Integer, String>();
        Set<String> variables = new HashSet<String>();


        for(JsonNode pattern : patterns){
            String name = pattern.get("name").getTextValue();
            patternNames.add(name);

            JsonNode vars = pattern.get("variables");


            for(JsonNode var : vars){
                String variable = var.get("variable").getTextValue();

                if(!variables.contains(variable)){
                    variables.add(variable);
                }

                if(var.get("columnIndex").isInt()){
                    int columnIndex = var.get("columnIndex").getIntValue();
                    if(variableMap.get(columnIndex) == null){
                        variableMap.put(columnIndex, variable);
                    }
                    else if(!variableMap.get(columnIndex).equals(variable)){
                        System.out.println("Each column can only represent one variable!");
                    }
                }
            }
        }

        populousModel.setVariables(variables);
        populousModel.setVariableMapper(variableMap);

        int patternCount = 1;
        String patName = ontologyConfig.getProperty("opplPattern.name."+patternCount);
        Set<PopulousPattern> opplPatterns = new HashSet<PopulousPattern>();

        while(patName != null){
            if(patternNames.contains(patName)){
                String fullPattern = ontologyConfig.getProperty("opplPattern.fullpattern."+patternCount);
                opplPatterns.add(new PopulousPattern(patName, fullPattern));
            }

            patternCount++;
            patName = ontologyConfig.getProperty("opplPattern.name."+patternCount);
        }

        populousModel.setPopulousPattern(opplPatterns);

    }

    public void setOntologyInformtaion(Properties ontologyConfig) {
        populousModel.setSourceOntologyIRI(IRI.create(ontologyConfig.getProperty("sourceOntology.iri")));

        populousModel.setSourceOntologyPhysicalIRI(IRI.create(ontologyConfig.getProperty("sourceOntology.fileLocation")));

        populousModel.setSourceOntologyOutputLocation(ontologyConfig.getProperty("sourceOntology.outputLocation"));

        Set<IRI> imports = new HashSet<IRI>();
        int importCount = 1;
        String iri = ontologyConfig.getProperty("importOntology.iri."+importCount);

        while(iri != null){
            imports.add(IRI.create(iri));

            importCount++;
            iri = ontologyConfig.getProperty("importOntology.iri."+importCount);
        }

        populousModel.setImportedOntologies(imports);

    }

 /**
  * This method processes the data part of the JSON and transforms it into a DataCollection of DataAttributes (columns) and DataObjects (rows)
  * */
     public void processData(JsonNode data){

        int rowNum = data.size();
        int colNum = data.get(0).size();

        dataCollection = new SimpleDataCollection(rowNum, colNum);
        LinkedHashMap<Integer, String> variables = populousModel.getVariableMapper();

 //go through the data row by row
        for(JsonNode row : data){
            SimpleDataObject dataRow = new SimpleDataObject(row.size());
            int ind = 0;
//go through the row cell by cell
            for(JsonNode cell : row){
//for each cell, check if there is an existing column attribute
                SimpleDataAttribute column;
                if(dataCollection.getDataAttributes().size() <= ind || dataCollection.getDataAttributes().get(ind) == null){
                    column = new SimpleDataAttribute(ind);
                    dataCollection.addDataAttribute(column);

                    if(variables.get(ind) != null){
                         column.addVariable(variables.get(ind));
                    }
                }
                else {
                    column = (SimpleDataAttribute) dataCollection.getDataAttributes().get(ind);
                }
                String value = cell.get("value").getTextValue();
                PopulousDataRestriction restriction = null;
                String restrictionName = null;
                Integer restrictIndex = null;
                if(cell.get("restrictionIndex").isInt()){
                    restrictIndex = cell.get("restrictionIndex").getIntValue();
                    restriction = populousModel.getRestrictions().get(restrictIndex);
                    if(restriction != null){
                        restrictionName = restriction.getRestrictionName();
//if this kind of restriction hasn't been applied yet in this column, get all the permissible terms from the ontology
                        if(!column.getDataRestrictions().contains(restriction)){
                            column.addDataRestriction(restriction);

                            List<Term> allTerms = validationService.getPermissibleTerms(restriction.getRestrictionParentURI(), restriction.getRestrictionType().toString(), restriction.getRestrictionOntology());

                            for(Term term : allTerms){
                                if(!column.getPermissibleTerms().contains(term)){
                                    column.addTerm(term);
                                }
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


