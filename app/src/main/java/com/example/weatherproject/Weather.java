package com.example.weatherproject;

public class Weather {
    String image;
    double temp;
    long time;
    String desc;

    public Weather(String image, double temp, long time, String desc){
        this.image = image;
        this.temp = temp;
        this.time = time;
        this.desc = desc;
    }

    public String getImage(){
        return image;
    }

    public double getTemperature(){
        return temp;
    }

    public String getDescription() {
        return desc;
    }

    public long getTime() {
        return time;
    }
}
