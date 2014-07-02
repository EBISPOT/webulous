package uk.ac.ebi.fgpt.populous.patterns;

import org.semanticweb.owlapi.model.IRI;
import uk.ac.ebi.fgpt.populous.model.AutoIDGenerator;
import uk.ac.ebi.fgpt.populous.model.EntityCreation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Author: Simon Jupp<br>
 * Date: Oct 7, 2010<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class PopulousModel {

    private IRI sourceOntologyIRI;

    private IRI sourceOntologyPhysicalIRI;
    private Set<IRI> importedOntologies;

    private  LinkedHashMap<Integer, String> variableBinding;

    private int sheet = 0;
    private int startRow = 0;
    private int endRow = 0;

    private Set<SimplePopulousPattern> simplePopulousPattern;
    private Set<String> variables;
    private List<Integer> columns;
    private boolean isNewSourceOntology = true;

    private uk.ac.ebi.fgpt.populous.patterns.entity.SimpleEntityCreation entityPrefs;

    public PopulousModel () {
        importedOntologies = new HashSet<IRI> ();
        simplePopulousPattern = new HashSet<SimplePopulousPattern>();
        variables = new HashSet<String>();
        columns = new ArrayList<Integer>();
    }

    public EntityCreation getEntity() {
        return entityPrefs;
    }

    public void setEntity(uk.ac.ebi.fgpt.populous.patterns.entity.SimpleEntityCreation entityPrefs) {
        this.entityPrefs = entityPrefs;
    }


    public Set<SimplePopulousPattern> getPopulousPatterns() {
        return simplePopulousPattern;
    }


    public Set<IRI> getImportedOntologies() {
        return importedOntologies;
    }


    public IRI getSourceOntologyIRI() {
        return sourceOntologyIRI;
    }

    public void setSourceOntologyIRI(IRI sourceOntologyIRI) {
        this.sourceOntologyIRI = sourceOntologyIRI;
    }

    public LinkedHashMap<Integer, String> getVariableMapper() {
        return variableBinding;
    }

    public void setVariableMapper( LinkedHashMap<Integer, String> variableMapper) {
        this.variableBinding = variableMapper;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow -1;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow -1;
    }


    public List<Integer> getColumns () {
        return columns;
    }


    public int getColumnInt (String columnName) {
        // this is wrong!!
        return (int) columnName.charAt(0) - 65;
    }

    public IRI getSourceOntologyPhysicalIRI() {
        return sourceOntologyPhysicalIRI;
    }

    public void setSourceOntologyPhysicalIRI(IRI sourceOntologyPhysicalIRI) {
        this.sourceOntologyPhysicalIRI = sourceOntologyPhysicalIRI;
    }

    public void setSheet(int i) {
        this.sheet = i;

    }

    public int getSheet () {
        return sheet;
    }


//    COMMENTED THIS OUT BECAUSE I'M NOT QUITE SURE WHAT IT DOES YET
//    public Set<OntologyTermValidation> getValidations(WorkbookManager workbookManager, Workbook workbook) {
//
//        Set<OntologyTermValidation> validationSet = new HashSet<OntologyTermValidation>();
//        if (!columns.isEmpty()) {
//            for (int columnInt : this.columns) {
//                Range range = new Range(workbook.getSheet(sheet), columnInt, startRow, columnInt, endRow);
//                System.err.println(range.toString());
//                validationSet.addAll(workbookManager.getOntologyTermValidationManager().getIntersectingValidations(range));
//            }
//        }
//        return validationSet;
//    }

    public boolean isNewOntology() {
        return isNewSourceOntology;
    }

    public void isNewOntology(boolean b) {
        isNewSourceOntology = b;
    }

    public Set<String> getVariables() {
        return variables;
    }

    public void loadProperties (File file) throws IOException {
        Properties p = new Properties();
        p.loadFromXML(new FileInputStream(file));
        setProperties(p);
    }

    private void setProperties (Properties prop) {
        setSheet(Integer.valueOf(prop.getProperty("sheet")));
        int col = 0;
        getColumns().clear();
        while (prop.containsKey(("column" + col))) {
            getColumns().add(Integer.valueOf(prop.getProperty("column" + col)));
            col++;
        }

        setStartRow(Integer.valueOf(prop.getProperty("startrow")) + 1);
        setEndRow(Integer.valueOf(prop.getProperty("endrow")) + 1);
        setSourceOntologyIRI(IRI.create(prop.getProperty("sourceontology.iri")));
        setSourceOntologyPhysicalIRI(IRI.create(prop.getProperty("sourceontology.physical")));
        this.isNewSourceOntology = Boolean.valueOf(prop.getProperty("sourceontology.isnew"));

        int k = 0;
        getImportedOntologies().clear();
        while (prop.containsKey("importedontology.iri" + k)) {
            getImportedOntologies().add(IRI.create(prop.getProperty("importedontology.iri" + k)));
            k++;
        }

        LinkedHashMap<Integer, String> variableMapper = new LinkedHashMap<Integer, String> ();
        for (int cols : columns) {
            variableMapper.put(cols, prop.getProperty("bindingcol." + cols));
        }
        getVariables().addAll(variableMapper.values());
        setVariableMapper(variableMapper);

        int j = 0;
        getPopulousPatterns().clear();
        while (prop.containsKey("pattern.id." + j)) {
            SimplePopulousPattern p = new SimplePopulousPattern();
            String id = prop.getProperty("pattern.id." + j);
            p.setPatternID(id);
            p.setPatternName(prop.getProperty("pattern.name." + id));
            p.setPatternValue(prop.getProperty("pattern.value." + id));
            getPopulousPatterns().add(p);
            j++;
        }

        uk.ac.ebi.fgpt.populous.patterns.entity.SimpleEntityCreation entityCreation = new uk.ac.ebi.fgpt.populous.patterns.entity.SimpleEntityCreation();

        // entity creation preferences
        entityCreation.setUseDefaultBaseURI(Boolean.valueOf(prop.getProperty("entity.usebaseuri")));
        entityCreation.setDefaultBaseURI(prop.getProperty("entity.baseuri"));
        entityCreation.setDefaultSeparator(prop.getProperty("entity.seperator"));
        entityCreation.setFragmentAutoGenerated(Boolean.valueOf(prop.getProperty("entity.autogeneratefragment")));
        entityCreation.setGenerateNameLabel(Boolean.valueOf(prop.getProperty("entity.namelabel")));
        entityCreation.setGenerateIDLabel(Boolean.valueOf(prop.getProperty("entity.idlabel")));
        entityCreation.setNameLabelURI(URI.create(prop.getProperty("entity.labeluri")));
        entityCreation.setNameLabelLang(prop.getProperty("entity.labellang"));
        try {
            entityCreation.setAutoIDGeneratorClass((Class<AutoIDGenerator>)Class.forName(prop.getProperty("entity.autoidclass")));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        entityCreation.setAutoIDStart(Integer.valueOf(prop.getProperty("entity.start")));
        entityCreation.setAutoIDEnd(Integer.valueOf(prop.getProperty("entity.end")));
        entityCreation.setAutoIDDigitCount(Integer.valueOf(prop.getProperty("entity.digit")));
        entityCreation.setPrefix(prop.getProperty("entity.prefix"));
        entityCreation.setSuffix(prop.getProperty("entity.suffix"));
        setEntity(entityCreation);


    }

    public Properties getPopulousModelProperties () {

        Properties prop = new Properties();

        prop.setProperty("sheet", String.valueOf(sheet));
        for (int x = 0 ; x<columns.size(); x++) {
            prop.setProperty("column" + x, String.valueOf(columns.get(x)));
        }

        prop.setProperty("startrow", String.valueOf(startRow));
        prop.setProperty("endrow", String.valueOf(endRow));
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
        for (SimplePopulousPattern p : simplePopulousPattern) {
            prop.setProperty("pattern.id." + j , p.getPatternID());
            prop.setProperty("pattern.name." + p.getPatternID(), p.getPatternName());
            prop.setProperty("pattern.value." + p.getPatternID(), p.getPatternValue());
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

    public void saveModel (File file) throws IOException {

        getPopulousModelProperties().storeToXML(new FileOutputStream(file), "populous model");
//        prop.storeToXML();


//    XMLOutputFactory factory      = XMLOutputFactory.newInstance();
//
//        try {
//            XMLStreamWriter writer = factory.createXMLStreamWriter(
//                    new FileWriter(file));
//
//            writer.writeStartDocument();
//            writer.writeStartElement("workflow");
//
//            writer.writeStartElement("sheet");
//            writer.writeCharacters(String.valueOf(sheet));
//            writer.writeEndElement();
//
//
//            for (int col : columns) {
//                writer.writeStartElement("column");
//                writer.writeCharacters(String.valueOf(col));
//                writer.writeEndElement();
//            }
//
//            writer.writeStartElement("startRow");
//            writer.writeCharacters(String.valueOf(startRow));
//            writer.writeEndElement();
//
//            writer.writeStartElement("endRow");
//            writer.writeCharacters(String.valueOf(endRow));
//            writer.writeEndElement();
//
//            writer.writeStartElement("sourceOntology");
//            writer.writeAttribute("iri", sourceOntologyIRI.toString());
//            writer.writeAttribute("physical", sourceOntologyPhysicalIRI.toString());
//            writer.writeAttribute("new", String.valueOf(isNewSourceOntology));
//            writer.writeEndElement();
//
//            writer.writeStartElement("importedOntology");
//            for (IRI iri : importedOntologies) {
//                writer.writeStartElement("import");
//                writer.writeAttribute("iri", iri.toString());
//                writer.writeEndElement();
//            }
//            writer.writeEndElement();
//
//            writer.writeStartElement("patterns");
//            for (Integer col : variableBinding.keySet()) {
//                writer.writeStartElement("pattern");
//
//                writer.writeEndElement();
//            }
//            writer.writeEndElement();
//
//
//
//
//            writer.writeStartElement("data");
//            writer.writeAttribute("name", "value");
//            writer.writeEndElement();
//
//            writer.writeEndElement(); // workflow
//            writer.writeEndDocument();
//
//            writer.flush();
//            writer.close();
//
//        } catch (XMLStreamException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

//    public static void main(String[] args) {
//        WorkbookManager manager = new WorkbookManager();
//
//        try {
//            Workbook workbook = manager.loadWorkbook(URI.create("file:/Users/simon/Desktop/kupo_cells_small.xls"));
//            PopulousModel model = new PopulousModel();
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
