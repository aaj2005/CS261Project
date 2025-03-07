package com.example;


import javafx.scene.shape.Rectangle;

public abstract class Vehicle{
    public static final double VEHICLE_SPEED = 2.25;
    public static final double VEHICLE_GAP = 15;

    private boolean made_turn = false; // if the car successfully made the turn
    private boolean turning = false; // if the car is currently turning

    public abstract Rectangle getShape();

    public abstract double getHeight();

    // let the car object know that the turn has been completed
    public void set_made_turn() {
        this.made_turn = true;
        this.turning = false;
    }


    // if car is currently turning
    public boolean is_turning(){
        return turning;
    }

    // let the car object know that the car is turning
    public void set_turning(){
        this.turning = true;
    }

    // check if the turn is completed
    public boolean has_made_turn() {
        return made_turn;

    }



}
