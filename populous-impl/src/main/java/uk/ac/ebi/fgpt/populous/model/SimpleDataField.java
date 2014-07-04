package uk.ac.ebi.fgpt.populous.model;

/**
 * Created by dwelter on 04/07/14.
 */
public class SimpleDataField implements DataField{

    private String value, type, typeIRI;

    public SimpleDataField(String value, String type, String typeIRI){
        this.value = value;
        this.type = type;
        this.typeIRI = typeIRI;
    }


    @Override
    public String getValue() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getIRI() {
        return null;
    }
}
