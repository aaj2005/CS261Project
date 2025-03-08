package com.example;

public enum Direction {
    // rotation: determine angle to rotate car so that it faces in the correct direction
    // lane switch: factor to determine direction of offsetting a car left/right or top/bottom when choosing a lane
   // right/left_turn_offset: to adjust the car's position on the lane after making a turn
    // right_trans/left_trans: to determine factor for mathematical equation when performing reflections/transformations for turning right/left
    // right/left _turn_pos: offset to the car's initial position before turning (javafx weirdness)
    // road_after_left/right: get the junction_arm index for the road that you end up in after turning left/right

    TOP(
        180,
        -1, 0,
        1, -1,
        -1, 1,
        0,0,
        0,Car.CAR_HEIGHT+Car.CAR_WIDTH/2,
        Car.CAR_WIDTH/2,Car.CAR_HEIGHT/2,
        3,1
    ),
    RIGHT(
        0,
        0, -1,
        -1, 1,
        1,-1,
        0,0,
        -Car.CAR_WIDTH/2,0,
        Car.CAR_HEIGHT/2,Car.CAR_WIDTH/2,
        0,2
    ),
    BOTTOM(
        0,
        1, 0,
        1, -1,
        -1,1,
        0,0,
        0,Car.CAR_HEIGHT+Car.CAR_WIDTH/2,
        Car.CAR_WIDTH/2,Car.CAR_HEIGHT/2,
        1,3
    ),
    LEFT(
        180,
        0, 1,
        -1, 1,
        1,-1,
        0,0,
        -Car.CAR_WIDTH/2,0,
        Car.CAR_HEIGHT/2,Car.CAR_WIDTH/2,
        2,0
    );

    private final double lane_switch_x;
    private final double lane_switch_y;
    private final double rotation;
    private final double right_trans_x;
    private final double right_trans_y;
    private final double left_trans_x;
    private final double left_trans_y;
    private final double right_turn_offset_x;
    private final double right_turn_offset_y;
    private final double left_turn_offset_x;
    private final double left_turn_offset_y;
    private final double right_turn_pos_x;
    private final double right_turn_pos_y;
    private final int road_after_left;
    private final int road_after_right;

    Direction(
            double rotation,
            double lane_switch_x, double lane_switch_y,
            double right_trans_x, double right_trans_y,
            double left_trans_x, double left_trans_y,
            double right_turn_offset_x, double right_turn_offset_y,
            double left_turn_offset_x, double left_turn_offset_y,
            double right_turn_pos_x, double right_turn_pos_y,
            int road_after_left, int road_after_right
    ){
        this.rotation = rotation;
        this.lane_switch_x = lane_switch_x;
        this.lane_switch_y = lane_switch_y;
        this.right_trans_x = right_trans_x;
        this.right_trans_y = right_trans_y;
        this.left_trans_x = left_trans_x;
        this.left_trans_y = left_trans_y;
        this.right_turn_offset_x = right_turn_offset_x;
        this.right_turn_offset_y = right_turn_offset_y;
        this.left_turn_offset_x = left_turn_offset_x;
        this.left_turn_offset_y = left_turn_offset_y;
        this.right_turn_pos_x = right_turn_pos_x;
        this.right_turn_pos_y = right_turn_pos_y;
        this.road_after_right = road_after_right;
        this.road_after_left = road_after_left;
    }

    public double getRotation() {
        return rotation;
    }

    public double getLane_switch_x() {
        return lane_switch_x;
    }

    public double getLane_switch_y() {return lane_switch_y;}

    public double getRight_trans_x() {
        return right_trans_x;
    }

    public double getRight_trans_y() {
        return right_trans_y;
    }

    public double getLeft_trans_x() {
        return left_trans_x;
    }

    public double getLeft_trans_y() {
        return left_trans_y;
    }

    public double getRight_turn_offset_x() {
        return right_turn_offset_x;
    }

    public double getRight_turn_offset_y() {
        return right_turn_offset_y;
    }

    public double getLeft_turn_offset_x() {
        return left_turn_offset_x;
    }

    public double getLeft_turn_offset_y() {
        return left_turn_offset_y;
    }


    public double getRight_turn_pos_x() {
        return right_turn_pos_x;
    }

    public double getRight_turn_pos_y() {
        return right_turn_pos_y;
    }

    public int getRoad_after_left() {
        return road_after_left;
    }

    public int getRoad_after_right() {
        return road_after_right;
    }

    public static Direction getLeft(Direction a) {
        switch (a) {
            case TOP: return RIGHT;
            case RIGHT: return BOTTOM;
            case BOTTOM: return LEFT;
            case LEFT: return TOP;
        }
        return null;
    }

    public static Direction getRight(Direction a) {
        switch (a) {
            case TOP: return LEFT;
            case RIGHT: return TOP;
            case BOTTOM: return RIGHT;
            case LEFT: return BOTTOM;
        }
        return null;
    }

    public static Direction getOpposite(Direction a) {
        switch (a) {
            case TOP: return BOTTOM;
            case BOTTOM: return TOP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
        }
        return null;
    }
}

