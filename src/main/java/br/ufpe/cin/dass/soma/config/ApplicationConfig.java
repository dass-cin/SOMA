package br.ufpe.cin.dass.soma.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties()
public class ApplicationConfig {

    private String constraintValidationThreshold;

    private String elementValidationThreshold;

    private String numberOfPairsThreshold;

    private String ontologyPairsFile;

    private String requirementsFile;

    private String stopWordsFile;

    private String expId;

    private String wordnetDir;

    private boolean generateSegments = true;

    private String generationExtension = "simple";

    private String segmentPath = "/tmp";

    private Ontologies ontologies = new Ontologies();

    private double matchingThreshold;

    private boolean alignmentEnabled = false;

    public boolean isAlignmentEnabled() {
        return alignmentEnabled;
    }

    public void setAlignmentEnabled(boolean alignmentEnabled) {
        this.alignmentEnabled = alignmentEnabled;
    }

    public String getGenerationExtension() {
        return generationExtension;
    }

    public void setGenerationExtension(String generationExtension) {
        this.generationExtension = generationExtension;
    }

    public boolean isGenerateSegments() {
        return generateSegments;
    }

    public void setGenerateSegments(boolean generateSegments) {
        this.generateSegments = generateSegments;
    }

    public String getWordnetDir() {
        return wordnetDir;
    }

    public void setWordnetDir(String wordnetDir) {
        this.wordnetDir = wordnetDir;
    }

    public String getExpId() {
        return expId;
    }

    public void setExpId(String expId) {
        this.expId = expId;
    }

    public Ontologies getOntologies() {
        return ontologies;
    }

    public void setOntologies(Ontologies ontologies) {
        this.ontologies = ontologies;
    }

    public static class Ontologies {

        private String mainLanguage = "en";

        private String snLanguage = "sn";

        private String part = "part_of";

        private String externalNamespaces = "http://www.w3.org/1999/02/22-rdf-syntax-ns#" + ";" +
                "http://www.w3.org/TR/xmlschema-2/#" + ";" +
                "http://www.w3.org/2002/07/owl#" + ";" +
                "http://www.geneontology.org/formats/oboInOwl#" + ";" +
                "http://www.w3.org/2000/01/rdf-schema#" + ";" +
                "http://xmlns.com/foaf/0.1/" + ";" +
                "http://purl.org/dc/elements/1.1/";

        public String getPart() {
            return part;
        }

        public void setPart(String part) {
            this.part = part;
        }

        public String getSnLanguage() {
            return snLanguage;
        }

        public void setSnLanguage(String snLanguage) {
            this.snLanguage = snLanguage;
        }

        public String getMainLanguage() {
            return mainLanguage;
        }

        public void setMainLanguage(String mainLanguage) {
            this.mainLanguage = mainLanguage;
        }

        public String getExternalNamespaces() {
            return externalNamespaces;
        }

        public void setExternalNamespaces(String externalNamespaces) {
            this.externalNamespaces = externalNamespaces;
        }
    }

    public String getOntologyPairsFile() {
        return ontologyPairsFile;
    }

    public void setOntologyPairsFile(String ontologyPairsFile) {
        this.ontologyPairsFile = ontologyPairsFile;
    }

    public String getRequirementsFile() {
        return requirementsFile;
    }

    public void setRequirementsFile(String requirementsFile) {
        this.requirementsFile = requirementsFile;
    }

    public String getConstraintValidationThreshold() {
        return constraintValidationThreshold;
    }

    public void setConstraintValidationThreshold(String constraintValidationThreshold) {
        this.constraintValidationThreshold = constraintValidationThreshold;
    }

    public String getElementValidationThreshold() {
        return elementValidationThreshold;
    }

    public void setElementValidationThreshold(String elementValidationThreshold) {
        this.elementValidationThreshold = elementValidationThreshold;
    }

    public String getNumberOfPairsThreshold() {
        return numberOfPairsThreshold;
    }

    public void setNumberOfPairsThreshold(String numberOfPairsThreshold) {
        this.numberOfPairsThreshold = numberOfPairsThreshold;
    }

    public String getStopWordsFile() {
        return stopWordsFile;
    }

    public void setStopWordsFile(String stopWordsFile) {
        this.stopWordsFile = stopWordsFile;
    }

    public String getSegmentPath() {
        return segmentPath;
    }

    public void setSegmentPath(String segmentPath) {
        this.segmentPath = segmentPath;
    }

    public double getMatchingThreshold() {
        return matchingThreshold;
    }

    public void setMatchingThreshold(double matchingThreshold) {
        this.matchingThreshold = matchingThreshold;
    }
}
