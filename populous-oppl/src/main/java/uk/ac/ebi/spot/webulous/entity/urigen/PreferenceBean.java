package uk.ac.ebi.spot.webulous.entity.urigen;

import uk.ac.ebi.spot.webulous.model.AutoIDGenerator;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * @author Simon Jupp
 * @date 31/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class PreferenceBean  {

    private int preferenceId =-1;
    private String ontologyName;
    private AutoIDGenerator autoIDGenerator;
    private String ontologyUri;
    private String ontologyPhysicalUri;
    private String baseUri;
    private String separator;
    private String prefix;
    private String suffix;
    private int autoIdStart;
    private int autoIdEnd;
    private int autoIdDigitCount;
    private long lastIdInSequence;
    private boolean checkSource;
    private Date lastChecked;
    private String md5;

    private String autoIdGenerator;

    private boolean success = true;
    private String message;

    public PreferenceBean() {

    }
    public PreferenceBean(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAutoIdGenerator(String autoIdGenerator) {
        this.autoIdGenerator = autoIdGenerator;
    }

    public void setOntologyUri(String ontologyUri) {
        this.ontologyUri = ontologyUri;
    }

    public String getAutoIdGenerator() {
        return autoIdGenerator;
    }


    public boolean getCheckSource() {
        return checkSource;
    }

    public boolean statusOK() {
        return success;
    }

    public String conflictMessage() {
        return message;
    }

    public void setCheckSource(boolean checkSource) {
        this.checkSource = checkSource;
    }

    public int getPreferenceId() {
        return preferenceId;
    }

    public String getOntologyName() {
        return ontologyName;
    }

    public void setOntologyName(String ontologyname) {
        this.ontologyName = ontologyname;
    }

    public String getOntologyPhysicalUri() {
        return ontologyPhysicalUri;
    }

    public String getOntologyUri() {
        return ontologyUri;
    }

    public void setOntologyPhysicalUri(String ontologyPhysicalUri) {
        this.ontologyPhysicalUri = ontologyPhysicalUri;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getAutoIdStart() {
        return autoIdStart;
    }

    public void setAutoIdStart(int autoIdStart) {
        this.autoIdStart = autoIdStart;
    }

    public int getAutoIdEnd() {
        return autoIdEnd;
    }

    public void setAutoIdEnd(int autoIdDigitEnd) {
        this.autoIdEnd = autoIdDigitEnd;
    }

    public int getAutoIdDigitCount() {
        return autoIdDigitCount;
    }

    public void setAutoIdDigitCount(int autoIdDigitCount) {
        this.autoIdDigitCount = autoIdDigitCount;
    }

    public long getLastIdInSequence() {
        return lastIdInSequence;
    }

    public void setLastIdInSequence(long lastIdInSequence) {
        this.lastIdInSequence = lastIdInSequence;
    }

    @Override
    public String toString() {
        return "PreferenceBean{" +
                "preferenceId=" + preferenceId +
                ", ontologyName='" + ontologyName + '\'' +
                ", ontologyPhysicalUri='" + ontologyPhysicalUri + '\'' +
                ", baseUri='" + baseUri + '\'' +
                ", separator='" + separator + '\'' +
                ", prefix='" + prefix + '\'' +
                ", suffix='" + suffix + '\'' +
                ", autoIdStart=" + autoIdStart +
                ", autoIdEnd=" + autoIdEnd +
                ", autoIdDigitCount=" + autoIdDigitCount +
                ", lastIdInSequence=" + lastIdInSequence +
                ", checkSource=" + checkSource +
                ", autoIdGenerator='" + autoIdGenerator + '\'' +
                '}';
    }


    public void setPreferenceId(int id) {
        this.preferenceId = id;
    }


}
