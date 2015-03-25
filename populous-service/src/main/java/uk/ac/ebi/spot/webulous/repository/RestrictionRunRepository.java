package uk.ac.ebi.spot.webulous.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.webulous.model.RestrictionRunDocument;
import uk.ac.ebi.spot.webulous.model.Status;

import java.util.List;

/**
 * @author Simon Jupp
 * @date 24/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public interface RestrictionRunRepository extends MongoRepository<RestrictionRunDocument, String> {

    List<RestrictionRunDocument> findByStatus(Status status);

    List<RestrictionRunDocument> findByTemplateId(String templateId, Sort sort);

    RestrictionRunDocument findByTemplateIdAndStatus(String id, Status queued);
}
