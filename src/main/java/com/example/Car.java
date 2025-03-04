package com.example;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Car extends Vehicle{

    private final Rectangle shape;

    // car static properties
    public static final ImagePattern car_texture_ver = new ImagePattern(new Image("car2dtopviewver.png"));
    public static final ImagePattern car_texture_hor = new ImagePattern(new Image("car2dtopviewhor.png"));
    public static final double CAR_WIDTH = 18;
    public static final double CAR_HEIGHT = 43.6;

    // used for determining the direction the car will travel to
    private Cardinal dir;

    private final Direction direction; // origin direction of car


    /////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// CONSTRUCTOR //////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    public Car(Direction direction, double pos_x, double pos_y, Cardinal dir){
        // choose appropriate texture based on junction arm
        if (direction == Direction.TOP || direction == Direction.BOTTOM){
            shape = new Rectangle(CAR_WIDTH, CAR_HEIGHT);
            shape.setFill(car_texture_ver);
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
        this.dir = dir;

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





    public double getHeight(){
        return CAR_HEIGHT;
    }

    public Cardinal getDir(){
        return this.dir;
    }

}
