package uk.ac.ebi.spot.webulous;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.ebi.spot.webulous.model.DataConversionRunDocument;
import uk.ac.ebi.spot.webulous.service.DataConversionService;
import uk.ac.ebi.spot.webulous.service.RestrictionService;

/**
 * @author Simon Jupp
 * @date 30/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@SpringBootApplication
public class OpplApp  implements CommandLineRunner {

    @Autowired
    private DataConversionService dataConversionService;

    @Override
    public void run(String... strings) throws Exception {
        for (DataConversionRunDocument runDocument : dataConversionService.getAllQueued()) {
            dataConversionService.runDataConversion(runDocument);
        }
    }
}
