package uk.ac.ebi.fgpt.populous.model;

import java.util.Collection;

/**
 * Created by dwelter on 26/06/14.
 *
 * A DataCollection is a
 *
 */
public interface DataCollection {

    int getDataItemCount();

    int getDataAttributeCount();

    Collection<DataAttribute> getDataAttributes();

    Collection<DataItem> getDataItems();
}
