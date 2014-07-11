package uk.ac.ebi.fgpt.populous.entity;

import uk.ac.ebi.fgpt.populous.model.EntityCreation;
import uk.ac.ebi.fgpt.populous.model.LabelDescriptor;

import java.net.URI;

/**
 * Author: Simon Jupp<br>
 * Date: Jan 4, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class CustomLabelDescriptor implements LabelDescriptor {


    public String getLanguage(EntityCreation entityPrefs) {
        return entityPrefs.getNameLabelLang();
    }


    public URI getURI(EntityCreation entityPrefs) {
        return entityPrefs.getNameLabelURI();
    }
}
