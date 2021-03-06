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

    private String templateGroupName;

    private boolean active;

    private int defaultNumberOfRows = 100;
    private String urigenserver;
    private String activeOntology;
    private Set<String> ontologyImports;

    @NotEmpty(message = "Please enter at least one data restriction")
    private List<PopulousDataRestriction> dataRestrictions;

    private List<PopulousPattern> patterns;

    @NotEmpty(message = "Please enter at least one admin e-mail address")
    private String adminEmailAddresses;

    @Override
    public String getTemplateGroupName() {
        return templateGroupName;
    }

    public void setTemplateGroupName(String templateGroupName) {
        this.templateGroupName = templateGroupName;
    }

    public PopulousTemplateDocument() {
        this.active = true;
        this.dataRestrictions = new ArrayList<PopulousDataRestriction>();
        this.patterns = new ArrayList<PopulousPattern>();
    }

    public String getAdminEmailAddresses() {
        return adminEmailAddresses;
    }

    public void setAdminEmailAddresses(String adminEmailAddresses) {
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
