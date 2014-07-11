package uk.ac.ebi.fgpt.populous.utils;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

/**
 * Created by dwelter on 04/07/14.
 */
public class OntologyConfiguration {

    private OWLOntologyManager manager;
    private OWLDataFactory factory;
    private OWLReasoner reasoner;


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


    public OWLReasoner getReasoner(OWLOntology ont){

        if(reasoner == null ){
            OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
            ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
             OWLReasonerConfiguration config = new SimpleConfiguration(
                    progressMonitor);
             OWLReasoner reasoner = reasonerFactory.createReasoner(ont, config);
            // Ask the reasoner to do all the necessary work now
            reasoner.precomputeInferences();

        }

        return reasoner;
    }


}
