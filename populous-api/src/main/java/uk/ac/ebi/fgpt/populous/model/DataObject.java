package uk.ac.ebi.fgpt.populous.model;

import java.util.List;

/**
 * Created by dwelter on 27/06/14.
 *
 *
 * A DataObject is a single record to be processed by Populous, such as row in a spreadsheet. It contains a set of DataFieds.
 *
 *
 */
public interface DataObject {


    List<DataField> getDataFields();

    int getDataFieldCount();

}
