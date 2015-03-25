package uk.ac.ebi.spot.webulous.service;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.ac.ebi.fgpt.OntologyDAO;
import uk.ac.ebi.fgpt.OntologyDAOImpl;
import uk.ac.ebi.spot.webulous.model.*;
import uk.ac.ebi.spot.webulous.repository.PopulousTemplateRepository;
import uk.ac.ebi.spot.webulous.repository.RestrictionRunRepository;

import java.util.*;

/**
 * @author Simon Jupp
 * @date 24/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Service
public class RestrictionService {

    @Autowired
    private PopulousTemplateRepository templateRepository;

    @Autowired
    RestrictionRunRepository restrictionRunRepository;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public Logger getLog() {
        return  logger;
    }

    public RestrictionService() {

    }

    public PopulousTemplateDocument getTemplateDocumentById (String templateId) {
        return templateRepository.findOne(templateId);
    }

    public List<RestrictionRunDocument> findByTemplateId(String templateId, Sort sort) {
       return restrictionRunRepository.findByTemplateId(templateId, sort);
    }

    public List<RestrictionRunDocument> findAll(Sort sort) {
        return restrictionRunRepository.findAll(sort);
    }

    // this methid checks for restriction creations that are queued to be updated
    public void runAllQueued() {

        getLog().trace("Checking for restrictions that are queued");
        for (RestrictionRunDocument restrictionRunDocument : restrictionRunRepository.findByStatus(Status.QUEUED)) {

            run(restrictionRunDocument);


        }
    }

    public void run(RestrictionRunDocument restrictionRunDocument) {
        // set the status and update
        restrictionRunDocument.setStatus(Status.UPDATING);
        restrictionRunDocument.setLastUpdate(new Date());
        restrictionRunRepository.save(restrictionRunDocument);


        String templateId = restrictionRunDocument.getTemplateId();
        PopulousTemplateDocument populousTemplateDocument = templateRepository.findOne(templateId);

        if (populousTemplateDocument == null) {
            getLog().error("Found a run that reference a missing template with id " + templateId);
            restrictionRunDocument.setStatus(Status.FAILED);
            restrictionRunDocument.setMessage("Failed to run as no template found with id " + templateId);
            restrictionRunDocument.setLastUpdate(new Date());
        } else {

            Collection<IRI> importIris = new HashSet<IRI>();

            for (String iri : populousTemplateDocument.getOntologyImports()) {
                importIris.add(IRI.create(iri));
            }

            try {
                OntologyDAO dao = new OntologyDAOImpl(importIris) {
                    @Override
                    public OWLReasoner getOWLReasoner(OWLOntology owlOntology) {
                        return new Reasoner(owlOntology);
                    }
                };

                for (PopulousDataRestriction restriction : populousTemplateDocument.getDataRestrictions()) {
                    RestrictionType type = restriction.getRestrictionType();
                    String[][] values = new String [0][0];
                    List<OWLClass> classes = new ArrayList<OWLClass>();
                    if (type.equals(RestrictionType.DESCENDANTS)) {
                        classes = new ArrayList<OWLClass>(dao.getDescendantClasses(restriction.getClassExpression()));

                    }
                    else if (type.equals(RestrictionType.SUBCLASS)) {
                        classes = new ArrayList<OWLClass>(dao.getSubclasses(restriction.getClassExpression()));
                    }
                    if (!classes.isEmpty()) {
                        values = createValuesArray(classes, dao);
                        restriction.setValues(values);
                    }
                }

                templateRepository.save(populousTemplateDocument);
                restrictionRunDocument.setStatus(Status.COMPLETE);
                restrictionRunDocument.setMessage("Run completed!");

            } catch (Exception e) {
                getLog().error(e.getMessage(), e);
                restrictionRunDocument.setStatus(Status.FAILED);
                restrictionRunDocument.setMessage(e.getMessage());
            } finally {
                restrictionRunRepository.save(restrictionRunDocument);
            }
        }
    }

    private String[][] createValuesArray(List<OWLClass> classes, OntologyDAO dao) {
        String [][] values;
        values = new String[classes.size()][2];
        for (int x = 0; x<classes.size(); x++) {
            values[x][0] = classes.get(x).getIRI().toString();
            values[x][1] = dao.getRendering(classes.get(x));
        }
        return values;

    }


    public void deleteRun(String runid) {
        restrictionRunRepository.delete(runid);
    }

    public RestrictionRunDocument findOne(String runid) {
        return restrictionRunRepository.findOne(runid);
    }
}
