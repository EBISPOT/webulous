package uk.ac.ebi.spot.webulous.model;

import org.semanticweb.owlapi.model.*;
import uk.ac.ebi.spot.webulous.exception.OWLEntityCreationException;

import java.net.URI;

/**
 * Author: Simon Jupp<br>
 * Date: Jan 4, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public interface OWLEntityFactory {

    /**
     *
     * @param shortName user supplied name
     * @param baseURI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws uk.ac.ebi.spot.webulous.exception.OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    OWLEntityCreationSet<OWLClass> createOWLClass(String shortName, URI baseURI) throws OWLEntityCreationException;


    OWLEntityCreationSet<OWLClass> createOWLClass(String shortName, URI baseURI, OWLClass parent) throws OWLEntityCreationException;


    /**
     *
     * @param shortName user supplied name
     * @param baseURI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    OWLEntityCreationSet<OWLObjectProperty> createOWLObjectProperty(String shortName, URI baseURI) throws OWLEntityCreationException;

    /**
     *
     * @param shortName user supplied name
     * @param baseURI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    OWLEntityCreationSet<OWLDataProperty> createOWLDataProperty(String shortName, URI baseURI) throws OWLEntityCreationException;

    /**
     *
     * @param shortName user supplied name
     * @param baseURI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    OWLEntityCreationSet<OWLNamedIndividual> createOWLIndividual(String shortName, URI baseURI) throws OWLEntityCreationException;

    /**
     *
     * @param type OWLClass, OWLObjectProperty, OWLDataProperty or OWLIndividual
     * @param shortName user supplied name
     * @param baseURI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    <T extends OWLEntity> OWLEntityCreationSet<T> createOWLEntity(Class<T> type, String shortName, URI baseURI) throws OWLEntityCreationException;


    /**
     * Use this to check if the entity can be created without affecting any generated IDs
     * @param type OWLClass, OWLObjectProperty, OWLDataProperty or OWLIndividual
     * @param shortName user supplied name
     * @param baseURI specify a base or leave as null to let the factory decide
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     * @return an entity creation set - this should never be applied to the ontology
     */
    <T extends OWLEntity> OWLEntityCreationSet<T> preview(Class<T> type, String shortName, URI baseURI) throws OWLEntityCreationException;

}
