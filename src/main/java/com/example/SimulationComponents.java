package com.example;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
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

    private static final double MAJOR_STRIPE_WIDTH = 3;
    private static final double MINOR_STRIPE_WIDTH = 2;
    private static final double THICK_STRIPE_THICKNESS = 4;

    // maximum number of lanes set
    private final int max_out;

    // animations object for turns
//    private Animations animations;
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
    
    // how many pixels off the screen cars are allowed to spawn
    // this means that queues extend off the screen, rather than ending right at the very edge
    public static final double spawn_offset = 300;
    
    // used to check if car is in junction
    private BoundingBox junction_rectangle;
    public BoundingBox getJunctionRectangle() {
        return this.junction_rectangle;
    }

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
                                boolean is_bus_lane3, boolean is_bus_lane4,
                                float[] vph_1, float[] vph_2, float[] vph_3, float[] vph_4,
                                double crossing_rph, double crossing_dur

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

        double junc_left_x   = Math.min(this.corners[0].getWidth(), this.corners[2].getWidth());
        double junc_top_y    = Math.min(this.corners[0].getHeight(), this.corners[1].getHeight());
        double junc_right_x  = SimulationComponents.sim_w - Math.min(this.corners[1].getWidth(), this.corners[3].getWidth());
        double junc_bottom_y = SimulationComponents.sim_h - Math.min(this.corners[2].getHeight(), this.corners[3].getHeight());
        double ped_width     = SimulationComponents.PEDESTRIAN_CROSSING_WIDTH*this.has_pedestrian;
        junction_rectangle = new BoundingBox(
            junc_left_x - ped_width,
            junc_top_y - ped_width,
            junc_right_x - junc_left_x + ped_width*2,
            junc_bottom_y - junc_top_y + ped_width*2
        );

        // determine the center
        center_x = corners[0].getWidth() + Lane.lane_w *max_out;
        center_y = corners[1].getHeight() + Lane.lane_w *max_out;

        double pedestrian_offset = (PEDESTRIAN_CROSSING_WIDTH+Vehicle.VEHICLE_GAP/2) * has_pedestrian;
        
        // line dividing incoming/outgoing lanes for each junction arm
        this.lane_separation = new Rectangle[] {
            this.stripeMajor(new Rectangle( // top junction arm
                    corners[0].getWidth() + Lane.lane_w *max_out - MAJOR_STRIPE_WIDTH/2, 0, // X and Y
                    MAJOR_STRIPE_WIDTH, Math.min(corners[0].getHeight(),corners[1].getHeight())-pedestrian_offset // Width and Height
            ), false), // right junction arm
            this.stripeMajor(new Rectangle(
                    sim_w - Math.min(corners[1].getWidth(),corners[3].getWidth())+pedestrian_offset, corners[1].getHeight() + Lane.lane_w *max_out-MAJOR_STRIPE_WIDTH/2, // X and Y
                    Math.min(corners[1].getWidth(),corners[3].getWidth())-pedestrian_offset,MAJOR_STRIPE_WIDTH  // Width and Height
            ), true),
            this.stripeMajor(new Rectangle( // bottom junction arm
                    sim_w-(corners[3].getWidth() + Lane.lane_w *max_out)-MAJOR_STRIPE_WIDTH/2, sim_h-Math.min(corners[2].getHeight(),corners[3].getHeight())+pedestrian_offset, // X and Y
                    MAJOR_STRIPE_WIDTH, Math.min(corners[2].getHeight(),corners[3].getHeight())-pedestrian_offset // Width and Height
            ), false),
            this.stripeMajor(new Rectangle( // left junction arm
                    0, sim_h - (corners[2].getHeight() + Lane.lane_w *max_out)-MAJOR_STRIPE_WIDTH/2, // X and Y
                    Math.min(corners[0].getWidth(),corners[2].getWidth())-pedestrian_offset, MAJOR_STRIPE_WIDTH // Width and Height
            ), true)
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
                true,
                cars_to_remove,
                center_x,
                center_y
        );

        // set the spawn position for the TOP junction arm
        junction_arms_in[0].set_start(
                sim_w-getCornerDims("tr")[0]-Car.CAR_WIDTH- (Lane.lane_w-Car.CAR_WIDTH)/2,
                // Math.min(getCornerDims("tl")[1],getCornerDims("tr")[1])-Car.CAR_HEIGHT-Car.CAR_GAP
                -Car.CAR_HEIGHT -Car.CAR_HEIGHT/2 -this.spawn_offset
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
                true,
                cars_to_remove,
                center_x,
                center_y
        );

        // set the spawn position for the RIGHT junction arm
        junction_arms_in[1].set_start(
                // sim_w-Math.min(getCornerDims("tr")[0],getCornerDims("br")[0]),
                sim_w + Car.CAR_HEIGHT/2 + this.spawn_offset,
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
                true,
                cars_to_remove,
                center_x,
                center_y
        );

        // set the spawn position for the BOTTOM junction arm
        junction_arms_in[2].set_start(
                getCornerDims("bl")[0]+ (Lane.lane_w-Car.CAR_WIDTH)/2,
                // sim_h-Math.min(getCornerDims("bl")[1],getCornerDims("br")[1])
                sim_h+Car.CAR_HEIGHT/2 +this.spawn_offset
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
                true,
                cars_to_remove,
                center_x,
                center_y
        );

        // set the spawn position for the LEFT junction arm
        junction_arms_in[3].set_start(
                // getCornerDims("tl")[0]- Car.CAR_HEIGHT,
                -Car.CAR_HEIGHT -this.spawn_offset,
                getCornerDims("tl")[1] + (Lane.lane_w-Car.CAR_WIDTH)/2
        );

        junction_arms_out[0] = new Road(max_out,getCornerDims("tl"),getCornerDims("tr"), false, Direction.TOP, vph_1, left_turn1, right_turn1, is_bus_lane1, max_out,false, cars_to_remove, center_x, center_y);
        junction_arms_out[1] = new Road(max_out,getCornerDims("tr"),getCornerDims("br"), false, Direction.RIGHT, vph_2, left_turn2, right_turn2, is_bus_lane2, max_out,false, cars_to_remove, center_x, center_y);
        junction_arms_out[2] = new Road(max_out,getCornerDims("br"),getCornerDims("bl"), false, Direction.BOTTOM, vph_3, left_turn3, right_turn3, is_bus_lane3, max_out,false, cars_to_remove, center_x, center_y);
        junction_arms_out[3] = new Road(max_out,getCornerDims("bl"),getCornerDims("tl"), false, Direction.LEFT, vph_4, left_turn4, right_turn4, is_bus_lane4, max_out,false, cars_to_remove, center_x, center_y);

        traffic_system = new TrafficLights(new int[] {0,0,0,0},crossing_rph,crossing_dur,junction_arms_in, this.getJunctionRectangle());
        lights = traffic_system.create_rectangles(getLane_separation(), PEDESTRIAN_SCALE_FACTOR, getCenters(), max_out);


        corners[0].setFill(Color.GREEN);
        corners[1].setFill(Color.GREEN);
        corners[2].setFill(Color.GREEN);
        corners[3].setFill(Color.GREEN);


        if (crossings_enabled){
            addCrossings(NUMBER_OF_LINES);
        }
        addLaneSeparators(lanes_arm1,lanes_arm2,lanes_arm3,lanes_arm4);

        for (Rectangle rect : getCorners())         { root.getChildren().add(rect); }
        for (Rectangle rect : createStopMarkings()) { root.getChildren().add(rect); }
        for (Rectangle rect : getLane_separation()) { root.getChildren().add(rect); }
        for (Rectangle rect : getCrossings())       { root.getChildren().add(rect); }
        for (Rectangle rect : getArrows())          { root.getChildren().add(rect); }
        for (Rectangle rect : getLights())          { root.getChildren().add(rect); }

        this.timeline = new Timeline(
                new KeyFrame(Duration.millis(33), e -> animation(root))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    // returns the distance between the junction and the end of the stop markers (plus a little buffer)
    public double getStopMarkingBuffer() {
        double pedestrian_offset = (PEDESTRIAN_CROSSING_WIDTH+Vehicle.VEHICLE_GAP/2)*has_pedestrian;
        return pedestrian_offset + THICK_STRIPE_THICKNESS + Vehicle.VEHICLE_GAP/2;
    }

    public Rectangle[] createStopMarkings() {
        double pedestrian_offset = (PEDESTRIAN_CROSSING_WIDTH + Vehicle.VEHICLE_GAP/2) * has_pedestrian;
        double stripe_length = 12;
        double gap_length = 4;

        return new Rectangle[] {
            // top arm
            this.stripeVertical(
                new Rectangle(
                    /* x */ center_x,
                    /* y */ Math.min(corners[0].getHeight(), corners[1].getHeight()) - pedestrian_offset - THICK_STRIPE_THICKNESS,
                    /* width */  sim_w - center_x - corners[1].getWidth(),
                    /* height */ THICK_STRIPE_THICKNESS
                ), Color.WHITE, stripe_length+gap_length, gap_length
            ),

            // right arm
            this.stripeHorizontal(
                new Rectangle(
                    /* x */ sim_w - Math.min(corners[3].getWidth(), corners[1].getWidth()) + pedestrian_offset,
                    /* y */ center_y,
                    /* width */  THICK_STRIPE_THICKNESS,
                    /* height */ sim_h - center_y - corners[3].getHeight()
                ), Color.WHITE, stripe_length+gap_length, gap_length
            ),

            // bottom arm
            this.stripeVertical(
                new Rectangle(
                    /* x */ corners[2].getWidth(),
                    /* y */ sim_h - Math.min(corners[2].getHeight(), corners[3].getHeight()) + pedestrian_offset,
                    /* width */  sim_w - corners[2].getWidth() - center_x,
                    /* height */ THICK_STRIPE_THICKNESS
                ), Color.WHITE, stripe_length+gap_length, gap_length
            ),

            // left arm
            this.stripeHorizontal(
                new Rectangle(
                    /* x */ Math.min(corners[0].getWidth(), corners[2].getWidth()) - pedestrian_offset - THICK_STRIPE_THICKNESS,
                    /* y */ corners[0].getHeight(),
                    /* width */  THICK_STRIPE_THICKNESS,
                    /* height */ sim_h - corners[0].getHeight() - center_y
                ), Color.WHITE, stripe_length+gap_length, gap_length
            ),
        };
    }

    public void start_simulation(){
        if (!running) {
            timeline.play();
            traffic_system.lights_start();
            for (int i=0; i<4;++i){
                int lane_size = junction_arms_in[i].get_lane_size();
                for (int j=0; j<lane_size;++j){
                    ArrayList<Vehicle> v = junction_arms_in[i].getLanes().get(j).getVehicles();
                    for (int k=0;k<v.size();++k){

                        if (v.get(k) instanceof Car){
                            ((Car) v.get(k)).getAnimations().resume_turns();
                        }
                    }
                }
            }
            for (int i=0; i<4;++i){
                int lane_size = junction_arms_out[i].get_lane_size();
                for (int j=0; j<lane_size;++j){
                    ArrayList<Vehicle> v = junction_arms_out[i].getLanes().get(j).getVehicles();
                    for (int k=0;k<v.size();++k){

                        if (v.get(k) instanceof Car){
                            ((Car) v.get(k)).getAnimations().resume_turns();
                        }
                    }
                }
            }
        }
        running = true;
    }

    public void stop_simulation(){
        if (running) {
            timeline.stop();
            for (int i=0; i<4;++i){
                int lane_size = junction_arms_in[i].get_lane_size();
                for (int j=0; j<lane_size;++j){
                    ArrayList<Vehicle> v = junction_arms_in[i].getLanes().get(j).getVehicles();
                    for (int k=0;k<v.size();++k){

                        if (v.get(k) instanceof Car){
                            ((Car) v.get(k)).getAnimations().pause_turns();
                        }
                    }
                }
            }
            for (int i=0; i<4;++i){
                int lane_size = junction_arms_out[i].get_lane_size();
                for (int j=0; j<lane_size;++j){
                    ArrayList<Vehicle> v = junction_arms_out[i].getLanes().get(j).getVehicles();
                    for (int k=0;k<v.size();++k){

                        if (v.get(k) instanceof Car){
                            ((Car) v.get(k)).getAnimations().pause_turns();
                        }
                    }
                }
            }
            traffic_system.lights_stop();
        }
        running = false;
    }

    private void animation(AnchorPane root){
        this.update(root);
    }


    public void addLaneSeparators(int lanes_arm1,int lanes_arm2,int lanes_arm3,int lanes_arm4){
        double pedestrian_offset = (PEDESTRIAN_CROSSING_WIDTH+Vehicle.VEHICLE_GAP/2) * has_pedestrian;

        Rectangle[] rectangles;
        for (int i=1;i<max_out;++i) {
            rectangles = new Rectangle[]{
                    this.stripeMinor(new Rectangle( // top junction arm
                            corners[0].getWidth() + Lane.lane_w * i, 0, // X and Y
                            MAJOR_STRIPE_WIDTH, Math.min(corners[0].getHeight(), corners[1].getHeight()) - pedestrian_offset // Width and Height
                    ), false),
                    this.stripeMinor(new Rectangle( // right junction arm
                            sim_w - Math.min(corners[1].getWidth(), corners[3].getWidth()) + pedestrian_offset, corners[1].getHeight() + Lane.lane_w * i, // X and Y
                            Math.min(corners[1].getWidth(), corners[3].getWidth()) - pedestrian_offset, MINOR_STRIPE_WIDTH  // Width and Height
                    ), true),
                    this.stripeMinor(new Rectangle( // bottom junction arm
                            sim_w - (corners[3].getWidth() + Lane.lane_w * i), sim_h - Math.min(corners[2].getHeight(), // X and Y
                            corners[3].getHeight()) + pedestrian_offset,
                            MINOR_STRIPE_WIDTH, Math.min(corners[2].getHeight(), corners[3].getHeight()) - pedestrian_offset // Width and Height
                    ), false),
                    this.stripeMinor(new Rectangle( // left junction arm
                            0, sim_h - (corners[2].getHeight() + Lane.lane_w * i), // X and Y
                            Math.min(corners[0].getWidth(), corners[2].getWidth()) - pedestrian_offset, MINOR_STRIPE_WIDTH // Width and Height
                    ), true)
            };
            
            root.getChildren().addAll(rectangles);
        }
        for (int i =0;i<lanes_arm1;++i) {
            Rectangle rect = new Rectangle( // top junction arm
                    sim_w - corners[1].getWidth() - Lane.lane_w * i, 0, // X and Y
                    MINOR_STRIPE_WIDTH, Math.min(corners[0].getHeight(), corners[1].getHeight()) - pedestrian_offset // Width and Height
            );

            rect = this.stripeMinor(rect, false);
            root.getChildren().addAll(rect);
        }
        for (int i =0;i<lanes_arm2;++i) {
            Rectangle rect = new Rectangle( // right junction arm
                    sim_w - Math.min(corners[1].getWidth(), corners[3].getWidth()) + pedestrian_offset, sim_h - corners[3].getHeight() - Lane.lane_w * i, // X and Y
                    Math.min(corners[1].getWidth(), corners[3].getWidth()) - pedestrian_offset, MINOR_STRIPE_WIDTH  // Width and Height
            );
            
            rect = this.stripeMinor(rect, true);
            root.getChildren().addAll(rect);
        }
        for (int i =1;i<lanes_arm3;++i) {
            Rectangle rect = new Rectangle( // bottom junction arm
                corners[2].getWidth() + Lane.lane_w * i, // X
                sim_h - Math.min(corners[2].getHeight(), corners[3].getHeight()) + pedestrian_offset, // Y
                MINOR_STRIPE_WIDTH, Math.min(corners[2].getHeight(), corners[3].getHeight()) - pedestrian_offset // Width and Height
            );
            
            rect = this.stripeMinor(rect, false);
            root.getChildren().addAll(rect);
        }
        for (int i =1;i<lanes_arm4;++i) {
            Rectangle rect = new Rectangle( // left junction arm
                    0, (corners[0].getHeight() + Lane.lane_w * i), // X and Y
                    Math.min(corners[0].getWidth(), corners[2].getWidth()) - pedestrian_offset, MINOR_STRIPE_WIDTH // Width and Height
            );

            rect = this.stripeMinor(rect, true);
            root.getChildren().addAll(rect);
        }
    }

    /*
     * Create minor stripes on a road
     * (Minor stripes separate lanes going in the same direction)
     * @param r a rectangle at the correct position
     * @param vertical whether the stripes should be horizontal or vertical
     * @return a version of r with the same dimensions that is now striped
     */
    public Rectangle stripeMinor(Rectangle r, boolean vertical) {
        if (vertical) { return this.stripeVertical(r, Color.WHITESMOKE, 30, 10); }
        else { return this.stripeHorizontal(r, Color.WHITESMOKE, 30, 10); }
    }

    /*
     * Same as stripeMinor but used between lanes going in opposite directions
     */
    public Rectangle stripeMajor(Rectangle r, boolean vertical) {
        if (vertical) { return this.stripeVertical(r, Color.WHITE, 40, 10); }
        else { return this.stripeHorizontal(r, Color.WHITE, 40, 10); }
    }

    /*
     * Puts grey stripes along a rectangle
     * @param stripe_separation how far the stripes are from each other
     * @param stripe_width how wide the stripes are
     * @return a striped rectangle
     */
    public Rectangle stripeVertical(Rectangle r, Color stripe_color, double stripe_separation, double stripe_width) {
        r.setFill(stripe_color);

        Canvas canvas = new Canvas(r.getWidth(), r.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.rgb(148,148,148));
        for (int pos = 0; pos < r.getWidth(); pos+=stripe_separation) {
            gc.fillRect(pos, 0, stripe_width, r.getHeight());
        }

        Rectangle stripedRectangle = new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        stripedRectangle.setFill(new ImagePattern(canvas.snapshot(null, null)));

        return stripedRectangle;
    }

    public Rectangle stripeHorizontal(Rectangle r, Color stripe_color, double stripe_separation, double stripe_height) {
        r.setFill(stripe_color);
        
        Canvas canvas = new Canvas(r.getWidth(), r.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.rgb(148,148,148));
        for (int pos = 0; pos < r.getHeight(); pos+=stripe_separation) {
            gc.fillRect(0, pos, r.getWidth(), stripe_height);
        }

        Rectangle stripedRectangle = new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        stripedRectangle.setFill(new ImagePattern(canvas.snapshot(null, null)));

        return stripedRectangle;
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
            boolean can_enter_junction = !this.junction_arms_in[((i-1)%4+4)%4].existsCarInJunction(this.junction_rectangle);
            this.junction_arms_in[i].moveCars(traffic_system.getLight_status()[i], can_enter_junction);

            /*
            // move cars if the light is green and the junction is clear
            if (traffic_system.getLight_status()[i] && !traffic_system.isCar_in_junction()){

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
                                    c.getAnimations().turn_left(c, c.getDirection(), c.getShape().getX()+c.getDirection().getRight_turn_pos_x(), c.getShape().getY()+c.getDirection().getRight_turn_pos_y(), num_of_lanes, max_out);
                                    junction_arms_out[lane.getDirection().getRoad_after_left()].getLanes().get(j).add_car(to_move);
                                }else if ( j== num_of_lanes-1 && Road.isRightOf(lane.getDir(),c.getDir()) && lane.is_right()){
                                    c.getAnimations().turn_right(c, c.getDirection(), c.getShape().getX()+c.getDirection().getRight_turn_pos_x(), c.getShape().getY()+c.getDirection().getRight_turn_pos_y());
                                    junction_arms_out[lane.getDirection().getRoad_after_right()].getLanes().get(j).add_car(to_move);
                                }else{
                                    junction_arms_out[i].getLanes().get(j).add_car(to_move);
                                }
                            }
                        }
                    }
                }
            }
            */

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
            all_arrows.addAll(junction_arms_in[i].getArrows(this.getStopMarkingBuffer()));
        }
        return all_arrows;
    }


}
