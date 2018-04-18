package br.ufpe.cin.dass.soma.model;

import java.util.*;

/**
 * Created by diego on 06/06/17.
 */
public class PropertyModel extends  OntologyElement {

    private Set comments = new HashSet();

    private String type;

    private Set ranges = new HashSet();

    private Set domains = new HashSet();

    private List subProperties = new ArrayList();

    private List superProperties = new ArrayList();

    private List equivalentProperties = new ArrayList();

    private List values = new ArrayList();

    private Set inverseOf = new HashSet();

    private Map<String, Double> validatedConstraints = new HashMap<>();

    private double relevance;

    private boolean transitive;

    private boolean functional;

    private boolean dataTypeProperty;

    private boolean objectProperty;

    private boolean symmetricProperty;

    private boolean inverseFunctionalProperty;

    public Set getComments() {
        return comments;
    }

    public void setComments(Set comments) {
        this.comments = comments;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set getRanges() {
        return ranges;
    }

    public void setRanges(Set ranges) {
        this.ranges = ranges;
    }

    public Set getDomains() {
        return domains;
    }

    public void setDomains(Set domains) {
        this.domains = domains;
    }

    public List getSubProperties() {
        return subProperties;
    }

    public void setSubProperties(List subProperties) {
        this.subProperties = subProperties;
    }

    public List getSuperProperties() {
        return superProperties;
    }

    public void setSuperProperties(List superProperties) {
        this.superProperties = superProperties;
    }

    public List getEquivalentProperties() {
        return equivalentProperties;
    }

    public void setEquivalentProperties(List equivalentProperties) {
        this.equivalentProperties = equivalentProperties;
    }

    public boolean isTransitive() {
        return transitive;
    }

    public void setTransitive(boolean transitive) {
        this.transitive = transitive;
    }

    public boolean isFunctional() {
        return functional;
    }

    public void setFunctional(boolean functional) {
        this.functional = functional;
    }

    public boolean isDataTypeProperty() {
        return dataTypeProperty;
    }

    public void setDataTypeProperty(boolean dataTypeProperty) {
        this.dataTypeProperty = dataTypeProperty;
    }

    public boolean isObjectProperty() {
        return objectProperty;
    }

    public void setObjectProperty(boolean objectProperty) {
        this.objectProperty = objectProperty;
    }

    public boolean isSymmetricProperty() {
        return symmetricProperty;
    }

    public void setSymmetricProperty(boolean symmetricProperty) {
        this.symmetricProperty = symmetricProperty;
    }

    public boolean isInverseFunctionalProperty() {
        return inverseFunctionalProperty;
    }

    public void setInverseFunctionalProperty(boolean inverseFunctionalProperty) {
        this.inverseFunctionalProperty = inverseFunctionalProperty;
    }

    public Set getInverseOf() {
        return inverseOf;
    }

    public void setInverseOf(Set inverseOf) {
        this.inverseOf = inverseOf;
    }

    public List getValues() {
        return values;
    }

    public void setValues(List values) {
        this.values = values;
    }

    public Map<String, Double> getValidatedConstraints() {
        return validatedConstraints;
    }

    public void setValidatedConstraints(Map<String, Double> validatedConstraints) {
        this.validatedConstraints = validatedConstraints;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }
}
