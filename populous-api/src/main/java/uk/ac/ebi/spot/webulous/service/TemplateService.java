package uk.ac.ebi.spot.webulous.service;

import uk.ac.ebi.spot.webulous.model.PopulousTemplate;

import java.util.List;

/**
 * @author Simon Jupp
 * @date 16/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public interface TemplateService<T extends PopulousTemplate> {

    public List<T> findAll();

}
