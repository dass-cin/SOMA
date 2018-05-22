package br.ufpe.cin.dass.soma.data;

public class Input {

    private String id;
    private String sourceOntology;
    private String targetOntology;
    private String keywords;
    private String matcher;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getMatcher() {
        return matcher;
    }

    public void setMatcher(String matcher) {
        this.matcher = matcher;
    }
}
