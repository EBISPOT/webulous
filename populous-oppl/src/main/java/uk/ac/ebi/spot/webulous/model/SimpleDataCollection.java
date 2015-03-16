package uk.ac.ebi.spot.webulous.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dwelter on 04/07/14.
 */
public class SimpleDataCollection implements DataCollection {

    private int dataObjectCount, dataAttributeCount;

    private List<DataAttribute> dataAttributes;
    private List<DataObject> dataObjects;


    public SimpleDataCollection(int objectCount, int attributeCount){
        this.dataAttributeCount = attributeCount;
        this.dataObjectCount = objectCount;
        dataAttributes = new ArrayList<DataAttribute>();
        dataObjects = new ArrayList<DataObject>();

    }


    public void addDataAttribute(SimpleDataAttribute attribute) {
        dataAttributes.add(attribute);
    }

    public void addDataObject(SimpleDataObject object) {
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
    public List<DataAttribute> getDataAttributes() {
        return dataAttributes;
    }

    @Override
    public List<DataObject> getDataObjects() {
        return dataObjects;
    }
}
