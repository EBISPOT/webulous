package uk.ac.ebi.spot.webulous.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.webulous.model.*;
import uk.ac.ebi.spot.webulous.service.WebulousTemplateService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 13/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Controller
@RequestMapping("/templates")
public class TemplateController {

    @Value("${webulous.ui.readonly}")
    boolean readOnly = false;

    @Autowired
    private WebulousTemplateService webulousTemplateService;

    @ModelAttribute("all_templates")
    public List<PopulousTemplateDocument> getTemplates() {
        return webulousTemplateService.findAll();
    }

    @ModelAttribute("all_templates")
    public Page<PopulousTemplateDocument> getTemplates(Pageable pageable) {
        return webulousTemplateService.findAll(pageable);
    }


    @RequestMapping(value = "", produces="application/json", method = RequestMethod.GET)
    public @ResponseBody
    Collection<TemplateSummary> getTemplateSummary(@RequestParam(value = "groupName", required=false) String groupName) {
        Collection<TemplateSummary> templateSummaries = new HashSet<TemplateSummary>();
        for (PopulousTemplateDocument document : webulousTemplateService.findActive()) {
            if (StringUtils.isNoneEmpty(groupName)) {
                if (document.getTemplateGroupName() != null) {
                    if (document.getTemplateGroupName().toLowerCase().equals(groupName.toLowerCase())) {
                        templateSummaries.add(new TemplateSummary(document.getId(), document.getDescription()));
                    }
                }
            }
            else {
                templateSummaries.add(new TemplateSummary(document.getId(), document.getDescription()));
            }
        }
        return templateSummaries;
    }

    @RequestMapping("")
    public String showAllTemplates(Model model) {
        if (readOnly) {
            model.addAttribute("readonly", true);
        }
        return "templates";
    }

    @RequestMapping(value = "/{templateId}/restrictions/{restrictionIndex}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getRestrictionValuesByIndex(Model model, @PathVariable String templateId, @PathVariable String restrictionIndex) {

        PopulousTemplateDocument populousTemplateDocument= webulousTemplateService.findOne(templateId);

        PopulousDataRestriction populousDataRestriction = populousTemplateDocument.getDataRestrictions().get(Integer.parseInt(restrictionIndex));
        model.addAttribute("restrictionIndex", restrictionIndex);
        model.addAttribute("populousRestriction", populousDataRestriction);
        model.addAttribute("populousTemplateDocument", populousTemplateDocument);
        return "restriction_values";
    }

    @RequestMapping(value = "/{templateId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getTemplateById(Model model, @PathVariable String templateId, final RedirectAttributes redirectAttributes) {
        PopulousTemplateDocument populousTemplateDocument= webulousTemplateService.findOne(templateId);

        if (readOnly) {
            model.addAttribute("readonly", true);
        }

        if (populousTemplateDocument == null) {
            redirectAttributes.addFlashAttribute("error", "No template with id " + templateId);
            return "redirect:/templates";
        }
        model.addAttribute("populousTemplateDocument", populousTemplateDocument);
        return "template";
    }

    // save existing template

    @RequestMapping(value = "/{templateId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String saveTemplate(
            @ModelAttribute PopulousTemplateDocument populousTemplateDocument,
            Model model,
            BindingResult bindingResult,
            @PathVariable String templateId,
            final RedirectAttributes redirectAttributes) {

        if (readOnly) {
            redirectAttributes.addFlashAttribute("error", "Can't save this is a read only instance");
        }
        else {
            populousTemplateDocument = webulousTemplateService.save(populousTemplateDocument);
            redirectAttributes.addFlashAttribute("message", "Successfully updated template: " + populousTemplateDocument.getId());
        }

        return "redirect:/templates/" + populousTemplateDocument.getId();
    }


    @RequestMapping(value = "/{templateId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST, params={"delete"})
    public String deleteTemplate(
            @ModelAttribute PopulousTemplateDocument populousTemplateDocument,
            final RedirectAttributes redirectAttributes) {

        if (readOnly) {
            redirectAttributes.addFlashAttribute("error", "Can't save this is a read only instance");
            return "redirect:/templates";
        }

        webulousTemplateService.remove(populousTemplateDocument);
        redirectAttributes.addFlashAttribute("message", "Successfully removed template: " + populousTemplateDocument.getId());
        return "redirect:/templates";
    }

    @RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET, params={"refresh"})
    public String refreshAllTemplates(
            @RequestParam(value = "groupName", required=false) String groupName,
            final RedirectAttributes redirectAttributes) {

        if (readOnly) {
            redirectAttributes.addFlashAttribute("error", "Can't refresh, this is a read only instance");
            return "redirect:/templates";
        }

        if (StringUtils.isNoneEmpty(groupName)) {
            List<RestrictionRunDocument> runDocuments = webulousTemplateService.refreshGroup(groupName, false);
            if (runDocuments.size() == 0) {
                redirectAttributes.addFlashAttribute("error", "No group " + groupName);
            }
            else {
                redirectAttributes.addFlashAttribute("message", "Successfully sent restriction refresh request for group: " + groupName);
            }
            return "redirect:/restrictions";
        }
        else {
            for (PopulousTemplateDocument doc : webulousTemplateService.findAll()) {
                webulousTemplateService.refresh(doc.getId(), false);
            }
        }

        redirectAttributes.addFlashAttribute("message", "Successfully sent restriction refresh request");
        return "redirect:/restrictions";
    }

