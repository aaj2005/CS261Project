package com.example;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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

    private int has_pedestrian;

    private Timeline timeline;
    private Boolean running = false;

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
                                boolean left_turn4,boolean right_turn4,
                                boolean is_bus_lane1, boolean is_bus_lane2,
                                boolean is_bus_lane3, boolean is_bus_lane4

                                ){

        root = new AnchorPane();
        root.setBackground(new Background(new BackgroundFill(Color.rgb(148,148,148), CornerRadii.EMPTY, Insets.EMPTY)));

        // number of lanes exiting junction for each arm
        max_out = Math.max(Math.max(Math.max(lanes_arm1,lanes_arm2),lanes_arm3),lanes_arm4);
        this.has_pedestrian = crossings_enabled ? 1 : 0;
        // cars not yet in the junction
        junction_arms_in = new Road[4];

        // cars in the junction or that have left the junction
        junction_arms_out = new Road[4];
        // NORTH - EAST - SOUTH - WEST
        float[] vph_1 = new float[] {0, 0, 3600, 3600};
        float[] vph_2 = new float[] {3600, 0, 0, 0};
        float[] vph_3 = new float[] {0, 36000, 3600, 3600};
        float[] vph_4 = new float[] {0, 0, 0, 3600};



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
                getCornerDims("tl"), // the two corners that are adjacent to the Road
                getCornerDims("tr"), // the two corners that are adjacent to the Road
                crossings_enabled, // pedestrian crossings
                Direction.TOP, // the junction arm direction
                vph_1, // VPH for each outbound direction,
                left_turn1, // left turn
                right_turn1, // right turn
                is_bus_lane1,
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
                getCornerDims("tr"), // the two corners that are adjacent to the Road
                getCornerDims("br"), // the two corners that are adjacent to the Road
                crossings_enabled,  // pedestrian crossings
                Direction.RIGHT, // the junction arm direction
                vph_2, // VPH for each outbound direction
                left_turn2, // left turn
                right_turn2, // right turn
                is_bus_lane2,
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
                getCornerDims("br"), // the two corners that are adjacent to the Road
                getCornerDims("bl"), // the two corners that are adjacent to the Road
                crossings_enabled,  // pedestrian crossings
                Direction.BOTTOM, // the junction arm direction
                vph_3, // VPH for each outbound direction
                left_turn3, // left turn
                right_turn3, // right turn
                is_bus_lane3,
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
                getCornerDims("bl"), // the two corners that are adjacent to the Road
                getCornerDims("tl"), // the two corners that are adjacent to the Road
                crossings_enabled,  // pedestrian crossings
                Direction.LEFT, // the junction arm direction
                vph_4, // VPH for each outbound direction
                left_turn4, // left turn
                right_turn4, // right turn
                is_bus_lane4,
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

        junction_arms_out[0] = new Road(max_out,getCornerDims("tl"),getCornerDims("tr"), false, Direction.TOP, vph_1, left_turn1, right_turn1, is_bus_lane1, max_out,animations,false, cars_to_remove);
        junction_arms_out[1] = new Road(max_out,getCornerDims("tr"),getCornerDims("br"), false, Direction.RIGHT, vph_2, left_turn2, right_turn2, is_bus_lane2, max_out,animations,false, cars_to_remove);
        junction_arms_out[2] = new Road(max_out,getCornerDims("br"),getCornerDims("bl"), false, Direction.BOTTOM, vph_3, left_turn3, right_turn3, is_bus_lane3, max_out,animations,false, cars_to_remove);
        junction_arms_out[3] = new Road(max_out,getCornerDims("bl"),getCornerDims("tl"), false, Direction.LEFT, vph_4, left_turn4, right_turn4, is_bus_lane4, max_out,animations,false, cars_to_remove);

        traffic_system = new TrafficLights(new int[] {0,0,0,0},60,4,junction_arms_out);
        lights = traffic_system.create_rectangles(getLane_separation(), PEDESTRIAN_SCALE_FACTOR, getCenters());


        corners[0].setFill(Color.GREEN);
        corners[1].setFill(Color.GREEN);
        corners[2].setFill(Color.GREEN);
        corners[3].setFill(Color.GREEN);


        if (crossings_enabled){
            addCrossings(NUMBER_OF_LINES);
        }
        addLaneSeparators(lanes_arm1,lanes_arm2,lanes_arm3,lanes_arm4);

        for (Rectangle rect : getCorners()){
            root.getChildren().add(rect);
        }
        for (Rectangle rect : getLane_separation()){
            root.getChildren().add(rect);
        }
        for (Rectangle rect : getCrossings()){
            root.getChildren().add(rect);
        }
        for (Rectangle rect: getArrows()){
            root.getChildren().add(rect);
        }
        for (Rectangle rect: getLights()){
            root.getChildren().add(rect);
        }

        this.timeline = new Timeline(
                new KeyFrame(Duration.millis(33), e -> animation(root))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);

    }

    public void start_simulation(){
        if (running) {
            timeline.play();
            traffic_system.lights_start();
            animations.resume_turns();
        }
        running = true;
    }

    public void stop_simulation(){
        if (!running) {
            timeline.stop();
            animations.pause_turns();
            traffic_system.lights_stop();
        }
        running = false;
    }


    private void animation(AnchorPane root){
        this.update(root);
    }



    public void addLaneSeparators(int lanes_arm1,int lanes_arm2,int lanes_arm3,int lanes_arm4){
        Rectangle[] rectangles;
        for (int i=1;i<max_out;++i) {
            rectangles = new Rectangle[]{
                    new Rectangle( // top junction arm
                            corners[0].getWidth() + Lane.lane_w * i, 0, // X and Y
                            1, Math.min(corners[0].getHeight(), corners[1].getHeight()) - PEDESTRIAN_CROSSING_WIDTH * has_pedestrian // Width and Height
                    ),
                    new Rectangle( // right junction arm
                            sim_w - Math.min(corners[1].getWidth(), corners[3].getWidth()) + PEDESTRIAN_CROSSING_WIDTH * has_pedestrian, corners[1].getHeight() + Lane.lane_w * i, // X and Y
                            Math.min(corners[1].getWidth(), corners[3].getWidth()) - PEDESTRIAN_CROSSING_WIDTH * has_pedestrian, 1  // Width and Height
                    ),
                    new Rectangle( // bottom junction arm
                            sim_w - (corners[3].getWidth() + Lane.lane_w * i), sim_h - Math.min(corners[2].getHeight(), // X and Y
                            corners[3].getHeight()) + PEDESTRIAN_CROSSING_WIDTH * has_pedestrian,
                            1, Math.min(corners[2].getHeight(), corners[3].getHeight()) - PEDESTRIAN_CROSSING_WIDTH * has_pedestrian // Width and Height
                    ),
                    new Rectangle( // left junction arm
                            0, sim_h - (corners[2].getHeight() + Lane.lane_w * i), // X and Y
                            Math.min(corners[0].getWidth(), corners[2].getWidth()) - PEDESTRIAN_CROSSING_WIDTH * has_pedestrian, 1 // Width and Height
                    ),

            };
            for (Rectangle r : rectangles) {
                r.setFill(Color.WHITE);
            }
            root.getChildren().addAll(rectangles);
        }
        for (int i =0;i<lanes_arm1;++i) {
            Rectangle rect = new Rectangle( // top junction arm
                    sim_w - corners[1].getWidth() - Lane.lane_w * i, 0, // X and Y
                    1, Math.min(corners[0].getHeight(), corners[1].getHeight()) - PEDESTRIAN_CROSSING_WIDTH * has_pedestrian // Width and Height
            );
//            if ()
            rect.setFill(Color.WHITE);
            root.getChildren().addAll(rect);
        }
        for (int i =0;i<lanes_arm2;++i) {
            Rectangle rect = new Rectangle( // right junction arm
                    sim_w - Math.min(corners[1].getWidth(), corners[3].getWidth()) + PEDESTRIAN_CROSSING_WIDTH * has_pedestrian, sim_h - corners[2].getHeight() - Lane.lane_w * i, // X and Y
                    Math.min(corners[1].getWidth(), corners[3].getWidth()) - PEDESTRIAN_CROSSING_WIDTH * has_pedestrian, 1  // Width and Height
            );
            rect.setFill(Color.WHITE);
            root.getChildren().addAll(rect);
        }
        for (int i =0;i<lanes_arm3;++i) {
            Rectangle rect = new Rectangle( // bottom junction arm
                    (corners[3].getWidth() + Lane.lane_w * i), sim_h - Math.min(corners[2].getHeight(), // X and Y
                    corners[3].getHeight()) + PEDESTRIAN_CROSSING_WIDTH * has_pedestrian,
                    1, Math.min(corners[2].getHeight(), corners[3].getHeight()) - PEDESTRIAN_CROSSING_WIDTH * has_pedestrian // Width and Height
            );
            rect.setFill(Color.WHITE);
            root.getChildren().addAll(rect);
        }
        for (int i =0;i<lanes_arm4;++i) {
            Rectangle rect = new Rectangle( // left junction arm
                    0, (corners[0].getHeight() + Lane.lane_w * i), // X and Y
                    Math.min(corners[0].getWidth(), corners[2].getWidth()) - PEDESTRIAN_CROSSING_WIDTH * has_pedestrian, 1 // Width and Height
            );
            rect.setFill(Color.WHITE);
            root.getChildren().addAll(rect);
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
                        Vehicle v = lane.get_first_car();
                        // if the vehicle is currently turning, move it to the junction_arms_out array
                        if (v instanceof Car && v.is_turning()){
                            Car c = (Car) v;
                            if (j==0 && Road.isLeftOf(lane.getDir(),c.getDir()) && lane.is_left()){
                                junction_arms_out[lane.getDirection().getRoad_after_left()].getLanes().get(j).add_car(lane.remove_first_car());
                            }else if ( j== num_of_lanes-1 && Road.isRightOf(lane.getDir(),c.getDir())&& lane.is_right()){
                                junction_arms_out[lane.getDirection().getRoad_after_right()].getLanes().get(j).add_car(lane.remove_first_car());
                            }
                        }else if(lane.car_in_junction(lane.get_first_car())){
                            // move the car from the junction_arms_in array to the correct junction_arms_out array depending on the direction the car will go to

                            Vehicle to_move = lane.remove_first_car();
                            // buses always go straight
                            if (to_move instanceof Bus){
                                junction_arms_out[i].getLanes().get(j).add_car(to_move);
                            }else if (to_move instanceof Car){
                                Car c = (Car) to_move;

                                if (j==0 && Road.isLeftOf(lane.getDir(),c.getDir()) && lane.is_left()){
                                    animations.turn_left(c, c.getDirection(), c.getShape().getX()+c.getDirection().getRight_turn_pos_x(), c.getShape().getY()+c.getDirection().getRight_turn_pos_y(), num_of_lanes, max_out);
                                    junction_arms_out[lane.getDirection().getRoad_after_left()].getLanes().get(j).add_car(to_move);
                                }else if ( j== num_of_lanes-1 && Road.isRightOf(lane.getDir(),c.getDir()) && lane.is_right()){
                                    animations.turn_right(c, c.getDirection(), c.getShape().getX()+c.getDirection().getRight_turn_pos_x(), c.getShape().getY()+c.getDirection().getRight_turn_pos_y());
                                    junction_arms_out[lane.getDirection().getRoad_after_right()].getLanes().get(j).add_car(to_move);
                                }else{
                                    junction_arms_out[i].getLanes().get(j).add_car(to_move);
                                }
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

    public ArrayList<Rectangle> getArrows(){
        ArrayList<Rectangle> all_arrows = new ArrayList<Rectangle>();
        for (int i=0; i<4; i++){
            all_arrows.addAll(junction_arms_in[i].getArrows(GET_PEDESTRIAN_CROSSING_WIDTH()));
        }
        return all_arrows;
    }


}
