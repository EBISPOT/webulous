package uk.ac.ebi.fgpt.populous.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dwelter on 11/07/14.
 */
public class PopulousInput {

    private String sourceOntology;
    private List<String> importedOntologies;
    private EntityCreationStrategy strategy;
    private List<String> opplPatterns;
    private Map<Integer, PopulousDataRestriction> restrictions;
    private Map<String, Integer> opplVariables;

    public Map<String, Integer> getOpplVariables() {
        return opplVariables;
    }

    public void setOpplVariables(Map<String, Integer> opplVariables) {
        this.opplVariables = opplVariables;
    }


    private ArrayList<ArrayList<String>> data;

    public String getSourceOntology() {
        return sourceOntology;
    }

    public void setSourceOntology(String sourceOntology) {
        this.sourceOntology = sourceOntology;
    }

    public List<String> getImportedOntologies() {
        return importedOntologies;
    }

    public void setImportedOntologies(List<String> importedOntologies) {
        this.importedOntologies = importedOntologies;
    }

    public EntityCreationStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(EntityCreationStrategy strategy) {
        this.strategy = strategy;
    }

    public List<String> getOpplPatterns() {
        return opplPatterns;
    }

    public void setOpplPatterns(List<String> opplPatterns) {
        this.opplPatterns = opplPatterns;
    }

    public Map<Integer, PopulousDataRestriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Map<Integer, PopulousDataRestriction> restrictions) {
        this.restrictions = restrictions;
    }

    public ArrayList<ArrayList<String>> getData() {
        return data;
    }

    public void setData(ArrayList<ArrayList<String>> data) {
        this.data = data;
    }
}
