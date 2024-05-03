package com.example.pika_shu;

public class soin {
    int _id;
    String _lat;
    String _long;
    public soin(int id, String lat, String longitude) {
        this._id = id;
        this._lat = lat;
        this._long = longitude;
    }

    public soin() {
    }

    public soin(int i, String string) {
    }

    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getLat(){
        return this._lat;
    }

    public void setLat(String lat){
        this._lat = lat;
    }

    public String getLong(){
        return this._long;
    }

    public void setLong(String longitude){
        this._long = longitude;
    }
}