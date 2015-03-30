package uk.ac.ebi.spot.webulous.model;

import java.util.Map;

/**
 * Created by dwelter on 02/07/14.
 */
public class PopulousPattern {

    private String patternName;
    private String patternValue;

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

    public String getPatternValue() {
        return patternValue;
    }

    public void setPatternValue(String patternValue) {
        this.patternValue = patternValue;
    }


    public PopulousPattern(String patternName, String patternValue) {
        this.patternName = patternName;
        this.patternValue = patternValue;
    }

    public PopulousPattern() {

    }
}
