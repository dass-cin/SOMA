package br.ufpe.cin.dass.soma.services;

import br.ufpe.cin.dass.soma.client.OntologyCatalogClient;
import br.ufpe.cin.dass.soma.error.CouldNotImportOntologyException;
import br.ufpe.cin.dass.soma.util.Utils;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.util.*;

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

    public String generateSourceOntologySegmentQuery(URI souceOntology, Set<String> keywords, SegmentGenerationExtension extension) {

//        ArrayList<Map<String, Object>> ontologySegment = null;

        String ontologyName = Utils.getOntologyNameFromURI(souceOntology);

        StringBuilder query = new StringBuilder();

        for (String keyword : keywords) {
            try {
                ResponseEntity<List<Map<String, Object>>> result = ontologyCatalog.findNodesByKeyword(ontologyName, keyword);
                if (result.getStatusCode().equals(HttpStatus.OK)) {
                    List<Map<String, Object>> resultBodyList = result.getBody();
                    resultBodyList.forEach( resultBody -> {
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
                                    "WHERE c1.uri = '%s'\n" +
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
                    });

                }
            } catch (Exception e) {
                log.warn("Keyword {} not found in the catalog", keyword);
            }
        }
        log.info("segment generation query = \n {}", query.toString());
//        ResponseEntity<?> result = ontologyCatalog.getQueryResult(query.toString());
//        if (result.getStatusCode().equals(HttpStatus.OK)) {
//            ontologySegment = (ArrayList<Map<String, Object>>) result.getBody();
//        }

        return query.toString();

    }

    public String generateTargetOntologySegmentQuery(URI sourceOntologySegment, URI targetOntology, SegmentGenerationExtension extension) throws OWLOntologyCreationException {

        String ontologyName = Utils.getOntologyNameFromURI(sourceOntologySegment);

        String targetOntologyNameFull = Utils.getOntologyNameFromURI(targetOntology);
        String targetOntologyName = Character.toLowerCase(targetOntologyNameFull.charAt(0)) + targetOntologyNameFull.substring(1);
        StringBuilder query = new StringBuilder();

        OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
        OWLDataFactory owlDataFactory = owlManager.getOWLDataFactory();
        OWLOntology ontology = owlManager.loadOntology(IRI.create("file:///"+ sourceOntologySegment.toString()));
        OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        reasoner.precomputeInferences();
        //process classes
        ontology.classesInSignature().forEach(ontologyClass -> {
            //gerar consulta com base nesses elementos e no neighbor
            if (query.length() > 0) {
                query.append(UNION);
            }
            String elementName = ontologyClass.getIRI().toString().replaceAll("http://.*#", "");
            //ClassNode validation (first level):
            query.append(String.format("MATCH(c1:ClassNode)-[r:isA]-(c2:ClassNode)\n" +
                    "WHERE c1.uri =~ 'http://%s' AND c1.name =~ '%s' \n" +
                    "RETURN c1,TYPE(r),c2", targetOntologyName+".*", elementName));
            if (extension.equals(EXPANDED)) {
                query.append("\nUNION\n");
                query.append(String.format("MATCH(c1:ObjectPropertyNode)-[r]-(c2:ClassNode)\n" +
                        "WHERE c2.uri =~ 'http://%s' AND c2.name =~ '%s' \n" +
                        "RETURN c1,TYPE(r),c2", targetOntologyName+".*", ".*"+ elementName+".*"));
                query.append(UNION);
                query.append(String.format("MATCH(c1:DataPropertyNode)-[r]-(c2:ClassNode)\n" +
                        "WHERE c2.uri =~ 'http://%s' AND c2.name =~ '%s' \n" +
                        "RETURN c1,TYPE(r),c2", targetOntologyName+".*", ".*"+ elementName+".*"));
            }
        });

        ontology.objectPropertiesInSignature().forEach(objectProperty -> {
            String elementName = objectProperty.getIRI().toString().replaceAll("http://.*#", "");

            if (query.length() > 0) {
                query.append(UNION);
            }

            query.append(String.format("MATCH(c1:ObjectPropertyNode)-[r:domain]-(c2:ClassNode)\n" +
                    "WHERE c1.uri =~ 'http://%s' AND c1.name =~ '%s' \n" +
                    "RETURN c1,TYPE(r),c2", targetOntologyName+".*", ".*"+ elementName+".*"));
            query.append(UNION);
            query.append(String.format("MATCH(c1:ObjectPropertyNode)-[r:range]-(c2:ClassNode)\n" +
                    "WHERE c1.uri =~ 'http://%s' AND c1.name =~ '%s' \n" +
                    "RETURN c1,TYPE(r),c2", targetOntologyName+".*", ".*"+ elementName+".*"));

        });

        ontology.dataPropertiesInSignature().forEach(dataProperty -> {
            String elementName = dataProperty.getIRI().toString().replaceAll("http://.*#", "");

            if (query.length() > 0) {
                query.append(UNION);
            }
            query.append(String.format("MATCH(c1:DataPropertyNode)-[r:domain]-(c2:ClassNode)\n" +
                    "WHERE c1.uri =~ 'http://%s' AND c1.name =~ '%s' \n" +
                    "RETURN c1,TYPE(r),c2", targetOntologyName+".*", ".*"+ elementName+".*"));

        });

        log.info("Target segment query generation {} ", query.toString());

        return query.toString();

    }

}
