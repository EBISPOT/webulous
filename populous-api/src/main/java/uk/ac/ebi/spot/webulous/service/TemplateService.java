package uk.ac.ebi.spot.webulous.service;

import uk.ac.ebi.spot.webulous.model.PopulousTemplate;

import java.util.List;

/**
 * @author Simon Jupp
 * @date 16/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public interface TemplateService<T extends PopulousTemplate,Q> {

    List<T> findAll();

    T findOne(String templateId);

    Q refresh(String id, boolean force);
    List<Q> refreshGroup(String groupName, boolean force);

    List<T> findByTemplateGroupName(String groupName);
}
