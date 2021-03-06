package uk.ac.ebi.spot.webulous.model;

import org.semanticweb.owlapi.model.IRI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Author: Simon Jupp<br>
 * Date: Oct 7, 2010<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class PropertyBasedPopulousModel implements PopulousModel{

    private IRI sourceOntologyIRI;

    private IRI sourceOntologyPhysicalIRI;
    private String sourceOntologyOutputLocation;
    private Set<IRI> importedOntologies;

    private  LinkedHashMap<Integer, String> variableBinding;

    private Set<PopulousPattern> simplePopulousPattern;
    private Set<String> variables;
    private boolean isNewSourceOntology = true;

    private Map<Integer, PopulousDataRestriction> restrictions;

    private EntityCreation entityPrefs;

    public PropertyBasedPopulousModel() {
        importedOntologies = new HashSet<IRI> ();
        simplePopulousPattern = new HashSet<PopulousPattern>();
        variables = new HashSet<String>();
    }

    @Override
    public EntityCreation getEntity() {
        return entityPrefs;
    }

    public void createEntity(EntityCreation entityPrefs) {
        this.entityPrefs = entityPrefs;
    }

    @Override
    public Set<PopulousPattern> getPopulousPatterns() {
        return simplePopulousPattern;
    }

    public void setPopulousPattern(Set<PopulousPattern> patterns){
        this.simplePopulousPattern = patterns;
    }

    @Override
    public Set<IRI> getImportedOntologies() {
        return importedOntologies;
    }

    public void setImportedOntologies(Set<IRI> importedOntologies){
        this.importedOntologies = importedOntologies;
    }

    @Override
    public IRI getSourceOntologyIRI() {
        return sourceOntologyIRI;
    }

    public void setSourceOntologyIRI(IRI sourceOntologyIRI) {
        this.sourceOntologyIRI = sourceOntologyIRI;
    }

    @Override
    public LinkedHashMap<Integer, String> getVariableMapper() {
        return variableBinding;
    }

    public void setVariableMapper( LinkedHashMap<Integer, String> variableMapper) {
        this.variableBinding = variableMapper;
    }

    @Override
    public IRI getSourceOntologyPhysicalIRI() {
        return sourceOntologyPhysicalIRI;
    }

    public void setSourceOntologyPhysicalIRI(IRI sourceOntologyPhysicalIRI) {
        this.sourceOntologyPhysicalIRI = sourceOntologyPhysicalIRI;
    }

    @Override
    public boolean isNewOntology() {
        return isNewSourceOntology;
    }

    public void setIsNewOntology(boolean b) {
        isNewSourceOntology = b;
    }

    @Override
    public Set<String> getVariables() {
        return variables;
    }

    public void setVariables(Set<String> variables){
        this.variables = variables;
    }

    public Map<Integer, PopulousDataRestriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Map<Integer, PopulousDataRestriction> restrictions) {
        this.restrictions = restrictions;
    }


//    @Override
//    public void loadProperties (File file) throws IOException {
//        Properties p = new Properties();
//        p.loadFromXML(new FileInputStream(file));
//        setProperties(p);
//    }
//
//    private void setProperties (Properties prop) {
//
//        setSourceOntologyIRI(IRI.create(prop.getProperty("sourceontology.iri")));
//        setSourceOntologyPhysicalIRI(IRI.create(prop.getProperty("sourceontology.physical")));
//        this.isNewSourceOntology = Boolean.valueOf(prop.getProperty("sourceontology.isnew"));
//
//        int k = 0;
//        getImportedOntologies().clear();
//        while (prop.containsKey("importedontology.iri" + k)) {
//            getImportedOntologies().add(IRI.create(prop.getProperty("importedontology.iri" + k)));
//            k++;
//        }
//
//        LinkedHashMap<Integer, String> variableMapper = new LinkedHashMap<Integer, String> ();
//        for (int cols : columns) {
//            variableMapper.put(cols, prop.getProperty("bindingcol." + cols));
//        }
//        getVariables().addAll(variableMapper.values());
//        setVariableMapper(variableMapper);
//
//        int j = 0;
//        getPopulousPatterns().clear();
//        while (prop.containsKey("pattern.id." + j)) {
//            SimplePopulousPattern p = new SimplePopulousPattern();
//            String id = prop.getProperty("pattern.id." + j);
//            p.setPatternID(id);
//            p.setPatternName(prop.getProperty("pattern.name." + id));
//            p.setPatternValue(prop.getProperty("pattern.value." + id));
//            getPopulousPatterns().add(p);
//            j++;
//        }
//
//        SimpleEntityCreation entityCreation = new SimpleEntityCreation();
//
//        // entity creation preferences
//        entityCreation.setUseDefaultBaseURI(Boolean.valueOf(prop.getProperty("entity.usebaseuri")));
//        entityCreation.setDefaultBaseURI(prop.getProperty("entity.baseuri"));
//        entityCreation.setDefaultSeparator(prop.getProperty("entity.seperator"));
//        entityCreation.setFragmentAutoGenerated(Boolean.valueOf(prop.getProperty("entity.autogeneratefragment")));
//        entityCreation.setGenerateNameLabel(Boolean.valueOf(prop.getProperty("entity.namelabel")));
//        entityCreation.setGenerateIDLabel(Boolean.valueOf(prop.getProperty("entity.idlabel")));
//        entityCreation.setNameLabelURI(URI.create(prop.getProperty("entity.labeluri")));
//        entityCreation.setNameLabelLang(prop.getProperty("entity.labellang"));
//        try {
//            entityCreation.setAutoIDGeneratorClass((Class<AutoIDGenerator>)Class.forName(prop.getProperty("entity.autoidclass")));
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        entityCreation.setAutoIDStart(Integer.valueOf(prop.getProperty("entity.start")));
//        entityCreation.setAutoIDEnd(Integer.valueOf(prop.getProperty("entity.end")));
//        entityCreation.setAutoIDDigitCount(Integer.valueOf(prop.getProperty("entity.digit")));
//        entityCreation.setPrefix(prop.getProperty("entity.prefix"));
//        entityCreation.setSuffix(prop.getProperty("entity.suffix"));
//        setEntity(entityCreation);
//
//
//    }

