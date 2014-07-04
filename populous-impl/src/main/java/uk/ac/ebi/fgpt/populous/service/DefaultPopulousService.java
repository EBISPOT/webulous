package uk.ac.ebi.fgpt.populous.service;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import uk.ac.ebi.fgpt.populous.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.populous.model.DataCollection;
import uk.ac.ebi.fgpt.populous.model.PopulousModel;
import uk.ac.ebi.fgpt.populous.patterns.PopulousPatternExecutor;

/**
 * Created by dwelter on 04/07/14.
 *
 * Default implementation of the PopulousService interface. The default implementation takes data coming in from the webapp as a spreadsheet
 *
 */
public class DefaultPopulousService implements PopulousService {


    private OntologyConfiguration config;
    private DataCollection dataCollection;
    private PopulousModel populousModel;

    public DefaultPopulousService(/*some stuff needs to come in here: default*/){

        config = new OntologyConfiguration();


        try {
            PopulousPatternExecutor executor = new PopulousPatternExecutor(dataCollection, config.getOntologyManager(), populousModel, populousModel.getEntity());
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void createPopulousModel() {

    }

    @Override
    public void parseInputData(Object inputData) {

    }
}
