package uk.ac.ebi.spot.webulous.model;

/**
 * @author Simon Jupp
 * @date 31/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class TemplateSummary {
    public String id;
    public String description;

    public TemplateSummary(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public TemplateSummary() {

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
