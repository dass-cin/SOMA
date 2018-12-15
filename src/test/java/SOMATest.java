import br.ufpe.cin.dass.soma.SomaApplication;
import br.ufpe.cin.dass.soma.client.OntologyCatalogClient;
import br.ufpe.cin.dass.soma.error.CouldNotImportOntologyException;
import br.ufpe.cin.dass.soma.services.SOMAService;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SomaApplication.class)
@WebAppConfiguration()
public class SOMATest {

    @Inject
    private SOMAService somaService;

    @Inject
    private OntologyCatalogClient ontologyCatalogClient;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void shouldGenerateOntologySegment() throws URISyntaxException {

        URI sourceOntology = new URI("/Users/diegopessoa/Projects/doutorado/ontologies/conference/cmt.owl");

        Set<String> keywords = Arrays.asList(new String[]{ "document", "title", "abstract" }).stream().collect(Collectors.toSet());

        String segmentQuery = somaService.generateSourceOntologySegmentQuery(sourceOntology, keywords, SOMAService.SegmentGenerationExtension.SIMPLE);

        assertTrue(!segmentQuery.isEmpty());

        ontologyCatalogClient.exportOntologySegmentAsFile(segmentQuery, "cmt", "/tmp/cmt-source-segment.owl");

    }

    @Test
    public void shouldGenerateOntologySegment2() throws URISyntaxException {

        URI sourceOntology = new URI("/Users/diegopessoa/Projects/doutorado/ontologies/conference/cmt.owl");

        Set<String> keywords = Arrays.asList(new String[]{ "author", "email", "participant", "name", "presentation" }).stream().collect(Collectors.toSet());

        String segmentQuery = somaService.generateSourceOntologySegmentQuery(sourceOntology, keywords, SOMAService.SegmentGenerationExtension.SIMPLE);

        assertTrue(!segmentQuery.isEmpty());

    }

    @Test
    public void shouldGenerateTargetOntologySegment() throws URISyntaxException, MalformedURLException, OWLOntologyCreationException {

//        URI sourceOntologySegment = new URI("/tmp/source-segment-cmt-Conference.owl");
        URI sourceOntologySegment = new URI("/tmp/cmt-source-segment.owl");

        URI targetOntology = new URI("/Users/diegopessoa/Projects/doutorado/ontologies/conference/Conference.owl");

        String segmentQuery = somaService.generateTargetOntologySegmentQuery(sourceOntologySegment, targetOntology, SOMAService.SegmentGenerationExtension.SIMPLE);

        assertTrue(!segmentQuery.isEmpty());
    }



}
