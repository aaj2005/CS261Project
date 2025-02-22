package com.example;

import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

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

    public Lane(int lane_capacity,boolean has_pedestrian, double start_x, double start_y, Direction direction, int lane_number) {
        cars = new ArrayList<>();
        this.direction = direction;
        this.lane_capacity = lane_capacity;
        this.has_pedestrian = has_pedestrian;
        spawn_position_x = 0;
        spawn_position_y = 0;
        this.lane_number = lane_number;
    }

    public void setSpawn_position_x(double spawn_position_x) {
        this.spawn_position_x = spawn_position_x + direction.getLane_switch_x() * (lane_w * lane_number) ;
    }

    public void setSpawn_position_y(double spawn_position_y) {
        this.spawn_position_y = spawn_position_y + direction.getLane_switch_y() * (lane_w * lane_number);
    }

    public Rectangle spawn_car(){

        cars.add(new Car(direction, spawn_position_x, spawn_position_y));
        spawn_position_x += direction.getLane_pivot_x();
        spawn_position_y += direction.getLane_pivot_y();
        return cars.get(cars.size()-1).getShape();
    }

    public Car get_first_car(){
        if (!cars.isEmpty()){
            return cars.get(0);
        }
        throw new IndexOutOfBoundsException("No car in specified lane");
    }

}
