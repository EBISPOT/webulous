package uk.ac.ebi.fgpt.populous.controller;

import junit.framework.TestCase;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Created by dwelter on 09/09/14.
 */
public class TestPopulousController extends TestCase{

    private     PopulousController populousController;

    @Before
    public void setUp(){

       populousController = new PopulousController();



    }


    @Test
    public void testSourceOntologyParameters(){

        String onto1 = "efo";
        String onto2 = "clo";

        Map<String, Object> efoSource = populousController.getSourceOntologyParameters(onto1);
        Map<String, String> source = (Map<String, String>) efoSource.get("sourceOntology");
        assertEquals("Experimental Factor Ontology", source.get("name"));


        Map<String, Object> cloSource = populousController.getSourceOntologyParameters(onto2);
        assertNull(cloSource.get("sourceOntology"));

    }
    
    
    @Test
    public void testDataSubmission(){

        String data = "{\"configuration\":{\"sourceOntology\":\"EFO\",\"dataRestrictions\":[{\"columnIndex\":0,\"opplVariable\":\"?cellLine\",\"restrictionOntology\":null,\"restrictionType\":null,\"restrictionName\":null,\"restrictionURI\":null},{\"columnIndex\":1,\"opplVariable\":\"?disease\",\"restrictionOntology\":\"EFO\",\"restrictionType\":\"descendants\",\"restrictionName\":\"disease\",\"restrictionURI\":\"http://www.ebi.ac.uk/efo/EFO_0000408\"},{\"columnIndex\":2,\"opplVariable\":null,\"restrictionOntology\":null,\"restrictionType\":null,\"restrictionName\":null,\"restrictionURI\":null},{\"columnIndex\":3,\"opplVariable\":null,\"restrictionOntology\":null,\"restrictionType\":null,\"restrictionName\":null,\"restrictionURI\":null},{\"columnIndex\":4,\"opplVariable\":null,\"restrictionOntology\":null,\"restrictionType\":null,\"restrictionName\":null,\"restrictionURI\":null},\n" +
                "{\"columnIndex\":5,\"opplVariable\":\"?sex\",\"restrictionOntology\":\"EFO\",\"restrictionType\":\"children\",\"restrictionName\":\"sex\",\"restrictionURI\":\"http://www.ebi.ac.uk/efo/EFO_0000695\"},{\"columnIndex\":6,\"opplVariable\":null,\"restrictionOntology\":null,\"restrictionType\":null,\"restrictionName\":null,\"restrictionURI\":null},{\"columnIndex\":7,\"opplVariable\":null,\"restrictionOntology\":null,\"restrictionType\":null,\"restrictionName\":null,\"restrictionURI\":null}]},\"data\":[[\"BL-2\",\"Burkitts lymphoma\",\"cell type\",\"bone marrow\",\"Homo sapiens\",\"male\",null,\"BL-2\"],[\"JVM-2\",\"lymphoma\",\"lymphoblast\",\"blood\",\"Homo sapiens\",\"female\",null,\"JVM-2\"],[\"KARPAS 422\",\"diffuse large B-cell lymphoma\",\"cell type\",\"lymphatic system\",\"Homo sapiens\",\"female\",null,\"KARPAS 422\"],[\"U-266\",\"plasmacytoma\",\"lymphocyte\",\"blood\",\"Homo sapiens\",\"male\",null,\"U-266\"],[\"Z-138\",\"lymphoma\",\"lymphoblast\",\"organism part\",\"Homo sapiens\",\"male\",null,\"Z-138\"],[\"MEL-GATA-1-ER\",\"type II diabetes\",\"erythroblast\",\"blood\",\"Mus musculus\",null,\"This is a mouse suspension cell line derived from MEL cells by stable transfection with a GATA-1-ER fusion protein construct as described by Choe et al., 2003 (Cancer Res 63, 6363–6369, 2003). These cells can be terminally differentiated into mature erythroid cells with β-estradiol treatment, while GATA-1 alone can induce MEL cells to differentiate and to lose their tumorigenic properties. [PMID: 14559825]\",\"MEL-GATA-1-ER\"],[\"Patski\",\"breast cancer\",\"fibroblast\",\"kidney\",\"Mus musculus\",\"female\",\"Mouse Embryonic Kidney Fibroblast. As described in Lingenfelter et al., 1998 (Nat Genet. 1998 18:212-3) and Yang et al., 2010 (Genome Res. 2010 20:614-22), PATSKI is a female interspecific mouse fibroblast that was derived from the embryonic kidney of an M.spretus x C57BL/6J hybrid mouse such that the C57Bl/6J X chromosome (maternal) is always the inactive X. This is an adherent cell line.\",\"Patski\"],[\"416B\",\"amyloidosis\",\"myeloid lineage restricted progenitor cell\",\"blood\",\"Mus musculus\",\"male\",\"Mouse hematopoietic suspension cell line positive for CD34. The cells have a diploid complement of chromosomes, are non-tumorigenic and bipotential (can be induced to differentiate in vivo into two distinct haematopoietic lineages), and which in appropriate circumstances protect mice from potentially lethal radiation. [PMID: 763330]\",\"416B\"],[\"ES-Bruce4\",null,\"embryonic stem cell\",\"embryo\",\"Mus musculus\",\"male\",\"An embryonic cell line isolated from C57BL/6 mouse strain. Injection of Bruce4 cells into C57BL/6 blastocysts will produce agouti chimeras.\",\"ES-Bruce4\"],[\"46C\",\"liver disease\",\"mouse neural progenitor cell\",\"embryo\",\"Mus musculus\",\"male\",\"46C is an embryonic cell line, constructed in the laboratory of Austin Smith, in which a drug resistance gene is placed under the control of a Sox1 promoter. Cells were isolated from the 129a mouse strain. [PMID: 12524553]\",\"46C\"],[\"TT2\",null,\"embryonic stem cell\",\"embryo\",\"Mus musculus\",\"male\",\"ES-cells isolated from C57BL/6xCBA\",\"TT2\"],[\"J185a\",null,\"myoblast\",\"embryo\",\"Mus musculus\",null,\"Fetal myoblast Desmin+\",\"J185a\"]]}\n" +
                "\n";


//        ObjectMapper mapper = new ObjectMapper();
//        JsonFactory factory = mapper.getJsonFactory();

        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(jsonFactory);


        try {
         //   JsonParser jp = factory.createJsonParser(data);

            JsonNode actualObj = mapper.readTree(data);
            System.out.println(actualObj.get("configuration"));
            System.out.println(actualObj.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
