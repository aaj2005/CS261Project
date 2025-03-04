package com.example;

// used to aid in calculation of max wait time
public class StatCar {
    public double t; // how long the car has waited
    public double d; // distance from the car to the junction

    public StatCar(double t, double d) {
        this.t = t;
        this.d = d;
    }
}
