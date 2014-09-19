package uk.ac.ebi.fgpt.populous.model;

/**
 * Created by dwelter on 11/07/14.
 */
public class PopulousDataRestriction {

    private int restrictionIndex;
    private String restrictionParentName;
    private String restrictionParentURI;
    private String restrictionType;
    private String restrictionOntology;
//    private String opplVariable;

    public int getRestrictionIndex() {
        return restrictionIndex;
    }

    public void setRestrictionIndex(int restrictionIndex) {
        this.restrictionIndex = restrictionIndex;
    }

    public String getRestrictionOntology(){
        return restrictionOntology;
    }

    public void setRestrictionOntology(String ontology){
        restrictionOntology = ontology;
    }

    public String getRestrictionParentName() {
        return restrictionParentName;
    }

    public void setRestrictionParentName(String parentName) {
        this.restrictionParentName = parentName;
    }

    public String getRestrictionParentURI() {
        return restrictionParentURI;
    }

    public void setRestrictionParentURI(String restrictionParentURI) {
        this.restrictionParentURI = restrictionParentURI;
    }

    public String getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(String restrictionType) {
        this.restrictionType = restrictionType;
    }

    //restrictions are now independent of the actual OPPL variables
//    public String getOpplVariable() {
//        return opplVariable;
//    }
//
//    public void setOpplVariable(String opplVariable) {
//        this.opplVariable = opplVariable;
//    }
}
