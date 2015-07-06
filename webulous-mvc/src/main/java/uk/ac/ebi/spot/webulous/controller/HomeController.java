package uk.ac.ebi.spot.webulous.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Simon Jupp
 * @date 01/04/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Controller
@RequestMapping("")
public class HomeController {
    @RequestMapping({"", "index"})
    public String showHome() {
        return "index";
    }

    @RequestMapping({"/contact"})
    public String showContact() {
        return "contact";
    }

}
