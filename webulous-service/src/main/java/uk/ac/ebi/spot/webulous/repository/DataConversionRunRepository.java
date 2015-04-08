package uk.ac.ebi.spot.webulous.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.webulous.model.DataConversionRunDocument;
import uk.ac.ebi.spot.webulous.model.RestrictionRunDocument;
import uk.ac.ebi.spot.webulous.model.Status;

import java.util.List;

/**
 * @author Simon Jupp
 * @date 24/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@RepositoryRestResource(collectionResourceRel = "submissions", path = "submissions")
public interface DataConversionRunRepository extends MongoRepository<DataConversionRunDocument, String> {

    List<DataConversionRunDocument> findByStatus(Status status);

    List<DataConversionRunDocument> findByTemplateId(String templateId);
}
