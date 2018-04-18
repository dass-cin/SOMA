package br.ufpe.cin.dass.soma.data;

import java.util.HashMap;
import java.util.Map;

public class ReferenceAlignment {

    private String sourceOntology;
    private String targetOntology;
    private Map<String, String> correspondences = new HashMap<>();

    public String getSourceOntology() {
        return sourceOntology;
    }

    public void setSourceOntology(String sourceOntology) {
        this.sourceOntology = sourceOntology;
    }

    public String getTargetOntology() {
        return targetOntology;
    }

    public void setTargetOntology(String targetOntology) {
        this.targetOntology = targetOntology;
    }

    public Map<String, String> getCorrespondences() {
        return correspondences;
    }

    public void setCorrespondences(Map<String, String> correspondences) {
        this.correspondences = correspondences;
    }
}
