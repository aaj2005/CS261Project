package com.example;

import javafx.animation.AnimationTimer;
import javafx.animation.PathTransition;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////                                                            //////////////////////////////////////////
    ////////////////////                     Turn Right                             ///////////////////////////////////////////
    ////////////////////                                                             //////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private AnimationTimer timer_right;
    private PathTransition pathTransitionRight;
    private boolean right_paused = false;

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

        // set the start position of the transition
        MoveTo moveTo = new MoveTo();
        moveTo.setX(carX);
        moveTo.setY(carY);

        // pivot point for the curve
        double pivot_point_x;
        double pivot_point_y;

        // determine pivot point based on which junction arm the car is in
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
        quad.setX(endX); // set X coordinate of destination
        quad.setY(endY); // set Y coordinate of destination
        quad.setControlX(pivot_point_x); // set X pivot point
        quad.setControlY(pivot_point_y); // set Y pivot point

        double bezier_arc_length = integrateSimpsons(1000,carX, carY, pivot_point_x, pivot_point_y, endX, endY);
        double totalDuration =  bezier_arc_length/(0.033*1000*Car.VEHICLE_SPEED*1.5); // animation seconds
        // create path object with the quadratic Bézier curve and start position
        Path path = new Path();
        path.getElements().add(moveTo);
        path.getElements().add (quad);

        // create the path transition animation object
        pathTransitionRight = new PathTransition();
        System.out.println("path transition object created");
        pathTransitionRight.setDuration(Duration.millis(totalDuration*1000));
        pathTransitionRight.setNode(car.getShape());
        pathTransitionRight.setPath(path);

        car.set_turning();
        // rotate the car as it moves
        timer_right = new AnimationTimer() {
            double startTime = 0;
            double elapsedTime =0;
            @Override
            public void handle(long now) {
                // time passed in the animation
                if (right_paused){
                    startTime = now - elapsedTime;
                    return;
                }
                elapsedTime = (now - startTime);
                // determine start time
                if (startTime == 0.0){
                    startTime = elapsedTime;
                    elapsedTime = (now - startTime);
                }



                // percentage into the animation
                double progress = (elapsedTime/1_000_000_000.0) / totalDuration; // Normalize [0,1]
                double newAngle=0;
                // calculate the angle based on derivative
                double slope_angle = Math.atan(
                        (-2 * (1 - progress) * carY + 2 * pivot_point_y * (-2 * progress + 1) + 2 * progress * endY)
                                / (-2 * (1 - progress) * carX + 2 * pivot_point_x * (-2 * progress + 1) + 2 * progress * endX)
                ) * 180 / Math.PI;
                // unit circle angle shifting based on the junction arm the car is initially in
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
                // rotate the car
                car.getShape().setRotate(newAngle);

                // make the path transition run alongside the rotation
//                pathTransition.jumpTo(Duration.seconds(progress*totalDuration));
                // stop transition once animation time is reached
                if ((elapsedTime/1_000_000_000.0) >= totalDuration) {
                    car.set_made_turn();
                    System.out.println("true");
                    pathTransitionRight = null;
                    stop();
                }
            }
        };
        pathTransitionRight.play();
        timer_right.start();

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////                                                            //////////////////////////////////////////
    ////////////////////                     Turn Left                               ///////////////////////////////////////////
    ////////////////////                                                             //////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private AnimationTimer timer_left;
    private PathTransition pathTransitionLeft;
    private boolean left_paused = false;


    public void turn_left(Car car, Direction direction, double carX, double carY, int lane_number, int max_lane_out){
        // covert to quadrant-based system
        double startX_trans = to_00_coordinate(carX, carY,direction)[0];
        double startY_trans = to_00_coordinate(carX, carY,direction)[1];

        // perform reflections/translations
        double end_x_pre_transition = startY_trans * direction.getLeft_trans_x()+direction.getLeft_turn_offset_x();
        double end_y_pre_transition = startX_trans * direction.getLeft_trans_y()+direction.getLeft_turn_offset_y();



        // offsets because javafx is funny
        double[] offset = {0,0};
        switch(direction){
            case TOP:
                offset[1] =direction.getLane_switch_x()*(lane_number-max_lane_out-2)*Lane.lane_w+(-Car.CAR_WIDTH+Car.VEHICLE_GAP);
                break;
            case BOTTOM:
                offset[1] =direction.getLane_switch_x()*(lane_number-max_lane_out+2)*Lane.lane_w+(-Car.CAR_WIDTH+Car.VEHICLE_GAP);
                break;
            case RIGHT:
                offset[0] =direction.getLane_switch_y()*(max_lane_out-lane_number+1)*Lane.lane_w+(Car.CAR_WIDTH+Car.CAR_WIDTH/2+Car.VEHICLE_GAP);
                break;
            case LEFT:
                offset[0] =direction.getLane_switch_y()*(max_lane_out-lane_number-1)*Lane.lane_w+(Car.CAR_WIDTH+Car.CAR_WIDTH/2+Car.VEHICLE_GAP);
                break;
        }

        // convert back to default coordinate system
        double endX = from_00_coordinates(end_x_pre_transition, end_y_pre_transition,direction)[0] - offset[0];
        double endY = from_00_coordinates(end_x_pre_transition, end_y_pre_transition,direction)[1]- offset[1] ;

        // set the start position of the transition
        MoveTo moveTo = new MoveTo();
        moveTo.setX(carX);
        moveTo.setY(carY);

        // pivot point for the curve
        double pivot_point_x;
        double pivot_point_y;

        // determine pivot point based on which junction arm the car is in
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

        double bezier_arc_length = integrateSimpsons(1000,carX, carY, pivot_point_x, pivot_point_y, endX, endY);
        double totalDuration =  bezier_arc_length/(0.033*1000*Car.VEHICLE_SPEED); // animation seconds

        // create path object with the quadratic Bézier curve and start position
        Path path = new Path();
        path.getElements().add(moveTo);
        path.getElements().add (quad);
        // create the path transition animation object
        pathTransitionLeft = new PathTransition();
        pathTransitionLeft.setDuration(Duration.millis(totalDuration*1000));
        pathTransitionLeft.setNode(car.getShape());
        pathTransitionLeft.setPath(path);
        car.set_turning();

        // rotate the car as it moves
        timer_left = new AnimationTimer() {
            double startTime = 0;
            double elapsedTime = 0;
            @Override
            public void handle(long now) {
                // time passed in the animation
                if (left_paused){
                    startTime = now - elapsedTime;
                    return;
                }
                elapsedTime = (now - startTime);


                // determine start time
                if (startTime == 0.0){
                    startTime = elapsedTime;
                    elapsedTime = (now - startTime);
                }


                // percentage into the animation
                double progress = (elapsedTime/1_000_000_000.0) / totalDuration; // Normalize [0,1]
                double newAngle=0;

                // calculate the angle based on derivative
                double slope_angle = Math.atan(
                        (-2 * (1 - progress) * carY + 2 * pivot_point_y * (-2 * progress + 1) + 2 * progress * endY)
                                / (-2 * (1 - progress) * carX + 2 * pivot_point_x * (-2 * progress + 1) + 2 * progress * endX)
                ) * 180 / Math.PI;

                // unit circle angle shifting based on the junction arm the car is initially in
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

                // rotate the car
                car.getShape().setRotate(newAngle);
                // make the path transition run alongside the rotation
//                pathTransition.jumpTo(Duration.seconds(progress*totalDuration));

                // stop transition once animation time is reached

                if ((elapsedTime/1_000_000_000.0) >= totalDuration) {
                    car.set_made_turn();

                    pathTransitionLeft = null;
                    stop();
                }
            }
        };

        pathTransitionLeft.play();
        timer_left.start();
    }

    public void pause_turns(){
        if (pathTransitionRight != null){
            pathTransitionRight.pause();
        }
        if (timer_right != null){
            right_paused = true;
        }
        if (pathTransitionLeft!= null){
            pathTransitionLeft.pause();
        }
        if (timer_left != null){
            left_paused = true;
        }
    }

    public void resume_turns(){
        if (timer_right != null){
            right_paused = false;
        }
        if (timer_left != null){
            left_paused = false;
        }
        if (pathTransitionLeft!= null){
            pathTransitionLeft.play();
        }
        if (pathTransitionRight != null){
            pathTransitionRight.play();
        }
    }

    private static double[] bezierDerivative(double t, double carX, double carY, double pivotX, double pivotY, double endX, double endY ) {
        double x = (-2 * (1 - t) * carX + 2 * pivotX * (-2 * t + 1) + 2 * t * endX);
        double y = (-2 * (1 - t) * carY + 2 * pivotY * (-2 * t + 1) + 2 * t * endY);
        return new double[]{x, y};
    }

    
    private static double speed(double t, double carX, double carY, double pivotX, double pivotY, double endX, double endY) {
        double[] deriv = bezierDerivative(t, carX, carY, pivotX, pivotY, endX, endY);
        return Math.sqrt(deriv[0] * deriv[0] + deriv[1] * deriv[1]);
    }

    // approximate integral calculation for arc length
    private static double integrateSimpsons( int N, double carX, double carY, double pivotX, double pivotY, double endX, double endY) {
        double h = 1.0 / N; // Step size
        double sum = speed(0,carX, carY, pivotX, pivotY, endX, endY) + speed(1,carX, carY, pivotX, pivotY, endX, endY);

        for (int i = 1; i < N; i++) {
            double t = i * h;
            sum += (i % 2 == 0 ? 2 : 4) * speed(t,carX, carY, pivotX, pivotY, endX, endY);
        }

        return (h / 3) * sum;
    }




}
