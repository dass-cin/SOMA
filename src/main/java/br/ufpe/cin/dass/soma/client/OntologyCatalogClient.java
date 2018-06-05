package br.ufpe.cin.dass.soma.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name="ontology-catalog", url="${ontology.catalog.url}")
public interface OntologyCatalogClient {

    @PostMapping("ontologies/import")
    ResponseEntity<?> importOntology(@RequestBody String ontologyURI);

    @PutMapping("ontologies/query")
    ResponseEntity<?> getQueryResult(@RequestBody String cypherQuery);

    @GetMapping("keyword-search/{ontologyName}")
    ResponseEntity<Map<String, Object>> findNodeByKeyword(@PathVariable("ontologyName") String ontologyName, @RequestParam("keyword") String keyword);

    @PutMapping("/ontology-export")
    ResponseEntity<String> exportOntologySegmentAsFile(@RequestBody String segmentationQuery, @RequestParam("ontologyName") String ontologyName, @RequestParam("filepath") String filePath);


}
