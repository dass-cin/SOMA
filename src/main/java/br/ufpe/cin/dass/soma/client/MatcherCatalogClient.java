package br.ufpe.cin.dass.soma.client;

import br.cin.ufpe.dass.matchers.core.Alignment;
import br.cin.ufpe.dass.matchers.core.Matcher;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="matcher-catalog", url="${matcher.catalog.url}")
public interface MatcherCatalogClient {

    @PostMapping("/alignment")
    ResponseEntity<Alignment> align(@RequestParam("ontology1") String ontology1Path, @RequestParam("ontology2") String ontology2Path, @RequestParam("matcher") String matcher, @RequestParam("experiment") String experiment);

    @GetMapping("/matcher")
    ResponseEntity<List<Matcher>> listMatchers();

}
