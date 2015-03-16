package uk.ac.ebi.spot.webulous.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.webulous.model.PopulousTemplateDocument;
import uk.ac.ebi.spot.webulous.repository.PopulousTemplateRepository;

import java.util.List;

/**
 * @author Simon Jupp
 * @date 16/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Service
public class WebulousTemplateService implements TemplateService<PopulousTemplateDocument> {

    @Autowired
    private PopulousTemplateRepository templateRepository;

    public WebulousTemplateService() {
    }

    public List<PopulousTemplateDocument> findAll() {
        return this.templateRepository.findAll();
    }
}
