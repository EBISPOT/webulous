package uk.ac.ebi.spot.webulous.model;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Created by dwelter on 02/07/14.
 */
public interface NewEntities {
     IRI getBaseIRI();


     boolean isRandom();


     String getPrefix();

     OWLManager getManager();


     OWLOntology getOntology();

}