    @RequestMapping(value = "/{templateId}", produces = MediaType.TEXT_HTML_VALUE, method = {RequestMethod.GET, RequestMethod.POST}, params={"refresh"})
    public String refreshTemplate(
            @PathVariable String templateId,
            @RequestParam(value = "groupName", required=false) String groupName,
            final RedirectAttributes redirectAttributes) {

        if (readOnly) {
            redirectAttributes.addFlashAttribute("error", "Can't refresh, this is a read only instance");
            return "redirect:/templates";
        }
        webulousTemplateService.refresh(templateId, false);
        redirectAttributes.addFlashAttribute("message", "Successfully sent restriction refresh request");
        return "redirect:/restrictions";
    }

    @RequestMapping(value={"/new", "/{templateId}"}, params={"addDataRestriction"})
    public String addDataRestriction(final PopulousTemplateDocument populousTemplateDocument, Model model, final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {


        if (readOnly) {
            redirectAttributes.addFlashAttribute("error", "Can't create restrictions this is a read only version");
            return "redirect:/templates";
        }

        PopulousDataRestriction dataRestriction = new PopulousDataRestriction();
        dataRestriction.setColumnIndex(populousTemplateDocument.getDataRestrictions().size() + 1);
        populousTemplateDocument.getDataRestrictions().add(dataRestriction);
        model.addAttribute("populousTemplateDocument", populousTemplateDocument);

        return "template";
    }

    @RequestMapping(value={"/new", "/{templateId}"}, params={"removeDataRestriction"})
    public String removeDataRestriction(final PopulousTemplateDocument populousTemplateDocument, Model model, final BindingResult bindingResult, final HttpServletRequest req) {
        int rowId = Integer.valueOf(req.getParameter("removeDataRestriction"));
        populousTemplateDocument.getDataRestrictions().remove(rowId);
        model.addAttribute("populousTemplateDocument", populousTemplateDocument);

        return "template";
    }

    @RequestMapping(value={"/new", "/{templateId}"}, params={"addPattern"})
    public String addPattern(final PopulousTemplateDocument populousTemplateDocument, final BindingResult bindingResult, Model model) {
        populousTemplateDocument.getPatterns().add(new PopulousPattern());
        model.addAttribute("populousTemplateDocument", populousTemplateDocument);
        return "template";
    }

    @RequestMapping(value={"/new", "/{templateId}"}, params={"removePattern"})
    public String removePattern(final PopulousTemplateDocument populousTemplateDocument,final BindingResult bindingResult,  Model model, final HttpServletRequest req) {
        int rowId = Integer.valueOf(req.getParameter("removePattern"));
        populousTemplateDocument.getPatterns().remove(rowId);
        model.addAttribute("populousTemplateDocument", populousTemplateDocument);
        return "template";
    }

    // create new template methods

    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String showNewTemplate(Model model, final RedirectAttributes redirectAttributes) {

        if (readOnly) {
            redirectAttributes.addFlashAttribute("error", "Can't create new template this is a read only version");
            return "redirect:/templates";
        }
        PopulousTemplateDocument   populousTemplateDocument = new PopulousTemplateDocument();
        model.addAttribute("populousTemplateDocument", populousTemplateDocument);
        return "template";
    }

    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String saveTemplate(@Valid @ModelAttribute PopulousTemplateDocument populousTemplateDocument, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if (readOnly) {
            redirectAttributes.addFlashAttribute("error", "Can't create new template this is a read only version");
            return "all_templates";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("populousTemplateDocument", populousTemplateDocument);
            return "template";
        }

        populousTemplateDocument = webulousTemplateService.save(populousTemplateDocument);
        redirectAttributes.addFlashAttribute("message", "Successfully created template: " + populousTemplateDocument.getId());
        return "redirect:/templates/" + populousTemplateDocument.getId();
    }




}
