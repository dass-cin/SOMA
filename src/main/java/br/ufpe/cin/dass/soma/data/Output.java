package br.ufpe.cin.dass.soma.data;

public class Output {

    private String pairId;
    private String expId;
    private Double precision;
    private Double recall;
    private Double fMeasure;
    private Long executionTime;
    private Integer totalClasses;
    private Integer totalProperties;
    private Integer classSegmentPairs;
    private Integer propertySegmentPairs;
    private String matcher;

    public String getPairId() {
        return pairId;
    }

    public void setPairId(String pairId) {
        this.pairId = pairId;
    }

    public String getExpId() {
        return expId;
    }

    public void setExpId(String expId) {
        this.expId = expId;
    }

    public Double getPrecision() {
        return precision;
    }

    public void setPrecision(Double precision) {
        this.precision = precision;
    }

    public Double getRecall() {
        return recall;
    }

    public void setRecall(Double recall) {
        this.recall = recall;
    }

    public Double getfMeasure() {
        return fMeasure;
    }

    public void setfMeasure(Double fMeasure) {
        this.fMeasure = fMeasure;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public Integer getClassSegmentPairs() {
        return classSegmentPairs;
    }

    public void setClassSegmentPairs(Integer classSegmentPairs) {
        this.classSegmentPairs = classSegmentPairs;
    }

    public Integer getPropertySegmentPairs() {
        return propertySegmentPairs;
    }

    public void setPropertySegmentPairs(Integer propertySegmentPairs) {
        this.propertySegmentPairs = propertySegmentPairs;
    }

    public Integer getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(Integer totalClasses) {
        this.totalClasses = totalClasses;
    }

    public Integer getTotalProperties() {
        return totalProperties;
    }

    public void setTotalProperties(Integer totalProperties) {
        this.totalProperties = totalProperties;
    }

    public String getMatcher() {
        return matcher;
    }

    public void setMatcher(String matcher) {
        this.matcher = matcher;
    }
}
