package uk.ac.ebi.fgpt.populous.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dwelter on 04/07/14.
 */
public class SimpleDataCollection implements DataCollection {

    private int dataObjectCount, dataAttributeCount;

    private Collection<DataAttribute> dataAttributes;
    private Collection<DataObject> dataObjects;


    public SimpleDataCollection(int objectCount, int attributeCount){
        this.dataAttributeCount = attributeCount;
        this.dataObjectCount = objectCount;
        dataAttributes = new ArrayList<DataAttribute>();
        dataObjects = new ArrayList<DataObject>();

    }


    public void addDataAttribute(DataAttribute attribute) {
        dataAttributes.add(attribute);
    }

    public void addDataObject(DataObject object) {
        dataObjects.add(object);
    }

    @Override
    public int getDataObjectCount() {
        return dataObjectCount;
    }

    @Override
    public int getDataAttributeCount() {
        return dataAttributeCount;
    }

    @Override
    public Collection<DataAttribute> getDataAttributes() {
        return dataAttributes;
    }

    @Override
    public Collection<DataObject> getDataObjects() {
        return dataObjects;
    }
}
