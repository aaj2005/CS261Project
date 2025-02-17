package com.example;

public enum Direction {

    TOP(0, -Car.CAR_HEIGHT-Car.CAR_GAP,180, -Car.CAR_WIDTH/2, (-Car.CAR_HEIGHT - Car.CAR_GAP)/2, -Car.CAR_WIDTH, 0 ),
    BOTTOM(0,Car.CAR_HEIGHT+ Car.CAR_GAP,0, Car.CAR_WIDTH, Car.CAR_HEIGHT + Car.CAR_GAP, Car.CAR_WIDTH, 0),
    RIGHT(Car.CAR_HEIGHT+Car.CAR_GAP, 0,-90, 0, 0, 0, -Car.CAR_WIDTH),
    LEFT(-Car.CAR_HEIGHT- Car.CAR_GAP, 0, 90, 0,0, 0, Car.CAR_WIDTH)
    ;

    private final double lane_pivot_x;
    private final double lane_pivot_y;
    private final double lane_switch_x;
    private final double lane_switch_y;
    private final double rotation;
    private final double x_offset;
    private final double y_offset;



    Direction(double x, double y, double rotation, double x_offset, double y_offset, double lane_switch_x, double lane_switch_y){
        lane_pivot_x = x;
        lane_pivot_y = y;
        this.rotation = rotation;
        this.x_offset = x_offset;
        this.y_offset = y_offset;
        this.lane_switch_x = lane_switch_x;
        this.lane_switch_y = lane_switch_y;
    }

    public double getLane_pivot_x() {
        return lane_pivot_x;
    }

    public double getLane_pivot_y() {
        return lane_pivot_y;
    }

    public double getRotation() {
        return rotation;
    }

    public double getX_offset() {
        return x_offset;
    }

    public double getY_offset() {
        return y_offset;
    }

    public double getLane_switch_x() {
        return lane_switch_x;
    }

    public double getLane_switch_y() {
        return lane_switch_y;
    }

}

