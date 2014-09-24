package uk.ac.ebi.fgpt.populous.model;

/**
 * Created by dwelter on 04/07/14.
 */
public class SimpleDataField implements DataField{

    private String value, type;//, typeIRI;
    private int columnIndex;
    private Integer restrictionIndex;

    public SimpleDataField(String value, String type, Integer restriction, int ind){
        this.value = value;
        this.type = type;
   //     this.typeIRI = typeIRI;
        this.columnIndex = ind;
        this.restrictionIndex = restriction;
    }


    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getType() {
        return type;
    }

//    @Override
//    public String getIRI() {
//        return typeIRI;
//    }

    @Override
    public int getIndex(){
        return columnIndex;
    }

    public Integer getRestrictionIndex(){
        return restrictionIndex;
    }
}
