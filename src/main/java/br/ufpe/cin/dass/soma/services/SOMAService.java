package br.ufpe.cin.dass.soma.services;

import br.ufpe.cin.dass.soma.client.OntologyCatalogClient;
import br.ufpe.cin.dass.soma.error.CouldNotImportOntologyException;
import br.ufpe.cin.dass.soma.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static br.ufpe.cin.dass.soma.services.SOMAService.SegmentGenerationExtension.EXPANDED;

@Service
public class SOMAService {

    private Logger log = LoggerFactory.getLogger(SOMAService.class);

    private final OntologyCatalogClient ontologyCatalog;

    public SOMAService(OntologyCatalogClient ontologyCatalog) {
        this.ontologyCatalog = ontologyCatalog;
    }

    private static String UNION = "\nUNION\n";

    public enum SegmentGenerationExtension { SIMPLE, EXPANDED}

    public void processWorkflow(URI sourceOntology, URI targetOntology, Set<String> keywords, SegmentGenerationExtension extension) {

        try {
            //1) Ontologies importing and pre-processing
            importOntologiesInTheCatalog(sourceOntology, targetOntology);

            //2) Source ontology segmentation (and keyword search)
            ArrayList<Map<String, Object>> ontologySegment = generateSourceOntologySegment(sourceOntology, keywords, extension);

            //3) Matching execution



        } catch (CouldNotImportOntologyException e) {
            log.error("Could not import ontology");
        }



    }

    public void importOntologiesInTheCatalog(URI sourceOntology, URI targetOntology) throws CouldNotImportOntologyException {
        ResponseEntity<?> responseEntitySource = ontologyCatalog.importOntology(sourceOntology.toString());
        ResponseEntity<?>  responseEntityTarget = ontologyCatalog.importOntology(targetOntology.toString());
        if (!responseEntitySource.getStatusCode().equals(HttpStatus.OK) || !responseEntityTarget.getStatusCode().equals(HttpStatus.OK)) {
            throw new CouldNotImportOntologyException("It was not possible to import ontology in the catalog");
        }
        log.info("Source ontology {} and target ontology {} successfully imported", sourceOntology, targetOntology);
    }

    public ArrayList<Map<String, Object>> generateSourceOntologySegment(URI souceOntology, Set<String> keywords, SegmentGenerationExtension extension) {

        ArrayList<Map<String, Object>> ontologySegment = null;

        String ontologyName = Utils.getOntologyNameFromURI(souceOntology);

        StringBuilder query = new StringBuilder();

        for (String keyword : keywords) {
            try {
                ResponseEntity<Map<String, Object>> result = ontologyCatalog.findNodeByKeyword(ontologyName, keyword);
                if (result.getStatusCode().equals(HttpStatus.OK)) {
                    Map<String, Object> resultBody = result.getBody();
                    String elementType = (String) resultBody.get("type");
                    LinkedHashMap<String, String> ontologyElement = (LinkedHashMap<String, String>) resultBody.get("node");
                    if (elementType.equals("ClassNode")) {
                        if (query.length() > 0) {
                            query.append(UNION);
                        }
                        //ClassNode validation (first level):
                        query.append(String.format("MATCH(c1:ClassNode)-[r:isA]-(c2:ClassNode)\n" +
                                "WHERE c1.uri = '%s' \n" +
                                "RETURN c1,TYPE(r),c2", (ontologyElement.get("uri"))));
                        if (extension.equals(EXPANDED)) {
                            query.append("\nUNION\n");
                            query.append(String.format("MATCH(c1:ObjectPropertyNode)-[r]-(c2:ClassNode)\n" +
                                    "WHERE c2.uri = '%s'\n" +
                                    "RETURN c1,TYPE(r),c2", (ontologyElement.get("uri"))));
                            query.append(UNION);
                            query.append(String.format("MATCH(c1:DataPropertyNode)-[r]-(c2:ClassNode)\n" +
                                    "WHERE c2.uri = '%s'\n" +
                                    "RETURN c1,TYPE(r),c2", (ontologyElement.get("uri"))));
                        }
                    } else if (elementType.equals("ObjectPropertyNode")) {
                        if (query.length() > 0) {
                            query.append(UNION);
                        }
                        query.append(String.format("MATCH(c1:ObjectPropertyNode)-[r:domain]-(c2:ClassNode)\n" +
                                "WHERE c1.ui = '%s'\n" +
                                "RETURN c1,TYPE(r),c2", (ontologyElement.get("uri"))));
                        query.append(UNION);
                        query.append(String.format("MATCH(c1:ObjectPropertyNode)-[r:range]-(c2:ClassNode)\n" +
                                "WHERE c1.uri = '%s'\n" +
                                "RETURN c1,TYPE(r),c2", (ontologyElement.get("uri"))));
                    } else if (elementType.equals("DataPropertyNode")) {
                        if (query.length() > 0) {
                            query.append(UNION);
                        }
                        query.append(String.format("MATCH(c1:DataPropertyNode)-[r:domain]-(c2:ClassNode)\n" +
                                "WHERE c1.uri = '%s'\n" +
                                "RETURN c1,TYPE(r),c2", (ontologyElement.get("uri"))));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                log.warn("Keyword {} not found in the catalog", keyword);
            }
        }
        log.info("segment generation query = \n {}", query.toString());
        ResponseEntity<?> result = ontologyCatalog.getQueryResult(query.toString());
        if (result.getStatusCode().equals(HttpStatus.OK)) {
            ontologySegment = (ArrayList<Map<String, Object>>) result.getBody();
        }

        return ontologySegment;

    }

    public void segmentPairsGeneration(String sourceOntologyName, String targetOntologyName) {



    }

}
