package br.ufpe.cin.dass.soma.data;

import java.util.HashMap;

public class Requirement {

    private String id;
    private HashMap<String, String> constraints;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, String> getConstraints() {
        return constraints;
    }

    public void setConstraints(HashMap<String, String> constraints) {
        this.constraints = constraints;
    }

}
