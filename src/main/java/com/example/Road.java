package com.example;

/* DUMMY CLASS */
public class Road implements JunctionElement {
    public float actual_light_duration; // how long the light is green for
    public float inbound_vph;           // how many cars come into the junction from this road per second
    public int lanes;                   // number of lanes on this road
    
    public Road(float actual_light_duration, float inbound_vph, int lanes) {
        this.actual_light_duration = actual_light_duration;
        this.inbound_vph = inbound_vph;
        this.lanes = lanes;
    }
}
