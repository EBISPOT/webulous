package uk.ac.ebi.fgpt.populous.model;

import org.semanticweb.owlapi.model.IRI;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;

/**
 * Created by dwelter on 03/07/14.
 */
public interface PopulousModel {

/**
 * Returns an EntityCreation object,
 * **/
    public EntityCreation getEntity();


    public Set<PopulousPattern> getPopulousPatterns();


    public Set<IRI> getImportedOntologies();


    public IRI getSourceOntologyIRI();

    public IRI getSourceOntologyPhysicalIRI();


    public LinkedHashMap<Integer, String> getVariableMapper();


    public boolean isNewOntology();


    public Set<String> getVariables();

//    public void loadProperties (File file) throws IOException;


    public Properties getPopulousModelProperties () ;

    public void saveModel (File file) throws IOException ;

}
