package uk.ac.ebi.fgpt;

import org.apache.commons.lang3.StringUtils;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxClassExpressionParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.util.*;

/**
 * @author Simon Jupp
 * @date 20/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public abstract class OntologyDAOImpl implements OntologyDAO {


    private OWLOntologyManager manager;
    private BidirectionalShortFormProviderAdapter sfp;
    private ManchesterOWLSyntaxClassExpressionParser mosiParser;

    public abstract OWLReasoner getOWLReasoner(OWLOntology owlOntology);

    public OntologyDAOImpl (Collection<IRI> ontologies) throws OWLOntologyCreationException {
        this(ontologies, Collections.singleton(OWLRDFVocabulary.RDFS_LABEL.getIRI()));
    }

    public OntologyDAOImpl (Collection<IRI> ontologies, Collection<IRI> labelIris) throws OWLOntologyCreationException {

        System.setProperty("entityExpansionLimit", "1000000000");
        this.manager = OWLManager.createOWLOntologyManager();
        for (IRI iri: ontologies){
            manager.loadOntology(iri);
        }


        List<OWLAnnotationProperty> properties = new ArrayList<OWLAnnotationProperty>();
        for(IRI labelIri: labelIris) {
            properties.add(manager.getOWLDataFactory().getOWLAnnotationProperty(labelIri));
        }

        AnnotationValueShortFormProvider annotationProvider = new AnnotationValueShortFormProvider(
                properties,
                new HashMap<OWLAnnotationProperty, List<String>>(),
                manager,
                new SimpleShortFormProvider()


        ) {

        };

        sfp = new BidirectionalShortFormProviderAdapter(manager.getOntologies(), annotationProvider) {
            @Override
            protected String generateShortForm(OWLEntity entity) {
                String shortform = super.generateShortForm(entity);
                if (shortform.contains(" ")){ // if this is a multiword name
                    shortform = "\'" + shortform + "\'";
                }
                return shortform;
            }
        };


        mosiParser = new ManchesterOWLSyntaxClassExpressionParser(manager.getOWLDataFactory(), new ShortFormEntityChecker(sfp));
    }

    @Override
    public Set<OWLClass> getSubclasses(String manchesterSyntaxExpression) throws ParserException {

        OWLClassExpression expression = mosiParser.parse(manchesterSyntaxExpression);
        HashSet<OWLClass> classes = new HashSet<OWLClass>();

        for (OWLOntology ontology : manager.getOntologies()) {
            classes.addAll(getOWLReasoner(ontology).getSubClasses(expression, true).getFlattened());
        }

        return classes;
    }

    @Override
    public Set<OWLClass> getDescendantClasses(String manchesterSyntaxExpression) throws ParserException  {
        OWLClassExpression expression = mosiParser.parse(manchesterSyntaxExpression);
        HashSet<OWLClass> classes = new HashSet<OWLClass>();

        for (OWLOntology ontology : manager.getOntologies()) {
            OWLReasoner reasoner = getOWLReasoner(ontology);

            for (OWLClass cls : reasoner.getSubClasses(expression, false).getFlattened()) {

                if (!cls.isOWLNothing() && reasoner.isSatisfiable(cls)) {
                    classes.add(cls);
                }

            }
        }

        return classes;
    }

    public String getRendering(OWLClass owlClass) {
        String render = sfp.getShortForm(owlClass);
        if (StringUtils.isBlank(render)) {
            render = owlClass.getIRI().getFragment();
        }

        if (StringUtils.isBlank(render)) {
            render = owlClass.getIRI().toString();
        }

            // remove single quotes
        if (render.contains(" ") && render.startsWith("'") && render.endsWith("'")) {
            render = render.substring(1, (render.length() -1));
        }

        return  render;
    }


}
