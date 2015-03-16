package uk.ac.ebi.spot.webulous;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Simon Jupp
 * @date 13/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@EnableAutoConfiguration
@Configuration
@ComponentScan
public class WebulousApp {
    public static void main(String[] args) {
        SpringApplication.run(WebulousApp.class);
    }
}