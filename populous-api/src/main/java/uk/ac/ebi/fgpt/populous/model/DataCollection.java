package uk.ac.ebi.fgpt.populous.model;

import java.util.Collection;

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

    Collection<DataAttribute> getDataAttributes();

    Collection<DataObject> getDataObjects();
}
