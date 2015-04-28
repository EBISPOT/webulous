package uk.ac.ebi.spot.webulous.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Simon Jupp
 * @date 28/04/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // dynamically generated docs pages
        registry.addViewController("docs").setViewName("docs-template");

    }
}