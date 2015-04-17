package uk.ac.ebi.spot.webulous.impl;

import uk.ac.ebi.spot.webulous.model.PopulousDataRestriction;
import uk.ac.ebi.spot.webulous.model.PopulousPattern;
import uk.ac.ebi.spot.webulous.model.PopulousTemplate;

import java.util.List;
import java.util.Set;

/**
 * @author Simon Jupp
 * @date 26/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class SimplePopulousTemplate implements PopulousTemplate {

    private String id;

    private String description;
    private String templateGroupName;

    private boolean active;

    private int defaultNumberOfRows = 100;
    private String urigenserver;
    private String activeOntology;
    private Set<String> ontologyImports;

    private List<PopulousDataRestriction> dataRestrictions;

    private List<PopulousPattern> patterns;

    private String adminEmailAddresses;

    @Override
    public String getTemplateGroupName() {
        return templateGroupName;
    }

    public void setTemplateGroupName(String templateGroupName) {
        this.templateGroupName = templateGroupName;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public int getDefaultNumberOfRows() {
        return defaultNumberOfRows;
    }

    public void setDefaultNumberOfRows(int defaultNumberOfRows) {
        this.defaultNumberOfRows = defaultNumberOfRows;
    }

    @Override
    public String getUrigenserver() {
        return urigenserver;
    }

    public void setUrigenserver(String urigenserver) {
        this.urigenserver = urigenserver;
    }

    @Override
    public String getActiveOntology() {
        return activeOntology;
    }

    public void setActiveOntology(String activeOntology) {
        this.activeOntology = activeOntology;
    }

    @Override
    public Set<String> getOntologyImports() {
        return ontologyImports;
    }

    public void setOntologyImports(Set<String> ontologyImports) {
        this.ontologyImports = ontologyImports;
    }

    @Override
    public List<PopulousDataRestriction> getDataRestrictions() {
        return dataRestrictions;
    }

    public void setDataRestrictions(List<PopulousDataRestriction> dataRestrictions) {
        this.dataRestrictions = dataRestrictions;
    }

    @Override
    public List<PopulousPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<PopulousPattern> patterns) {
        this.patterns = patterns;
    }

    @Override
    public String getAdminEmailAddresses() {
        return adminEmailAddresses;
    }

    public void setAdminEmailAddresses(String adminEmailAddresses) {
        this.adminEmailAddresses = adminEmailAddresses;
    }
}
