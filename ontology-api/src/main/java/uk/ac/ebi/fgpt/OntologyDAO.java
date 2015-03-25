package uk.ac.ebi.fgpt;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.Set;

/**
 * @author Simon Jupp
 * @date 20/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public interface OntologyDAO {
    OWLReasoner getOWLReasoner(OWLOntology owlOntology);

    Set<OWLClass> getSubclasses(String manchesterSyntaxExpression) throws ParserException ;

    Set<OWLClass> getDescendantClasses(String manchesterSyntaxExpression)  throws ParserException;

    String getRendering(OWLClass owlClass);

}
