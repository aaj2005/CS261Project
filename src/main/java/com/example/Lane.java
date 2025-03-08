package com.example;

import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Lane {

    private Direction direction;

    private ArrayList<Vehicle> cars;
    private boolean is_bus;
    // if left or right lane
    private boolean is_left =false;
    private boolean is_right=false;

    private final int has_pedestrian;
    public static final double lane_w = 30;
    private double spawn_position_x;
    private double spawn_position_y;
    private final int lane_number;

    // position where the front of the road is at (just before entering the junction)
    private double front_of_road_x=0;
    private double front_of_road_y=0;

    private final double[] corner1_dims;
    private final double[] corner2_dims;

    // the number of lanes exiting the junction
    private int max_lane_out;

    // number of lanes in the current junction arm
    private int lanes_in_road;

    // animation object
    private Animations animations;

    // if the road object storing this lane is responsible for cars that haven't yet entered the junction
    private boolean road_going_into_junction;

    // the cardinal direction of the road
    private Cardinal dir;

    /////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// CONSTRUCTOR //////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    public Lane(
        int max_lane_out,
        boolean has_pedestrian,
        Direction direction,
        int lane_number,
        int lanes_in_road,
        double[] corner1_dims,
        double[] corner2_dims,
        Animations animations,
        boolean road_going_into_junction,
        Cardinal dir
    ) {
        this.dir = dir;
        cars = new ArrayList<>();
        this.direction = direction;
        this.has_pedestrian = has_pedestrian ? 1 : 0;
        this.lane_number = lane_number; // the lane number in the particular road
        this.lanes_in_road = lanes_in_road;
        this.max_lane_out = max_lane_out;
        this.animations =animations;
        this.road_going_into_junction = road_going_into_junction;
        switch (direction){
            case TOP:
                front_of_road_y = Math.min(corner1_dims[1],corner2_dims[1]);
                break;
            case BOTTOM:
                front_of_road_y = Math.min(SimulationComponents.sim_h-corner1_dims[1],SimulationComponents.sim_h-corner2_dims[1]);

                break;
            case RIGHT:
                front_of_road_x = Math.max(SimulationComponents.sim_w-corner1_dims[0],SimulationComponents.sim_w-corner2_dims[0]);
                break;
            case LEFT:
                front_of_road_x = Math.min(corner1_dims[0],corner2_dims[0]);
                break;
        }
        this.corner1_dims = corner1_dims;
        this.corner2_dims = corner2_dims;

        spawn_position_x = 0;
        spawn_position_y = 0;
    }
    /*
     * Spawns a car in the spawn zone (offscreen)
     * @return {@code null} if another car is taking up a spawn zone, otherwise the gui component of the car
     */

    public Rectangle spawn_car(Cardinal dir) {
        Vehicle c;
        if (is_bus){
            c = (Bus) new Bus(direction,spawn_position_x, spawn_position_y);
        }else{
            c = (Car) new Car(direction, spawn_position_x, spawn_position_y,dir);
        }

        // first check if a car is taking up the spawn zone
        if (!this.existsCarInSpawnZone()) {

            // if not, add a car
            cars.add(c);
            return c.getShape();
        } else {
            return null;
        }
    }
    /*
     * checks if there is a car in the spawn zone
     * hence blocking other cars from spawning
     */

    public boolean existsCarInSpawnZone() {
        for (Vehicle car : this.cars) {
            Rectangle carRect = car.getShape();
            double x = carRect.getX(), y = carRect.getY();
            switch (this.direction) {
                case TOP:
                    if (y < -Car.CAR_HEIGHT/2 + Car.VEHICLE_GAP) { return true; }
                    break;
                case RIGHT:
                    if (x > SimulationComponents.sim_w - Car.CAR_HEIGHT/2 - Car.VEHICLE_GAP) { return true; }
                    break;
                case BOTTOM:
                    if (y > SimulationComponents.sim_h - Car.CAR_HEIGHT/2 - Car.VEHICLE_GAP) { return true; }
                    break;
                case LEFT:
                    if (x < Car.VEHICLE_GAP) { return true; }
                    break;
            }
        }
        return false;
    }
    public boolean car_in_junction(Vehicle car){
        Rectangle carRect = car.getShape();
        double x = carRect.getX(), y = carRect.getY();
        switch (this.direction) {
            case TOP:

                if (y >= front_of_road_y - SimulationComponents.GET_PEDESTRIAN_CROSSING_WIDTH()*this.has_pedestrian) {return true;}
                break;
            case RIGHT:
                if (x <= front_of_road_x + SimulationComponents.GET_PEDESTRIAN_CROSSING_WIDTH()*this.has_pedestrian) { return true; }
                break;
            case BOTTOM:
                if (y <= front_of_road_y + SimulationComponents.GET_PEDESTRIAN_CROSSING_WIDTH()) { return true; }
                break;
            case LEFT:
                if (x >= front_of_road_x - car.getHeight()-SimulationComponents.GET_PEDESTRIAN_CROSSING_WIDTH()*this.has_pedestrian) { return true; }
                break;
        }
        return false;

    }

    public void add_car(Vehicle car){
        cars.add(car);
    }

    public Vehicle remove_first_car(){
        Vehicle temp = cars.get(0);
        cars.remove(0);
        return temp;
    }


    /*
     * moves all cars in the lane towards the junction
     * @param light_is_green whether the light for the road light is green or not
     * @return an arraylist of all the cars that should be despawned
     */
    public ArrayList<Rectangle> moveCars(boolean light_is_green) {
        ArrayList<Vehicle> cars_to_remove=new ArrayList<>();
        ArrayList<Rectangle> car_shape_remove = new ArrayList<>();
        
        for (Vehicle c : this.cars) {
            // get x and y pos of car currently
            Rectangle carRect = c.getShape();
            double x = carRect.getX(), y = carRect.getY();
            double[] dirMod = this.getDirectionModifier(this.direction);

            if (c instanceof Car){
                Car car = (Car) c;
                System.out.println("is a car");
                
                // ----- MOVE THE CAR ----- //

                if (this.isCarInJam(car) && this.cars.indexOf(car)==1) {
                    System.out.println("A");
                }

                // wait till car reaches very front of lane (so it can turn)
                // check if car is in the front of the lane (about to enter the junction)
                
                // if car is on an outbound lane, move straight
                if (!road_going_into_junction && car.has_made_turn()) {
                    System.out.println("going straight after junction exit");
                    carRect.setX(x + dirMod[0]*Car.VEHICLE_SPEED);
                    carRect.setY(y + dirMod[1]*Car.VEHICLE_SPEED);
                }


                // if the car hasn't yet reached the jam
                else if (!car.is_turning() && !this.isCarInJam(car)) {
                    System.out.println("hasn't reached jam yet, going straight");
                    carRect.setX(x + dirMod[0]*Car.VEHICLE_SPEED);
                    carRect.setY(y + dirMod[1]*Car.VEHICLE_SPEED);
                }

                // cars can only turn if the light is green
                else if (light_is_green && !car.is_turning() && !car.has_made_turn() && this.hasCarReachedJunction(car)) {
                    // make the car turn left
                    if (Road.isLeftOf(this.dir,car.getDir())) {
                        System.out.println("car turning left");
                        animations.turn_left(car, car.getDirection(), x+car.getDirection().getRight_turn_pos_x(), y+car.getDirection().getRight_turn_pos_y(), lanes_in_road, max_lane_out);
                    }
                    
                    // make the car turn right
                    else if (Road.isRightOf(this.dir,car.getDir())) {
                        System.out.println("car turning right");
                        animations.turn_right(car, car.getDirection(), x+car.getDirection().getRight_turn_pos_x(),y+car.getDirection().getRight_turn_pos_y());
                    }
                }
            } else {
                System.out.println("bus");
                carRect.setX(x + dirMod[0]*Car.VEHICLE_SPEED);
                carRect.setY(y + dirMod[1]*Car.VEHICLE_SPEED);
            }

            // check if cars are outside the simulation view, if so destroy them
            switch (this.direction) {
                case TOP:
                    if (carRect.getY() >= SimulationComponents.sim_h || carRect.getX() >= SimulationComponents.sim_w || carRect.getX()+Car.CAR_HEIGHT <= 0){
                        cars_to_remove.add(c);
                        car_shape_remove.add(c.getShape());
                    }
                    break;
                case BOTTOM:
                    if (carRect.getY()+Car.CAR_HEIGHT <= 0 || carRect.getX() >= SimulationComponents.sim_w || carRect.getX()+Car.CAR_HEIGHT <= 0){
                        cars_to_remove.add(c);
                        car_shape_remove.add(c.getShape());
                    }
                    break;
                case LEFT:
                    if (carRect.getX() >= SimulationComponents.sim_w || carRect.getY()+Car.CAR_HEIGHT <= 0 || carRect.getY() >= SimulationComponents.sim_h){
                        cars_to_remove.add(c);
                        car_shape_remove.add(c.getShape());
                    }
                    break;
                case RIGHT:
                    if (carRect.getX()+Car.CAR_HEIGHT <= 0 || carRect.getY()+Car.CAR_HEIGHT <= 0 || carRect.getY() >= SimulationComponents.sim_h){
                        cars_to_remove.add(c);
                        car_shape_remove.add(c.getShape());
                    }
                    break;
            }
        }

        // remove cars that are out of the simulation view from car array
        for (Vehicle car : cars_to_remove){
            cars.remove(car);
        }
        // cars outside simulation view to be removed from anchor pane
        return car_shape_remove;
    }

    /*
     * converts a direction into a tuple like (0,1)
     * the reason for this is that then this tuple can be multiplied by the
     * car speed to move the car
     * For example, a car coming from the right is moving in the negative x direction
     * so (-1, 0) is returned. This causes the car to move -1*carspeed = carspeed units on the x-axis
     * and 0*carspeed = 0 units on the y-axis
     */
    private double[] getDirectionModifier(Direction d) {
        switch(d) {
            case TOP:    return new double[] { 0, 1 };
            case BOTTOM: return new double[] { 0, -1 };
            case LEFT:   return new double[] { 1, 0 };
            case RIGHT:  return new double[] { -1, 0 };
            default: return new double[]{}; // hopefully this state should never be reached
        }
    }

    /*
     * checks if the car is in the correct place to start turning into a different lane
     */
    private boolean hasCarReachedJunction(Car car) {
        Rectangle carRect = car.getShape();
        double x = carRect.getX(), y = carRect.getY();
        boolean turning_cond = ((Road.isLeftOf(this.dir,car.getDir()) && is_left) || (Road.isRightOf(this.dir,car.getDir()) && is_right));

        switch (this.direction){
            case TOP:
                return Math.min(corner2_dims[1],corner1_dims[1]) - Car.CAR_HEIGHT- Car.VEHICLE_GAP <=y && turning_cond && !car.has_made_turn();
            case RIGHT:
                return SimulationComponents.sim_w-Math.min(corner1_dims[0],corner2_dims[0])+ Car.CAR_HEIGHT >= x && turning_cond && !car.has_made_turn();
            case BOTTOM:
                return SimulationComponents.sim_h-Math.min(corner1_dims[1],corner2_dims[1]) >= y && turning_cond && !car.has_made_turn();
            case LEFT:
                return Math.min(corner1_dims[0],corner2_dims[0]) - Car.CAR_HEIGHT- Car.VEHICLE_GAP <= x && turning_cond && !car.has_made_turn();
        }

        return false;
    }

    /*
     * checks if a car is queueing
     */
    private boolean isCarInJam(Car car) {
        Car prevCar = this.getCarAhead(car);
        if (prevCar == null) { // if the car is first in the queue,

            if (this.hasCarReachedJunction(car)) {
                System.out.println("Car " + this.cars.indexOf(car) + " reached junction");
            }

            return this.hasCarReachedJunction(car); // check if it has reached the junction
        } else { // if the car is not the first in the queue,

            if (this.isSpacingTooSmall(prevCar, car)) {
                System.out.println("Car " + this.cars.indexOf(car) + " too close to previous car");
            }

            return this.isSpacingTooSmall(prevCar, car); // check if the car is close to the one in front of it
        }
    }

    /*
     * given a car in a lane, this function finds the first car before it
     * @param car a car
     * @return {@code null} if this is the first car in the lane, or the car before it
     */
    private Car getCarAhead(Car car) {
        /*
         * this function works by getting where the car is in the array,
         * then iterating backwards from the index of the car to the beginning of the array
         * the first car in the same lane encountered is removed
         */
        int index = this.cars.indexOf(car);
        for (int i=index-1; 0<=i; i--) {
            Vehicle vehicle = this.cars.get(i);
            if (vehicle instanceof Car) {
                return (Car) vehicle;
            }
        }
        return null;
    }

    /*
     * checks if the spacing between two cars is too small
     * if it is, that means the cars must be queueing
     * @param car1 some car potentiall in the jam
     * @param car2 the car behind it
     * @return if the cars are queueing or not
     */
    private boolean isSpacingTooSmall(Car car1, Car car2) {
        // x and y of the first car
        Rectangle car1_rect = car1.getShape();
        double x1 = car1_rect.getX(), y1 = car1_rect.getY();
        
        // x and y of the second car
        Rectangle car2_rect = car2.getShape();
        double x2 = car2_rect.getX(), y2 = car2_rect.getY();

        Direction dir = car1.getDirection();
        double[] dir_mod = this.getDirectionModifier(dir);

        switch (dir) {
            case TOP:
            case BOTTOM:
                return Math.abs(y2-y1)<Car.CAR_HEIGHT+Car.VEHICLE_GAP;
            case LEFT:
            case RIGHT:
                return Math.abs(x2-x1)<Car.CAR_HEIGHT+Car.VEHICLE_GAP;
        }        
        return false;
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// GETTERS + SETTERS ////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public ArrayList<Vehicle> getCars() {
        return cars;
    }
    public Direction getDirection() {
        return direction;
    }

    public Vehicle get_first_car(){
        if (!cars.isEmpty()){
            return cars.get(0);
        }
        throw new IndexOutOfBoundsException("No car in specified lane");
    }

    public void set_left_turn(){
        is_left = true;
    }
    public void set_right_turn(){
        is_right = true;
    }

    public void set_bus_lane(){
        is_bus = true;
    }

    // set the Y spawn position for the lane
    public void setSpawn_position_x(double spawn_position_x) {
        this.spawn_position_x = spawn_position_x + direction.getLane_switch_x() * (lane_w * lane_number) ;
    }

    // set the X spawn position for the lane
    public void setSpawn_position_y(double spawn_position_y) {
        this.spawn_position_y = spawn_position_y + direction.getLane_switch_y() * (lane_w * lane_number);
    }

    public Cardinal getDir() {
        return dir;
    }

    public boolean is_right() {
        return is_right;
    }

    public boolean is_left() {
        return is_left;
    }
}
