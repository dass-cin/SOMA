package br.ufpe.cin.dass.soma.services;

import br.cin.ufpe.dass.matchers.core.Alignment;
import br.cin.ufpe.dass.matchers.core.Correspondence;
import br.ufpe.cin.dass.soma.client.MatcherCatalogClient;
import br.ufpe.cin.dass.soma.client.OntologyCatalogClient;
import br.ufpe.cin.dass.soma.config.ApplicationConfig;
import br.ufpe.cin.dass.soma.data.Input;
import br.ufpe.cin.dass.soma.data.Output;
import br.ufpe.cin.dass.soma.data.ReferenceAlignment;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class ExecutionService implements ItemProcessor<Input, Output>  {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private MatcherCatalogClient matcherCatalogClient;

    @Autowired
    private OntologyCatalogClient ontologyCatalogClient;

    @Autowired
    private SOMAService somaService;

    private Logger log = LoggerFactory.getLogger(ExecutionService.class);

    @Override
    public Output process(Input ontologyPair) throws Exception {

        log.info("Starting to process pair : {}-{} in matcher {}", ontologyPair.getSourceOntology(),  ontologyPair.getTargetOntology(), ontologyPair.getMatcher()   );

        ontologyCatalogClient.importOntology("file://" +ontologyPair.getSourceOntology());
//        ontologyCatalogClient.importOntology("file://" +ontologyPair.getTargetOntology());

        long startProcessing = System.currentTimeMillis();

        String segmentSourceFile = "";

        if (ontologyPair.getKeywords() != null && !ontologyPair.getKeywords().isEmpty()) {
            //using segments
            Set<String> keywords = Arrays.asList(ontologyPair.getKeywords().split(",")).stream().collect(Collectors.toSet());

            String generationExtensionString = applicationConfig.getGenerationExtension();
            SOMAService.SegmentGenerationExtension extension = SOMAService.SegmentGenerationExtension.valueOf(generationExtensionString.toUpperCase());

            String sourceOntologySegmentQuery = somaService.generateSourceOntologySegmentQuery(URI.create(ontologyPair.getSourceOntology()), keywords, extension);

            segmentSourceFile = applicationConfig.getSegmentPath() + "/" + "segment-"+ontologyPair.getId()+".owl";

            ResponseEntity responseEntityfileSegmentGeneration = ontologyCatalogClient.exportOntologySegmentAsFile(sourceOntologySegmentQuery, FilenameUtils.getName(URI.create(ontologyPair.getSourceOntology()).getPath()), segmentSourceFile);

            if (!responseEntityfileSegmentGeneration.getStatusCode().equals(HttpStatus.OK)) {
                throw new Exception(String.format("Fail to generate segment for ontology pair %s", ontologyPair.getId()));
            }

            if (!new File(segmentSourceFile).exists()) {
                throw new Exception(String.format("Segment file %s does not exists", segmentSourceFile));
            }

        }

        String sourceOntology = ontologyPair.getSourceOntology();

        if (!segmentSourceFile.isEmpty()) {
            sourceOntology = segmentSourceFile;
        }

        ResponseEntity<Alignment> alignmentResponseEntity = matcherCatalogClient.align(segmentSourceFile, ontologyPair.getTargetOntology(), ontologyPair.getMatcher());
        if (!alignmentResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
            throw new Exception(String.format("Fail to execute alingment for matcher %s", ontologyPair.getMatcher()));
        }
        Alignment alignment = alignmentResponseEntity.getBody();

        ReferenceAlignment referenceAlignment = evaluationService.loadReferenceAlignment(ontologyPair.getId());

        int truePositives = 0;
        int falsePositives = 0;
        int falseNegatives = 0;

        for (Correspondence correspondence : alignment.getCorrespondences()) {
            if (correspondence.getSimilarityValue() < applicationConfig.getMatchingThreshold()) {
                continue;
            }
            String sourceElement = correspondence.getSourceElement().toString(); //http://cmt#adjustedBy
            String targetElement = correspondence.getTargetElement().toString();
            if ((referenceAlignment.getCorrespondences().containsKey(sourceElement)
                    && referenceAlignment.getCorrespondences().containsValue(targetElement)) ||
                    (referenceAlignment.getCorrespondences().containsKey(targetElement))
                            && referenceAlignment.getCorrespondences().containsValue(sourceElement)) {
                truePositives++;
            }
            if ((!referenceAlignment.getCorrespondences().containsKey(sourceElement) ||
                    !referenceAlignment.getCorrespondences().containsValue(targetElement)) &&
                    !referenceAlignment.getCorrespondences().containsValue(targetElement) ||
                    !referenceAlignment.getCorrespondences().containsKey(sourceElement)) {
                falsePositives++;
            }

            for (Map.Entry<String, String> reference : referenceAlignment.getCorrespondences().entrySet()) {
                for (Correspondence alignmentCorrespondence : alignment.getCorrespondences()) {
                    if ((!alignmentCorrespondence.getSourceElement().equals(reference.getKey()) &&
                            !alignmentCorrespondence.getTargetElement().equals(reference.getValue())) ||
                            !alignmentCorrespondence.getSourceElement().equals(reference.getValue()) &&
                                    !alignmentCorrespondence.getTargetElement().equals(reference.getKey())) {
                        falseNegatives++;
                    }
                }
            }

        }

        long endProcessing = System.currentTimeMillis();

        Output output = new Output();
        output.setMatcher(ontologyPair.getMatcher());
        output.setPairId(ontologyPair.getId());
        output.setExecutionTime(endProcessing-startProcessing);
        output.setExpId(applicationConfig.getExpId());
        output.setPrecision((double)truePositives / (double)(truePositives + falsePositives));
        output.setRecall(Double.valueOf((double)truePositives / (double)(truePositives + falseNegatives)));
        if ((output.getRecall() + output.getPrecision()) > 0) {
            output.setfMeasure( 2 * (((double)output.getPrecision() * output.getRecall()) / (output.getPrecision() + output.getRecall())));
        } else {
            output.setfMeasure(0.0);
        }

        return output;
    }

}
