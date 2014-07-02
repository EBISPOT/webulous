package uk.ac.ebi.fgpt.populous.patterns;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.coode.oppl.Variable;
import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
import org.coode.oppl.variabletypes.*;
import org.coode.parsers.BidirectionalShortFormProviderAdapter;
//import org.coode.parsers.test.JUnitTestErrorListener;
import org.coode.parsers.common.SystemErrorEcho;
import org.coode.patterns.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import uk.ac.ebi.fgpt.populous.patterns.entity.CustomOWLEntityFactory;
import uk.ac.ebi.fgpt.populous.exception.OWLEntityCreationException;
import uk.ac.ebi.fgpt.populous.patterns.entity.SimpleOWLEntityCreationSet;


import java.util.*;

/* THIS IS JUPP'S MAGIC CLASS*/

/*
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
 * Date: Oct 11, 2010<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class PopulousPatternExecutor {

    private static Logger logger = Logger.getLogger(PopulousPatternExecutor.class);

    private WorkbookManager workbookManager;
    private Workbook workbook;

    private PopulousModel populousModel;
    private uk.ac.ebi.fgpt.populous.patterns.entity.SimpleEntityCreation entityCreation;
    private OWLOntologyManager ontologyManager;

    private Set<OWLOntology> ontologies;
    private OWLOntology activeOntology;

    private ParserFactory pf;
    private OPPLPatternParser parser;
    private PatternModel patternModel;

    private Map<Integer, Map<String, IRI>> shortFromMapper;
    private BidirectionalShortFormProviderAdapter shortFormProvider;

    private Random randomGenerator;

    private List<OWLAnnotationProperty> props;

    CustomOWLEntityFactory cf;
//    final JUnitTestErrorListener errorListener = new JUnitTestErrorListener();


    public PopulousPatternExecutor(WorkbookManager workbookManager, OWLOntologyManager man, PopulousModel populousModel, uk.ac.ebi.fgpt.populous.patterns.entity.SimpleEntityCreation newEntities) throws OWLOntologyCreationException {
        logger.debug("Starting Pattern Executor");
        this.workbookManager = workbookManager;
        this.workbook = workbookManager.getWorkbook();
        this.populousModel = populousModel;
        this.entityCreation = newEntities;
        randomGenerator = new Random();

        ontologyManager = man;


        if (!ontologyManager.contains(populousModel.getSourceOntologyIRI())) {
            logger.debug("creating new ontology " + populousModel.getSourceOntologyIRI().toString());
            activeOntology = ontologyManager.createOntology(populousModel.getSourceOntologyIRI());
        }
        else {
            activeOntology = ontologyManager.getOntology(populousModel.getSourceOntologyIRI());
        }

        this.cf = new CustomOWLEntityFactory(ontologyManager, activeOntology, entityCreation);

//        try {
//            for (IRI iri : populousModel.getImportedOntologies()) {
//                logger.debug("loading imported ontology " + iri.toString());
////                ontologyManager.loadOntology(iri);
//            }
//
//        } catch (Exception e) {
//
//            System.err.println("exceptionL" + e.getMessage());
//        }



        this.pf = new ParserFactory(activeOntology, ontologyManager);
        this.parser = pf.build(new SystemErrorEcho());

        // set up short form provider
        IRI iri = OWLRDFVocabulary.RDFS_LABEL.getIRI();
        OWLAnnotationProperty prop = ontologyManager.getOWLDataFactory().getOWLAnnotationProperty(iri);
        props = new ArrayList<OWLAnnotationProperty>();
        props.add(prop);
        ShortFormProvider provider = new AnnotationValueShortFormProvider(props, new HashMap<OWLAnnotationProperty, List<String>>(), ontologyManager);
        shortFormProvider = new BidirectionalShortFormProviderAdapter(ontologyManager, ontologyManager.getOntologies(), provider);

    }

    public void generateOPPLScript() {

        QuickFailRuntimeExceptionHandler handler = new QuickFailRuntimeExceptionHandler();

        for (SimplePopulousPattern pattern : populousModel.getPopulousPatterns()) {
            System.err.println("Got pattern: " + pattern.getPatternName() + "\n" + pattern.getPatternValue());
            this.patternModel = parser.parse(pattern.getPatternValue());

            List<OWLAxiomChange> changes = new ArrayList<OWLAxiomChange>();


            if (!patternModel.getInputVariables().isEmpty()) {

                Map<String, Variable> opplVariableMap = new HashMap<String, Variable>();
                for (Variable v : patternModel.getInputVariables()) {
                    System.err.println("Loading input variables:" + v.getName());
                    opplVariableMap.put(v.getName(), v);
                }

                shortFromMapper = new HashMap<Integer, Map<String, IRI>>();
                // for each column, create a new short form provider for the specific ontologies
                for (Integer column : populousModel.getVariableMapper().keySet()) {

                    // get the range for this column
                    Range range = new Range(workbook.getSheet(0), column, populousModel.getStartRow(), column, populousModel.getEndRow());

                    if (!workbookManager.getContainingOntologyTermValidations(range).isEmpty()) {
                        for (OntologyTermValidation val : workbookManager.getContainingOntologyTermValidations(range)) {

                            OntologyTermValidationDescriptor desc = val.getValidationDescriptor();
                            shortFromMapper.put(column, getTermToURIMapping(desc.getTerms()));
                        }
                    }
                    else {
                        shortFromMapper.put(column, new HashMap<String, IRI>());
                    }

                }


                // look for data in the rows for a pattern...

                logger.debug("Start row:" + populousModel.getStartRow());
                logger.debug("End row:" + populousModel.getEndRow());

                for (int row =  populousModel.getStartRow(); row < populousModel.getEndRow() + 1; row++  ) {
                    logger.debug("Reading row:" + row);


                    InstantiatedPatternModel ipm = pf.getPatternFactory().createInstantiatedPatternModel(patternModel, handler);


                    for (Integer column : populousModel.getVariableMapper().keySet()) {

                        if (column != null) {

                            Cell cell = workbook.getSheet(populousModel.getSheet()).getCellAt(column, row);

                            if (cell != null) {


//                                System.err.println("cell value: " + cell.getValue());
                                String variable = populousModel.getVariableMapper().get(column);

                                if (StringUtils.isNotBlank(variable)) {

                                    if (opplVariableMap.keySet().contains(variable)) {
                                        Variable v = opplVariableMap.get(variable);
                                        VariableType type = v.getType();

                                        if (type.accept(variableVisitor).equals(5)) {
                                            if (StringUtils.isNotBlank(cell.getValue())) {
//                                                System.err.println("instantiating variable:" + opplVariableMap.get(variable).getName() + " to " + cell.getValue());
                                                String [] values = cell.getValue().split("\\s*\\|\\s*");
                                                for (String s : values) {
                                                    s = s.trim();
                                                    ipm.instantiate(opplVariableMap.get(variable), ontologyManager.getOWLDataFactory().getOWLLiteral(s));
                                                }
                                            }
                                        }
                                        else if (type.accept(variableVisitor).equals(1)) {
                                            for (OWLEntity entity : getOWLEntities2(cell, column, 1)) {
                                                if (entity.isOWLClass()) {
                                                    logger.debug("instantiating variable:" + opplVariableMap.get(variable).getName() + " to " + entity.getIRI().toString());
//                                                    System.err.println("instantiating variable:" + opplVariableMap.get(variable).getName() + " to " + entity.getIRI().toString());
                                                    ipm.instantiate(opplVariableMap.get(variable), entity);
                                                }

                                            }
                                        }
                                        else if (type.accept(variableVisitor).equals(4)) {
                                            for (OWLEntity entity : getOWLEntities2(cell, column, 4)) {
                                                if (entity.isOWLNamedIndividual()) {
                                                    logger.debug("instantiating variable:" + opplVariableMap.get(variable).getName() + " to " + entity.getIRI().toString());
//                                                    System.err.println("instantiating variable:" + opplVariableMap.get(variable).getName() + " to " + entity.getIRI().toString());
                                                    ipm.instantiate(opplVariableMap.get(variable), entity);
                                                }

                                            }
                                        }


                                    }


                                }
                            }
                        }
                    }

                    NonClassPatternExecutor patternExecutor = new NonClassPatternExecutor(ipm, activeOntology, ontologyManager, IRI.create("http://e-lico.eu/populous#OPPL_pattern"), handler);
                    changes.addAll(patternExecutor.visit(patternModel));
//                    changes.addAll(changes);

//                    patternModel.accept()
//                    changes.addAll(patternModel.accept(patternExecutor));

                }

            }
            else {
//                InstantiatedPatternModel ipm = pf.getPatternFactory().createInstantiatedPatternModel(patternModel);
                // its just a script so execute it!
                InstantiatedPatternModel ipm = pf.getPatternFactory().createInstantiatedPatternModel(patternModel, handler);

                NonClassPatternExecutor patternExecutor = new NonClassPatternExecutor(ipm, activeOntology, ontologyManager, IRI.create("http://e-lico.eu/populous#OPPL_pattern"), handler);
                changes.addAll(patternExecutor.visit(ipm.getPatternModel().getOpplStatement()));


            }

            for (OWLAxiomChange change : changes) {
                System.err.println(change.toString());
            }
            ontologyManager.applyChanges(changes);

        }



    }

    private VariableTypeVisitorEx variableVisitor = new VariableTypeVisitorEx ()
    {

        public Object visitCLASSVariableType(CLASSVariableType classVariableType) {
            return 1;
        }

        public Object visitOBJECTPROPERTYVariableType(OBJECTPROPERTYVariableType objectpropertyVariableType) {
            return 2;
        }

        public Object visitDATAPROPERTYVariableType(DATAPROPERTYVariableType datapropertyVariableType) {
            return 3;
        }

        public Object visitINDIVIDUALVariableType(INDIVIDUALVariableType individualVariableType) {
            return 4;
        }

        public Object visitCONSTANTVariableType(CONSTANTVariableType constantVariableType) {
            return 5;
        }

        public Object visitANNOTATIONPROPERTYVariableType(ANNOTATIONPROPERTYVariableType annotationpropertyVariableType) {
            return 6;
        }
    };


    private Map<String, IRI> getTermToURIMapping(Collection<Term> terms) {
        Map<String, IRI> result = new HashMap<String, IRI> ();
        for (Term t : terms) {
            result.put(t.getName(), t.getIRI());
        }
        return result;  //To change body of created methods use File | Settings | File Templates.
    }

    public void saveOntology(IRI file) throws OWLOntologyStorageException {
        ontologyManager.saveOntology(activeOntology, file);
    }

    private Set<OWLEntity> getOWLEntities2(Cell cell, Integer column, Integer type) {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();

        String value = cell.getValue();
        if (!value.equals("")) {
            String [] values = value.split("\\s*\\|\\s*");
            for (String s : values) {
                s = s.trim();
                logger.debug("Looking up:" + s);
                entities.addAll(getEntityShortForms2(s, column, type));
            }
        }

        return entities;
    }

//    private Set<OWLEntity> getOWLEntities(Cell cell) {
//        Set<OWLEntity> entities = new HashSet<OWLEntity>();
//
//        String value = cell.getValue();
//        if (!value.equals("")) {
//            String [] values = value.split("\\s*\\|\\s*");
//            for (String s : values) {
//                s = s.trim();
//                logger.debug("Looking up:" + s);
//                entities.addAll(getEntityShortForms(s));
//            }
//        }
//
//        return entities;
//
//    }

    private Set<OWLEntity> getEntityShortForms2(String shortForm, Integer column, Integer type) {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();

        // first look in the validation list
        for (String s : shortFromMapper.get(column).keySet()) {
            if (s.toLowerCase().equals(shortForm.toLowerCase())) {
                logger.debug("Entity found:" + s.toLowerCase());

                if (type == 1) {

                    entities.add(ontologyManager.getOWLDataFactory().getOWLClass(shortFromMapper.get(column).get(s)));

                }
                else if (type == 4) {

                    entities.add(ontologyManager.getOWLDataFactory().getOWLNamedIndividual(shortFromMapper.get(column).get(s)));

                }
            }
        }

        // then look in the all the ontologies
        if (entities.isEmpty()) {
            for (String s : shortFormProvider.getShortForms()) {
                if (s.toLowerCase().equals(shortForm.toLowerCase())) {
                    logger.debug("Entity found:" + s.toLowerCase());
                    entities.addAll(shortFormProvider.getEntities(s));
                }
                else if (s.toLowerCase().equals(shortForm.toLowerCase().replaceAll(" ", "_")) ) {
                    logger.debug("Entity found:" + s.toLowerCase());
                    entities.addAll(shortFormProvider.getEntities(s));
                }
            }
        }

        // finally create a new entity
        if (entities.isEmpty()) {
            OWLEntity e = getNewEntities2(shortForm, type);
            entities.add(e);
        }
        return entities;
    }

//    private Set<OWLEntity> getEntityShortForms(String shortForm) {
//        Set<OWLEntity> entities = new HashSet<OWLEntity>();
//        Set<String> unknown = new HashSet<String>();
//
//        for (String s : shortFormProvider.getShortForms()) {
//            if (s.toLowerCase().equals(shortForm.toLowerCase())) {
//                logger.debug("Entity found:" + s.toLowerCase());
//
//                entities.addAll(shortFormProvider.getEntities(s));
//            }
//        }
//
//        if (entities.isEmpty()) {
//            entities.add(getNewEntities(shortForm));
//        }
//        return entities;
//    }

    private OWLEntity getNewEntities2(String newTerm, Integer type) {

//        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        // this is wrong, assumes classes all the time.
        //
        logger.debug("Creating new term:" + newTerm);


//        OWLClass cls = ontologyManager.getOWLDataFactory().getOWLClass(
//                IRI.create(newEntities.getBaseIRI().toURI() + newEntities.getPrefix() + randomGenerator.nextInt(60000)));
//        ontologyManager.addAxiom(activeOntology, ontologyManager.getOWLDataFactory().getOWLDeclarationAxiom(cls));
//        IRI iri = OWLRDFVocabulary.RDFS_LABEL.getIRI();
//        OWLAnnotationProperty prop = ontologyManager.getOWLDataFactory().getOWLAnnotationProperty(iri);
//        OWLStringLiteral literal = ontologyManager.getOWLDataFactory().getOWLStringLiteral(newTerm);
//        ontologyManager.addAxiom(activeOntology, ontologyManager.getOWLDataFactory().getOWLAnnotationAssertionAxiom(prop, cls.getIRI(), literal));

        if (type == 1) {
            SimpleOWLEntityCreationSet<OWLClass> ecs = null;
            try {
                ecs = cf.createOWLClass(newTerm, entityCreation.getDefaultBaseURI());
            } catch (OWLEntityCreationException e) {
                e.printStackTrace();
            }
            if (ecs.getOntologyChanges() != null) {
                ontologyManager.applyChanges(ecs.getOntologyChanges());
            }
            shortFormProvider.add(ecs.getOWLEntity());
            return ecs.getOWLEntity();

        }
        else if (type == 4) {
            SimpleOWLEntityCreationSet<OWLNamedIndividual> ecs = null;
            try {
                ecs = cf.createOWLIndividual(newTerm, entityCreation.getDefaultBaseURI());
            } catch (OWLEntityCreationException e) {
                e.printStackTrace();
            }
            if (ecs.getOntologyChanges() != null) {
                ontologyManager.applyChanges(ecs.getOntologyChanges());
            }
            shortFormProvider.add(ecs.getOWLEntity());
            return ecs.getOWLEntity();

        }
        return null;

    }

//    private OWLEntity getNewEntities(String newTerm) {
//
////        Set<OWLEntity> entities = new HashSet<OWLEntity>();
//        // this is wrong, assumes classes all the time.
//        //
//        logger.debug("Creating new term:" + newTerm);
//
//            OWLClass cls = ontologyManager.getOWLDataFactory().getOWLClass(
//                    IRI.create(newEntities.getBaseIRI().toURI() + "#" + newTerm.replaceAll(" ", "_")));
//            ontologyManager.addAxiom(activeOntology, ontologyManager.getOWLDataFactory().getOWLDeclarationAxiom(cls));
//
//
//        return cls;

//    }

}
