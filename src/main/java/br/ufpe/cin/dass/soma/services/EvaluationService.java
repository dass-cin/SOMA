package br.ufpe.cin.dass.soma.services;

import br.ufpe.cin.dass.soma.config.ApplicationConfig;
import br.ufpe.cin.dass.soma.data.ReferenceAlignment;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

@Service
public class EvaluationService {

    private final ApplicationConfig applicationConfig;

    public EvaluationService(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public ReferenceAlignment loadReferenceAlignment(String ontologyPairId) throws MalformedURLException, DocumentException {
        Path path = Paths.get("gold-standard/"+applicationConfig.getExpId()+"/"+ontologyPairId+".rdf");
        SAXReader reader = new SAXReader();
        final Document document = reader.read(path.toUri().toURL());
        ReferenceAlignment a = new ReferenceAlignment();
        a.setSourceOntology("");
        Element alignment = document.getRootElement().element("Alignment");
        a.setSourceOntology(alignment.element("onto1").element("Ontology").valueOf("@rdf:about"));
        a.setTargetOntology(alignment.element("onto2").element("Ontology").valueOf("@rdf:about"));
        Iterator it = document.getRootElement().element("Alignment").elementIterator("map");
        while(it.hasNext()) {
            Element map = (Element) it.next();
            Element cell = map.element("Cell");
            a.getCorrespondences().put(cell.element("entity1").valueOf("@rdf:resource"), cell.element("entity2").valueOf("@rdf:resource"));
        }
        return a;
    }

}
