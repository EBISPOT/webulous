package uk.ac.ebi.spot.webulous.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.webulous.model.*;
import uk.ac.ebi.spot.webulous.repository.PopulousTemplateRepository;
import uk.ac.ebi.spot.webulous.repository.RestrictionRunRepository;

import java.util.*;

/**
 * @author Simon Jupp
 * @date 16/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Service
public class WebulousTemplateService implements TemplateService<PopulousTemplateDocument, RestrictionRunDocument> {

    @Autowired
    private PopulousTemplateRepository templateRepository;

    @Autowired
    RestrictionService restrictionService;

    @Autowired
    RestrictionService service;
    public WebulousTemplateService() {
    }

    public List<PopulousTemplateDocument> findAll() {
        return this.templateRepository.findAll();
    }

    public List<PopulousTemplateDocument> findActive() {
        return this.templateRepository.findByActive(true);
    }

    public Page<PopulousTemplateDocument> findAll(Pageable pageable) {
        return this.templateRepository.findAll(pageable);
    }

    @Override
    public PopulousTemplateDocument findOne(String templateId) {
        return this.templateRepository.findOne(templateId);
    }

    @Override
    public RestrictionRunDocument refresh(String templateId, boolean force) {

        if (!StringUtils.isBlank(templateId)) {
            PopulousTemplateDocument template = templateRepository.findOne(templateId);

            if (template == null) {
                throw new ResourceNotFoundException("Template doesn't exists for id" + templateId);
            }
            RestrictionRunDocument runDocument = restrictionService.queueTemplate(template);
            if (force) {
                service.run(runDocument);
            }
            return runDocument;
        }
        return null;
    }

    @Override
    public List<RestrictionRunDocument> refreshGroup(String groupName, boolean force) {

        List<RestrictionRunDocument> runs = new ArrayList<RestrictionRunDocument>();
        for (PopulousTemplateDocument doc : findByTemplateGroupName(groupName)) {
            runs.add(refresh(doc.getId(), force));
        }
        return runs;
    }

    public PopulousTemplateDocument save(PopulousTemplateDocument template) {
        PopulousTemplateDocument templateDocument =  templateRepository.save(template);
        // queue it up for running if it has restrictions
        restrictionService.queueTemplate(templateDocument);
        return templateDocument;
    }



    public void remove(PopulousTemplateDocument populousTemplateDocument) {
        templateRepository.delete(populousTemplateDocument.getId());
    }

    public List<PopulousTemplateDocument> findByTemplateGroupName(String groupName) {
        return templateRepository.findByTemplateGroupName(groupName);
    }
}
