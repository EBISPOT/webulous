package uk.ac.ebi.spot.webulous;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.ebi.spot.webulous.repository.PopulousTemplateRepository;
import uk.ac.ebi.spot.webulous.service.RestrictionService;

/**
 * @author Simon Jupp
 * @date 24/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@SpringBootApplication
public class RestrictionUpdateApp  implements CommandLineRunner {

    @Autowired
    private RestrictionService restrictionService;

    @Override
    public void run(String... strings) throws Exception {
        restrictionService.runAllQueued();
    }
}
