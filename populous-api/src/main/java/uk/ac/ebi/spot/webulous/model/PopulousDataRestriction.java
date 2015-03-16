package uk.ac.ebi.spot.webulous.model;

/**
 * Created by dwelter on 11/07/14.
 */
public class PopulousDataRestriction {

    private int columnIndex;
    private String restrictionName;
    private String classExpression;
    private RestrictionType restrictionType;
    private String defaultValue;
    private boolean multivalueField;
    private boolean isRequired;
    private String [][] values;
    private String restrictionParentURI;
    private String restrictionOntology;

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getRestrictionName() {
        return restrictionName;
    }

    public void setRestrictionName(String restrictionName) {
        this.restrictionName = restrictionName;
    }

    public String getClassExpression() {
        return classExpression;
    }

    public void setClassExpression(String classExpression) {
        this.classExpression = classExpression;
    }

    public RestrictionType getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(RestrictionType restrictionType) {
        this.restrictionType = restrictionType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isMultivalueField() {
        return multivalueField;
    }

    public void setMultivalueField(boolean multivalueField) {
        this.multivalueField = multivalueField;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public String[][] getValues() {
        return values;
    }

    public void setValues(String[][] values) {
        this.values = values;
    }

    public String getRestrictionParentURI() {
        return restrictionParentURI;
    }

    public String getRestrictionOntology() {
        return restrictionOntology;
    }
}
