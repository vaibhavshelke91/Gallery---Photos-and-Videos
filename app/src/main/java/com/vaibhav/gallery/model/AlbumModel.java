package com.vaibhav.gallery.model;

import java.util.ArrayList;

public class AlbumModel {
    private int id;
    private String path;
    private ArrayList<ImageModel> models;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<ImageModel> getModels() {
        return models;
    }

    public void setModels(ArrayList<ImageModel> models) {
        this.models = models;
    }
}
