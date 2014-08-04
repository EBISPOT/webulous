package uk.ac.ebi.fgpt.populous.controller;

/**
 * Created by dwelter on 09/07/14.
 *
 *
 * The controller receives data from webulous, converts it into the appropriate DataCollection model, instantiates config stuff, then passes it all on to
 * a DefaultPopulousService to process
 *
 * required API calls
 * - a POST call that takes all the data in one big chunk. See JSONtemplate.txt for what the incoming JSON should look like
 *
 * - a validation call that gets passed a single OPPL pattern and associated source ontology info in order to validate the actual pattern. Return valid or invalid message
 * this should still return a status report while the server is processing, then a success/failure message
 *
 * - in theory, we could also have a GET method that gets one or more rows of data as per JSON template and returns something, either a full ontology file or a location of a file
 *
 *
 */
public class PopulousController {
}
