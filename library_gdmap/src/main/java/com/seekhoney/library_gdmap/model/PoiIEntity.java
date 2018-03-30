package com.seekhoney.library_gdmap.model;

import java.io.Serializable;

/**
 * Created by ryan on 17/6/6.
 */

public class PoiIEntity implements Serializable
{
    private String title;
    private String address;
    private double latlng;
    private double longlng;

    public PoiIEntity(String title, String address, double latlng, double longlng) {
        this.title = title;
        this.address = address;
        this.latlng = latlng;
        this.longlng = longlng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatlng() {
        return latlng;
    }

    public void setLatlng(double latlng) {
        this.latlng = latlng;
    }

    public double getLonglng() {
        return longlng;
    }

    public void setLonglng(double longlng) {
        this.longlng = longlng;
    }



}
