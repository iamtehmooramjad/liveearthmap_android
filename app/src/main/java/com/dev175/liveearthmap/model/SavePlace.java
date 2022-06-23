package com.dev175.liveearthmap.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

@Entity
public class SavePlace {

    @PrimaryKey(autoGenerate = true)
    int placeId;
    String srcAddress;
    double srcLat;
    double srcLng;
    String desAddress;
    double desLat;
    double desLng;

    public SavePlace(String srcAddress, double srcLat, double srcLng, String desAddress, double desLat, double desLng) {
        this.srcAddress = srcAddress;
        this.srcLat = srcLat;
        this.srcLng = srcLng;
        this.desAddress = desAddress;
        this.desLat = desLat;
        this.desLng = desLng;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public String getSrcAddress() {
        return srcAddress;
    }

    public void setSrcAddress(String srcAddress) {
        this.srcAddress = srcAddress;
    }

    public double getSrcLat() {
        return srcLat;
    }

    public void setSrcLat(double srcLat) {
        this.srcLat = srcLat;
    }

    public double getSrcLng() {
        return srcLng;
    }

    public void setSrcLng(double srcLng) {
        this.srcLng = srcLng;
    }

    public String getDesAddress() {
        return desAddress;
    }

    public void setDesAddress(String desAddress) {
        this.desAddress = desAddress;
    }

    public double getDesLat() {
        return desLat;
    }

    public void setDesLat(double desLat) {
        this.desLat = desLat;
    }

    public double getDesLng() {
        return desLng;
    }

    public void setDesLng(double desLng) {
        this.desLng = desLng;
    }
}
