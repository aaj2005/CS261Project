package com.example;

import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Lane {

    private Direction direction;

    private ArrayList<Car> cars;
    private int lane_capacity;
    private boolean is_bus;
    private boolean is_left =false;
    private boolean is_right=false;
    private int has_pedestrian;
    public static final double lane_w = 30;
    private double spawn_position_x;
    private double spawn_position_y;
    private int lane_number;
    private double front_of_road_x=0;
    private double front_of_road_y=0;
    private double[] corner1_dims;
    private double[] corner2_dims;

    private int max_lane_out;
    private int lanes_in_road;
    private Animations animations;

    public Lane(
        int lane_capacity,
        int max_lane_out,
        boolean has_pedestrian,
        Direction direction,
        int lane_number,
        int lanes_in_road,
        double[] corner1_dims,
        double[] corner2_dims,
        Animations animations
    ) {

        cars = new ArrayList<>();
        this.direction = direction;
        this.lane_capacity = lane_capacity;
        this.has_pedestrian = has_pedestrian ? 1 : 0;
        this.lane_number = lane_number; // the lane number in the particular road
        this.lanes_in_road = lanes_in_road;
        this.max_lane_out = max_lane_out;
        this.animations =animations;
        switch (direction){
            case TOP:
                front_of_road_y = Math.min(corner1_dims[1],corner2_dims[1]) - Car.CAR_HEIGHT;
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
        this.corner2_dims = corner1_dims;

        spawn_position_x = 0;
        spawn_position_y = 0;
    }
    public void set_left_turn(){
        is_left = true;
    }
    public void set_right_turn(){
        is_right = true;
    }
    // set the Y spawn position for the lane
    public void setSpawn_position_x(double spawn_position_x) {
        this.spawn_position_x = spawn_position_x + direction.getLane_switch_x() * (lane_w * lane_number) ;
    }

    // set the X spawn position for the lane
    public void setSpawn_position_y(double spawn_position_y) {
        this.spawn_position_y = spawn_position_y + direction.getLane_switch_y() * (lane_w * lane_number);
    }

    /*
     * Spawns a car in the spawn zone (offscreen)
     * @return {@code null} if another car is taking up a spawn zone, otherwise the gui component of the car
     */
    public Rectangle spawn_car(Cardinal dir) {
        Car c = new Car(direction, spawn_position_x, spawn_position_y, dir);

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
        for (Car car : this.cars) {
            Rectangle carRect = car.getShape();
            double x = carRect.getX(), y = carRect.getY();
            switch (this.direction) {
                case TOP:
                    if (y < -Car.CAR_HEIGHT/2 + Car.CAR_GAP) { return true; }
                    break;
                case RIGHT:
                    if (x > SimulationComponents.sim_w - Car.CAR_HEIGHT/2 - Car.CAR_GAP) { return true; }
                    break;
                case BOTTOM:
                    if (y > SimulationComponents.sim_h - Car.CAR_HEIGHT/2 - Car.CAR_GAP) { return true; }
                    break;
                case LEFT:
                    if (x < Car.CAR_GAP) { return true; }
                    break;
            }
        }
        return false;
    }

    public boolean car_in_junction(Car car){
        Rectangle carRect = car.getShape();
        double x = carRect.getX(), y = carRect.getY();
        switch (this.direction) {
            case TOP:
//                    System.out.println("Car Y:"+ y + " Front of road Y:"+ front_of_road_y);
                if (y >= front_of_road_y - SimulationComponents.GET_PEDESTRIAN_CROSSING_WIDTH()*this.has_pedestrian) { return true; }
                break;
            case RIGHT:
                if (x <= front_of_road_x + SimulationComponents.GET_PEDESTRIAN_CROSSING_WIDTH()*this.has_pedestrian) { return true; }
                break;
            case BOTTOM:
                if (y <= front_of_road_y + SimulationComponents.GET_PEDESTRIAN_CROSSING_WIDTH()) { return true; }
                break;
            case LEFT:
                if (x >= front_of_road_x - Car.CAR_HEIGHT-SimulationComponents.GET_PEDESTRIAN_CROSSING_WIDTH()*this.has_pedestrian) { return true; }
                break;
        }
        return false;

    }

    public Car get_first_car(){
        if (!cars.isEmpty()){
            return cars.get(0);
        }
        throw new IndexOutOfBoundsException("No car in specified lane");
    }

    public void add_car(Car car){
        cars.add(car);
    }

    public Car remove_first_car(){
        Car temp = cars.get(0);
        cars.remove(0);
        return temp;
    }

    /*
     * moves all cars in the lane towards the junction
     */
    public void moveCars() {
        ArrayList<Rectangle> cars_to_remove=new ArrayList<>();
        ArrayList<Car> car_shape_remove = new ArrayList<>();
        for (Car c : this.cars) {
            // get x and y pos of car currently
            Rectangle carRect = c.getShape();
            double x = carRect.getX(), y = carRect.getY();

            // move the car
            double[] dirMod = this.getDirectionModifier(this.direction);

            // wait till car reaches very front of lane (so it can turn)
            boolean car_ready_to_turn = false;
            switch (this.direction){
                case TOP:
                    car_ready_to_turn = Math.min(corner2_dims[1],corner1_dims[1])-Car.CAR_HEIGHT-Car.CAR_GAP <=y && (is_left || is_right) && !c.has_made_turn();
                    break;
                case RIGHT:
                    car_ready_to_turn =( SimulationComponents.sim_w-Math.min(corner1_dims[0],corner2_dims[0]) >= x && (is_left || is_right) && !c.has_made_turn());
//                    System.out.println(car_ready_to_turn);
                    break;
                case BOTTOM:
                    car_ready_to_turn = SimulationComponents.sim_h-Math.min(corner1_dims[1],corner2_dims[1]) >= y && (is_left || is_right) && !c.has_made_turn();
                    break;
                case LEFT:
                    car_ready_to_turn = Math.min(corner1_dims[0],corner2_dims[0])- Car.CAR_HEIGHT <= x && (is_left || is_right) && !c.has_made_turn();
                    break;
            }
            if (!car_ready_to_turn && !c.is_turning()){
                carRect.setX(x + dirMod[0]*Car.CAR_SPEED);
                carRect.setY(y + dirMod[1]*Car.CAR_SPEED);
            }else if (is_left && !c.is_turning() && !c.has_made_turn()){
                animations.turn_left(c, c.getDirection(), x+c.getDirection().getRight_turn_pos_x(), y+c.getDirection().getRight_turn_pos_y(), lanes_in_road, max_lane_out);
            }else if (is_right && !c.is_turning() && !c.has_made_turn()){
                animations.turn_right(c, c.getDirection(), x+c.getDirection().getRight_turn_pos_x(),y+c.getDirection().getRight_turn_pos_y());
            }
            switch (this.direction){
                case TOP:
                    if (carRect.getY() >= SimulationComponents.sim_h || carRect.getX() >= SimulationComponents.sim_w || carRect.getX()+Car.CAR_HEIGHT <= 0){
                        cars_to_remove.add(c.getShape());
                        car_shape_remove.add(c);
                    }
                    break;
                case BOTTOM:
                    if (carRect.getY()+Car.CAR_HEIGHT <= 0 || carRect.getX() >= SimulationComponents.sim_w || carRect.getX()+Car.CAR_HEIGHT <= 0){
                        cars_to_remove.add(c.getShape());
                        car_shape_remove.add(c);
                    }
                    break;
                case LEFT:
                    if (carRect.getX() >= SimulationComponents.sim_w || carRect.getY()+Car.CAR_HEIGHT <= 0 || carRect.getY() >= SimulationComponents.sim_h){
                        cars_to_remove.add(c.getShape());
                        car_shape_remove.add(c);
                    }
                    break;
                case RIGHT:
                    if (carRect.getX()+Car.CAR_HEIGHT <= 0 || carRect.getY()+Car.CAR_HEIGHT <= 0 || carRect.getY() >= SimulationComponents.sim_h){
                        cars_to_remove.add(c.getShape());
                        car_shape_remove.add(c);
                    }
                    break;
            }
        }
        // remove car from car array
        for (Car car : car_shape_remove){
            cars.remove(car);
        }
        // remove car from simulation display
        for(Rectangle car: cars_to_remove){
            App.root.getChildren().remove(car);
        }
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
            default: return null; // hopefully this state should never be reached
        }
    }

    public ArrayList<Car> getCars() {
        return cars;
    }

    public Direction getDirection() {
        return direction;
    }
    public boolean is_right(){
        return this.is_right;
    }
    public boolean is_left(){
        return this.is_left;
    }
}
