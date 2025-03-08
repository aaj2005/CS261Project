package com.example;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Road extends JunctionElement{

    // each lane object in the particular road
    private ArrayList<Lane> lanes;

    public static int default_light_duration = 30;

    // direction of the road (TOP, RIGHT, BOTTOM, LEFT)
    private Direction direction;
    private boolean has_left_turn;
    private boolean has_right_turn;

    // the cardinal direction of the road
    private Cardinal cardinal_pos;

    // how frequently cars spawn for each outbound direction
    // spawnRate[Direction.Up] = how frequently a car spawns on this road and is headed north
    // if cars never spawn, spawnRate[x] will be -1
    private float[] spawnFreq = new float[4];

    // when was the last time a car spawned?
    // a car will be spawned if some time variable > lastSpawn[x] + spawnRate[x]
    private float[] lastSpawn = new float[4];
    
    // number of cars spawned in each lane
    // used to determine in which lane to spawn cars
    // prevents one particular lane from having too many cars on it
    private int[] numSpawned;
    private ArrayList<Rectangle> cars_to_remove;


    private ArrayList<Rectangle> lane_arrows;
    public static final ImagePattern left_arrow = new ImagePattern(
    new Image(TrafficLights.class.getResource("/leftarrow.png").toExternalForm()));
    public static final ImagePattern right_arrow = new ImagePattern(
    new Image(TrafficLights.class.getResource("/rightarrow.png").toExternalForm()));
    public static final ImagePattern forward_arrow = new ImagePattern(
    new Image(TrafficLights.class.getResource("/forwardarrow.png").toExternalForm()));
    public static final ImagePattern bus_lane_writing = new ImagePattern(
    new Image(TrafficLights.class.getResource("/buslane.png").toExternalForm()));
    private int lane_count;
    private double[] corner1;
    private double[] corner2;
    private boolean has_pedestrian;
    private boolean has_bus_lane;

    /////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// CONSTRUCTOR //////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    /*
     * @param vph The vph outbound in every direction
     */
    public Road(
            int lane_count,
            double[] corner1, // two adjacent corners to the road
            double[] corner2, // two adjacent corners to the road
            boolean has_pedestrian,
            Direction direction,
            float[] vph,
            int max_lane_out,
            Animations animations,
            boolean road_going_into_junction,
            ArrayList<Rectangle> cars_to_remove
    ){
        light_status = false;

        // lane object
        lanes = new ArrayList<>(5);
        this.cars_to_remove = cars_to_remove;
        // instantiate lane object for each lane
        this.setCardinalPos(direction);
        for (int i=0; i<lane_count;i++){
            lanes.add(new Lane(max_lane_out,has_pedestrian,direction,i,lane_count,corner1,corner2,animations,road_going_into_junction,cardinal_pos));
        }
        this.direction = direction;

        // calculates how long it takes for a car to spawn
        for (int i=0; i<4; i++) {
            this.spawnFreq[i] = (vph[i] == 0) ? -1 : 3600/vph[i];
        }

        /*
        * BUGFIX:
        * if the vphs for each outbound lane are identical, they will try to spawn at the exact same time always
        * this means that every car but one will fail to spawn every time on a one-lane road
        * offsetting the time by changing the initial values of lastspawn fixes this
        */
        for (int i=0; i<4; i++) {
            this.lastSpawn[i] = this.spawnFreq[i]/(i+1);
        }


        this.numSpawned = new int[lane_count];

        this.lane_count=lane_count;
        this.corner1=corner1;
        this.corner2=corner2;
        this.has_pedestrian = has_pedestrian;
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// CONSTRUCTOR //////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public Road(
            int lane_count,
            double[] corner1,
            double[] corner2,
            boolean has_pedestrian,
            Direction direction,
            float[] vph,
            boolean has_left_turn,
            boolean has_right_turn,
            boolean has_bus_lane,
            int max_lane_out,
            Animations animations,
            boolean road_going_into_junction,
            ArrayList<Rectangle> cars_to_remove
    ){
        this(lane_count, corner1, corner2, has_pedestrian, direction, vph, max_lane_out,animations,road_going_into_junction,cars_to_remove);
        this.has_left_turn = has_left_turn;
        this.has_right_turn = has_right_turn;
        this.has_bus_lane = has_bus_lane;
        if (has_left_turn){
            lanes.get(0).set_left_turn();
        }
        if (has_right_turn){
            lanes.get(lanes.size()-1).set_right_turn();
        }
        if (has_bus_lane){
            lanes.get(0).set_bus_lane();
        }
    }






    public void setCardinalPos(Direction d) {
        switch (d) {
            case TOP:
                this.cardinal_pos = Cardinal.N;
                break;
            case LEFT:
                this.cardinal_pos = Cardinal.E;
                break;
            case BOTTOM:
                this.cardinal_pos = Cardinal.S;
                break;
            case RIGHT:
                this.cardinal_pos = Cardinal.W;
                break;
        };
    }

    // set the start spawn position for the cars
    public void set_start(double start_x, double start_y){
        for (Lane lane : lanes){
            lane.setSpawn_position_x(start_x);
            lane.setSpawn_position_y(start_y);
        }
    }

    public Rectangle spawn_car_in_lane(int i, Cardinal dir){
        if (i < lanes.size()){
            this.lastSpawn[dir.ordinal()] += this.spawnFreq[dir.ordinal()]; // update the last time a car spawned because a car is spawning like right now
            this.numSpawned[i]++;                                           // increment the amount of cars that have spawned on that lane
            return lanes.get(i).spawn_car(dir);                             // actually spawn a car and return its shape
        }else{
            throw new IndexOutOfBoundsException("Lane "+ i+ " does not exist!");
        }

    }

    /*
     * Checks if a car is due to spawn on a lane.
     * Recommended to run repeatedly until returns null in order to get every car which is due to spawn
     * @param the time right now
     * @return {@code null} if no cars are due to spawn, a cardinal direction if a car headed in that direction is about to spawn
     */

    public Cardinal dueSpawn(float time) {
        for (Cardinal d : Cardinal.values()) {
            int i = d.ordinal();
            if (this.spawnFreq[i] != -1 && this.lastSpawn[i] + this.spawnFreq[i] < time) {
                this.lastSpawn[i] += this.spawnFreq[i];
                return d;
            }
        }

        return null;
    }
    /*
     * Spawns a car in a lane
     * @param dir the direction in which the car will be headed
     */

    public Rectangle spawnCar(Cardinal dir) {
        // checks if the car will be turning left
        if (isLeftOf(this.cardinal_pos, dir)) {
            // if there exists a left-turn lane, that's where the car will spawn
            // assumes that the left-turn lane is 0
            if (this.has_left_turn) {
                return this.spawn_car_in_lane(0, dir);
            }

            // iterate over lanes from left to right
            // spawn a car in the lane which has the least amount of cars in it
            int lane = 0;
            while (
                    lane+1<this.lanes.size() // check that you haven't reached the last lane
                            && this.numSpawned[lane] > this.numSpawned[lane+1] // check that there are more cars here than in the next lane
            ) { lane++; }

            return this.spawn_car_in_lane(lane, dir);
        }

        // same drill but now right to left
        else if (isRightOf(this.cardinal_pos, dir)) {
            if (this.has_right_turn) {
                return this.spawn_car_in_lane(this.lanes.size()-1, dir);
            }

            int lane = this.lanes.size()-1;
            while (lane > 0 && this.numSpawned[lane] > this.numSpawned[lane-1]) {
                lane--;
            }

            return this.spawn_car_in_lane(lane, dir);
        }

        // now for the cars going straight ahead
        else {
            Integer[] lane_nums = straightAheadLaneOrderer();

            if (lane_nums.length == 1) {
                return this.spawn_car_in_lane(lane_nums[0], dir);
            } else {
                int best=lane_nums[0];
                int i=0;
                while (i+1 < lane_nums.length) {
                    int left_lane_index  = lane_nums[i+1];
                    boolean left_smaller = this.numSpawned[left_lane_index] < this.numSpawned[lane_nums[i]];
                    int right_lane_index = -1;
                    boolean right_smaller = false;
                    if (i+2 < lane_nums.length) {
                        right_lane_index = lane_nums[i+2];
                        right_smaller = this.numSpawned[right_lane_index] < this.numSpawned[lane_nums[i]];
                    }

                    if (left_smaller && right_smaller) {
                        best = this.numSpawned[left_lane_index] < this.numSpawned[right_lane_index] ? left_lane_index : right_lane_index;
                    } else if (left_smaller) {
                        best = left_lane_index;
                    } else if (right_smaller) {
                        best = right_lane_index;
                    }

                    i += 2;
                }
                return this.spawn_car_in_lane(best, dir);
            }
        }
    }
    /*
     * Checks if b is left of a
     * For a car coming in from the north, east is to the left, so isLeftOf(N, E) === true
     */

    public static boolean isLeftOf(Cardinal a, Cardinal b) {
        Cardinal[] validA = { Cardinal.N, Cardinal.E, Cardinal.S, Cardinal.W };
        Cardinal[] validB = { Cardinal.E, Cardinal.S, Cardinal.W, Cardinal.N };
        for (int i=0; i<4; i++) {
            if (a == validA[i] && b == validB[i]) {
                return true;
            }
        }
        return false;
    }
    /*
     * Checks if b is right of a
     * For a car coming in from the south, east is to the left, so isRightOf(S, E) === true
     */

    public static boolean isRightOf(Cardinal a, Cardinal b) {
        Cardinal[] validA = { Cardinal.N, Cardinal.E, Cardinal.S, Cardinal.W };
        Cardinal[] validB = { Cardinal.W, Cardinal.N, Cardinal.E, Cardinal.S };
        for (int i=0; i<4; i++) {
            if (a == validA[i] && b == validB[i]) {
                return true;
            }
        }
        return false;
    }
    /*
     * Cars entering a junction have a preference for a particular lane depending on
     * which direction they are headed. This function creates the list of lane numbers
     * in order of descending preference for cars that are heading straight ahead
     */

    private Integer[] straightAheadLaneOrderer() {
        // get total spawn rate
        float total_spawn_rate = 0;
        for (int i=0; i<4; i++) {
            if (this.spawnFreq[i] != -1) {
                total_spawn_rate += 1/this.spawnFreq[i];
            }
        }

        // prevents division by zero errors later
        if (total_spawn_rate == 0) { return new Integer[this.lanes.size()]; }

        // get the rate of left and rightbound cars
        float rightbound_rate=0, leftbound_rate=0;
        for (Cardinal dir : Cardinal.values()) {
            int i = dir.ordinal();
            if (isLeftOf(this.cardinal_pos, dir)) {
                leftbound_rate = this.spawnFreq[i] == -1 ? 0 : 1/this.spawnFreq[i];
            }
            else if (isRightOf(this.cardinal_pos, dir)) {
                rightbound_rate = this.spawnFreq[i] == -1 ? 0 : 1/this.spawnFreq[i];
            }
        }

        // here's the theory: imagine the road as a continuous stream
        // l = leftboundrate/totalspawnrate, r = rightboundrate/totalspawnrate
        // l is the proportion of the stream that is devoted to leftbound cars
        // r is the proportion of the stream that is devoted to rightbound cars
        // it makes sense that the leftbound cars are on the left of the stream
        // so between l and r, you should see the part of the stream devoted to straight-ahead cars
        // the middle of this stream is l + (r-l)/2
        // multiply this by the number of lanes and it gives you the rough lane that
        // straight-ahead cars prefer
        float proportion = (leftbound_rate + (total_spawn_rate-rightbound_rate-leftbound_rate)/2)/total_spawn_rate;
        int best_lane = Math.max(Math.min(Math.round(proportion*this.lanes.size()), this.lanes.size()-1), 0);

        ArrayList<Integer> lanes = new ArrayList<>();
        lanes.add(best_lane);

        int left_ptr=best_lane-1, right_ptr=best_lane+1;
        while(left_ptr >= 0 && right_ptr < this.lanes.size()) {
            lanes.add(left_ptr);
            lanes.add(right_ptr);
            left_ptr--;
            right_ptr++;
        }

        while(left_ptr >= 0) {
            lanes.add(left_ptr);
            left_ptr--;
        }

        while (right_ptr < this.lanes.size()) {
            lanes.add(right_ptr);
            right_ptr++;
        }

        return lanes.toArray(new Integer[lanes.size()]);
    }
    
    /*
     * Move all the cars on a road
     * @param light_is_green whether the light for that road is green or not
     */
    public void moveCars(boolean light_is_green) {
        for (Lane lane : this.lanes) {
            this.cars_to_remove.addAll(lane.moveCars(light_is_green));
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// GETTERS + SETTERS ////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public ArrayList<Lane> getLanes() {
        return lanes;
    }


    public Vehicle get_car_from_lane(int lane_number){
        return lanes.get(lane_number).get_first_car();
    }

    public Direction getDirection() {
        return direction;
    }

    public int get_lane_size(){
        return lanes.size();
    }

    public ArrayList<Rectangle> getArrows(double PEDESTRIAN_CROSSING_WIDTH){
        double pedestrian_value = 0;
        if (this.has_pedestrian){
            pedestrian_value = 1;
        }
        lane_arrows = new ArrayList<Rectangle>();
        double arrow_x = 0;
        double arrow_y = 0;
        double rotate = 0;
        for (int i=0; i<this.lane_count; i++){
            switch (this.direction){
                case TOP:
                    arrow_y = Math.min(this.corner2[1],this.corner1[1])-Car.CAR_HEIGHT-PEDESTRIAN_CROSSING_WIDTH*pedestrian_value;
                    //System.out.println("Position = "+arrow_y);
                    arrow_x = SimulationComponents.sim_h-this.corner2[0]-(i*30)-Car.CAR_HEIGHT+Car.CAR_WIDTH;
                    rotate = 180;
                    break;
                case RIGHT:
                    arrow_x = SimulationComponents.sim_w-Math.min(this.corner1[0],this.corner2[0])+Car.VEHICLE_GAP-6+PEDESTRIAN_CROSSING_WIDTH*pedestrian_value;
                    //System.out.println("Position = "+arrow_x);

                    arrow_y = SimulationComponents.sim_w-this.corner2[1]-(i*30)+Car.CAR_WIDTH-Car.CAR_HEIGHT-10;
                    rotate = 270;
                    break;
                case BOTTOM:
                    arrow_y = SimulationComponents.sim_h-Math.min(this.corner1[1],this.corner2[1])+PEDESTRIAN_CROSSING_WIDTH*pedestrian_value;
                    //System.out.println("Position = "+arrow_y);
                    arrow_x = corner2[0]+(i*30)+6;
                    rotate = 0;
                    break;
                case LEFT:
                    arrow_x = Math.min(this.corner1[0],this.corner2[0])+Car.VEHICLE_GAP-Car.CAR_HEIGHT-PEDESTRIAN_CROSSING_WIDTH*pedestrian_value;
                    //System.out.println("Position = "+arrow_x);
                    arrow_y = this.corner2[1]+(i*30)-6;
                    rotate = 90;
                    break;
            }
            lane_arrows.add(new Rectangle(arrow_x, arrow_y, 18, 43.6));
            lane_arrows.get(i).setFill(forward_arrow);
            lane_arrows.get(i).setRotate(rotate);
        }
        if (this.has_left_turn){
            lane_arrows.get(0).setFill(left_arrow);
        }if (this.has_right_turn){
            lane_arrows.get(lane_count-1).setFill(right_arrow);
        }if (this.has_bus_lane){
            lane_arrows.get(0).setFill(bus_lane_writing);
        }
        return lane_arrows;
    }
}


