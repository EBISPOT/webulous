package uk.ac.ebi.fgpt.populous.model;

import java.util.Map;

/**
 * Created by dwelter on 02/07/14.
 *
 * An OntologyTermRange is a set of ontology terms to which a given DataAttribute is limited
 *
 *
 */
public interface OntologyTermRange {

    String getType();

    String getTypeIRI();

    Map<String, String> getOntologyTermRange();
}
