package uk.ac.ebi.spot.webulous.service;

import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.webulous.entity.PseudoRandomAutoIDGenerator;
import uk.ac.ebi.spot.webulous.entity.SimpleEntityCreation;
import uk.ac.ebi.spot.webulous.entity.UrigenEntityFactory;
import uk.ac.ebi.spot.webulous.model.DataSubmission;
import uk.ac.ebi.spot.webulous.model.DataConversionRunDocument;
import uk.ac.ebi.spot.webulous.model.PopulousTemplateDocument;
import uk.ac.ebi.spot.webulous.model.Status;
import uk.ac.ebi.spot.webulous.repository.DataConversionRunRepository;
import uk.ac.ebi.spot.webulous.repository.PopulousTemplateRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 25/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Service
public class DataConversionService {

    @Autowired
    DataConversionRunRepository dataConversionRunRepository;

    @Autowired
    PopulousTemplateRepository populousTemplateRepository;

    @Autowired
    MailService mailService;

    @Value("${webulous.oppl.output:}")
    String defaultOutputPath = "/tmp";

    @Value("${webulous.oppl.output.display:}")
    private String displayOutputPath;


    @Value("${webulous.sendemail:false}")
    private boolean sendEmail;

    public DataConversionRunDocument queueDataConversion(DataSubmission submission) {

        PopulousTemplateDocument templateDocument = populousTemplateRepository.findOne(submission.getTemplateId());
        if (templateDocument == null) {
            throw new ResourceNotFoundException("No template with that id exists on this server");
        }

        DataConversionRunDocument runDocument = new DataConversionRunDocument();
        runDocument.setTemplateId(templateDocument.getId());
        runDocument.setTemplateName(templateDocument.getDescription());
        runDocument.setStatus(Status.QUEUED);
        runDocument.setLastUpdated(new Date());
        runDocument.setData(submission.getData());
        runDocument.setMessage("Data submitted and queued");
        runDocument.setUserEmail(submission.getEmail());
        runDocument.setReference(submission.getReference());
        runDocument.setApiKey(submission.getUrigenApiKey());
        return  dataConversionRunRepository.save(runDocument);
    }

    public List<DataConversionRunDocument> getAllQueued () {
        return dataConversionRunRepository.findByStatus(Status.QUEUED);
    }

    public DataConversionRunDocument runDataConversion(DataConversionRunDocument runDocument) {

        runDocument.setStatus(Status.UPDATING);
        runDocument.setLastUpdated(new Date());
        runDocument.setMessage("OPPL running...");
        dataConversionRunRepository.save(runDocument);

        PopulousTemplateDocument templateDocument = populousTemplateRepository.findOne(runDocument.getTemplateId());


        OpplPatternExecutionService patternExecutionService = new OpplPatternExecutionService();

        List<String> errorCollector = new ArrayList<String>();

        String filename = runDocument.getId()  + ".owl";
        File outFile = new File(defaultOutputPath, filename);
        String ontologyIri = "http://www.ebi.ac.uk/webulous/" + filename;
        if (displayOutputPath != null) {
            ontologyIri = displayOutputPath + "/" + filename;
        }

        try {
            OWLOntology ontology = null;
            if (!StringUtils.isEmpty(templateDocument.getUrigenserver()) && !StringUtils.isEmpty(runDocument.getApiKey())) {

                UrigenEntityFactory entityFactory = new UrigenEntityFactory(
                        patternExecutionService.getOntologyManager(),
                        patternExecutionService.getActiveOntology(ontologyIri),
                        templateDocument.getUrigenserver(),
                        runDocument.getApiKey(),
                        templateDocument.getActiveOntology()
                );
                ontology = patternExecutionService.executeOPPLPatterns(ontologyIri, runDocument.getData(), templateDocument, entityFactory, errorCollector);
            } else {
                ontology = patternExecutionService.executeOPPLPatterns(ontologyIri, runDocument.getData(), templateDocument, errorCollector);
            }

            OWLOntologyManager manager = ontology.getOWLOntologyManager();
            manager.saveOntology(ontology, new FileOutputStream(outFile));

        } catch (Exception e) {
            errorCollector.add(e.getMessage());
        }

        if (errorCollector.isEmpty())  {

            runDocument.setStatus(Status.COMPLETE);
            runDocument.setMessage("Run complete! File available at " + displayOutputPath + "/" + filename);
            runDocument.setLastUpdated(new Date());


        } else {
            runDocument.setStatus(Status.FAILED);
            runDocument.setLastUpdated(new Date());
            runDocument.setMessage(Arrays.toString(errorCollector.toArray()));
        }

        if (sendEmail) {
            // e-mail everyone
            String subject = "Webulous run for " + runDocument.getTemplateName() + ": " + runDocument.getStatus();
            String message =  "Your data submitted to webulous template " + runDocument.getTemplateName() + " has completed with status: " + runDocument.getStatus() + "\n\n" +
                    runDocument.getMessage()  + "\n";

            mailService.sendEmailNotification(
                    runDocument.getUserEmail(),
                    templateDocument.getAdminEmailAddresses().split(","),
                    subject,
                    message);
        }
        return dataConversionRunRepository.save(runDocument);
    }

    public List<DataConversionRunDocument> findAll(Sort sort) {
        return dataConversionRunRepository.findAll(sort);
    }

    public List<DataConversionRunDocument> findByTemplateId(String templateId) {
        return dataConversionRunRepository.findByTemplateId(templateId);
    }

    public void deleteRun(String runid) {
        dataConversionRunRepository.delete(runid);
    }

    public DataConversionRunDocument findOne(String runid) {
        return dataConversionRunRepository.findOne(runid);
    }

    public List<DataConversionRunDocument> findByStatus(Status status) {
        return dataConversionRunRepository.findByStatus(status);
    }
}
