package uk.ac.ebi.spot.webulous;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import java.net.URI;

/**
 * @author Simon Jupp
 * @date 16/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class WebulousRestMVCConfirguation extends RepositoryRestMvcConfiguration {

    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.setBaseUri(URI.create("/api"));
    }

}
