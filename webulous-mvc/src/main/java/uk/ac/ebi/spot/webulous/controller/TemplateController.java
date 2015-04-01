package uk.ac.ebi.spot.webulous.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.webulous.model.PopulousDataRestriction;
import uk.ac.ebi.spot.webulous.model.PopulousPattern;
import uk.ac.ebi.spot.webulous.model.PopulousTemplateDocument;
import uk.ac.ebi.spot.webulous.model.TemplateSummary;
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
    Collection<TemplateSummary> getTemplateSummary() {
        Collection<TemplateSummary> templateSummaries = new HashSet<TemplateSummary>();
        for (PopulousTemplateDocument document : webulousTemplateService.findActive()) {
            templateSummaries.add(new TemplateSummary(document.getId(), document.getDescription()));
        }
        return templateSummaries;
    }

    @RequestMapping("")
    public String showAllTemplates() {
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

        populousTemplateDocument = webulousTemplateService.save(populousTemplateDocument);
        redirectAttributes.addFlashAttribute("message", "Successfully updated template: " + populousTemplateDocument.getId());

//        PopulousTemplateDocument templateDocument= webulousTemplateService.findOne(templateId);
//        model.addAttribute("document", templateDocument);
        return "redirect:/templates/" + populousTemplateDocument.getId();
    }

    @RequestMapping(value={"/new", "/{templateId}"}, params={"addDataRestriction"})
    public String addDataRestriction(final PopulousTemplateDocument populousTemplateDocument, Model model, final BindingResult bindingResult) {
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
    public String showNewTemplate(Model model) {
        PopulousTemplateDocument   populousTemplateDocument = new PopulousTemplateDocument();
        model.addAttribute("populousTemplateDocument", populousTemplateDocument);
        return "template";
    }

    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String saveTemplate(@Valid @ModelAttribute PopulousTemplateDocument populousTemplateDocument, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("populousTemplateDocument", populousTemplateDocument);
            return "template";
        }

        populousTemplateDocument = webulousTemplateService.save(populousTemplateDocument);
        redirectAttributes.addFlashAttribute("message", "Successfully created template: " + populousTemplateDocument.getId());
        return "redirect:/templates/" + populousTemplateDocument.getId();
    }




}
