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
    RestrictionRunRepository restrictionRunRepository;

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
            RestrictionRunDocument runDocument = queueTemplate(template);
            if (force) {
                service.run(runDocument);
            }
            return runDocument;
        }
        return null;
    }






    public PopulousTemplateDocument save(PopulousTemplateDocument template) {
        PopulousTemplateDocument templateDocument =  templateRepository.save(template);
        // queue it up for running if it has restrictions
        queueTemplate(templateDocument);
        return templateDocument;
    }

    private RestrictionRunDocument queueTemplate(PopulousTemplateDocument templateDocument) {
        RestrictionRunDocument restrictionRunDocument = null;
        if (!templateDocument.getDataRestrictions().isEmpty()) {

            restrictionRunDocument = restrictionRunRepository.findByTemplateIdAndStatus(templateDocument.getId(), Status.QUEUED);
            if (restrictionRunDocument == null) {

                restrictionRunDocument = new RestrictionRunDocument();
                restrictionRunDocument.setTemplateId(templateDocument.getId());
                restrictionRunDocument.setTemplateName(templateDocument.getDescription());
                restrictionRunDocument.setStatus(Status.QUEUED);
                restrictionRunDocument.setLastUpdate(new Date());
                restrictionRunDocument.setMessage("Restrictions for this template are queued and waiting...");

                restrictionRunDocument = restrictionRunRepository.save(restrictionRunDocument);
            }
        }
        return restrictionRunDocument;
    }

    public void remove(PopulousTemplateDocument populousTemplateDocument) {
        templateRepository.delete(populousTemplateDocument.getId());
    }
}
