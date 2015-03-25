package uk.ac.ebi.spot.webulous.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author Simon Jupp
 * @date 24/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Document(collection = "opplrun")
public class RestrictionRunDocument {

    @Id
    private String id;
    private String templateId;
    private String templateName;
    private Date lastUpdate;
    private Status status;
    private String message;

    public RestrictionRunDocument() {
        this.status = Status.OK;
        this.message = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
