package com.example;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class SimulationComponents {


    // center of the simulation
    private final double center_x;
    private final double center_y;
    private AnchorPane root;

    // the corners of the simulation
    private Rectangle[] corners;
    private Rectangle[] lights;

    private ArrayList<Rectangle> crossings;
    private Rectangle[] lane_separation;

    // cars which are about to enter the junction
    private Road junction_arms_in[];

    // cars which have exited the junction
    private Road junction_arms_out[];


    public static final double sim_w = 600;
    public static final double sim_h = 600;

    private static final double SCALE_FACTOR = 2;
    private static final double PEDESTRIAN_SCALE_FACTOR = 15;
    private static final int NUMBER_OF_LINES = 33;
    private static double PEDESTRIAN_CROSSING_WIDTH;

    // maximum number of lanes set
    private final int max_out;

    // animations object for turns
    private Animations animations;
    private float time = 0;


    // cars to be removed which are outside the simulation area
    private ArrayList<Rectangle> cars_to_remove = new ArrayList<>();

    public static double GET_PEDESTRIAN_CROSSING_WIDTH(){
        return PEDESTRIAN_CROSSING_WIDTH;
    }
    // traffic light logic instance
    private TrafficLights traffic_system;


    /////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// CONSTRUCTOR //////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    // junction arm: top - right - bottom - left
    public SimulationComponents(int lanes_arm1,
                                int lanes_arm2,
                                int lanes_arm3,
                                int lanes_arm4,
                                boolean crossings_enabled,
                                boolean left_turn1,boolean right_turn1,
                                boolean left_turn2,boolean right_turn2,
                                boolean left_turn3,boolean right_turn3,
                                boolean left_turn4,boolean right_turn4

    ){

        root = new AnchorPane();
        // number of lanes exiting junction for each arm
        max_out = Math.max(Math.max(Math.max(lanes_arm1,lanes_arm2),lanes_arm3),lanes_arm4);

        // cars not yet in the junction
        junction_arms_in = new Road[4];

        // cars in the junction or that have left the junction
        junction_arms_out = new Road[4];

        // vph for each junction arm starting from top junction clockwise
        float[] vph_1 = new float[] {0, 360000, 1800, 900};
        float[] vph_2 = new float[] {0, 0, 3000, 50000000};
        float[] vph_3 = new float[] {0, 10000, 0, 1800};
        float[] vph_4 = new float[] {90000, 0, 0, 0};


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

        animations = new Animations(center_x, center_y);
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

        // create a road instance for TOP junction arm, this road is specifically for cars entering the junction
        junction_arms_in[0] = new Road(
                lanes_arm1, // number of lanes
                1, // priority
                getCornerDims("tl"), // the two corners that are adjacent to the Road
                getCornerDims("tr"), // the two corners that are adjacent to the Road
                crossings_enabled, // pedestrian crossings
                Direction.TOP, // the junction arm direction
                vph_1, // VPH for each outbound direction,
                left_turn1, // left turn
                right_turn1, // right turn
                max_out, // maximum number of lanes in one road
                animations,
                true,
                cars_to_remove
        );

        // set the spawn position for the TOP junction arm
        junction_arms_in[0].set_start(
                sim_w-getCornerDims("tr")[0]-Car.CAR_WIDTH- (Lane.lane_w-Car.CAR_WIDTH)/2,
                // Math.min(getCornerDims("tl")[1],getCornerDims("tr")[1])-Car.CAR_HEIGHT-Car.CAR_GAP
                -Car.CAR_HEIGHT -Car.CAR_HEIGHT/2
        );

        // create a road instance for RIGHT junction arm, this road is specifically for cars entering the junction
        junction_arms_in[1] = new Road(
                lanes_arm2, // number of lanes,
                1, // priority
                getCornerDims("tr"), // the two corners that are adjacent to the Road
                getCornerDims("br"), // the two corners that are adjacent to the Road
                crossings_enabled,  // pedestrian crossings
                Direction.RIGHT, // the junction arm direction
                vph_2, // VPH for each outbound direction
                left_turn2, // left turn
                right_turn2, // right turn
                max_out, // maximum number of lanes in one road
                animations,
                true,
                cars_to_remove
        );

        // set the spawn position for the RIGHT junction arm
        junction_arms_in[1].set_start(
                // sim_w-Math.min(getCornerDims("tr")[0],getCornerDims("br")[0]),
                sim_w + Car.CAR_HEIGHT/2,
                sim_h-getCornerDims("br")[1]- Car.CAR_WIDTH
        );

        // create a road instance for BOTTOM junction arm, this road is specifically for cars entering the junction
        junction_arms_in[2] = new Road(
                lanes_arm3, // number of lanes,
                1, // priority
                getCornerDims("br"), // the two corners that are adjacent to the Road
                getCornerDims("bl"), // the two corners that are adjacent to the Road
                crossings_enabled,  // pedestrian crossings
                Direction.BOTTOM, // the junction arm direction
                vph_3, // VPH for each outbound direction
                left_turn3, // left turn
                right_turn3, // right turn
                max_out, // maximum number of lanes in one road
                animations,
                true,
                cars_to_remove
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
                1, // priority
                getCornerDims("bl"), // the two corners that are adjacent to the Road
                getCornerDims("tl"), // the two corners that are adjacent to the Road
                crossings_enabled,  // pedestrian crossings
                Direction.LEFT, // the junction arm direction
                vph_4, // VPH for each outbound direction
                left_turn4, // left turn
                right_turn4, // right turn
                max_out, // maximum number of lanes in one road
                animations,
                true,
                cars_to_remove
        );

        // set the spawn position for the LEFT junction arm
        junction_arms_in[3].set_start(
                // getCornerDims("tl")[0]- Car.CAR_HEIGHT,
                -Car.CAR_HEIGHT,
                getCornerDims("tl")[1] + (Lane.lane_w-Car.CAR_WIDTH)/2
        );

        junction_arms_out[0] = new Road(lanes_arm1,1,getCornerDims("tl"),getCornerDims("tr"), false, Direction.TOP, vph_1, left_turn1, right_turn1,max_out,animations,false, cars_to_remove);
        junction_arms_out[1] = new Road(lanes_arm2,1,getCornerDims("tr"),getCornerDims("br"), false, Direction.RIGHT, vph_2, left_turn2, right_turn2,max_out,animations,false, cars_to_remove);
        junction_arms_out[2] = new Road(lanes_arm3,1,getCornerDims("br"),getCornerDims("bl"), false, Direction.BOTTOM, vph_3, left_turn3, right_turn3,max_out,animations,false, cars_to_remove);
        junction_arms_out[3] = new Road(lanes_arm4,1,getCornerDims("bl"),getCornerDims("tl"), false, Direction.LEFT, vph_4, left_turn4, right_turn4,max_out,animations,false, cars_to_remove);

        traffic_system = new TrafficLights(10,10,10,10,60,2,junction_arms_out);
        lights = traffic_system.create_rectangles(getLane_separation(), PEDESTRIAN_SCALE_FACTOR, getCenters());
        traffic_system.run_lights();

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
        PEDESTRIAN_CROSSING_WIDTH = verticalcrossings[0].getWidth();
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
                // all the ones due to spawn have spawned
                outRoad = road.dueSpawn(this.time);
            }
        }
    }


    private void moveCars() {
        for (int i=0; i< 4; i++) {

            // move cars if the light is green and the junction is clear
            if (traffic_system.getLight_status()[i] && !traffic_system.isCar_in_junction()){
                this.junction_arms_in[i].moveCars(); // move the cars

                int num_of_lanes =junction_arms_in[i].getLanes().size();
                for (int j=0; j< num_of_lanes; j++ ){
                    Lane lane = junction_arms_in[i].getLanes().get(j);
                    if (!lane.getCars().isEmpty()){
                        // check if the car has entered the junction
                        if(lane.car_in_junction(lane.get_first_car())){
                            // move the car from the junction_arms_in array to the correct junction_arms_out array depending on the direction the car will go to
                            if (j==0 && lane.is_left()){
                                junction_arms_out[lane.getDirection().getRoad_after_left()].getLanes().get(j).add_car(lane.remove_first_car());
                            }else if (j==num_of_lanes-1 && lane.is_right()){
                                junction_arms_out[lane.getDirection().getRoad_after_right()].getLanes().get(j).add_car(lane.remove_first_car());
                            }else{
                                junction_arms_out[i].getLanes().get(j).add_car(lane.remove_first_car());
                            }

                        }
                    }
                }

            }

            // always move cars that are in the junction
            this.junction_arms_out[i].moveCars();

            // remove cars which are outside the simulation display area
            for (Rectangle c: cars_to_remove){
                root.getChildren().remove(c);
            }
        }
    }



    /////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// GETTERS + SETTERS ////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public AnchorPane getRoot() {
        return root;
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

    public Rectangle[] getCorners() {
        return corners;
    }
    public Rectangle[] getLights(){
        return lights;
    }
    public double[] getCenters(){
        return new double[]{center_x, center_y};
    }

}
