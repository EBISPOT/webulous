package uk.ac.ebi.spot.webulous.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.ebi.spot.webulous.model.PopulousTemplateDocument;
import uk.ac.ebi.spot.webulous.service.WebulousTemplateService;

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

//    public TemplateController() {
//    }


    @ModelAttribute("webulous_templates")
    public List<PopulousTemplateDocument> getTemplates() {
        return webulousTemplateService.findAll();
    }

    @RequestMapping("/")
    public String showAllTemplates() {
        return "webulous_templates";
    }

    @RequestMapping("/add")
    public String showNewTemplate() {
        return "new_template";
    }




}
