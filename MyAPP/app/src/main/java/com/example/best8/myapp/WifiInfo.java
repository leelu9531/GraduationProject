package com.example.best8.myapp;

/**
 * Created by best8 on 2016/5/26.
 */
public class WifiInfo {

    private String time;
    private String SSID;
    private double RSSI;
    private String MAC;

    public WifiInfo(String time,String SSID,double RSSI,String MAC){
        this.time = time;
        this.SSID = SSID;
        this.RSSI = RSSI;
        this.MAC  = MAC;
    }

    public String getTime(){
        return this.time;
    }

    public String getSSID(){
        return this.SSID;
    }

    public double getRSSI(){
        return this.RSSI;
    }

    public String getMAC(){
        return this.MAC;
    }


}
