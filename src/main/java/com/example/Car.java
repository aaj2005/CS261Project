package com.example;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Car {

    private final Rectangle shape;

    // car static properties
    public static final ImagePattern car_texture = new ImagePattern(new Image("car2dtopviewver.png"));
    public static final ImagePattern car_texture_hor = new ImagePattern(new Image("car2dtopviewhor.png"));
    public static final double CAR_WIDTH = 18;
    public static final double CAR_HEIGHT = 43.6;
    public static final double CAR_GAP = 15;
    public static final double CAR_SPEED = 2;



    private boolean made_turn = false; // if the car successfully made the turn
    private boolean turning = false; // if the car is currently turning
    private final Direction direction; // origin direction of car
    private final boolean is_going_right; // if the car will turn right
    private final boolean is_going_left; // if the car will turn left

    /////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// CONSTRUCTOR //////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    public Car(Direction direction, double pos_x, double pos_y, boolean is_right, boolean is_left){
        // choose appropriate texture based on junction arm
        if (direction == Direction.TOP || direction == Direction.BOTTOM){
            shape = new Rectangle(CAR_WIDTH, CAR_HEIGHT);
            shape.setFill(car_texture);
        }else{
            shape = new Rectangle(CAR_HEIGHT, CAR_WIDTH);
            shape.setFill(car_texture_hor);
        }
        // set the spawn position of the car
        shape.setX(pos_x);
        shape.setY(pos_y);
        // rotate to face correct direction
        shape.setRotate(direction.getRotation());
        this.direction = direction;
        this.is_going_right = is_right;
        this.is_going_left = is_left;

    }

    /////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// GETTERS + SETTERS ////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    public Direction getDirection() {
        return direction;
    }

    public Rectangle getShape() {
        return shape;
    }

    public boolean is_going_right() {
        return is_going_right;
    }

    // let the car object know that the turn has been completed
    public void set_made_turn() {
        this.turning = false;
        this.made_turn = true;
    }

    public boolean is_going_left() {
        return is_going_left;
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
