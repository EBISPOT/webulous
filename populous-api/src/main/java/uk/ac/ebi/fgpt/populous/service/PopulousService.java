package uk.ac.ebi.fgpt.populous.service;

/**
 * Created by dwelter on 02/07/14.
 *
 *
 * takes whatever comes in from CL client/web app/other and converts it into something the backend can use
 *
 *
 * process spreadsheet/CSV file/other row by row into a DataCollection consisting of DataItems and DataAttributes
 *
 * load all ontologies (source and imported) into the OWLOntologyManager
 *
 *
 */
public interface PopulousService<I> {

    void createPopulousModel();

    void parseInputData(I inputData );

}
