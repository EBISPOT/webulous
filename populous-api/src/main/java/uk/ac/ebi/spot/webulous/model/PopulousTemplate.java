package uk.ac.ebi.spot.webulous.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Simon Jupp
 * @date 16/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public interface PopulousTemplate {

    String getId();
    Collection<String> getAdminEmailAddresses();
    String getDescription();
    boolean isActive();
    int getDefaultNumberOfRows();
    String getUrigenserver();
    String getActiveOntology();
    Set<String> getOntologyImports();
    List<PopulousDataRestriction> getDataRestrictions();
    List<PopulousPattern> getPatterns();
}
