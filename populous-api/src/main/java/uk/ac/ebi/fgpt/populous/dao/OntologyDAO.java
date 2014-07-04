package uk.ac.ebi.fgpt.populous.dao;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Created by dwelter on 04/07/14.
 */
public interface OntologyDAO {


     Resource getOntologyResource();

    
     String getOntologyURI();


     String getOntologySynonymAnnotationURI();

     OWLOntology getOntology();

 
     String getOntologyObsoleteClassURI();

     OntologyConfiguration getOntologyConfiguration();

   
    /**
     * Fetches terms that can be matched against EFO.  By default, this excludes anything that is based on units (i.e.
     * falls within the set of things that might be a member of the class UO:0000000).  Searches are performed by exact
     * text matching on term label or synonyms.
     *
     * @param label the label of the OWL class to search for
     * @return the collection of ontology terms retrieved
     */
     Collection<OWLClass> getOWLClassesByLabel(String label) ;
    /**
     * Fetches an owl class by the URI of that class.  The string passed to this method should be a properly formatted
     * string representation of the URI for this class.
     *
     * @param str the URI of the class
     * @return the OWL class with this URI, if found, or null if not present
     */
     OWLClass getOWLClassByURI(String str) ;

    /**
     * Fetches an owl class by URI.  Internally, this method converts the given URI to an IRI - this method is a
     * convenience method that is shorthand for <code>getOWLClassByIRI(IRI.create(uri));</code>
     *
     * @param uri the URI of the class
     * @return the OWL class with this URI, if found, or null if not present
     */
     OWLClass getOWLClassByURI(URI uri) ;

    /**
     * Searches the index of owl class looking for a class with an IRI matching the one supplied.  This method will
     * return null if the supplied IRI does not match that of any class in the loaded ontology.
     *
     * @param iri the IRI of the class to retrieve
     * @return the OWL class with this IRI, if found, or null if absent
     */
     OWLClass getOWLClassByIRI(IRI iri) ;
    /**
     * Retrieve all possible names for the supplied class.  Class "names" include any labels attached to this class, or
     * any synonyms supplied.
     *
     * @param owlClass the owl class to derive names for
     * @return the list of strings representing labels or synonyms for this class
     */
     List<String> getClassNames(OWLClass owlClass) ;

    /**
     * Recovers all string values of the rdfs:label annotation attribute on the supplied class.  This is computed over
     * the inferred hierarchy, so labels of any equivalent classes will also be returned.
     *
     * @param owlClass the class to recover labels for
     * @return the literal values of the rdfs:label annotation
     */
     Set<String> getClassRDFSLabels(OWLClass owlClass) 

    /**
     * Recovers all synonyms for the supplied owl class, based on the literal value of the efo synonym annotation.  The
     * actual URI for this annotation is recovered from zooma-uris.properties, but at the time of writing was
     * 'http://www.ebi.ac.uk/efo/alternative_term'.  This class uses the
     *
     * @param owlClass the class to retrieve the synonyms of
     * @return a set of strings containing all aliases of the supplied class
     */
     Set<String> getClassSynonyms(OWLClass owlClass) ;
  

    /**
     * Retrieve all possible names for the supplied class.  Class "names" include any labels attached to this class, or
     * any synonyms supplied.
     *
     * @param owlOntology the ontology to search
     * @param owlClass    the owl class to derive names for
     * @return the list of strings representing labels or synonyms for this class
     */
    List<String> getClassNames(OWLOntology owlOntology, OWLClass owlClass) ;
    /**
     * Recovers all string values of the rdfs:label annotation attribute on the supplied class.  This is computed over
     * the inferred hierarchy, so labels of any equivalent classes will also be returned.
     *
     * @param owlOntology the ontology to search
     * @param owlClass    the class to recover labels for
     * @return the literal values of the rdfs:label annotation
     */
    Set<String> getClassRDFSLabels(OWLOntology owlOntology, OWLClass owlClass) ;
    /**
     * Recovers all synonyms for the supplied owl class, based on the literal value of the efo synonym annotation.  The
     * actual URI for this annotation is recovered from zooma-uris.properties, but at the time of writing was
     * 'http://www.ebi.ac.uk/efo/alternative_term'.  This class uses the
     *
     * @param owlOntology the ontology to search
     * @param owlClass    the class to retrieve the synonyms of
     * @return a set of strings containing all aliases of the supplied class
     */
    Set<String> getClassSynonyms(OWLOntology owlOntology, OWLClass owlClass) ;
    /**
     * Returns true if this ontology term is obsolete in EFO, false otherwise.  In EFO, a term is defined to be obsolete
     * if and only if it is a subclass of ObsoleteTerm.
     *
     * @param owlOntology   the ontology to search
     * @param obsoleteClass the owlClass that represents the "obsolete" superclass
     * @param owlClass      the owlClass to check for obsolesence
     * @return true if obsoleted, false otherwise
     */
    boolean isObsolete(OWLOntology owlOntology, OWLClass obsoleteClass, OWLClass owlClass);
}
