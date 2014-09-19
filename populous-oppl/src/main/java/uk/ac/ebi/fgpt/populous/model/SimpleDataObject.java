package uk.ac.ebi.fgpt.populous.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dwelter on 04/07/14.
 */
public class SimpleDataObject implements DataObject{

    private int dataFieldCount;
    private List<DataField> dataFields;


    public SimpleDataObject(int fieldCount){
        this.dataFieldCount = fieldCount;
        this.dataFields = new ArrayList<DataField>();
    }

    @Override
    public List<DataField> getDataFields() {
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
