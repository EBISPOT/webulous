package uk.ac.ebi.fgpt.populous.model;

/**
 * Created by dwelter on 11/07/14.
 */
public class PopulousDataRestriction {

    private int columnIndex;
    private String typeName;
    private String restrictionParentURI;
    private String restrictionType;
    private String opplVariable;

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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

    public String getOpplVariable() {
        return opplVariable;
    }

    public void setOpplVariable(String opplVariable) {
        this.opplVariable = opplVariable;
    }
}
