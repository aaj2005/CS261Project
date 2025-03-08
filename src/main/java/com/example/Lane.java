package com.example;

import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Lane {

    private Direction direction;

    private ArrayList<Vehicle> vehicles;
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

    public final double[] corner1_dims;
    public final double[] corner2_dims;

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
        vehicles = new ArrayList<>();
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
            c = (Car) new Car(direction, spawn_position_x, spawn_position_y,Cardinal.toDirection(dir));
        }

        // first check if a car is taking up the spawn zone
        if (!this.existsCarInSpawnZone()) {

            // if not, add a car
            vehicles.add(c);
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
        for (Vehicle car : this.vehicles) {
            Rectangle carRect = car.getShape();
            double x = carRect.getX(), y = carRect.getY();
            switch (this.direction) {
                case TOP:
                    if (y < -Car.CAR_HEIGHT/2 + Car.VEHICLE_GAP - SimulationComponents.spawn_offset) { return true; }
                    break;
                case RIGHT:
                    if (x > SimulationComponents.sim_w - Car.CAR_HEIGHT/2 - Car.VEHICLE_GAP + SimulationComponents.spawn_offset) { return true; }
                    break;
                case BOTTOM:
                    if (y > SimulationComponents.sim_h - Car.CAR_HEIGHT/2 - Car.VEHICLE_GAP + SimulationComponents.spawn_offset) { return true; }
                    break;
                case LEFT:
                    if (x < Car.VEHICLE_GAP - SimulationComponents.spawn_offset) { return true; }
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
        vehicles.add(car);
    }

    public Vehicle remove_first_car(){
        Vehicle temp = vehicles.get(0);
        vehicles.remove(0);
        return temp;
    }


    /*
     * moves all cars in the lane towards the junction
     * @param light_is_green whether the light for the road light is green or not
     * @return an arraylist of all the cars that should be despawned
     */
    public ArrayList<Rectangle> moveCars(boolean light_is_green) {
        ArrayList<Vehicle> vehicles_to_remove=new ArrayList<>();
        ArrayList<Rectangle> vehicle_shapes_to_remove = new ArrayList<>();
        
        for (Vehicle vehicle : this.vehicles) {
            // get x and y pos of car currently
            Rectangle vehicle_rect = vehicle.getShape();
            double x = vehicle_rect.getX(), y = vehicle_rect.getY();
            double[] lane_dir_mod = this.getDirectionModifier(this.direction);

            double[] car_dir_mod = this.getDirectionModifier(vehicle.getOutboundDirection());
            
            // ----- MOVE THE CAR ----- //
            // wait till car reaches very front of lane (so it can turn)
            // check if car is in the front of the lane (about to enter the junction)
            
            // if (!this.isCarInJam(car) && x < SimulationComponents.sim_w-Math.min(corner1_dims[0],corner2_dims[0])+ Car.CAR_HEIGHT) {
            //     System.out.println(SimulationComponents.sim_w-Math.min(corner1_dims[0],corner2_dims[0])+ Car.CAR_HEIGHT);
            // }

            // if car hasn't reached the jam yet, move forward
            if (!this.isVehicleInJam(vehicle) && !vehicle.hasStartedTurning()) {
                vehicle_rect.setX(x + lane_dir_mod[0]*Car.VEHICLE_SPEED);
                vehicle_rect.setY(y + lane_dir_mod[1]*Car.VEHICLE_SPEED);
            }

            // if the car has reached the jam but the light is red, don't move
            else if (this.isVehicleInJam(vehicle) && !vehicle.hasStartedTurning() && !light_is_green) {}

            // if the car has reached the junction, and the light is green, start turning
            else if (this.hasVehicleReachedJunction(vehicle) && !vehicle.hasStartedTurning() && light_is_green) {
                if (vehicle instanceof Car) { // buses do not turn so they are handled differently
                    Car car = (Car) vehicle;

                    // make the car turn left
                    if (Road.isLeftOf(Direction.toCardinal(this.direction),Direction.toCardinal(car.getOutboundDirection()))) {
                        animations.turn_left(car, car.getInboundDirection(), x+car.getInboundDirection().getRight_turn_pos_x(), y+car.getInboundDirection().getRight_turn_pos_y(), lanes_in_road, max_lane_out);
                    }
                    
                    // make the car turn right
                    else if (Road.isRightOf(Direction.toCardinal(this.direction),Direction.toCardinal(car.getOutboundDirection()))) {
                        animations.turn_right(car, car.getInboundDirection(), x+car.getInboundDirection().getRight_turn_pos_x(),y+car.getInboundDirection().getRight_turn_pos_y());
                    }

                    // make the car go straight ahead (no turning, so we just set the has completed turning flag to true)
                    else { vehicle.setMadeTurn(); }
                } else {
                    vehicle.setMadeTurn();
                }
            }

            // if the car has already passed the junction (equivalently, if the car has "turned"), keep the car going
            else if (vehicle.hasMadeTurn()) {                
                vehicle_rect.setX(x - car_dir_mod[0]*Car.VEHICLE_SPEED);
                vehicle_rect.setY(y - car_dir_mod[1]*Car.VEHICLE_SPEED);
                this.despawnVehicle(vehicles_to_remove, vehicle_shapes_to_remove, vehicle, vehicle.getOutboundDirection());
            }

            /*
            // if car is on an outbound lane, move straight
            if (car.has_made_turn()) {
                System.out.println("going straight after junction exit");
                
                double[] car_dir_mod = this.getDirectionModifier(car.getOutboundDirection());
                carRect.setX(x + car_dir_mod[0]*Car.VEHICLE_SPEED);
                carRect.setY(y + car_dir_mod[1]*Car.VEHICLE_SPEED);
            }

            // handle the cars going straight
            else if (light_is_green && Cardinal.isOpposite(car.getOutboundDirection(), this.dir)) {
                carRect.setX(x + lane_dir_mod[0]*Car.VEHICLE_SPEED);
                carRect.setY(y + lane_dir_mod[1]*Car.VEHICLE_SPEED);

                // set the has_turned flag to true in order to keep the car going even if the light is red
                // (because it has passed the junction)
                if (this.hasCarReachedJunction(car)) { car.set_made_turn(); }
            }

            // if the car hasn't yet reached the jam
            else if (!car.is_turning() && !this.isCarInJam(car)) {
                System.out.println("hasn't reached jam yet, going straight");
                carRect.setX(x + lane_dir_mod[0]*Car.VEHICLE_SPEED);
                carRect.setY(y + lane_dir_mod[1]*Car.VEHICLE_SPEED);
            }

            // cars can only turn if the light is green
            else if (light_is_green && !car.is_turning() && !car.has_made_turn() && this.hasCarReachedJunction(car)) {
                // make the car turn left
                if (Road.isLeftOf(this.dir,car.getOutboundDirection())) {
                    System.out.println("car turning left");
                    animations.turn_left(car, car.getDirection(), x+car.getDirection().getRight_turn_pos_x(), y+car.getDirection().getRight_turn_pos_y(), lanes_in_road, max_lane_out);
                }
                
                // make the car turn right
                else if (Road.isRightOf(this.dir,car.getOutboundDirection())) {
                    System.out.println("car turning right");
                    animations.turn_right(car, car.getDirection(), x+car.getDirection().getRight_turn_pos_x(),y+car.getDirection().getRight_turn_pos_y());
                }
            }
            */
        }

        // remove cars that are out of the simulation view from car array
        for (Vehicle vehicle : vehicles_to_remove){
            this.vehicles.remove(vehicle);
        }
        // cars outside simulation view to be removed from anchor pane
        return vehicle_shapes_to_remove;
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

    private double[] getDirectionModifier(Cardinal d) {
        switch(d) {
            case N: return new double[] { 0, -1 };
            case S: return new double[] { 0, 1 };
            case E: return new double[] { 1, 0 };
            case W: return new double[] { -1, 0 };
            default: return new double[]{}; // hopefully this state should never be reached
        }
    }

    /*
     * checks if the vehicle is in the correct place to start turning into a different lane
     * @param vehicle
     * @param lane the lane the vehicle is on
     */
    public boolean hasVehicleReachedJunction(Vehicle vehicle) {
        Rectangle vehicle_rect = vehicle.getShape();
        double x = vehicle_rect.getX(), y = vehicle_rect.getY();

        switch (vehicle.getInboundDirection()){
            case TOP:
                return Math.min(corner2_dims[1],corner1_dims[1]) - vehicle.getHeight() - Vehicle.VEHICLE_GAP - SimulationComponents.GET_PEDESTRIAN_CROSSING_WIDTH()*this.has_pedestrian <=y;
            case RIGHT:
                return SimulationComponents.sim_w-Math.min(corner1_dims[0],corner2_dims[0]) + SimulationComponents.GET_PEDESTRIAN_CROSSING_WIDTH()*this.has_pedestrian + Vehicle.VEHICLE_GAP >= x;
            case BOTTOM:
                return SimulationComponents.sim_h-Math.min(corner1_dims[1],corner2_dims[1]) + SimulationComponents.GET_PEDESTRIAN_CROSSING_WIDTH()*this.has_pedestrian + Vehicle.VEHICLE_GAP >= y;
            case LEFT:
                return Math.min(corner1_dims[0],corner2_dims[0]) - vehicle.getHeight() - Vehicle.VEHICLE_GAP - SimulationComponents.GET_PEDESTRIAN_CROSSING_WIDTH()*this.has_pedestrian <= x;
        }

        return false;
    }

    /*
     * checks if a vehicle is queueing
     */
    private boolean isVehicleInJam(Vehicle vehicle) {
        Vehicle prev_vehicle = this.getVehicleAhead(vehicle);
        if (prev_vehicle == null) { // if the vehicle is first in the queue,
            return this.hasVehicleReachedJunction(vehicle); // check if it has reached the junction
        } else { // if the car is not the first in the queue,
            return this.isSpacingTooSmall(prev_vehicle, vehicle); // check if the vehicle is close to the one in front of it
        }
    }

    /*
     * given a vehicle in a lane, this function finds the first vehicle before it
     * @param vehicle a vehicle
     * @return {@code null} if this is the first vehicle in the lane, or the vehicle before it
     */
    private Vehicle getVehicleAhead(Vehicle vehicle) {
        /*
         * this function works by getting where the vehicle is in the array,
         * then iterating backwards from the index of the vehicle to the beginning of the array
         * the first vehicle in the same lane encountered is removed
         */
        int index = this.vehicles.indexOf(vehicle);
        for (int i=index-1; 0<=i; i--) {
            Vehicle prev_vehicle = this.vehicles.get(i);
            if (!prev_vehicle.is_turning() && !prev_vehicle.hasMadeTurn()) {
                return prev_vehicle;
            }
        }
        return null;
    }

    /*
     * checks if the spacing between two vehicles is too small
     * if it is, that means the vehicles must be queueing
     * @param vehicle1 some vehicle potentiall in the jam
     * @param vehicle2 the vehicle behind it
     * @return if the vehicles are queueing or not
     */
    private boolean isSpacingTooSmall(Vehicle vehicle1, Vehicle vehicle2) {
        // x and y of the first car
        Rectangle car1_rect = vehicle1.getShape();
        double x1 = car1_rect.getX(), y1 = car1_rect.getY();
        
        // x and y of the second car
        Rectangle car2_rect = vehicle2.getShape();
        double x2 = car2_rect.getX(), y2 = car2_rect.getY();

        switch (vehicle1.getInboundDirection()) {
            case TOP:
            case BOTTOM:
                return Math.abs(y2-y1)<vehicle1.getHeight()+Vehicle.VEHICLE_GAP;
            case LEFT:
            case RIGHT:
                return Math.abs(x2-x1)<vehicle1.getHeight()+Vehicle.VEHICLE_GAP;
        }        
        return false;
    }

    // check if cars are outside the simulation view, if so destroy them
    private void despawnVehicle(ArrayList<Vehicle> cars_to_remove, ArrayList<Rectangle> car_shape_remove, Vehicle vehicle, Direction outboundDir) {
        Rectangle vehicle_rect = vehicle.getShape();
        switch (Direction.getOpposite(outboundDir)) {
            case TOP:
                if (vehicle_rect.getY() >= SimulationComponents.sim_h || vehicle_rect.getX() >= SimulationComponents.sim_w || vehicle_rect.getX()+vehicle.getHeight() <= 0){
                    cars_to_remove.add(vehicle);
                    car_shape_remove.add(vehicle.getShape());
                }
                break;
            case BOTTOM:
                if (vehicle_rect.getY()+Car.CAR_HEIGHT <= 0 || vehicle_rect.getX() >= SimulationComponents.sim_w || vehicle_rect.getX()+vehicle.getHeight() <= 0){
                    cars_to_remove.add(vehicle);
                    car_shape_remove.add(vehicle.getShape());
                }
                break;
            case LEFT:
                if (vehicle_rect.getX() >= SimulationComponents.sim_w || vehicle_rect.getY()+vehicle.getHeight() <= 0 || vehicle_rect.getY() >= SimulationComponents.sim_h){
                    cars_to_remove.add(vehicle);
                    car_shape_remove.add(vehicle.getShape());
                }
                break;
            case RIGHT:
                if (vehicle_rect.getX()+Car.CAR_HEIGHT <= 0 || vehicle_rect.getY()+vehicle.getHeight() <= 0 || vehicle_rect.getY() >= SimulationComponents.sim_h){
                    cars_to_remove.add(vehicle);
                    car_shape_remove.add(vehicle.getShape());
                }
                break;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// GETTERS + SETTERS ////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }
    public Direction getDirection() {
        return direction;
    }

    public Vehicle get_first_car(){
        if (!vehicles.isEmpty()){
            return vehicles.get(0);
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
