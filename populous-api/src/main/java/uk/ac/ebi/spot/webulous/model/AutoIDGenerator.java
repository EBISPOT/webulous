package uk.ac.ebi.spot.webulous.model;

import org.semanticweb.owlapi.model.OWLEntity;
import uk.ac.ebi.spot.webulous.exception.AutoIDException;

/**
 * Author: Simon Jupp<br>
 * Date: Dec 20, 2010<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public interface AutoIDGenerator {

    String getNextID(Class<? extends OWLEntity> type) throws AutoIDException;
    void setEntityPrefs(EntityCreation entitiesPrefs);  
    EntityCreation getEntitiesPrefs ();

    void initialise();
}

