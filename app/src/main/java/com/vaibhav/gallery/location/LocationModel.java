package com.vaibhav.gallery.location;

import android.location.Address;

import com.vaibhav.gallery.model.ImageModel;

import java.util.ArrayList;

public class LocationModel {
    private long lat;
    private long lon;
    private String name;

    private Address address;

    private ImageModel imageModel;

    public void setImageModel(ImageModel imageModel){
        this.imageModel=imageModel;
    }

    public ImageModel getImageModel(){
        return imageModel;
    }

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public long getLon() {
        return lon;
    }

    public void setLon(long lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name=name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


}
