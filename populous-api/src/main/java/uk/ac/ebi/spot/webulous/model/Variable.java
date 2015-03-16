package uk.ac.ebi.spot.webulous.model;

/**
 * @author Simon Jupp
 * @date 13/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class Variable {
    private String header;
    private String variableName;
    private int columnIndex;
    private String alternative;
    private boolean multivalue;
    private String type;
    private boolean required;
    private PopulousDataRestriction restriction;
    private String [][] values;

}
