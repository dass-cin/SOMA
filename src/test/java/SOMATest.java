import br.cin.ufpe.dass.ontologycatalog.OntologyCatalogApplication;
import br.cin.ufpe.dass.ontologycatalog.services.KeywordSearchService;
import br.ufpe.cin.dass.soma.SomaApplication;
import br.ufpe.cin.dass.soma.client.OntologyCatalogClient;
import br.ufpe.cin.dass.soma.error.CouldNotImportOntologyException;
import br.ufpe.cin.dass.soma.services.SOMAService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
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

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    public void shouldImportOntologiesInTheCatalog() throws URISyntaxException {

        URI sourceOntology = new URI("/Users/diego/ontologies/conference/cmt.owl");
        URI targetOntology = new URI("/Users/diego/ontologies/conference/conference.owl");
        try {
            somaService.importOntologiesInTheCatalog(sourceOntology, targetOntology);
        } catch (CouldNotImportOntologyException e) {
            e.printStackTrace();
            fail("Ontology could not be imported");
        }

    }

    @Test
    public void shouldGenerateOntologySegment() throws URISyntaxException {

        URI sourceOntology = new URI("/Users/diego/ontologies/conference/cmt.owl");

        Set<String> keywords = Arrays.asList(new String[]{ "document", "date", "author" }).stream().collect(Collectors.toSet());

        ArrayList<Map<String, Object>> segment = somaService.generateSourceOntologySegment(sourceOntology, keywords, SOMAService.SegmentGenerationExtension.EXPANDED);

        assertTrue(segment.size() > 0);

    }

}
