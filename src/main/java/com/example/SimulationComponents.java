package com.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class SimulationComponents {


    private ArrayList<Rectangle> lanes;

    private double center_x;
    private double center_y;

    private Rectangle[] corners;
    private Rectangle[] lane_separation;

    private Road junction_arms_in[];
    private Road junction_arms_out[];

    private int[] number_of_lanes = {1,1,1,1,1};

    public static final double sim_w = 600;
    public static final double sim_h = 600;

    private static final double SCALE_FACTOR = 2;

    private final int max_out;

    private TrafficLights traffic_system;

    // junction arm: top - right - bottom - left
    public SimulationComponents(int lanes_arm1, int lanes_arm2, int lanes_arm3, int lanes_arm4) {
        traffic_system = new TrafficLights(10,10,10,10,60,2);
        traffic_system.run_lights();
        // number of lanes exiting junction for each arm
        max_out = Math.max(Math.max(Math.max(lanes_arm1,lanes_arm2),lanes_arm3),lanes_arm4);

        corners = new Rectangle[]{
                new Rectangle( // top left
                        0,0, // X and Y
                        (sim_w/SCALE_FACTOR)-(max_out * Lane.lane_w), (sim_h/SCALE_FACTOR)-lanes_arm4 * Lane.lane_w // Width and Height
                ),
                new Rectangle( // top right
                        sim_w - ((sim_w/SCALE_FACTOR)-lanes_arm1 * Lane.lane_w), 0, // X and Y
                        (sim_w/SCALE_FACTOR)-lanes_arm1 * Lane.lane_w, (sim_h/SCALE_FACTOR)-max_out * Lane.lane_w // Width and Height
                ),
                new Rectangle( // bottom left
                        0, sim_h - ((sim_h/SCALE_FACTOR)-max_out * Lane.lane_w), // X and Y
                        (sim_w/SCALE_FACTOR)-lanes_arm3 * Lane.lane_w, (sim_h/SCALE_FACTOR)-max_out * Lane.lane_w // Width and Height
                ),
                new Rectangle( // bottom right
                        sim_w - ((sim_w/SCALE_FACTOR)-max_out * Lane.lane_w), sim_h - ((sim_h/SCALE_FACTOR)-lanes_arm2 * Lane.lane_w), // X and Y
                        (sim_w/SCALE_FACTOR)-max_out * Lane.lane_w, (sim_h/SCALE_FACTOR)-lanes_arm2 * Lane.lane_w // Width and Height
                ),
        };

        center_x = corners[0].getWidth() + Lane.lane_w *max_out;
        center_y = corners[1].getHeight() + Lane.lane_w *max_out;


        // line dividing incoming/outgoing lanes for each junction arm
        lane_separation = new Rectangle[]{
                new Rectangle( // top junction arm
                        corners[0].getWidth() + Lane.lane_w *max_out, 0, // X and Y
                        1, Math.min(corners[0].getHeight(),corners[1].getHeight()) // Width and Height
                ), // right junction arm
                new Rectangle(
                        sim_w - Math.min(corners[1].getWidth(),corners[3].getWidth()), corners[1].getHeight() + Lane.lane_w *max_out, // X and Y
                        Math.min(corners[1].getWidth(),corners[3].getWidth()),1  // Width and Height
                ),
                new Rectangle( // bottom junction arm
                        sim_w-(corners[3].getWidth() + Lane.lane_w *max_out), sim_h-Math.min(corners[2].getHeight(), // X and Y
                        corners[3].getHeight()), 1, Math.min(corners[2].getHeight(),corners[3].getHeight()) // Width and Height
                ),
                new Rectangle( // left junction arm
                        0, sim_h - (corners[2].getHeight() + Lane.lane_w *max_out), // X and Y
                        Math.min(corners[0].getWidth(),corners[2].getWidth()), 1 // Width and Height
                ),

        };

        junction_arms_in = new Road[4];
        junction_arms_out = new Road[4];

        junction_arms_in[0] = new Road(lanes_arm1,
                getCornerDims("tl"), getCornerDims("tr"), Car.VER_DIR, false, Direction.TOP
        );
        junction_arms_in[0].set_start(
                sim_w-getCornerDims("tr")[0]-Car.CAR_WIDTH- (Lane.lane_w-Car.CAR_WIDTH)/2,
                Math.min(getCornerDims("tl")[1],getCornerDims("tr")[1])-Car.CAR_HEIGHT-Car.CAR_GAP
        );


        junction_arms_in[1] = new Road(lanes_arm2, getCornerDims("tr"), getCornerDims("br"), Car.HOR_DIR,false, Direction.RIGHT
        );
        junction_arms_in[1].set_start(
                sim_w-Math.min(getCornerDims("tr")[0],getCornerDims("br")[0]),
                sim_h-getCornerDims("br")[1]- Car.CAR_WIDTH - Car.CAR_GAP
        );

        junction_arms_in[2] = new Road(lanes_arm3, getCornerDims("br"), getCornerDims("bl"), Car.VER_DIR, false, Direction.BOTTOM
        );
        junction_arms_in[2].set_start(
                getCornerDims("bl")[0]+ (Lane.lane_w-Car.CAR_WIDTH)/2,
                sim_h-Math.min(getCornerDims("bl")[1],getCornerDims("br")[1])
        );


        junction_arms_in[3] = new Road(lanes_arm4, getCornerDims("bl"), getCornerDims("tl"), Car.HOR_DIR,false, Direction.LEFT
        );
        junction_arms_in[3].set_start(
                getCornerDims("tl")[0]- Car.CAR_HEIGHT,
                getCornerDims("tl")[1] + (Lane.lane_w-Car.CAR_WIDTH)/2
        );

        carsToAdd = new Rectangle[]{

//                junction_arms_in[0].spawn_car_in_lane(4),
//                junction_arms_in[0].spawn_car_in_lane(3),
//                junction_arms_in[0].spawn_car_in_lane(2),
//                junction_arms_in[0].spawn_car_in_lane(1),
                junction_arms_in[0].spawn_car_in_lane(0),

                junction_arms_in[2].spawn_car_in_lane(0),
                junction_arms_in[3].spawn_car_in_lane(0),
                junction_arms_in[3].spawn_car_in_lane(0),
//                junction_arms_in[0].spawn_car_in_lane(0),
                junction_arms_in[1].spawn_car_in_lane(0),
//                junction_arms_in[1].spawn_car_in_lane(1),
//                junction_arms_in[2].spawn_car_in_lane(0),
//                junction_arms_in[3].spawn_car_in_lane(0),
//                junction_arms_in[3].spawn_car_in_lane(1),
//                junction_arms_in[0].spawn_car_in_lane(1),
        };
//        corners[2].setArcHeight(100);
//        corners[2].setArcWidth(100);
        corners[0].setFill(Color.GREEN);
        corners[1].setFill(Color.BLUE);
        corners[2].setFill(Color.RED);
        corners[3].setFill(Color.PINK);




    }
    public Rectangle[] carsToAdd;
    public Rectangle[] getCorners() {
        return corners;
    }
    public void setColour(int i, Color colour){
        corners[i].setFill(colour);
    }
    public Rectangle[] getLane_separation() {
        return lane_separation;
    }


    // make first car in specific lane turn right
    public void turn_right(int junc_arm, int lane_number){
        Animations animations = new Animations(center_x, center_y);
        Car car = junction_arms_in[junc_arm].get_car_from_lane(lane_number);
        animations.turn_right(
                car,
                junction_arms_in[junc_arm].getDirection(),
                car.getShape().getX()+ junction_arms_in[junc_arm].getDirection().getRight_turn_pos_x(),
                car.getShape().getY()+ junction_arms_in[junc_arm].getDirection().getRight_turn_pos_y()
        );
    }

    public void turn_left(int junc_arm, int lane_number){
        Animations animations = new Animations(center_x, center_y);
        Car car = junction_arms_in[junc_arm].get_car_from_lane(lane_number);
        animations.turn_left(
                car,
                junction_arms_in[junc_arm].getDirection(),
                car.getShape().getX() + junction_arms_in[junc_arm].getDirection().getLeft_turn_pos_x(),
                car.getShape().getY() + junction_arms_in[junc_arm].getDirection().getLeft_turn_pos_y(),
                junction_arms_in[junc_arm].get_lane_size(), max_out
        );
    }



    private int cornerTranslate(String s){
        switch (s){
            case "tl": return 0; // top left
            case "tr": return 1; // top right
            case "bl": return 2; // bottom left
            case "br": return 3; // bottom right
            default: return -1;
        }
    }

    public double[] getCornerDims(String corner){
        Rectangle c = corners[cornerTranslate(corner)];
        return new double[]{c.getWidth(), c.getHeight()};
    }

    public int junction_arm_to_int(String arm){
        switch (arm){
            case "top": return 0;
            case "right": return 1;
            case "bottom": return 2;
            case "left": return 3;
            default: return -1;
        }
    }

    public Car get_first_car(String junction_arm, int lane_number){
        return junction_arms_in[junction_arm_to_int(junction_arm)].get_car_from_lane(lane_number-1);
    }


    public void addCar(String junction_arm, int lane_number){

        junction_arms_in[junction_arm_to_int(junction_arm)].spawn_car_in_lane(lane_number-1);

    }



}
