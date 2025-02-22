package com.example;

import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Road extends JunctionElement{


    private ArrayList<Lane> lanes;
    private int priority = 1;
    public static int default_light_duration = 30;
    private int lane_capacity;
    private Direction direction;

    public Road(int lane_count, double[] corner1, double[] corner2, int dir,  boolean has_pedestrian, Direction direction){
        light_status = false;
        int lane_capacity = (int) (Math.floor(Math.min(corner1[dir], corner2[dir])/(Car.CAR_HEIGHT+Car.CAR_GAP)));
        lanes = new ArrayList<>(5);
        for (int i=0; i<lane_count;i++){
            lanes.add(new Lane(lane_capacity,has_pedestrian,0,0,direction,i));
        }
        this.lane_capacity = lane_capacity;
        this.direction = direction;
//        System.out.println(lane_capacity);
    }

    public Road(int lane_count, int priority, double[] corner1, double[] corner2, int dir,  boolean has_pedestrian, Direction direction){
        this(lane_count, corner1, corner2, dir, has_pedestrian, direction);
        this.priority = priority;
    }


    public void set_start(double start_x, double start_y){
        for (Lane lane : lanes){
            lane.setSpawn_position_x(start_x);
            lane.setSpawn_position_y(start_y);
        }
    }

    public int getLane_capacity() {
        return lane_capacity;
    }

    public Rectangle spawn_car_in_lane(int i){
        if (i < lanes.size()){
            return lanes.get(i).spawn_car();
        }else{
            throw new IndexOutOfBoundsException("Lane "+ i+ " does not exist!");
        }

    }

    public Car get_car_from_lane(int lane_number){
        return lanes.get(lane_number).get_first_car();
    }

    public Direction getDirection() {
        return direction;
    }
}
