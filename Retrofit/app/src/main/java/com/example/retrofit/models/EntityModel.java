package com.example.retrofit.models;

import com.google.gson.annotations.SerializedName;

public class EntityModel {

    public EntityModel() {}

    public EntityModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }
}