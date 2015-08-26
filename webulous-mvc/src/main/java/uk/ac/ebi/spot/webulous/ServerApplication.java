package uk.ac.ebi.spot.webulous;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Simon Jupp
 * @date 31/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@SpringBootApplication
@EnableScheduling
public class ServerApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        System.setProperty("entityExpansionLimit", "1000000000");
        return application.sources(ServerApplication.class);
    }

    public static void main(String[] args) {
        System.setProperty("entityExpansionLimit", "1000000000");
        SpringApplication.run(ServerApplication.class, args);
    }
}
