package uk.ac.ebi.fgpt.populous.service;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import uk.ac.ebi.fgpt.populous.utils.OntologyConfiguration;
import uk.ac.ebi.fgpt.populous.model.DataCollection;
import uk.ac.ebi.fgpt.populous.model.PopulousModel;

/**
 * Created by dwelter on 04/07/14.
 *
 * Default implementation of the PopulousService interface. The default implementation takes data coming in from a converter that processes it into the correct format.
 *
 */
public class DefaultPopulousService implements PopulousService {


    private OntologyConfiguration config;
    private DataCollection dataCollection;
    private PopulousModel populousModel;

    public DefaultPopulousService(DataCollection collection){


        this.dataCollection = collection;
        setUpOntologyConfiguration();

        createPopulousModel();

        try {
            PopulousPatternExecutionService executor = new PopulousPatternExecutionService(dataCollection, config.getOntologyManager(), populousModel, populousModel.getEntity());
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void createPopulousModel() {

    }

    @Override
    public void setUpOntologyConfiguration(){

    }


}
