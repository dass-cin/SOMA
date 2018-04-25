package br.ufpe.cin.dass.soma.data;

import java.util.Map;

public class SegmentPairs {

    Map<String, String> propertySegmentPairs;
    Map<String, String> ontologySegmentPairs;

    private long totalSegmentClasses;
    private long totalSegmentProperties;
    private long totalOriginalClasses;
    private long totalOriginalProperties;

    public SegmentPairs(Map<String, String> ontologySegmentPairs, Map<String, String> propertySegmentPairs) {
        this.propertySegmentPairs = propertySegmentPairs;
        this.ontologySegmentPairs = ontologySegmentPairs;
    }

    public Map<String, String> getPropertySegmentPairs() {
        return propertySegmentPairs;
    }

    public void setPropertySegmentPairs(Map<String, String> propertySegmentPairs) {
        this.propertySegmentPairs = propertySegmentPairs;
    }

    public Map<String, String> getOntologySegmentPairs() {
        return ontologySegmentPairs;
    }

    public void setOntologySegmentPairs(Map<String, String> ontologySegmentPairs) {
        this.ontologySegmentPairs = ontologySegmentPairs;
    }

    public long getTotalSegmentClasses() {
        return totalSegmentClasses;
    }

    public void setTotalSegmentClasses(long totalSegmentClasses) {
        this.totalSegmentClasses = totalSegmentClasses;
    }

    public long getTotalSegmentProperties() {
        return totalSegmentProperties;
    }

    public void setTotalSegmentProperties(long totalSegmentProperties) {
        this.totalSegmentProperties = totalSegmentProperties;
    }

    public long getTotalOriginalClasses() {
        return totalOriginalClasses;
    }

    public void setTotalOriginalClasses(long totalOriginalClasses) {
        this.totalOriginalClasses = totalOriginalClasses;
    }

    public long getTotalOriginalProperties() {
        return totalOriginalProperties;
    }

    public void setTotalOriginalProperties(long totalOriginalProperties) {
        this.totalOriginalProperties = totalOriginalProperties;
    }
}
