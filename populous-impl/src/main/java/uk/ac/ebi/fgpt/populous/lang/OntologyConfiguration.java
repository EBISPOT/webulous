package uk.ac.ebi.fgpt.populous.lang;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

/**
 * Created by dwelter on 04/07/14.
 */
public class OntologyConfiguration {

    private OWLOntologyManager manager;
    private OWLDataFactory factory;


    public void setOntologyManager(){
        this.manager = OWLManager.createOWLOntologyManager();
    }

    public OWLOntologyManager getOntologyManager(){
        return manager;
    }

    public void setDataFactory(){
        this.factory = manager.getOWLDataFactory();
    }

    public OWLDataFactory getDataFactory(){
        return factory;
    }

    public void loadOntology(IRI ontologyIRI, IRI documentIRI){
        if(documentIRI != null){
            manager.addIRIMapper(new SimpleIRIMapper(ontologyIRI, documentIRI));

            try {
                manager.loadOntology(ontologyIRI);
            }
            catch (OWLOntologyCreationException e) {
                e.printStackTrace();
            }


        }
    }


}
