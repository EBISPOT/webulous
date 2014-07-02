package uk.ac.ebi.fgpt.populous.model;

import java.util.Collection;

/**
 * Created by dwelter on 27/06/14.
 *
 *
 * A DataItem is a single record to be processed by Populous, such as row in a spreadsheet. It contains a set of DataFieds.
 *
 *
 */
public interface DataItem {


    Collection<DataField> getDataFields();

    int getDataFieldCount();

}
