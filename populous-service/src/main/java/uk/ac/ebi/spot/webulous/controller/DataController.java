package uk.ac.ebi.spot.webulous.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.webulous.model.DataSubmission;
import uk.ac.ebi.spot.webulous.model.DataConversionRunDocument;
import uk.ac.ebi.spot.webulous.model.RestrictionRunDocument;
import uk.ac.ebi.spot.webulous.service.DataConversionService;

import java.util.List;

/**
 * @author Simon Jupp
 * @date 25/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Controller
@RequestMapping("/submissions")
public class DataController {


    @Autowired
    private DataConversionService dataConversionService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public Logger getLog() {
        return  logger;
    }

    @ModelAttribute("all_submission_runs")
    public List<DataConversionRunDocument> getDataRuns() {
        return dataConversionService.findAll(new Sort(new Sort.Order(Sort.Direction.DESC, "lastUpdated")));
    }

    @RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String showDataRuns() {
        return "submissions";
    }

    @RequestMapping(value = "/{templateId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getRunsByTemplateId(Model model, @PathVariable String templateId, final RedirectAttributes redirectAttributes) {
        List<DataConversionRunDocument> runs = dataConversionService.findByTemplateId(templateId);
        model.addAttribute("all_submission_runs", runs);
        return "submissions";
    }

    @RequestMapping(value = "/{runid}/delete", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String deleteRun(Model model,@PathVariable String runid,final RedirectAttributes redirectAttributes) {
        dataConversionService.deleteRun(runid);
        redirectAttributes.addFlashAttribute("message", "Removed run with id: " + runid);
        return "redirect:/submissions";
    }



    @RequestMapping(value = "/{runid}/run", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String refreshTemplate(
            @PathVariable String runid,
            final RedirectAttributes redirectAttributes) {

        DataConversionRunDocument runDocument = dataConversionService.findOne (runid);

        try {
            dataConversionService.runDataConversion(runDocument);
            redirectAttributes.addFlashAttribute("message", "Oppl run complete complete for : " + runDocument.getId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Oppl run failed for : " + runDocument.getId() + ", message: " + e.getMessage());
        }
        return "redirect:/submissions";
    }


    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody SubmissionResponse submitData (@RequestBody DataSubmission submission) {

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
