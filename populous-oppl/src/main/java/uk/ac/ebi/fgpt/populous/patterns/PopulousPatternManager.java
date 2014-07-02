package uk.ac.ebi.fgpt.populous.patterns;


import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import uk.ac.ebi.fgpt.populous.patterns.model.OntologyTermValidation;
import uk.ac.ebi.fgpt.populous.patterns.model.Workbook;
import uk.ac.ebi.fgpt.populous.patterns.model.WorkbookManager;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Simon Jupp<br>
 * Date: Oct 7, 2010<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class PopulousPatternManager {

    public WorkbookManager workbookManager;
    public Workbook workbook;

    public WorkbookManager getWorkbookManager() {
        return workbookManager;
    }


    public Workbook getWorkbook() {
        return workbook;
    }

    public PopulousModel model;


    public PopulousPatternManager(WorkbookManager workbookManager, Workbook workbook) {
        this.workbookManager = workbookManager;
        this.workbook = workbook;
        this.model = new PopulousModel();
    }

    public PopulousModel getPopulousModels() {
        return model;
    }


    public static void main(String[] args) {

        WorkbookManager manager = new WorkbookManager();

        try {
            Workbook workbook = manager.loadWorkbook(URI.create("file:/Users/simon/Desktop/kupo_cells_small.xls"));
            PopulousPatternManager popManager = new PopulousPatternManager(manager, workbook);
            PopulousModel model = popManager.getPopulousModels();


            String pattern = "?cell:CLASS,\n" +
                "?anatomyPart:CLASS,\n" +
                "?partOfRestriction:CLASS = part_of some ?anatomyPart,\n" +
                "?anatomyIntersection:CLASS = createIntersection(?partOfRestriction.VALUES)\n" +
                "BEGIN\n" +
                "ADD ?cell equivalentTo ?anatomyIntersection\n" +
                "END;";

            String pattern3 = "BEGIN\n" +
                    "ADD MA_0001657 subClassOf participates_in some MA_0001657\n" +
                    "END;";
//            String pattern2 = "?cell:CLASS,\n" +
//                "?participant:CLASS,\n" +
//                "?participatesRestriction:CLASS = CL_0000000 and participates_in some ?participant,\n" +
//                "?participatesIntersection:CLASS = createIntersection(?participatesRestriction.VALUES)\n" +
//                "BEGIN\n" +
//                "ADD ?cell SubClassOf ?participatesIntersection\n" +
//                "END;";

            ArrayList<Integer> columns = new ArrayList<Integer> ();
            columns.add(0);
            columns.add(2);
//            columns.add(3);
            model.getPopulousPatterns().add(new SimplePopulousPattern("1", pattern));
            model.getPopulousPatterns().add(new SimplePopulousPattern("2", pattern3));
            model.setSheet(0);
            model.getColumns().addAll(columns);
            model.setStartRow(2);
            model.setEndRow(25);

            // load ontologies
            IRI baseURI = IRI.create("http://www.elico.eu/ontology/kupo");
            model.setSourceOntologyIRI(baseURI);

            Set<OntologyTermValidation> validations = model.getValidations(manager, workbook);
            for (OntologyTermValidation validation : validations) {

                // todo test this with live connection
                // model.getImportedOntologies().addAll(validation.getValidationDescriptor().getOntologyIRIs());
                for  (IRI iri : validation.getValidationDescriptor().getOntologyIRIs()) {
                    model.getImportedOntologies().add(validation.getValidationDescriptor().getPhysicalIRIForOntologyIRI(iri));
                    model.getImportedOntologies().add(IRI.create("file:/Users/simon/dev/kupo/workflow/owl/ro.owl"));
                }

            }


            LinkedHashMap<Integer, String> variableBinding = new LinkedHashMap<Integer, String>();
            variableBinding.put(0, "?cell");
            variableBinding.put(2, "?anatomyPart");
//            variableBinding.put(3, "?participant");

            model.setVariableMapper(variableBinding);

//            SimpleNewEntities newEntities = new SimpleNewEntities(baseURI, false, "");

            uk.ac.ebi.fgpt.populous.patterns.entity.SimpleEntityCreation newEntities = new uk.ac.ebi.fgpt.populous.patterns.entity.SimpleEntityCreation();
            try {

                PopulousPatternExecutor executor = new PopulousPatternExecutor(manager, manager.getOntologyManager(), model, newEntities);
                executor.generateOPPLScript();
                executor.saveOntology(IRI.create("file:/Users/simon/Desktop/tmp/kupo_small.owl"));


            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (OWLOntologyStorageException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



    }
}
