package uk.ac.ebi.spot.webulous.model;

import java.util.List;

/**
 * Created by dwelter on 02/07/14.
 *
 *
 * A DataAttribute represents a single kind of data from a DataCollection, eg a column from a spreadsheet. A DataAttribute can be limited to a defined set of values, eg only subclasses of type "disease"
 *
 */
public interface DataAttribute {


    List<Term> getPermissibleTerms();

//    String getTypeLabel();
//
//    String getTypeURI();
//
//    String getTypeRestriction();

    int getIndex();


    List<PopulousDataRestriction> getDataRestrictions();
}
