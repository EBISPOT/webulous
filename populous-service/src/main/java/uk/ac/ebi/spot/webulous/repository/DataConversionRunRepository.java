package uk.ac.ebi.spot.webulous.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.webulous.model.DataConversionRunDocument;

/**
 * @author Simon Jupp
 * @date 24/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public interface DataConversionRunRepository extends MongoRepository<DataConversionRunDocument, String> {

}
