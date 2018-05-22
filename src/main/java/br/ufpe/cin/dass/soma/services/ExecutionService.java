package br.ufpe.cin.dass.soma.services;

import br.cin.ufpe.dass.matchers.core.Alignment;
import br.cin.ufpe.dass.matchers.core.Correspondence;
import br.ufpe.cin.dass.soma.client.MatcherCatalogClient;
import br.ufpe.cin.dass.soma.client.OntologyCatalogClient;
import br.ufpe.cin.dass.soma.config.ApplicationConfig;
import br.ufpe.cin.dass.soma.data.Input;
import br.ufpe.cin.dass.soma.data.Output;
import br.ufpe.cin.dass.soma.data.ReferenceAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ExecutionService implements ItemProcessor<Input, Output>  {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private MatcherCatalogClient matcherCatalogClient;

    @Autowired
    private OntologyCatalogClient ontologyCatalogClient;

    private Logger log = LoggerFactory.getLogger(ExecutionService.class);

    @Override
    public Output process(Input ontologyPair) throws Exception {

        log.info("Starting to process pair : {}-{} in matcher {}", ontologyPair.getSourceOntology(),  ontologyPair.getTargetOntology(), ontologyPair.getMatcher()   );

        //@TODO import ontolog in catalog and generate segments

        long startProcessing = System.currentTimeMillis();

        ResponseEntity<Alignment> alignmentResponseEntity = matcherCatalogClient.align(ontologyPair.getSourceOntology(), ontologyPair.getTargetOntology(), ontologyPair.getMatcher());
        if (!alignmentResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
            throw new Exception(String.format("Fail to execute alingment for matcher %s", ontologyPair.getMatcher()));
        }
        Alignment alignment = alignmentResponseEntity.getBody();

        ReferenceAlignment referenceAlignment = evaluationService.loadReferenceAlignment(ontologyPair.getId());

        int truePositives = 0;
        int falsePositives = 0;
        int falseNegatives = 0;

        for (Correspondence correspondence : alignment.getCorrespondences()) {
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
