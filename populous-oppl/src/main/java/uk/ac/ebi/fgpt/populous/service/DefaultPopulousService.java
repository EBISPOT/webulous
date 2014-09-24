package uk.ac.ebi.fgpt.populous.service;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import uk.ac.ebi.fgpt.populous.entity.SimpleEntityCreation;
import uk.ac.ebi.fgpt.populous.model.DataCollection;
import uk.ac.ebi.fgpt.populous.model.PopulousModel;
import uk.ac.ebi.fgpt.populous.utils.OntologyConfiguration;

/**
 * Created by dwelter on 04/07/14.
 *
 * Default implementation of the PopulousService interface. The default implementation takes data coming in from a converter that processes it into the correct format.
 *
 */
public class DefaultPopulousService implements PopulousService<PopulousModel, DataCollection> {


    private DataCollection dataCollection;
    private PopulousModel populousModel;
    private SimpleEntityCreation creationStrategy;
    private OntologyConfiguration configuration;

    public DefaultPopulousService(DataCollection collection, PopulousModel model){


        this.dataCollection = collection;
        this.populousModel = model;
//        setUpOntologyConfiguration();

        if(populousModel.getEntity() == null){
            setEntityCreationStrategy();
        }

    }


    @Override
    public void setEntityCreationStrategy() {

        /**this is where the URiGEN tie-in should go*/
        creationStrategy = new SimpleEntityCreation();



    }

    @Override
    public void setUpPatternExecutor(PopulousModel populousModel, DataCollection data) {
        try {
            PopulousPatternExecutionService executor = new PopulousPatternExecutionService(data, configuration.getOntologyManager(), populousModel, creationStrategy);
            executor.executeOPPLPatterns();
            executor.saveOntology();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }


    public void setConfiguration(OntologyConfiguration configuration) {
        this.configuration = configuration;
    }
}
