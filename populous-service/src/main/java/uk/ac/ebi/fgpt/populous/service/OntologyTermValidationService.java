package uk.ac.ebi.fgpt.populous.service;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import uk.ac.ebi.fgpt.populous.model.Term;
import uk.ac.ebi.fgpt.populous.utils.OntologyConfiguration;

import java.io.IOException;
import java.util.*;

/**
 * Created by dwelter on 04/07/14.
 *
 * This class checks each restrictions that comes through in the service and then returns the appropriate restriction values
 *
 */
public class OntologyTermValidationService {

    private OntologyConfiguration ontologyConfiguration;
    private Map<String, String> loadedOntologies;
    private Properties configFile;

    public OntologyTermValidationService(String sourceOntology, OntologyConfiguration configuration){
        ontologyConfiguration = configuration;
        loadedOntologies = new HashMap<String, String>();
        configFile = new Properties();
        loadSourceOntology(sourceOntology);

    }

    public void loadSourceOntology(String sourceOntology){
        try {
            configFile.load(getClass().getClassLoader().getResource(sourceOntology+".properties").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            System.out.println("There is no config file for ontology " + sourceOntology);
        }

        String name = configFile.getProperty("sourceOntology.acronym");
        String iri = configFile.getProperty("sourceOntology.iri");
        String file = configFile.getProperty("sourceOntology.fileLocation");

        ontologyConfiguration.loadOntology(IRI.create(iri), IRI.create(file));
        loadedOntologies.put(name, iri);

//        int importCount = 1;
//        name = configFile.getProperty("importOntology.acronym."+importCount);
//        iri = configFile.getProperty("importOntology.iri."+importCount);
//        file = configFile.getProperty("importOntology.fileLocation."+importCount);
//
//        while(iri != null){
//            ontologyConfiguration.loadOntology(IRI.create(iri), IRI.create(file));
//            loadedOntologies.put(name, iri);
//
//            importCount++;
//            name = configFile.getProperty("importOntology.acronym."+importCount);
//            iri = configFile.getProperty("importOntology.iri."+importCount);
//            file = configFile.getProperty("importOntology.fileLocation."+importCount);
//        }

    }


    public List<Term> getPermissibleTerms(String restrictionParentURI, String restrictionType, String restrictionOntology) {
        List<Term> allTerms = new ArrayList<Term>();

        if(loadedOntologies.get(restrictionOntology) == null){
            int importCount = 1;
            String name = configFile.getProperty("importOntology.acronym." + importCount);

            while(name != null){
                if(name.equals(restrictionOntology)){
                    String iri = configFile.getProperty("importOntology.iri."+importCount);
                    String file = configFile.getProperty("importOntology.fileLocation."+importCount);
                    ontologyConfiguration.loadOntology(IRI.create(iri), IRI.create(file));
                    loadedOntologies.put(name, iri);
                }
                else{
                    importCount++;
                    name = configFile.getProperty("importOntology.acronym."+importCount);
                }

            }
        }
        OWLOntology ontology = ontologyConfiguration.getOntologyManager().getOntology(IRI.create(loadedOntologies.get(restrictionOntology)));

        OWLReasoner reasoner = ontologyConfiguration.getReasoner(IRI.create(loadedOntologies.get(restrictionOntology)));

        OWLClass parent = ontologyConfiguration.getDataFactory().getOWLClass(IRI.create(restrictionParentURI));

        boolean type;

        if(restrictionType == "children"){
            type = true;
        }
        else {
            type = false;
        }

        Set<OWLClass> subClses = reasoner.getSubClasses(parent, type).getFlattened();
        OWLAnnotationProperty label = ontologyConfiguration.getDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

        for(OWLClass cls : subClses){

            IRI iri = cls.getIRI();

            for(OWLAnnotation annot : cls.getAnnotations(ontology, label)){
                if(annot.getValue() instanceof  OWLLiteral){
                     String name = ((OWLLiteral) annot.getValue()).getLiteral();
                    allTerms.add(new Term(iri, name));
                }
            }
        }
        return allTerms;
    }
}
