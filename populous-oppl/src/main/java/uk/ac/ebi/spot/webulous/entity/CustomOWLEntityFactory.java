package uk.ac.ebi.spot.webulous.entity;

import org.slf4j.Logger;
import org.semanticweb.owlapi.model.*;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.webulous.exception.AutoIDException;
import uk.ac.ebi.spot.webulous.exception.OWLEntityCreationException;
import uk.ac.ebi.spot.webulous.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Simon Jupp<br>
 * Date: Jan 4, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class CustomOWLEntityFactory implements OWLEntityFactory {

    private Logger logger = LoggerFactory.getLogger(CustomOWLEntityFactory.class);

    private OWLOntologyManager mngr;

    private OWLOntology onto;

    private static AutoIDGenerator autoIDGenerator; // only a single generator between instances

    private LabelDescriptor labelDescriptor;

    EntityCreation entityPrefs;


    public CustomOWLEntityFactory(OWLOntologyManager mngr, OWLOntology onto, EntityCreation entityCreation) {
        this.mngr = mngr;
        this.onto = onto;
        this.entityPrefs = entityCreation;

    }


    public OWLEntityCreationSet<OWLClass> createOWLClass(String shortName, URI baseURI) throws OWLEntityCreationException {
        return createOWLEntity(OWLClass.class, shortName, baseURI);
    }

    public OWLEntityCreationSet<OWLClass> createOWLClass(String shortName, URI baseURI, OWLClass parent) throws OWLEntityCreationException {
        return createOWLEntity(OWLClass.class, shortName, baseURI, parent);
    }

    public OWLEntityCreationSet<OWLObjectProperty> createOWLObjectProperty(String shortName, URI baseURI) throws OWLEntityCreationException {
        return createOWLEntity(OWLObjectProperty.class, shortName, baseURI);
    }


    public OWLEntityCreationSet<OWLDataProperty> createOWLDataProperty(String shortName, URI baseURI) throws OWLEntityCreationException {
        return createOWLEntity(OWLDataProperty.class, shortName, baseURI);
    }


    public OWLEntityCreationSet<OWLNamedIndividual> createOWLIndividual(String shortName, URI baseURI) throws OWLEntityCreationException {
        return createOWLEntity(OWLNamedIndividual.class, shortName, baseURI);
    }


    public <T extends OWLEntity> OWLEntityCreationSet<T> createOWLEntity(Class<T> type, String shortName, URI baseURI) throws OWLEntityCreationException {
        try {

            if (baseURI == null){
                if (useDefaultBaseURI()){
                    baseURI = getDefaultBaseURI();
                }
                if (baseURI == null){
                    baseURI = onto.getOntologyID().getOntologyIRI().toURI();
                }
            }

            URI uri;
            String id = null;
            if (isFragmentAutoGenerated()){
                do{
                    id = getAutoIDGenerator().getNextID(type);
                    uri = createURI(id, baseURI);
                } while (isURIAlreadyUsed(uri)); // don't pun unnecessarily
            }
            else {
                uri = createURI(shortName, baseURI);

                if (isURIAlreadyUsed(type, uri)){
                    throw new OWLEntityCreationException("Entity already exists: " + uri);
                }

                if (isGenerateIDLabel()){
                    id = getAutoIDGenerator().getNextID(type); // critical it is unique?
                }
            }

            T entity = getOWLEntity(type, uri);

            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

            if (isGenerateIDLabel()){
                changes.addAll(createLabel(entity, id));
            }

            if (isGenerateNameLabel()){
                changes.addAll(createLabel(entity, shortName));
            }

            OWLDataFactory df = mngr.getOWLDataFactory();
            OWLAxiom ax = df.getOWLDeclarationAxiom(entity);
            changes.add(new AddAxiom(onto, ax));

            return new SimpleOWLEntityCreationSet<T>(entity, changes);
        }
        catch (URISyntaxException e) {
            throw new OWLEntityCreationException(e);
        }
        catch (AutoIDException e) {
            throw new OWLEntityCreationException(e);
        }
    }

    public <T extends OWLEntity> OWLEntityCreationSet<T> createOWLEntity(Class<T> type, String shortName, URI baseURI, OWLClass parent) throws OWLEntityCreationException {
        try {

            if (baseURI == null){
                if (useDefaultBaseURI()){
                    baseURI = getDefaultBaseURI();
                }
                if (baseURI == null){
                    baseURI = onto.getOntologyID().getOntologyIRI().toURI();
                }
            }

            URI uri;
            String id = null;
            if (isFragmentAutoGenerated()){
                do{
                    id = getAutoIDGenerator().getNextID(type);
                    uri = createURI(id, baseURI);
                } while (isURIAlreadyUsed(uri)); // don't pun unnecessarily
            }
            else {
                uri = createURI(shortName, baseURI);

                if (isURIAlreadyUsed(type, uri)){
                    throw new OWLEntityCreationException("Entity already exists: " + uri);
                }

                if (isGenerateIDLabel()){
                    id = getAutoIDGenerator().getNextID(type); // critical it is unique?
                }
            }

            T entity = getOWLEntity(type, uri);

            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

            if (isGenerateIDLabel()){
                changes.addAll(createLabel(entity, id));
            }

            if (isGenerateNameLabel()){
                changes.addAll(createLabel(entity, shortName));
            }

            OWLDataFactory df = mngr.getOWLDataFactory();
            OWLAxiom ax = df.getOWLSubClassOfAxiom((OWLClassExpression) entity,parent);
            changes.add(new AddAxiom(onto, ax));

            return new SimpleOWLEntityCreationSet<T>(entity, changes);
        }
        catch (URISyntaxException e) {
            throw new OWLEntityCreationException(e);
        }
        catch (AutoIDException e) {
            throw new OWLEntityCreationException(e);
        }
    }


    public <T extends OWLEntity> OWLEntityCreationSet<T> preview(Class<T> type, String shortName, URI baseURI) throws OWLEntityCreationException {
        // There is probably a better way round this.
        if (getAutoIDGenerator() instanceof Revertable){
            ((Revertable)getAutoIDGenerator()).checkpoint();
        }
        try{
            return createOWLEntity(type, shortName, baseURI);
        }
        catch(OWLEntityCreationException e){
            throw e;
        }
        finally{
            if (getAutoIDGenerator() instanceof Revertable){
                ((Revertable)getAutoIDGenerator()).revert();
            }
        }
    }


    protected URI createURI(String fragment, URI baseURI) throws URISyntaxException {
        fragment = fragment.replace(" ", "_");
        if (baseURI == null){
            if (useDefaultBaseURI()){
                baseURI = entityPrefs.getDefaultBaseURI();
            }
            else{
                baseURI = onto.getOntologyID().getOntologyIRI().toURI();
            }
        }
        String base = baseURI.toString().replace(" ", "_");
        if (!base.endsWith("#") && !base.endsWith("/")) {
            base += entityPrefs.getDefaultSeparator();
        }
        return new URI(base + fragment);
    }


    private List<? extends OWLOntologyChange> createLabel(OWLEntity owlEntity, String value) {
        LabelDescriptor descr = getLabelDescriptor();
        URI uri = descr.getURI(entityPrefs);
        String lang = descr.getLanguage(entityPrefs);

        OWLDataFactory df = mngr.getOWLDataFactory();
        OWLLiteral con;
        if (lang == null){
            con = df.getOWLStringLiteral(value);
        }
        else{
            con = df.getOWLStringLiteral(value, lang);
        }
        OWLAnnotation anno = df.getOWLAnnotation(df.getOWLAnnotationProperty(IRI.create(uri)), con);
        OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(owlEntity.getIRI(), anno);
        return Collections.singletonList(new AddAxiom(onto, ax));
    }


    private LabelDescriptor getLabelDescriptor() {
        Class<? extends LabelDescriptor> cls = entityPrefs.getLabelDescriptorClass();
        if (labelDescriptor == null || !cls.equals(labelDescriptor.getClass())){
            try {
                labelDescriptor = cls.newInstance();
            }
            catch (InstantiationException e) {
                logger.error("Cannot create label descriptor", e);
            }
            catch (IllegalAccessException e) {
                logger.error("Cannot create label descriptor", e);
            }
        }
        return labelDescriptor;
    }


    private AutoIDGenerator getAutoIDGenerator() {
        final Class<? extends AutoIDGenerator> prefAutoIDClass = entityPrefs.getAutoIDGeneratorClass();
        if (autoIDGenerator == null || !prefAutoIDClass.equals(autoIDGenerator.getClass())){
            try {
                autoIDGenerator = prefAutoIDClass.newInstance();
                autoIDGenerator.setEntityPrefs(entityPrefs);
                autoIDGenerator.initialise();
            }
            catch (InstantiationException e) {
                logger.error("Cannot create auto ID generator", e);
            }
            catch (IllegalAccessException e) {
                logger.error("Cannot create auto ID generator", e);
            }
        }
        return autoIDGenerator;
    }


    private <T extends OWLEntity> boolean isURIAlreadyUsed(Class<T> type, URI uri) {
        for (OWLOntology ont : mngr.getOntologies()){
            if ((OWLClass.class.isAssignableFrom(type) && ont.containsClassInSignature(IRI.create(uri))) ||
                (OWLObjectProperty.class.isAssignableFrom(type) && ont.containsObjectPropertyInSignature(IRI.create(uri))) ||
                (OWLDataProperty.class.isAssignableFrom(type) && ont.containsDataPropertyInSignature(IRI.create(uri))) ||
                (OWLIndividual.class.isAssignableFrom(type) && ont.containsIndividualInSignature(IRI.create(uri)))){
                return true;
            }
        }
        return false;
    }


    private boolean isURIAlreadyUsed(URI uri) {
        for (OWLOntology ont : mngr.getOntologies()){
            if (ont.containsClassInSignature(IRI.create(uri)) ||
                ont.containsObjectPropertyInSignature(IRI.create(uri)) ||
                ont.containsDataPropertyInSignature(IRI.create(uri)) ||
                ont.containsIndividualInSignature(IRI.create(uri))){
                return true;
            }
        }
        return false;
    }


    private <T extends OWLEntity> T getOWLEntity(Class<T> type, URI uri) {
        if (OWLClass.class.isAssignableFrom(type)){
            return (T)mngr.getOWLDataFactory().getOWLClass(IRI.create(uri));
        }
        else if (OWLObjectProperty.class.isAssignableFrom(type)){
            return (T)mngr.getOWLDataFactory().getOWLObjectProperty(IRI.create(uri));
        }
        else if (OWLDataProperty.class.isAssignableFrom(type)){
            return (T)mngr.getOWLDataFactory().getOWLDataProperty(IRI.create(uri));
        }
        else if (OWLNamedIndividual.class.isAssignableFrom(type)){
            return (T)mngr.getOWLDataFactory().getOWLNamedIndividual(IRI.create(uri));
        }
        return null;
    }


    protected boolean useDefaultBaseURI() {
        return entityPrefs.useDefaultBaseURI();
    }


    protected boolean isFragmentAutoGenerated() {
        return entityPrefs.isFragmentAutoGenerated();
    }


    protected boolean isGenerateNameLabel() {
        return entityPrefs.isGenerateNameLabel();
    }


    protected boolean isGenerateIDLabel() {
        return entityPrefs.isGenerateIDLabel();
    }


    protected URI getDefaultBaseURI() {
        return entityPrefs.getDefaultBaseURI();
    }


}
