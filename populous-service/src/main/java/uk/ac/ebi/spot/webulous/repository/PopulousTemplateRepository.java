package uk.ac.ebi.spot.webulous.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.webulous.model.PopulousTemplateDocument;

import java.util.List;

/**
 * Created by dwelter on 03/07/14.
 *
 *
 *  A data access object that defines methods to create and retrieve a PopulousModel from data provided through a PopulousService
 *
 */
public interface PopulousTemplateRepository extends MongoRepository<PopulousTemplateDocument, String> {

    Page<PopulousTemplateDocument> findAll(Pageable pageable);

}
