package uk.ac.ebi.spot.webulous.service;

import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.webulous.entity.IterativeAutoIDGenerator;
import uk.ac.ebi.spot.webulous.entity.PseudoRandomAutoIDGenerator;
import uk.ac.ebi.spot.webulous.entity.SimpleEntityCreation;
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

    public void runDataConversion(DataConversionRunDocument runDocument) {

        PopulousTemplateDocument templateDocument = populousTemplateRepository.findOne(runDocument.getTemplateId());


        OpplPatternExecutionService patternExecutionService = new OpplPatternExecutionService();

        // todo urigen impl goes here
        SimpleEntityCreation entityCreation = new SimpleEntityCreation();
        entityCreation.setFragmentAutoGenerated(true);
        entityCreation.setDefaultBaseURI(templateDocument.getActiveOntology());
        entityCreation.setAutoIDGeneratorClass(PseudoRandomAutoIDGenerator.class);

        List<String> errorCollector = new ArrayList<String>();

        String filename = runDocument.getId();
        File outFile = new File(defaultOutputPath, filename + ".owl");

        try {
            OWLOntology ontology = patternExecutionService.executeOPPLPatterns(runDocument.getData(), templateDocument, entityCreation, errorCollector);
            OWLOntologyManager manager = ontology.getOWLOntologyManager();



            try {
                manager.saveOntology(ontology, new FileOutputStream(outFile));
            } catch (OWLOntologyStorageException e) {
                errorCollector.add(e.getMessage());
            } catch (FileNotFoundException e) {
                errorCollector.add(e.getMessage());
            }

        } catch (OWLOntologyCreationException e) {
            errorCollector.add(e.getMessage());
        }

        if (errorCollector.isEmpty())  {

            runDocument.setStatus(Status.COMPLETE);
            runDocument.setMessage("Run complete! File available at " + outFile.getPath());
            runDocument.setLastUpdated(new Date());

            // e-mail everyone



        } else {
            runDocument.setStatus(Status.FAILED);
            runDocument.setLastUpdated(new Date());
            runDocument.setMessage(Arrays.toString(errorCollector.toArray()));
        }

        mailService.sendEmailNotification(runDocument.getUserEmail(), templateDocument.getAdminEmailAddresses(), runDocument);
        dataConversionRunRepository.save(runDocument);


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
}
