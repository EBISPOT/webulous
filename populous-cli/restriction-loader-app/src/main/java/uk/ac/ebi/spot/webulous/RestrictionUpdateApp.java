package uk.ac.ebi.spot.webulous;

import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Sort;
import uk.ac.ebi.spot.webulous.model.RestrictionRunDocument;
import uk.ac.ebi.spot.webulous.model.Status;
import uk.ac.ebi.spot.webulous.service.RestrictionService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 24/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@SpringBootApplication
@EnableConfigurationProperties
public class RestrictionUpdateApp  implements CommandLineRunner {

    @Autowired
    private RestrictionService restrictionService;

    private static boolean list = false;
    private static boolean queued = false;
    private static boolean idsOnly = false;
    private static boolean runAll = false;
    private static String [] run_ids = {};

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RestrictionUpdateApp.class, args);
   	}

    @Override
    public void run(String... strings) throws Exception {

        int parseArgs = parseArguments(strings);
        if (parseArgs> 0) {
            if (queued || list) {

                List<RestrictionRunDocument> runDocuments = new ArrayList<RestrictionRunDocument>();

                if (queued) {
                    runDocuments = restrictionService.findByStatus(Status.QUEUED);
                    if (runDocuments.isEmpty()) {
                        System.out.println("No queued jobs");
                    }
                }
                else {
                    runDocuments= restrictionService.findAll(new Sort(new Sort.Order(Sort.Direction.DESC, "lastUpdated")));
                }
                for (RestrictionRunDocument runDocument : runDocuments) {

                    if (idsOnly)  {
                        System.out.println(runDocument.getId());
                    }
                    else {
                        System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\n",
                                runDocument.getId(),
                                runDocument.getTemplateId(),
                                runDocument.getTemplateName(),
                                runDocument.getStatus(),
                                runDocument.getLastUpdate(),
                                runDocument.getMessage());
                    }

                }
            }
            else if (run_ids.length>0) {
                for (String id : run_ids) {
                    RestrictionRunDocument docToRun = restrictionService.findOne(id);
                    if (docToRun != null) {
                        System.out.println("Running job with id: " + docToRun.getId());
                        restrictionService.run(docToRun);
                        System.out.println("Job complete! " + docToRun.getId());
                    }
                    else {
                        System.err.println("Can't run " + id + ", no run exists with that id");
                    }
                }
            }
            else if (runAll) {
                System.out.println("Running all queued jobs...");
                restrictionService.runAllQueued();
                System.out.println("Running all queued jobs complete");
            }
        }
    }


    private static int parseArguments(String[] args) {

        CommandLineParser parser = new GnuParser();
        HelpFormatter help = new HelpFormatter();
        Options options = bindOptions();

        int parseArgs = 0;
        try {
            CommandLine cl = parser.parse(options, args, true);

            // check for mode help option
            if (cl.getArgs().length == 0 || cl.hasOption("h")) {
                // print out mode help
                help.printHelp("restriction-updater.jar", options, true);
                parseArgs += 1;
            }
            else {
                // find -f option to see if we are to force load
                if (cl.hasOption("l") ) {
                    list = true;
                    parseArgs += 1;

                }
                else if (cl.hasOption("q")) {
                    queued = true;
                    parseArgs += 1;
                }

                if (cl.hasOption("i")) {
                    idsOnly = true;
                    parseArgs += 1;
                }

                if (cl.hasOption("r")) {
                    run_ids = cl.getOptionValues("r");
                    parseArgs += 1;
                }
                else if (cl.hasOption("a")) {
                    runAll = true;
                    parseArgs += 1;
                }
            }
        }
        catch (ParseException e) {
            System.err.println("Failed to read supplied arguments");
            help.printHelp("publish", options, true);
            parseArgs += 1;
        }
        return parseArgs;
    }

    private static Options bindOptions() {
        Options options = new Options();

        // help
        Option helpOption = new Option("h", "help", false, "Print the help");
        options.addOption(helpOption);

        Option list = new Option("l", "list", false, "List all restriction run jobs");
        list.setRequired(false);

        Option queued = new Option("q", "queued", false, "List all queued restriction update jobs");
        queued.setRequired(false);

        Option idsOnly = new Option("i", "id", false, "Only return job ids");
        idsOnly.setRequired(false);

        Option runId = new Option("r", "run", true, "Start a specific run by run id");
        runId.setRequired(false);
        runId.setArgs(Option.UNLIMITED_VALUES);


        Option runAll = new Option("a", "runAll", false, "Run all queued jobs");
        runAll.setRequired(false);

        options.addOption(list);
        options.addOption(queued);
        options.addOption(idsOnly);
        options.addOption(runId);
        options.addOption(runAll);

        return options;
    }
}
