package uk.ac.ebi.spot.webulous.model;

import java.util.List;

/**
 * Created by dwelter on 26/06/14.
 *
 * A DataCollection is a complete set of data submitted by a user, eg in the form of a spreadsheet. It has a number of DataAttributes and DataItems, each of which can have
 * several DataFields
 *
 */
public interface DataCollection {

    int getDataObjectCount();

    int getDataAttributeCount();

    List<DataAttribute> getDataAttributes();

    List<DataObject> getDataObjects();
}
