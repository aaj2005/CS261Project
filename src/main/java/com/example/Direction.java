package com.example;

public enum Direction {

    TOP(
            0, -Car.CAR_HEIGHT-Car.CAR_GAP,
            180,
            -1, 0,
            1, -1,
            -1, 1,
            0,0,
            0,Car.CAR_HEIGHT,
            Car.CAR_WIDTH/2,Car.CAR_HEIGHT/2,
            Car.CAR_WIDTH/2,Car.CAR_HEIGHT/2,
            -1,-1
    ),
    BOTTOM(
            0,Car.CAR_HEIGHT+ Car.CAR_GAP,
            0,
            1, 0,
            1, -1,
            -1,1,
            0,0,
            0,Car.CAR_HEIGHT,
            Car.CAR_WIDTH/2,Car.CAR_HEIGHT/2,
            Car.CAR_WIDTH/2,Car.CAR_HEIGHT/2,
            -1,-1
    ),
    RIGHT(
            Car.CAR_HEIGHT+Car.CAR_GAP, 0,
            0,
            0, -1,
            -1, 1,
            1,-1,
            0,0,
            0,0,
            Car.CAR_HEIGHT/2,Car.CAR_WIDTH/2,
            Car.CAR_HEIGHT/2,Car.CAR_WIDTH/2,
            1,-1
    ),
    LEFT(
            -Car.CAR_HEIGHT- Car.CAR_GAP, 0,
            180,
            0, 1,
            -1, 1,
            1,-1,
            0,0,
            0,0,
            Car.CAR_HEIGHT/2,Car.CAR_WIDTH/2,
            Car.CAR_HEIGHT/2,Car.CAR_WIDTH/2,
            1,-1
    );

    private final double next_in_lane_x;
    private final double next_in_lane_y;
    private final double lane_switch_x;
    private final double lane_switch_y;
    private final double rotation;
    private final double right_trans_x;
    private final double right_trans_y;
    private final double left_trans_x;
    private final double left_trans_y;
    private final double straight_trans_x;
    private final double straight_trans_y;
    private final double right_turn_offset_x;
    private final double right_turn_offset_y;
    private final double left_turn_offset_x;
    private final double left_turn_offset_y;
    private final double right_turn_pos_x;
    private final double right_turn_pos_y;
    private final double left_turn_pos_x;
    private final double left_turn_pos_y;



    Direction(
            double next_in_lane_x, double next_in_lane_y,
            double rotation,
            double lane_switch_x, double lane_switch_y,
            double right_trans_x, double right_trans_y,
            double left_trans_x, double left_trans_y,
            double right_turn_offset_x, double right_turn_offset_y,
            double left_turn_offset_x, double left_turn_offset_y,
            double right_turn_pos_x, double right_turn_pos_y,
            double left_turn_pos_x, double left_turn_pos_y,
            double straight_trans_x, double straight_trans_y
    ){
        this.next_in_lane_x = next_in_lane_x;
        this.next_in_lane_y = next_in_lane_y;
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
        this.left_turn_pos_x = left_turn_pos_x;
        this.left_turn_pos_y = left_turn_pos_y;
        this.straight_trans_x = straight_trans_x;
        this.straight_trans_y = straight_trans_y;
    }

    public double get_next_in_lane_posX() {
        return next_in_lane_x;
    }

    public double get_next_in_lane_posY() {
        return next_in_lane_y;
    }

    public double getRotation() {
        return rotation;
    }

    public double getLane_switch_x() {
        return lane_switch_x;
    }

    public double getLane_switch_y() {
        return lane_switch_y;
    }



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

    public double getLeft_turn_pos_x() {
        return left_turn_pos_x;
    }

    public double getLeft_turn_pos_y() {
        return left_turn_pos_y;
    }

    public double getRight_turn_pos_x() {
        return right_turn_pos_x;
    }

    public double getRight_turn_pos_y() {
        return right_turn_pos_y;
    }

    public double getStraight_trans_x() {
        return straight_trans_x;
    }

    public double getStraight_trans_y() {
        return straight_trans_y;
    }
}

