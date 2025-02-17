package com.example;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class Car {

    public static ImagePattern car_texture = new ImagePattern(new Image("car2dtopview.png"));
    private Rectangle shape;
    private double speed;
    private double x_pos;
    private double y_pos;
    private double rotation;
    public static final double CAR_WIDTH = 18;
    public static final double CAR_HEIGHT = 43.6;
    public static final double CAR_GAP = 7;
    public static int HOR_DIR = 0;
    public static int VER_DIR = 1;

    public Car(Direction direction, double pos_x, double pos_y){
        shape = new Rectangle(CAR_WIDTH, CAR_HEIGHT);
        shape.setFill(car_texture);
        shape.setX(pos_x);
        shape.setY(pos_y);
        Rotate rotate = new Rotate();
        rotate.setPivotX(pos_x - direction.getX_offset());
        rotate.setPivotY(pos_y - direction.getY_offset());
        rotate.setAngle(direction.getRotation());
        x_pos = pos_x;
        y_pos = pos_y;
        rotation = direction.getRotation();
        shape.getTransforms().add(rotate);
    }

    public Rectangle getShape() {
        return shape;
    }
}
