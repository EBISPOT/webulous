package uk.ac.ebi.fgpt.populous.service;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import uk.ac.ebi.fgpt.populous.entity.SimpleEntityCreation;
import uk.ac.ebi.fgpt.populous.model.DataCollection;
import uk.ac.ebi.fgpt.populous.model.PopulousModel;
import uk.ac.ebi.fgpt.populous.model.SimplePopulousModel;
import uk.ac.ebi.fgpt.populous.utils.OntologyConfiguration;

/**
 * Created by dwelter on 04/07/14.
 *
 * Default implementation of the PopulousService interface. The default implementation takes data coming in from a converter that processes it into the correct format.
 *
 */
public class DefaultPopulousService implements PopulousService{ //<SimplePopulousModel, SimpleDataCollection> {


    private OntologyConfiguration config;
    private DataCollection dataCollection;
    private SimplePopulousModel populousModel;
    private SimpleEntityCreation creationStrategy;

    public DefaultPopulousService(DataCollection collection){


        this.dataCollection = collection;
        setUpOntologyConfiguration();

        setEntityCreationStrategy();
        createPopulousModel();

        setUpPatternExecutor(populousModel, dataCollection);



    }

    @Override
    public void createPopulousModel() {
       populousModel = new SimplePopulousModel();


    }


    @Override
    public void setUpOntologyConfiguration(){

    }

    @Override
    public void setEntityCreationStrategy() {
        creationStrategy = new SimpleEntityCreation();



    }

    @Override
    public void setUpPatternExecutor(PopulousModel populousModel, DataCollection data) {
        try {
            PopulousPatternExecutionService executor = new PopulousPatternExecutionService(data, config.getOntologyManager(), populousModel, creationStrategy);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }


}
