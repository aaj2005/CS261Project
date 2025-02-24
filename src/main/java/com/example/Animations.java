package com.example;

import javafx.animation.AnimationTimer;
import javafx.animation.PathTransition;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class Animations {

    private final double center_x;
    private final double center_y;

    public Animations(double center_x, double center_y) {
        this.center_x = center_x;
        this.center_y = center_y;
    }

    // go to quadrant based coordinate system
    private double[] to_00_coordinate(double x, double y, Direction direction){
        switch(direction){
            case TOP:
            case BOTTOM:
                return new double[]{center_x-x, y -center_y};
            case RIGHT:
            case LEFT:
                return new double[]{ x-center_x, center_y-y};
            default: return new double[]{0,0};
        }


    }

    // go back to the standard coordinate system
    private double[] from_00_coordinates(double x, double y, Direction direction){

        switch(direction){

            case TOP:
            case BOTTOM:
                return new double[]{x +center_x, y+center_y};
            case RIGHT:
            case LEFT:
                return new double[]{center_x-x, center_y-y};
            default: return new double[]{0,0};
        }

    }


    public void turn_right(Car car, Direction direction, double carX, double carY){


        // covert to quadrant-based system
        double startX_trans = to_00_coordinate(carX, carY,direction)[0];
        double startY_trans = to_00_coordinate(carX, carY,direction)[1];

        // perform reflections/translations
        double end_x_pre_transition = startY_trans * direction.getRight_trans_x() + direction.getRight_turn_offset_x();
        double end_y_pre_transition = startX_trans * direction.getRight_trans_y() + direction.getRight_turn_offset_y();

        // convert back to default coordinate system
        double endX = from_00_coordinates(end_x_pre_transition, end_y_pre_transition,direction)[0];
        double endY = from_00_coordinates(end_x_pre_transition, end_y_pre_transition,direction)[1];

        MoveTo moveTo = new MoveTo();
        moveTo.setX(carX);
        moveTo.setY(carY);
        double pivot_point_x;
        double pivot_point_y;

        switch (direction){
            case TOP:
            case BOTTOM:
                pivot_point_x = carX;
                pivot_point_y = endY;
                break;
            case RIGHT:
            case LEFT:
                pivot_point_x = endX;
                pivot_point_y = carY;
                break;
            default:
                pivot_point_x = 0;
                pivot_point_y = 0;
        }

        // Quadratic Bezier Curve
        QuadCurveTo quad = new QuadCurveTo();
        quad.setX(endX);
        quad.setY(endY);
        quad.setControlX(pivot_point_x);
        quad.setControlY(pivot_point_y);

        double totalDuration = 2.5; // animation seconds

        Path path = new Path();
        path.getElements().add(moveTo);
        path.getElements().add (quad);
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(totalDuration*1000));
        pathTransition.setNode(car.getShape());
        pathTransition.setPath(path);

        AnimationTimer timer = new AnimationTimer() {
            double startTime = 0;
            @Override
            public void handle(long now) {
                double elapsedTime = (now - startTime);

                if (startTime == 0.0){
                    startTime = elapsedTime;
                    elapsedTime = (now - startTime);
                }

                if (elapsedTime/1_000_000_000.0 >= totalDuration) {
                    startTime = 0;
                    elapsedTime = totalDuration* 1_000_000_000.0 ; // Clamp at total_duration
                }

                double progress = (elapsedTime/1_000_000_000.0) / totalDuration; // Normalize [0,1]
                double newAngle=0;
                double slope_angle = Math.atan(
                        (-2 * (1 - progress) * carY + 2 * pivot_point_y * (-2 * progress + 1) + 2 * progress * endY)
                                / (-2 * (1 - progress) * carX + 2 * pivot_point_x * (-2 * progress + 1) + 2 * progress * endX)
                ) * 180 / Math.PI;
                switch (direction){
                    case TOP:
                        newAngle = slope_angle -90;
                        break;
                    case BOTTOM:
                        newAngle = slope_angle +90;
                        break;
                    case RIGHT:
                        newAngle = Math.abs(slope_angle);
                        break;
                    case LEFT:
                        newAngle = -180+Math.abs(slope_angle);
                        break;
                }
                car.getShape().setRotate(newAngle);

                pathTransition.jumpTo(Duration.seconds(progress*totalDuration));
                if ((elapsedTime/1_000_000_000.0) >= totalDuration) {
                    stop();
                }
            }
        };
        pathTransition.play();
        timer.start();

    }

    public void turn_left(Car car, Direction direction, double carX, double carY, int lane_number, int max_lane_out){
        double startX_trans = to_00_coordinate(carX, carY,direction)[0];
        double startY_trans = to_00_coordinate(carX, carY,direction)[1];

        // perform reflections/translations
        double end_x_pre_transition = startY_trans * direction.getLeft_trans_x()+direction.getLeft_turn_offset_x();
        double end_y_pre_transition = startX_trans * direction.getLeft_trans_y()+direction.getLeft_turn_offset_y();

        // convert back to default coordinate system
        double[] offset = {0,0};
        switch(direction){
            case TOP:
                offset[1] =direction.getLane_switch_x()*(lane_number-max_lane_out-2)*Lane.lane_w+(-Car.CAR_WIDTH+Car.CAR_GAP);
                break;
            case BOTTOM:
                offset[1] =direction.getLane_switch_x()*(lane_number-max_lane_out+2)*Lane.lane_w+(-Car.CAR_WIDTH+Car.CAR_GAP);
                break;
            case RIGHT:
                offset[0] =direction.getLane_switch_y()*(max_lane_out-lane_number+1)*Lane.lane_w+(Car.CAR_WIDTH+Car.CAR_WIDTH/2+Car.CAR_GAP);
                break;
            case LEFT:
                offset[0] =direction.getLane_switch_y()*(max_lane_out-lane_number-1)*Lane.lane_w+(Car.CAR_WIDTH+Car.CAR_WIDTH/2+Car.CAR_GAP);
                break;
        }
        double endX = from_00_coordinates(end_x_pre_transition, end_y_pre_transition,direction)[0] - offset[0];
        double endY = from_00_coordinates(end_x_pre_transition, end_y_pre_transition,direction)[1]- offset[1] ;
        MoveTo moveTo = new MoveTo();
        moveTo.setX(carX);
        moveTo.setY(carY);
        double pivot_point_x;
        double pivot_point_y;

        switch (direction){
            case TOP:
            case BOTTOM:
                pivot_point_x = carX;
                pivot_point_y = endY;
                break;
            case RIGHT:
            case LEFT:
                pivot_point_x = endX;
                pivot_point_y = carY;
                break;
            default:
                pivot_point_x = 0;
                pivot_point_y = 0;
        }

        // Quadratic Bezier Curve
        QuadCurveTo quad = new QuadCurveTo();
        quad.setX(endX);
        quad.setY(endY);
        quad.setControlX(pivot_point_x);
        quad.setControlY(pivot_point_y);

        double totalDuration = 3.5; // animation seconds

        Path path = new Path();
        path.getElements().add(moveTo);
        path.getElements().add (quad);
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(totalDuration*1000));
        pathTransition.setNode(car.getShape());
        pathTransition.setPath(path);

        AnimationTimer timer = new AnimationTimer() {
            double startTime = 0;
            @Override
            public void handle(long now) {
                double elapsedTime = (now - startTime);

                if (startTime == 0.0){
                    startTime = elapsedTime;
                    elapsedTime = (now - startTime);
                }

                if (elapsedTime/1_000_000_000.0 >= totalDuration) {
                    startTime = 0;
                    elapsedTime = totalDuration* 1_000_000_000.0 ; // Clamp at total_duration
                }

                double progress = (elapsedTime/1_000_000_000.0) / totalDuration; // Normalize [0,1]
                double newAngle=0;
                double slope_angle = Math.atan(
                        (-2 * (1 - progress) * carY + 2 * pivot_point_y * (-2 * progress + 1) + 2 * progress * endY)
                                / (-2 * (1 - progress) * carX + 2 * pivot_point_x * (-2 * progress + 1) + 2 * progress * endX)
                ) * 180 / Math.PI;
                switch (direction){
                    case TOP:
                        newAngle = 180-(-slope_angle+90);
                        break;
                    case BOTTOM:
                        newAngle = (slope_angle-90);
                        break;
                    case RIGHT:
                        newAngle = -Math.abs(slope_angle);
                        break;
                    case LEFT:
                        newAngle = 180-Math.abs(slope_angle);
                        break;
                }

                car.getShape().setRotate(newAngle);

                pathTransition.jumpTo(Duration.seconds(progress*totalDuration));
                if ((elapsedTime/1_000_000_000.0) >= totalDuration) {
                    stop();
                }
            }
        };
        pathTransition.play();
        timer.start();
    }

}
