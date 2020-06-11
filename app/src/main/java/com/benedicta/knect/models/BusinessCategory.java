package com.benedicta.knect.models;

public class BusinessCategory {

    public String id;
    public String name;

    public BusinessCategory(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public BusinessCategory(String name) {
        this.name = name;
    }
}
