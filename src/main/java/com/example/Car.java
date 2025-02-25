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
    private String state;

    public Car(Direction direction, double pos_x, double pos_y){
        if (direction == Direction.TOP || direction == Direction.BOTTOM){
            shape = new Rectangle(CAR_WIDTH, CAR_HEIGHT);
            shape.setFill(car_texture);
        }else{
            shape = new Rectangle(CAR_HEIGHT, CAR_WIDTH);
            shape.setFill(car_texture_hor);
        }
        shape.setX(pos_x);
        shape.setY(pos_y);
        shape.setRotate(direction.getRotation());
    }

    public Rectangle getShape() {
        return shape;
    }
}
