package com.example;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Bus extends Vehicle {


    private final Rectangle shape;

    public static final ImagePattern bus_texture_ver = new ImagePattern(new Image("bustopviewver.png"));
    public static final ImagePattern bus_texture_hor = new ImagePattern(new Image("bustopviewhor.png"));
    private static final double BUS_WIDTH = 18;
    private static final double BUS_HEIGHT = 60;

    private final Direction direction; // origin direction of bus

    public Bus(Direction direction, double pos_x, double pos_y){
        if (direction == Direction.TOP || direction == Direction.BOTTOM){
            shape = new Rectangle(BUS_WIDTH, BUS_HEIGHT);
            shape.setFill(bus_texture_ver);
        }else{
            shape = new Rectangle(BUS_HEIGHT, BUS_WIDTH);
            shape.setFill(bus_texture_hor);
        }
        // set the spawn position of the car
        shape.setX(pos_x);
        shape.setY(pos_y);
        this.direction = direction;
        // rotate to face correct direction
        shape.setRotate(direction.getRotation());
    }

    public Direction getInboundDirection() {
        return this.direction;
    }

    public Direction getOutboundDirection() {
        return Direction.getOpposite(this.direction);
    }

    public Rectangle getShape() {
        return shape;
    }

    public double getHeight(){
        return BUS_HEIGHT;
    }
}
