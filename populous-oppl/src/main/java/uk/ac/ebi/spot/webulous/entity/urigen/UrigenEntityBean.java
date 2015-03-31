package uk.ac.ebi.spot.webulous.entity.urigen;

import java.util.Date;

/**
 * @author Simon Jupp
 * @date 31/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class UrigenEntityBean {

    private int Id;

    private String generatedUri;

    private String originalUri;

    private String shortForm;

    private String label;

    private int userId;

    private int preferencesId;

    private Date date;

    private String comment;

    private boolean statusOK;

    private String errorMessage;


    public UrigenEntityBean(String generatedUri, String originalUri, String shortForm, String label, int user, int preferences, Date date, String comment) {
        this.generatedUri = generatedUri;
        this.originalUri = originalUri;
        this.shortForm = shortForm;
        this.label = label;
        this.userId = user;
        this.preferencesId = preferences;
        this.date = date;
        this.comment = comment;

    }

    public UrigenEntityBean(boolean b, String s) {
        statusOK = b;
        errorMessage = s;
    }

    public boolean getStatusOK() {
        return statusOK;
    }

    public void setStatusOK(boolean statusOK) {
        this.statusOK = statusOK;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public UrigenEntityBean() {

    }

    public Date getDate() {
        return date;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public int getId() {
        return Id;
    }

    public String getGeneratedUri() {
        return generatedUri;
    }

    public String getOriginalUri() {
        return originalUri;
    }

    public String getShortForm() {
        return shortForm;
    }

    public String getLabel() {
        return label;
    }

    public String getComment() {
        return null;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "SimpleUrigenEntityImpl{" +
                "Id=" + Id +
                ", generatedUri='" + generatedUri + '\'' +
                ", originalUri='" + originalUri + '\'' +
                ", shortForm='" + shortForm + '\'' +
                ", label='" + label + '\'' +
                ", date=" + date +
                ", comment='" + comment + '\'' +
                '}';
    }

    public int getPreferencesId() {
        return preferencesId;
    }
}
