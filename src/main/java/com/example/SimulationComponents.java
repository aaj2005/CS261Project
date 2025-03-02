package com.example;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class SimulationComponents {


    private ArrayList<Rectangle> lanes;


    // center of the simulation
    private double center_x;
    private double center_y;

    // the corners of the simulation
    private Rectangle[] corners;

    private ArrayList<Rectangle> crossings;
    private Rectangle[] lane_separation;

    // cars which are about to enter the junction
    private Road junction_arms_in[];

    // cars which have exited the junction
    private Road junction_arms_out[];

    private int[] number_of_lanes = {1,1,1,1,1};

    public static final double sim_w = 600;
    public static final double sim_h = 600;

    private static final double SCALE_FACTOR = 2;
    private static final double PEDESTRIAN_SCALE_FACTOR = 15;
    private static final int NUMBER_OF_LINES = 33;

    // maximum number of lanes set
    private final int max_out;

    private float time = 0;

    // traffic light logic instance
    private TrafficLights traffic_system;

    // junction arm: top - right - bottom - left
    public SimulationComponents(int lanes_arm1, int lanes_arm2, int lanes_arm3, int lanes_arm4, boolean crossings_enabled){


        // number of lanes exiting junction for each arm
        max_out = Math.max(Math.max(Math.max(lanes_arm1,lanes_arm2),lanes_arm3),lanes_arm4);

        // generate the rectangles for each corner based on the input lanes
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

        // determine the center
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

        float[] vph_1 = new float[] {0, 360000, 1800, 900};
        float[] vph_2 = new float[] {0, 0, 3000, 50000000};
        float[] vph_4 = new float[] {90000, 0, 0, 0};
        float[] vph_3 = new float[] {0, 10000, 0, 1800};

        // create a road instance for TOP junction arm, this road is specifically for cars entering the junction
        junction_arms_in[0] = new Road(
                lanes_arm1, // number of lanes
                getCornerDims("tl"), // the two corners that are adjacent to the Road
                getCornerDims("tr"), // the two corners that are adjacent to the Road
                Car.VER_DIR, // this is for calculating the number of cars which each lane can hold
                false, // pedestrian crossings
                Direction.TOP, // the junction arm direction
                vph_1 // VPH for each outbound direction
        );

        // set the spawn position for the TOP junction arm
        junction_arms_in[0].set_start(
                sim_w-getCornerDims("tr")[0]-Car.CAR_WIDTH- (Lane.lane_w-Car.CAR_WIDTH)/2,
                // Math.min(getCornerDims("tl")[1],getCornerDims("tr")[1])-Car.CAR_HEIGHT-Car.CAR_GAP
                -Car.CAR_HEIGHT -Car.CAR_HEIGHT/2
        );

        // create a road instance for RIGHT junction arm, this road is specifically for cars entering the junction
        junction_arms_in[1] = new Road(
                lanes_arm2, // number of lanes
                getCornerDims("tr"), // the two corners that are adjacent to the Road
                getCornerDims("br"), // the two corners that are adjacent to the Road
                Car.HOR_DIR, // this is for calculating the number of cars which each lane can hold
                false,  // pedestrian crossings
                Direction.RIGHT, // the junction arm direction
                vph_2 // VPH for each outbound direction
        );

        // set the spawn position for the RIGHT junction arm
        junction_arms_in[1].set_start(
                // sim_w-Math.min(getCornerDims("tr")[0],getCornerDims("br")[0]),
                sim_w + Car.CAR_HEIGHT/2,
                sim_h-getCornerDims("br")[1]- Car.CAR_WIDTH - Car.CAR_GAP
        );

        // create a road instance for BOTTOM junction arm, this road is specifically for cars entering the junction
        junction_arms_in[2] = new Road(
                lanes_arm3, // number of lanes
                getCornerDims("br"), // the two corners that are adjacent to the Road
                getCornerDims("bl"), // the two corners that are adjacent to the Road
                Car.VER_DIR, // this is for calculating the number of cars which each lane can hold
                false,  // pedestrian crossings
                Direction.BOTTOM, // the junction arm direction
                vph_3 // VPH for each outbound direction
        );

        // set the spawn position for the BOTTOM junction arm
        junction_arms_in[2].set_start(
                getCornerDims("bl")[0]+ (Lane.lane_w-Car.CAR_WIDTH)/2,
                // sim_h-Math.min(getCornerDims("bl")[1],getCornerDims("br")[1])
                sim_h+Car.CAR_HEIGHT/2
        );

        // create a road instance for LEFT junction arm, this road is specifically for cars entering the junction
        junction_arms_in[3] = new Road(
                lanes_arm4, // number of lanes
                getCornerDims("bl"), // the two corners that are adjacent to the Road
                getCornerDims("tl"), // the two corners that are adjacent to the Road
                Car.HOR_DIR, // this is for calculating the number of cars which each lane can hold
                false,  // pedestrian crossings
                Direction.LEFT, // the junction arm direction
                vph_4 // VPH for each outbound direction
        );

        // set the spawn position for the LEFT junction arm
        junction_arms_in[3].set_start(
                // getCornerDims("tl")[0]- Car.CAR_HEIGHT,
                -Car.CAR_HEIGHT,
                getCornerDims("tl")[1] + (Lane.lane_w-Car.CAR_WIDTH)/2
        );


        junction_arms_out[0] = new Road(lanes_arm1,getCornerDims("tl"),getCornerDims("tr"),Car.VER_DIR, false, Direction.TOP,vph_1);
        junction_arms_out[1] = new Road(lanes_arm2,getCornerDims("tr"),getCornerDims("br"),Car.HOR_DIR, false, Direction.RIGHT,vph_2);
        junction_arms_out[2] = new Road(lanes_arm3,getCornerDims("br"),getCornerDims("bl"),Car.VER_DIR, false, Direction.BOTTOM,vph_3);
        junction_arms_out[3] = new Road(lanes_arm4,getCornerDims("bl"),getCornerDims("tl"),Car.HOR_DIR, false, Direction.LEFT,vph_4);

        traffic_system = new TrafficLights(10,10,10,10,60,2,junction_arms_out);
        traffic_system.run_lights();

        carsToAdd = new Rectangle[]{

//                junction_arms_in[0].spawn_car_in_lane(4),
//                junction_arms_in[0].spawn_car_in_lane(3),
//                junction_arms_in[0].spawn_car_in_lane(2),
//                junction_arms_in[0].spawn_car_in_lane(1),
                // junction_arms_in[0].spawn_car_in_lane(0, Cardinal.S),

                // junction_arms_in[2].spawn_car_in_lane(0, Cardinal.S),
                // junction_arms_in[3].spawn_car_in_lane(0, Cardinal.S),
                // junction_arms_in[3].spawn_car_in_lane(0, Cardinal.S),
//                junction_arms_in[0].spawn_car_in_lane(0),
                // junction_arms_in[1].spawn_car_in_lane(0, Cardinal.S),
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
        if (crossings_enabled){
            addCrossings(NUMBER_OF_LINES);
        }



    }
    public void addCrossings(int stripecount){
        crossings = new ArrayList<>();
        Rectangle [] verticalcrossings = new Rectangle[]{
                new Rectangle(
                        Math.min(getCornerPositions("tl")[0] + getCornerDims("tl")[0],getCornerPositions("bl")[0] + getCornerDims("bl")[0]) - (sim_w/PEDESTRIAN_SCALE_FACTOR), getCornerPositions("tl")[1]+ getCornerDims("tl")[1], (sim_w/PEDESTRIAN_SCALE_FACTOR), getCornerPositions("bl")[1] - getCornerPositions("tl")[1] - getCornerDims("tl")[1]
                ),
                new Rectangle(
                        Math.max(getCornerPositions("tr")[0], getCornerPositions("br")[0]), getCornerPositions("tr")[1]+ getCornerDims("tr")[1], (sim_w/PEDESTRIAN_SCALE_FACTOR), getCornerPositions("br")[1] - getCornerPositions("tr")[1] - getCornerDims("tr")[1]
                )
        };
        Rectangle [] horizontalcrossings = new Rectangle[]{
                new Rectangle(
                        getCornerPositions("tl")[0]+ getCornerDims("tl")[0], Math.min(getCornerPositions("tl")[1] + getCornerDims("tl")[1],getCornerPositions("tr")[1] + getCornerDims("tr")[1] )- (sim_w/PEDESTRIAN_SCALE_FACTOR), getCornerPositions("tr")[0] - getCornerPositions("tl")[0] - getCornerDims("tl")[0] , (sim_w/PEDESTRIAN_SCALE_FACTOR)
                ),
                new Rectangle(
                        getCornerPositions("bl")[0] + getCornerDims("bl")[0], Math.max(getCornerPositions("bl")[1], getCornerPositions("br")[1]), getCornerPositions("br")[0] - getCornerPositions("bl")[0] - getCornerDims("bl")[0], (sim_w/PEDESTRIAN_SCALE_FACTOR)
                )
        };
        for (Rectangle verticalrectangle: verticalcrossings){
            for (int i = 1; i < stripecount; i+= 2){
                crossings.add(new Rectangle(verticalrectangle.getX(), verticalrectangle.getY() + i * verticalrectangle.getHeight()/stripecount, verticalrectangle.getWidth(), verticalrectangle.getHeight()/stripecount));
            }
        }
        for (Rectangle horizontalrectangle: horizontalcrossings){
            for (int i = 1; i < stripecount; i+= 2){
                crossings.add(new Rectangle(horizontalrectangle.getX() + i * horizontalrectangle.getWidth()/stripecount, horizontalrectangle.getY(), horizontalrectangle.getWidth()/stripecount, horizontalrectangle.getHeight()));
            }
        }

        for (Rectangle crossing:crossings){
            crossing.setFill(Color.WHITE);

        }
    }
    public Rectangle[] carsToAdd;
    public Rectangle[] getCorners() {
        return corners;
    }
    public ArrayList<Rectangle> getCrossings() {
        if (crossings == null){
            return new ArrayList<>();
        }
        return crossings;
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
                car, // car object
                junction_arms_in[junc_arm].getDirection(), // direction enum
                car.getShape().getX()+ junction_arms_in[junc_arm].getDirection().getRight_turn_pos_x(), // X pos of car
                car.getShape().getY()+ junction_arms_in[junc_arm].getDirection().getRight_turn_pos_y() // Y pos of car
        );
    }

    // make first car in specific lane turn left
    public void turn_left(int junc_arm, int lane_number){
        Animations animations = new Animations(center_x, center_y);
        Car car = junction_arms_in[junc_arm].get_car_from_lane(lane_number);
        animations.turn_left(
                car, // car object
                junction_arms_in[junc_arm].getDirection(), // direction enum
                car.getShape().getX() + junction_arms_in[junc_arm].getDirection().getLeft_turn_pos_x(), // X pos of car
                car.getShape().getY() + junction_arms_in[junc_arm].getDirection().getLeft_turn_pos_y(), // Y pos of car
                junction_arms_in[junc_arm].get_lane_size(), max_out // number of lanes in the road, max number of lanes in simulation
        );
    }

    // make first car in specific lane go straight
    public void go_straight(int junc_arm, int lane_number){
        Animations animations = new Animations(center_x, center_y);
        Car car = junction_arms_in[junc_arm].get_car_from_lane(lane_number);
        animations.go_straight(
                car, // car object
                junction_arms_in[junc_arm].getDirection(), // direction enum
                car.getShape().getX() + junction_arms_in[junc_arm].getDirection().getLeft_turn_pos_x(), // X pos of car
                car.getShape().getY() + junction_arms_in[junc_arm].getDirection().getLeft_turn_pos_y(), // Y pos of car
                junction_arms_in[junc_arm].get_lane_size(), max_out // number of lanes in the road, max number of lanes in simulation
        );
    }


    /*
     * use enums u bozo
     */
    private int cornerTranslate(String s){
        switch (s){
            case "tl": return 0; // top left
            case "tr": return 1; // top right
            case "bl": return 2; // bottom left
            case "br": return 3; // bottom right
            default: return -1;
        }
    }

    // get dimensions of specific corner
    public double[] getCornerDims(String corner){
        Rectangle c = corners[cornerTranslate(corner)];
        return new double[]{c.getWidth(), c.getHeight()};
    }

    public double [] getCornerPositions(String corner){
        Rectangle c = corners[cornerTranslate(corner)];
        return new double[]{c.getX(), c.getY()};
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
        junction_arms_in[junction_arm_to_int(junction_arm)].spawn_car_in_lane(lane_number-1, Cardinal.N);
    }

    /*
     * runs each frame. Spawns and moves cars and the like
     */
    public void update(AnchorPane root) {
        this.time += 0.033;
        this.spawnCar(root);
        this.moveCars();
    }

    private void spawnCar(AnchorPane root) {
        for (Road road : this.junction_arms_in) {
            Cardinal outRoad = road.dueSpawn(this.time); // check if a car is due to spawn
            while (outRoad != null) {
                // spawn the car
                Rectangle carRect = road.spawnCar(outRoad);
                // the car may not be able to spawn due to traffic going offscreen. If this is the case, ignore this car :)
                if (carRect != null) { root.getChildren().add(carRect); }

                // continue to check if cars are due to spawn until
                // all of the ones due to spawn have spawned
                outRoad = road.dueSpawn(this.time);
            }
        }
    }

    private void moveCars() {
        for (int i=0; i< 4; i++) {

            // move cars if the light is green and the junction is clear
            if (traffic_system.getLight_status()[i] && !traffic_system.isCar_in_junction()){


                this.junction_arms_in[i].moveCars();

                for (int j=0; j< junction_arms_in[i].getLanes().size(); j++ ){
                    Lane lane = junction_arms_in[i].getLanes().get(j);
                    if (!lane.getCars().isEmpty()){
                        if(lane.car_in_junction(lane.get_first_car())){
                            junction_arms_out[i].getLanes().get(j).add_car(lane.remove_first_car());
                        }
                    }
                }

            }

            // always move cars that are in the junction
            this.junction_arms_out[i].moveCars();
        }
    }
}
