import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.fgpt.OntologyDAO;
import uk.ac.ebi.fgpt.OntologyDAOImpl;

import java.util.Collections;

/**
 * @author Simon Jupp
 * @date 23/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class OntologyDAOTest {

    public static void main(String[] args) {

        try {
            System.out.println("Hermit output");


            OntologyDAO hermitDao = new OntologyDAOImpl(Collections.<IRI>singleton(IRI.create("file:/Users/jupp/Dropbox/dev/ols/ontology-tools/src/test/resources/test1.owl"))) {
                @Override
                public OWLReasoner getOWLReasoner(OWLOntology owlOntology) {
                    return new Reasoner(owlOntology);
                }
            };

            for (OWLClass owlClass : hermitDao.getSubclasses("'part \"of\"' some 'label with spaces'")) {
                System.out.println(owlClass.getIRI() + "\t" + hermitDao.getRendering(owlClass));
            }
            for (OWLClass owlClass : hermitDao.getSubclasses("A")) {
                System.out.println(owlClass.getIRI() + "\t" + hermitDao.getRendering(owlClass));
            }
            System.out.println("");
            System.out.println("Elk output");


//            OntologyDAO elkDao = new OntologyDAOImpl(Collections.<IRI>singleton(IRI.create("file:/Users/jupp/Dropbox/dev/ols/ontology-tools/src/test/resources/test1.owl"))) {
//                @Override
//                public OWLReasoner getOWLReasoner(OWLOntology owlOntology) {
//                    return  new ElkReasonerFactory().createReasoner(owlOntology);
//                }
//            };
//
//            for (OWLClass owlClass : elkDao.getSubclasses("'part \"of\"' some 'label with spaces'")) {
//                System.out.println(owlClass.getIRI() + "\t" + elkDao.getRendering(owlClass));
//            }


        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        }

    }

}
