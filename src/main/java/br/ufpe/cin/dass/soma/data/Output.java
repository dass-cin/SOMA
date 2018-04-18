package br.ufpe.cin.dass.soma.data;

public class Output {

    private String pairId;
    private String expId;
    private Float precision;
    private Float recall;
    private Float fMeasure;
    private Long executionTime;
    private Integer totalClasses;
    private Integer totalProperties;
    private Integer classSegmentPairs;
    private Integer propertySegmentPairs;

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

    public Float getPrecision() {
        return precision;
    }

    public void setPrecision(Float precision) {
        this.precision = precision;
    }

    public Float getRecall() {
        return recall;
    }

    public void setRecall(Float recall) {
        this.recall = recall;
    }

    public Float getfMeasure() {
        return fMeasure;
    }

    public void setfMeasure(Float fMeasure) {
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
}
