package uk.ac.ebi.spot.webulous.model;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

/**
 * @author Simon Jupp
 * @date 12/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Document(collection = "templates")
public class PopulousTemplateDocument implements PopulousTemplate {

    @Id
    private String id;

    @NotBlank(message = "Please enter a description")
    private String description;

    private boolean active;

    private int defaultNumberOfRows = 100;
    private String urigenserver;
    private String activeOntology;
    private Set<String> ontologyImports;

    @NotEmpty(message = "Please enter at least one data restriction")
    private List<PopulousDataRestriction> dataRestrictions;

    private List<PopulousPattern> patterns;

    private Collection<String> adminEmailAddresses;


    public PopulousTemplateDocument() {
        this.active = true;
        this.dataRestrictions = new ArrayList<PopulousDataRestriction>();
        this.patterns = new ArrayList<PopulousPattern>();
    }

    public Collection<String> getAdminEmailAddresses() {
        return adminEmailAddresses;
    }

    public void setAdminEmailAddresses(Collection<String> adminEmailAddresses) {
        this.adminEmailAddresses = adminEmailAddresses;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean isActive) {
        this.active = isActive;
    }

    public int getDefaultNumberOfRows() {
        return defaultNumberOfRows;
    }

    public void setDefaultNumberOfRows(int defaultNumberOfRows) {
        this.defaultNumberOfRows = defaultNumberOfRows;
    }

    public String getUrigenserver() {
        return urigenserver;
    }

    public void setUrigenserver(String urigenserver) {
        this.urigenserver = urigenserver;
    }

    public String getActiveOntology() {
        return activeOntology;
    }

    public void setActiveOntology(String activeOntology) {
        this.activeOntology = activeOntology;
    }

    public Set<String> getOntologyImports() {
        return ontologyImports;
    }

    public void setOntologyImports(Set<String> ontologyImports) {
        this.ontologyImports = ontologyImports;
    }

    public List<PopulousDataRestriction> getDataRestrictions() {
        Collections.sort(dataRestrictions, new Comparator<PopulousDataRestriction>() {
            @Override
            public int compare(PopulousDataRestriction o1, PopulousDataRestriction o2) {
                if (o1.getColumnIndex() == 0) {
                    return  1;
                }
                else if (o2.getColumnIndex() == 0) {
                    return  -1;
                }
                else if (o1.getColumnIndex() <= o2.getColumnIndex()) {
                    return -1;
                }
                return 1;
            }
        });
        return dataRestrictions;
    }

    public void setDataRestrictions(List<PopulousDataRestriction> dataRestrictions) {
        this.dataRestrictions = dataRestrictions;
    }

    public List<PopulousPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<PopulousPattern> patterns) {
        this.patterns = patterns;
    }
}
