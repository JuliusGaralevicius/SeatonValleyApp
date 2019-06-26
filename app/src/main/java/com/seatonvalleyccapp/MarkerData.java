package com.seatonvalleyccapp;

/**
 * Created by julius on 14/03/2018.
 */

public class MarkerData {
    public long datePosted;
    public String ownerID;
    public boolean bResolved;
    public long resolvedTimestamp = 0;
    public String description;
    public double lon;
    public double lat;

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }


    public MarkerData(){
    }
    public MarkerData(long datePosted, String ownerID, boolean bResolved, String description, double lat, double lon){
        this.datePosted = datePosted;
        this.ownerID = ownerID;
        this.bResolved = bResolved;
        this.description = description;
        this.lon = lon;
        this.lat = lat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setDatePosted(long datePosted) {
        this.datePosted = datePosted;
    }

    public void setOwnerID(String postedBy) {
        this.ownerID = postedBy;
    }

    public void setbResolved(boolean bResolved) {
        this.bResolved = bResolved;
    }

    public long getDatePosted() {
        return datePosted;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public boolean getbResolved() {
        return bResolved;
    }

    public long getResolvedTimestamp() {
        return resolvedTimestamp;
    }
    public void setResolvedTimestamp(long resolvedTimestamp) {
        this.resolvedTimestamp = resolvedTimestamp;
    }
}