//    @Override
    public Properties getPopulousModelProperties () {

        Properties prop = new Properties();

        prop.setProperty("sourceontology.iri", sourceOntologyIRI.toString());
        prop.setProperty("sourceontology.physical", sourceOntologyPhysicalIRI.toString());
        prop.setProperty("sourceontology.isnew", String.valueOf(isNewSourceOntology));

        Integer k = 0;
        for (IRI iri : importedOntologies) {
            prop.setProperty("importedontology.iri" + k, iri.toString());
            k++;
        }

        for (Integer col : variableBinding.keySet()) {
            prop.setProperty("bindingcol." + col, variableBinding.get(col));
        }

        Integer j = 0;
        for (PopulousPattern p : simplePopulousPattern) {
//            prop.setProperty("pattern.id." + j , p.getPatternID());
//            prop.setProperty("pattern.name." + p.getPatternID(), p.getPatternName());
//            prop.setProperty("pattern.value." + p.getPatternID(), p.getPatternValue());
            j++;
        }

        // entity creation preferences
        prop.setProperty("entity.usebaseuri", String.valueOf(entityPrefs.useDefaultBaseURI()));
        prop.setProperty("entity.baseuri", entityPrefs.getDefaultBaseURI().toString());
        prop.setProperty("entity.seperator", entityPrefs.getDefaultSeparator());
        prop.setProperty("entity.autogeneratefragment", String.valueOf(entityPrefs.isFragmentAutoGenerated()));
        prop.setProperty("entity.namelabel", String.valueOf(entityPrefs.isGenerateNameLabel()));
        prop.setProperty("entity.idlabel", String.valueOf(entityPrefs.isGenerateIDLabel()));
        prop.setProperty("entity.labeluri", entityPrefs.getNameLabelURI().toString());
        prop.setProperty("entity.labellang", entityPrefs.getNameLabelLang());
        prop.setProperty("entity.autoidclass", entityPrefs.getAutoIDGeneratorName());
        prop.setProperty("entity.start", String.valueOf(entityPrefs.getAutoIDStart()));
        prop.setProperty("entity.end", String.valueOf(entityPrefs.getAutoIDEnd()));
        prop.setProperty("entity.digit", String.valueOf(entityPrefs.getAutoIDDigitCount()));
        prop.setProperty("entity.prefix", entityPrefs.getPrefix());
        prop.setProperty("entity.suffix", entityPrefs.getSuffix());

        return prop;
    }

    @Override
    public void saveModel (File file) throws IOException {

        getPopulousModelProperties().storeToXML(new FileOutputStream(file), "populous model");
    }

    @Override
    public String getSourceOntologyOutputLocation() {
        return sourceOntologyOutputLocation;
    }


    public void setSourceOntologyOutputLocation(String sourceOntologyOutputLocation) {
        this.sourceOntologyOutputLocation = sourceOntologyOutputLocation;
    }

//    public static void main(String[] args) {
//        WorkbookManager manager = new WorkbookManager();
//
//        try {
//            Workbook workbook = manager.loadWorkbook(URI.create("file:/Users/simon/Desktop/kupo_cells_small.xls"));
//            SimplePopulousModel model = new SimplePopulousModel();
//
//
//
//            ArrayList<Integer> columns = new ArrayList<Integer> ();
//            columns.add(0);
//            columns.add(2);
//            columns.add(3);
//            model.setSheet(0);
//            model.getColumns().addAll(columns);
//            model.setStartRow(1);
//            model.setEndRow(10);
//
//            // load ontologies
//            model.setSourceOntologyIRI(IRI.create("http://www.elico.eu/ontology/kupo"));
//
//            Set<OntologyTermValidation> validations = model.getValidations(manager, workbook);
//
//            LinkedHashMap<Integer, String> variableBinding = new LinkedHashMap<Integer, String>();
//            variableBinding.put(0, "?cell");
//            variableBinding.put(2, "?anatomyPart");
//            variableBinding.put(3, "?participant");
//
//            model.setVariableMapper(variableBinding);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//
//    }

}
