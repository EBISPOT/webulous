package uk.ac.ebi.spot.webulous.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.webulous.model.PopulousDataRestriction;
import uk.ac.ebi.spot.webulous.model.PopulousTemplateDocument;
import uk.ac.ebi.spot.webulous.model.RestrictionRunDocument;
import uk.ac.ebi.spot.webulous.model.Status;
import uk.ac.ebi.spot.webulous.service.RestrictionService;
import uk.ac.ebi.spot.webulous.service.WebulousTemplateService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 24/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Controller
@RequestMapping("/restrictions")
public class RestrictionController {

    @Value("${webulous.ui.readonly}")
    boolean readOnly = false;

    @Autowired
    private RestrictionService restrictionService;

    @ModelAttribute("all_restriction_runs")
    public List<RestrictionRunDocument> getRestrictionRuns() {
        return restrictionService.findAll(new Sort(new Sort.Order(Sort.Direction.DESC, "lastUpdated")));
    }

    @RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String showRestrictionRuns(Model model) {
        if (readOnly) {
            model.addAttribute("readonly", true);
        }
        return "restriction_runs";
    }

    @RequestMapping(value = "", params="templateId", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String showRestrictionRunsForTemplate(Model model, @RequestParam("templateId") String templateId) {
        if (readOnly) {
            model.addAttribute("readonly", true);
        }
        List<RestrictionRunDocument> runDocuments = restrictionService.findByTemplateId(templateId, new Sort(new Sort.Order(Sort.Direction.ASC, "lastUpdated")));
        model.addAttribute("all_restriction_runs", runDocuments);
        return "restriction_runs";
    }

    @RequestMapping(value = "/{runid}/delete", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String deleteRun(Model model,@PathVariable String runid,final RedirectAttributes redirectAttributes) {

        if (readOnly) {
            redirectAttributes.addFlashAttribute("error", "Can't delete, this is a read only version");
            return "redirect:/restrictions";
        }
        restrictionService.deleteRun(runid);
        redirectAttributes.addFlashAttribute("message", "Removed run with id: " + runid);
        return "redirect:/restrictions";
    }



    @RequestMapping(value = "/{runid}/run", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String refreshTemplate(
            @PathVariable String runid,
            final RedirectAttributes redirectAttributes) {

        if (readOnly) {
            redirectAttributes.addFlashAttribute("error", "Can't run, this is a read only version");
            return "redirect:/restrictions";
        }
        RestrictionRunDocument runDocument = restrictionService.findOne (runid);

        runDocument = restrictionService.run(runDocument);
        if (runDocument.getStatus().equals(Status.COMPLETE) ) {
            redirectAttributes.addFlashAttribute("message", "Refresh restriction values complete for : " + runDocument.getId());
        }
        else if (runDocument.getStatus().equals(Status.FAILED)) {
            redirectAttributes.addFlashAttribute("error", "Refresh restrictions failed for " + runDocument.getId() + ": " + runDocument.getMessage() );
        }
        return "redirect:/restrictions";
    }




}
