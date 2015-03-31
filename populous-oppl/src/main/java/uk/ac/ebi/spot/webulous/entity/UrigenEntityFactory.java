package uk.ac.ebi.spot.webulous.entity;

import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.webulous.entity.urigen.PreferenceBean;
import uk.ac.ebi.spot.webulous.entity.urigen.UrigenEntityBean;
import uk.ac.ebi.spot.webulous.entity.urigen.UrigenRequestBean;
import uk.ac.ebi.spot.webulous.entity.urigen.UserBean;
import uk.ac.ebi.spot.webulous.exception.AutoIDException;
import uk.ac.ebi.spot.webulous.exception.OWLEntityCreationException;
import uk.ac.ebi.spot.webulous.model.LabelDescriptor;
import uk.ac.ebi.spot.webulous.model.OWLEntityCreationSet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Simon Jupp
 * @date 31/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class UrigenEntityFactory extends CustomOWLEntityFactory
{
    private Logger logger = LoggerFactory.getLogger(getClass());


    private String urigenServer;
    private String apikey;
    private String ontologyUri;
    private PreferenceBean preferenceBean;
    private UserBean user;
    private OWLOntologyManager mngr;

    private OWLOntology onto;

    public UrigenEntityFactory(OWLOntologyManager mngr, OWLOntology onto, String urigenServer, String apikey, String ontologyUri) {

        super(mngr, onto, null);

        SimpleEntityCreation entityCreation = new SimpleEntityCreation();

        this.mngr = mngr;
        this.onto = onto;

        this.urigenServer = urigenServer;
        this.apikey = apikey;
        this.ontologyUri = ontologyUri;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PreferenceBean[]> responseEntity = restTemplate.getForEntity(urigenServer + "/api/preferences", PreferenceBean[].class);

        PreferenceBean[] preferences =responseEntity.getBody();
        // check both the user and the ontology exits

        for (PreferenceBean bean : preferences) {

            if (bean.getOntologyUri().equals(ontologyUri)) {
                preferenceBean = bean;
            }
        }

        this.user = restTemplate.getForObject(urigenServer + "/api/users/query?restApiKey=" + apikey, UserBean.class);

        if (preferenceBean == null) {
            throw new RuntimeException("No Urigen prefernce on "  +urigenServer + " for ontology " + ontologyUri);
        }
        if (user == null) {
            throw new RuntimeException("No Urigen user with that apiKey on "  +urigenServer + " for ontology " + ontologyUri);
        }
    }

    @Override
    public List<? extends OWLOntologyChange> createLabel(OWLEntity owlEntity, String value) {
        OWLDataFactory df = mngr.getOWLDataFactory();

        OWLLiteral con = df.getOWLLiteral(value);

        OWLAnnotation anno = df.getOWLAnnotation(df.getRDFSLabel(), con);
        OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(owlEntity.getIRI(), anno);
        return Collections.singletonList(new AddAxiom(onto, ax));
    }

    @Override
    protected URI getDefaultBaseURI() {
        return super.getDefaultBaseURI();
    }

    @Override
    public <T extends OWLEntity> OWLEntityCreationSet<T> createOWLEntity(Class<T> type, String shortName, URI baseURI) throws OWLEntityCreationException {

        String query = urigenServer + "/api/uris?restApiKey=" + apikey;

        UrigenRequestBean requestBean = new UrigenRequestBean(
                user.getId(),
                "http://urigen_random/" + Math.random(),
                preferenceBean.getPreferenceId(),
                shortName,
                ""
        );

        RestTemplate restTemplate = new RestTemplate();
        UrigenEntityBean entity = restTemplate.postForObject(query, requestBean, UrigenEntityBean.class);

        T owlEntity = getOWLEntity(type, URI.create(entity.getGeneratedUri()));

        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.addAll(createLabel(owlEntity, shortName));


        OWLDataFactory df = mngr.getOWLDataFactory();
        OWLAxiom ax = df.getOWLDeclarationAxiom(owlEntity);
        changes.add(new AddAxiom(onto, ax));

        return new SimpleOWLEntityCreationSet<T>(owlEntity, changes);
    }

}

