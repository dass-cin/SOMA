package br.ufpe.cin.dass.soma.services;

import br.ufpe.cin.dass.soma.config.ApplicationConfig;
import br.ufpe.cin.dass.soma.data.OntologyPair;
import br.ufpe.cin.dass.soma.data.Output;
import br.ufpe.cin.dass.soma.data.ReferenceAlignment;
import br.ufpe.cin.dass.soma.data.SegmentPairs;
import info.debatty.java.stringsimilarity.JaroWinkler;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class ExecutionService implements ItemProcessor<OntologyPair, Output>  {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private EvaluationService evaluationService;

    @Override
    public Output process(OntologyPair ontologyPair) throws Exception {

        long startProcessing = System.currentTimeMillis();

        SegmentPairs segmentPairs = null; //@TODO FIX

        ReferenceAlignment referenceAlignment = evaluationService.loadReferenceAlignment(ontologyPair.getId());

        int truePositives = 0;
        int falsePositives = 0;
        int falseNegatives= 0;

        //varrer cada par e procurar nos alinhamentos de referencia uma entrada em source ou target...
        for(Map.Entry<String, String> classEntry : segmentPairs.getOntologySegmentPairs().entrySet()) {
            if ((referenceAlignment.getCorrespondences().containsKey(classEntry.getKey()) &&
                    referenceAlignment.getCorrespondences().containsValue(classEntry.getValue())) ||
                    referenceAlignment.getCorrespondences().containsValue(classEntry.getValue()) &&
                            referenceAlignment.getCorrespondences().containsKey(classEntry.getKey())) {
                truePositives++;
            }
            if ((!referenceAlignment.getCorrespondences().containsKey(classEntry.getKey()) ||
                    !referenceAlignment.getCorrespondences().containsValue(classEntry.getValue())) &&
                    !referenceAlignment.getCorrespondences().containsValue(classEntry.getValue()) ||
                            !referenceAlignment.getCorrespondences().containsKey(classEntry.getKey())) {
                falsePositives++;
            }
        }

        for(Map.Entry<String, String> propertyEntry : segmentPairs.getPropertySegmentPairs().entrySet()) {
            if ((referenceAlignment.getCorrespondences().containsKey(propertyEntry .getKey()) &&
                    referenceAlignment.getCorrespondences().containsValue(propertyEntry .getValue())) ||
                    referenceAlignment.getCorrespondences().containsValue(propertyEntry .getValue()) &&
                            referenceAlignment.getCorrespondences().containsKey(propertyEntry .getKey())) {
                truePositives++;
            }
            if ((!referenceAlignment.getCorrespondences().containsKey(propertyEntry .getKey()) ||
                    !referenceAlignment.getCorrespondences().containsValue(propertyEntry .getValue())) &&
                    !referenceAlignment.getCorrespondences().containsValue(propertyEntry .getValue()) ||
                    !referenceAlignment.getCorrespondences().containsKey(propertyEntry .getKey())) {
                falsePositives++;
            }
        }

        for(Map.Entry<String, String> reference : referenceAlignment.getCorrespondences().entrySet()) {
            if (    //classes
                    (!segmentPairs.getOntologySegmentPairs().containsKey(reference.getKey()) ||
                !segmentPairs.getOntologySegmentPairs().containsValue(reference.getValue())) &&
                        (!segmentPairs.getOntologySegmentPairs().containsKey(reference.getValue()) ||
                        !segmentPairs.getOntologySegmentPairs().containsValue(reference.getKey())) &&
                    //properties
                    (!segmentPairs.getPropertySegmentPairs().containsKey(reference.getKey()) ||
                    !segmentPairs.getPropertySegmentPairs().containsValue(reference.getValue())) &&
                            (!segmentPairs.getPropertySegmentPairs().containsKey(reference.getValue()) ||
                    !segmentPairs.getPropertySegmentPairs().containsValue(reference.getKey()))) {
                falseNegatives++;
            }
        }

        long endProcessing = System.currentTimeMillis();

        Output output = new Output();
        output.setPairId(ontologyPair.getId());
        output.setExecutionTime(endProcessing-startProcessing);
        output.setExpId(applicationConfig.getExpId());
        output.setPrecision((float)truePositives / (float)(truePositives + falsePositives));
        output.setRecall(Float.valueOf((float)truePositives / (float)(truePositives + falseNegatives)));
        output.setClassSegmentPairs(segmentPairs.getOntologySegmentPairs().size());
        output.setPropertySegmentPairs(segmentPairs.getPropertySegmentPairs().size());
        output.setTotalClasses((int)segmentPairs.getTotalOriginalClasses());
        output.setTotalProperties((int)segmentPairs.getTotalOriginalProperties());
        if ((output.getRecall() + output.getPrecision()) > 0) {
            output.setfMeasure( 2 * (((float)output.getPrecision() * output.getRecall()) / (output.getPrecision() + output.getRecall())));
        } else {
            output.setfMeasure(0.0f);
        }

        return output;
    }

}
