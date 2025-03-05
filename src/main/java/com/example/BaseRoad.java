package com.example;

/* DUMMY CLASS */
public class BaseRoad extends JunctionElement {
    public double actual_light_duration; // how long the light is green for
    public double[] inbound_vph;         // how many cars come into the junction from this road per second
    public int lanes;                   // number of lanes on this road
    public Cardinal pos;                // where this road is on the screen (eg: road at the top is North)

    /*
     * ASSUMPTIONS:
     * all of the following lanes are included in the total lane count of the road
     * the leftmost lane is the left-turn lane, and vice versa for the right-turn lane
     */
    public boolean has_left_turn_lane;  // if the road has a left-turn lane
    public boolean has_right_turn_lane; // if the road has a right-turn lane
    
    /*
     * This constructior assumes that no left nor right-turn exist
     */
    public BaseRoad(
        double actual_light_duration,
        double[] inbound_vph,
        int lanes,
        Cardinal pos
    ) {
        this.actual_light_duration = actual_light_duration;
        this.inbound_vph = inbound_vph;
        this.lanes = lanes;
        this.pos = pos;
    }

    /*
     * This constructor allows you to specify what types of lanes exist
     */
    public BaseRoad(
        double actual_light_duration,
        double[] inbound_vph,
        int lanes,
        Cardinal pos,
        boolean has_left_turn_lane,
        boolean has_right_turn_lane
    ) {
        this(actual_light_duration, inbound_vph, lanes, pos);
        this.has_left_turn_lane = has_left_turn_lane;
        this.has_right_turn_lane = has_right_turn_lane;
    }
}
