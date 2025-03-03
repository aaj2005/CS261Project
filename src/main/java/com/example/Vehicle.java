package com.example;


import javafx.scene.shape.Rectangle;

public abstract class Vehicle{
    public static final double VEHICLE_SPEED = 2;
    public static final double VEHICLE_GAP = 15;


    public abstract Rectangle getShape();

    public abstract double getHeight();

}
