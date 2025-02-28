package com.example;

import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Lane {

    private Direction direction;

    private ArrayList<Car> cars;
    private int lane_capacity;
    private boolean is_bus;
    private boolean is_left;
    private boolean has_pedestrian;
    public static final double lane_w = 30;
    private double spawn_position_x;
    private double spawn_position_y;
    private int lane_number;

    public Lane(int lane_capacity, boolean has_pedestrian, double start_x, double start_y, Direction direction, int lane_number) {
        cars = new ArrayList<>();
        this.direction = direction;
        this.lane_capacity = lane_capacity;
        this.has_pedestrian = has_pedestrian;
        this.lane_number = lane_number;

        spawn_position_x = 0;
        spawn_position_y = 0;
    }

    public void setSpawn_position_x(double spawn_position_x) {
        this.spawn_position_x = spawn_position_x + direction.getLane_switch_x() * (lane_w * lane_number) ;
    }

    public void setSpawn_position_y(double spawn_position_y) {
        this.spawn_position_y = spawn_position_y + direction.getLane_switch_y() * (lane_w * lane_number);
    }

    public Rectangle spawn_car(Cardinal dir) {
        Car c = new Car(direction, spawn_position_x, spawn_position_y, dir);
        cars.add(c);
        return c.getShape();
    }

    public Car get_first_car(){
        if (!cars.isEmpty()){
            return cars.get(0);
        }
        throw new IndexOutOfBoundsException("No car in specified lane");
    }

    /*
     * moves all cars in the lane towards the junction
     */
    public void moveCars() {
        for (Car c : this.cars) {
            // get x and y pos of car currently
            Rectangle carRect = c.getShape();
            double x = carRect.getX(), y = carRect.getY();

            System.out.println("car at x: " + x + ", y: " + y);

            // move the car
            double[] dirMod = this.getDirectionModifier(this.direction);
            carRect.setX(x + dirMod[0]*Car.CAR_SPEED);
            carRect.setY(y + dirMod[1]*Car.CAR_SPEED);
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
}
