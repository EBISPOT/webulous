package uk.ac.ebi.fgpt.populous.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dwelter on 04/07/14.
 */
public class SimpleDataObject implements DataObject{

    private int dataFieldCount;
    private Collection<DataField> dataFields;


    public SimpleDataObject(int fieldCount){
        this.dataFieldCount = fieldCount;
        this.dataFields = new ArrayList<DataField>();
    }

    @Override
    public Collection<DataField> getDataFields() {
        return dataFields;
    }

    @Override
    public int getDataFieldCount() {
        return dataFieldCount;
    }

    public void addDataField(DataField field){
        dataFields.add(field);
    }
}
