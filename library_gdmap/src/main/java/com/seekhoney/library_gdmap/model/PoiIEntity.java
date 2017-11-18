package com.seekhoney.library_gdmap.model;

import com.amap.api.services.core.LatLonPoint;

/**
 * Created by ryan on 17/6/6.
 */

public class PoiIEntity
{
    private String title;
    private String address;
    private LatLonPoint latLonPoint;

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

    public LatLonPoint getLatLonPoint() {
        return latLonPoint;
    }

    public void setLatLonPoint(LatLonPoint latLonPoint) {
        this.latLonPoint = latLonPoint;
    }
}
