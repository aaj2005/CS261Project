package com.example;

public class PedestrianCrossing implements JunctionElement{
    public float ped_light_length; // length of time that the pedestrian light is green for
    public float ped_light_rate;   // how often the pedestrian light goes on in seconds

    public PedestrianCrossing(float ped_light_length, float ped_light_rate) {
        this.ped_light_length = ped_light_length;
        this.ped_light_rate = ped_light_rate;
    }
}
