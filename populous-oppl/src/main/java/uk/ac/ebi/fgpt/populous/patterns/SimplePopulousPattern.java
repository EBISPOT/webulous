package uk.ac.ebi.fgpt.populous.patterns;

import uk.ac.ebi.fgpt.populous.model.PopulousPattern;

import java.util.UUID;

/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Simon Jupp<br>
 * Date: Oct 7, 2010<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class SimplePopulousPattern implements PopulousPattern{

    private String patternID;
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

    public String getPatternID() {

        return patternID;
    }

    public void setPatternID(String patternID) {
        this.patternID = patternID;
    }

    public SimplePopulousPattern(String patternName, String patternValue) {
        this.patternID = UUID.randomUUID().toString();
        this.patternName = patternName;
        this.patternValue = patternValue;
    }

    public SimplePopulousPattern() {
        this.patternID = UUID.randomUUID().toString();

    }

}
