package uk.ac.ebi.fgpt.populous.model;

import java.util.ArrayList;

/**
 * Created by dwelter on 11/07/14.
 */
public class PopulousInput {

    private String sourceOntology;
    private ArrayList<String> importedOntologies;
    private EntityCreationStrategy strategy;
    private ArrayList<String> opplPatterns;
    private ArrayList<PopulousDataRestriction> restrictions;

    private ArrayList<ArrayList<String>> data;

    public String getSourceOntology() {
        return sourceOntology;
    }

    public void setSourceOntology(String sourceOntology) {
        this.sourceOntology = sourceOntology;
    }

    public ArrayList<String> getImportedOntologies() {
        return importedOntologies;
    }

    public void setImportedOntologies(ArrayList<String> importedOntologies) {
        this.importedOntologies = importedOntologies;
    }

    public EntityCreationStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(EntityCreationStrategy strategy) {
        this.strategy = strategy;
    }

    public ArrayList<String> getOpplPatterns() {
        return opplPatterns;
    }

    public void setOpplPatterns(ArrayList<String> opplPatterns) {
        this.opplPatterns = opplPatterns;
    }

    public ArrayList<PopulousDataRestriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(ArrayList<PopulousDataRestriction> restrictions) {
        this.restrictions = restrictions;
    }

    public ArrayList<ArrayList<String>> getData() {
        return data;
    }

    public void setData(ArrayList<ArrayList<String>> data) {
        this.data = data;
    }
}
