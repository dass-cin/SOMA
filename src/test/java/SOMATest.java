import br.ufpe.cin.dass.soma.SomaApplication;
import br.ufpe.cin.dass.soma.error.CouldNotImportOntologyException;
import br.ufpe.cin.dass.soma.services.SOMAService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

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


    @Test
    public void shouldGenerateOntologySegment() throws URISyntaxException {

        URI sourceOntology = new URI("/Users/diego/ontologies/conference/cmt.owl");

        Set<String> keywords = Arrays.asList(new String[]{ "document", "claim", "author", "person" }).stream().collect(Collectors.toSet());

        String segmentQuery = somaService.generateSourceOntologySegmentQuery(sourceOntology, keywords, SOMAService.SegmentGenerationExtension.SIMPLE);

        assertTrue(!segmentQuery.isEmpty());

    }


}
