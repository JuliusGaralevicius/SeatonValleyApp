package com.seatonvalleyccapp;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

/**
 * Created by julius on 14/03/2018.
 */

public class MarkerHashMap {
    private HashMap<Marker, String> markerToString;
    private HashMap<String, Marker> stringToMarker;
    public MarkerHashMap(){
        markerToString = new HashMap<>();
        stringToMarker = new HashMap<>();
    }
    public void addMarker(Marker m, String id){
        System.err.println("----------- Marker added id:" +id);
        markerToString.put(m, id);
        stringToMarker.put(id, m);
    }
    public Marker getMarker(String id){
        return stringToMarker.get(id);
    }
    public String getID(Marker m){
        return markerToString.get(m);
    }
    public void deleteMarker(String id){
        System.err.println("-------------- Marker deleted id:" +id);
        Marker markerToRemove = null;
        for (String a: markerToString.values()){
            if (a.equals(id)){
                markerToRemove = stringToMarker.get(id);
                markerToString.remove(markerToRemove);
                stringToMarker.remove(id);
                break;
            }
        }
    }
    public void deleteMarker(Marker m){
        for (Marker mr: stringToMarker.values()){
            if (mr.equals(m)){
                System.err.println("--------------------- Marker deleted id" +getID(mr));
                stringToMarker.remove(markerToString.get(m));

            }
        }
        markerToString.remove(m);
    }
}
