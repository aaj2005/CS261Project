package com.example;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class Car {

    public static final ImagePattern car_texture = new ImagePattern(new Image("car2dtopviewver.png"));
    public static final ImagePattern car_texture_hor = new ImagePattern(new Image("car2dtopviewhor.png"));
    private Rectangle shape;
    private double speed;
    public static final double CAR_WIDTH = 18;
    public static final double CAR_HEIGHT = 43.6;
    public static final double CAR_GAP = 4;
    public static int HOR_DIR = 0;
    public static int VER_DIR = 1;
    public static final double CAR_SPEED = 2;
    private String state;

    private Cardinal out_dir; // the direction in which the car will be leaving

    public Car(Direction direction, double pos_x, double pos_y, Cardinal out_dir){
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

        this.out_dir = out_dir;
    }

    public Rectangle getShape() {
        return shape;
    }
}
