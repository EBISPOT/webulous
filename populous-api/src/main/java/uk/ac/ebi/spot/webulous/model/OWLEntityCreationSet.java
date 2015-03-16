package uk.ac.ebi.spot.webulous.model;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import java.util.List;

/**
 * Created by dwelter on 02/07/14.
 */
public interface OWLEntityCreationSet<E extends OWLEntity> {

    E getOWLEntity();


    List<? extends OWLOntologyChange> getOntologyChanges();
}
