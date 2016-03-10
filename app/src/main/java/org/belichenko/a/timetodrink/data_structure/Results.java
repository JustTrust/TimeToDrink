package org.belichenko.a.timetodrink.data_structure;


import java.util.ArrayList;

public class Results {
    public Geometry geometry;
    public String icon;
    public String id;
    public String name;
    public ArrayList<Photos> photos;
    public String place_id;
    public float rating;
    public String reference;
    public String scope;
    public ArrayList<String> types;
    public String vicinity;

    public Results(String status) {
        this.name = status;
    }
}
