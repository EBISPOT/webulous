package uk.ac.ebi.spot.webulous.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * @author Simon Jupp
 * @date 12/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Document(collection = "templates")
public class PopulousTemplateDocument implements PopulousTemplate {

    @Id
    private String id;
    private String description;
    private boolean isActive;
    private int defaultNumberOfRows;
    private String urigenserver;
    private String activeOntology;
    private Set<String> ontologyImports;
    private Set<PopulousDataRestriction> dataRestrictions;
    private Set<PopulousPattern> patterns;


}
