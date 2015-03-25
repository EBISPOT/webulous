package uk.ac.ebi.spot.webulous.model;

/**
 * @author Simon Jupp
 * @date 25/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class DataSubmission {

    private String templateId;
    private String reference;
    private String urigenApiKey;
    private String email;
    private String [][] data;

    public DataSubmission () {

    }

    public DataSubmission (String templateId, String email) {
        this.templateId = templateId;
        this.email = email;
        this.data = new String[0][0];
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getUrigenApiKey() {
        return urigenApiKey;
    }

    public void setUrigenApiKey(String urigenApiKey) {
        this.urigenApiKey = urigenApiKey;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String[][] getData() {
        return data;
    }

    public void setData(String[][] data) {
        this.data = data;
    }
}
