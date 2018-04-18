package br.ufpe.cin.dass.soma.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by diego on 06/06/17.
 */
public class OntologyClass extends OntologyElement {

    private List synonyms = new ArrayList();
    private String commment;
    private List partOf = new ArrayList();
    private List hasPart = new ArrayList();
    private List supers = new ArrayList();
    private List subs = new ArrayList();
    private List equiv = new ArrayList();
    private List disjointWith = new ArrayList();
    private List seeAlso = new ArrayList();
    private List isDefinedBy = new ArrayList();
    private Map<String, Double> validatedConstraints = new HashMap<>();

    public List getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List synonyms) {
        this.synonyms = synonyms;
    }

    public List getPartOf() {
        return partOf;
    }

    public void setPartOf(List partOf) {
        this.partOf = partOf;
    }

    public List getHasPart() {
        return hasPart;
    }

    public void setHasPart(List hasPart) {
        this.hasPart = hasPart;
    }

    public List getSupers() {
        return supers;
    }

    public void setSupers(List supers) {
        this.supers = supers;
    }

    public List getSubs() {
        return subs;
    }

    public void setSubs(List subs) {
        this.subs = subs;
    }

    public List getEquiv() {
        return equiv;
    }

    public void setEquiv(List equiv) {
        this.equiv = equiv;
    }

    public List getDisjointWith() {
        return disjointWith;
    }

    public void setDisjointWith(List disjointWith) {
        this.disjointWith = disjointWith;
    }

    public String getCommment() {
        return commment;
    }

    public void setCommment(String commment) {
        this.commment = commment;
    }

    public List getSeeAlso() {
        return seeAlso;
    }

    public void setSeeAlso(List seeAlso) {
        this.seeAlso = seeAlso;
    }

    public List getIsDefinedBy() {
        return isDefinedBy;
    }

    public void setIsDefinedBy(List isDefinedBy) {
        this.isDefinedBy = isDefinedBy;
    }

    public Map<String, Double> getValidatedConstraints() {
        return validatedConstraints;
    }

    public void setValidatedConstraints(Map<String, Double> validatedConstraints) {
        this.validatedConstraints = validatedConstraints;
    }

}
