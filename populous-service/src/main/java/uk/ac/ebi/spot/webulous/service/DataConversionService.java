package uk.ac.ebi.spot.webulous.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.webulous.model.DataSubmission;
import uk.ac.ebi.spot.webulous.model.DataConversionRunDocument;
import uk.ac.ebi.spot.webulous.model.PopulousTemplateDocument;
import uk.ac.ebi.spot.webulous.model.Status;
import uk.ac.ebi.spot.webulous.repository.DataConversionRunRepository;
import uk.ac.ebi.spot.webulous.repository.PopulousTemplateRepository;

import java.util.Date;

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

    public void runDataConversion(DataConversionRunDocument runDocument) {



    }

}
