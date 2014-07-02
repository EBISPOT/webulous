package uk.ac.ebi.fgpt.populous.model;

/**
 * Created by dwelter on 27/06/14.
 *
 * A DataField represents a single value from a data collection, eg a single cell in a spreadsheet. It has a type (eg "disease") and a value (eg "type II diabetes"), as well as possibly an IRI (optional).
 *
 *
 *
 */
public interface DataField {

    String getValue();

    String getType();

    String getIRI();
}
