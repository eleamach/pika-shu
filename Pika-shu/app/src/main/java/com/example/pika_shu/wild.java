package com.example.pika_shu;

public class wild {
    int id;
    String lat;
    String lng;

    public wild(int id, String lat, String lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    public wild(){}
    public int getId(){return  this.id;}
    public void setID(int id){
        this.id = id;
    }
    public String getlat(){return  this.lat;}
    public void setlat(String lat){
        this.lat = lat;
    }
    public String getdlong(){return  this.lng;}
    public void setlong(String lng){
        this.lng = lng;
    }

}

