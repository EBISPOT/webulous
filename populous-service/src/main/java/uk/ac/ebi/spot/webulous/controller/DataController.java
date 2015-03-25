package uk.ac.ebi.spot.webulous.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ebi.spot.webulous.model.DataSubmission;
import uk.ac.ebi.spot.webulous.model.DataConversionRunDocument;
import uk.ac.ebi.spot.webulous.service.DataConversionService;

/**
 * @author Simon Jupp
 * @date 25/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class DataController {


    @Autowired
    private DataConversionService dataConversionService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public Logger getLog() {
        return  logger;
    }

    public @ResponseBody
    SubmissionResponse submitData (DataSubmission submission) {

        boolean success = true;
        String message ="";
        String runId = "";

        if (submission.getTemplateId() == null) {
            success = false;
            message = "You must submit a template identifier";

        }
        else if (submission.getData().length == 0) {
            success = false;
            message = "You must supply some data";
        }
        else {
            try {

                DataConversionRunDocument runDocument = dataConversionService.queueDataConversion(submission);

                runId = runDocument.getId();
                message = "Data submitted successfully, you will receive confirmation by e-mail shortly";

            } catch ( Exception e) {
                getLog().error("Error with data submission: " + e.getMessage(), e);
                success = false;
                message = "There was an error submitting your data: " + e.getMessage();
            }

        }
        return  new SubmissionResponse(runId, success, message);
    }

    public class SubmissionResponse {

        private String runId;
        private boolean success;
        private String message;

        public SubmissionResponse(String runId, boolean success, String message) {
            this.runId = runId;
            this.success = success;
            this.message = message;
        }

        public String getRunId() {
            return runId;
        }

        public void setRunId(String runId) {
            this.runId = runId;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
