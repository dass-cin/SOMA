import br.ufpe.cin.dass.soma.util.Utils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class UtilTest {

    @Test
    public void shouldGetOntologyNameFromURI() throws URISyntaxException {
        URI uri = new URI("/Users/diego/ontologies/conference/cmt.owl");
        String ontologyName = Utils.getOntologyNameFromURI(uri);
        assertEquals(ontologyName, "cmt");
    }

}
