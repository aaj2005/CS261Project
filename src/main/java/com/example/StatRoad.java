package com.example;

/*
 * You'll notice that (in the stat branch at least, defo not in the final version), this and Road have a lot of redundancy!
 * StatRoad essentially pulls out some attributes out of Road
 * They can then be edited in StatRoad without affecting the original Road
 * This allows us to do stuff like simulating left-turn lanes, check the conversion functions in this class
 */
public class StatRoad {
    public double actual_light_duration; // how long the light is green for
    public double[] inbound_vph;         // how many cars come into the junction from this road per second
    public int lanes;                   // number of lanes on this road
    public Cardinal pos;                // where this road is on the screen (eg: road at the top is North)

    /*
     * ASSUMPTIONS:
     * all of the following lanes are included in the total lane count of the road
     * the leftmost lane is the left-turn lane, and vice versa for the right-turn lane
     * the bus lane is leftmost, or second-leftmost if a left-turn lane exists
     */
    public boolean has_left_turn_lane;  // if the road has a left-turn lane
    public boolean has_right_turn_lane; // if the road has a right-turn lane
    
    public StatRoad(Road road) {
        this.actual_light_duration = road.actual_light_duration;
        this.inbound_vph = road.inbound_vph;
        this.lanes = road.lanes;
        this.has_left_turn_lane = road.has_left_turn_lane;
        this.has_right_turn_lane = road.has_right_turn_lane;
        this.pos = road.pos;
    }    
    
    public StatRoad(StatRoad road) {
        this.actual_light_duration = road.actual_light_duration;
        this.inbound_vph = road.inbound_vph;
        this.lanes = road.lanes;
        this.has_left_turn_lane = road.has_left_turn_lane;
        this.has_right_turn_lane = road.has_right_turn_lane;
        this.pos = road.pos;
    }

    /*
     * Creates a road that is essentially just one singular lane
     * And all non-left-turning traffic is set to 0 vph
     * Essentially the created road serves as a left-turn-lane simulator
     * Calculations can then be ran on this road for it to function like a left-turn-lane
     * @returns {@code null} if the road doesn't have a left-turn lane, or a road that simulates a left-turn lane otherwise
     */
    public StatRoad simulateLeftTurn() {
        if (!this.has_left_turn_lane) { return null; }

        StatRoad new_road = new StatRoad(this);
        new_road.lanes = 1;

        for (Cardinal dir : Cardinal.values()) {
            if (!Cardinal.isLeftOf(new_road.pos, dir)) {
                new_road.inbound_vph[dir.ordinal()] = 0;
            }
        }

        return new_road;
    }

    /*
     * Creates a road that is essentially just one singular lane
     * And all non-right-turning traffic is set to 0 vph
     * Essentially the created road serves as a right-right-lane simulator
     * Calculations can then be ran on this road for it to function like a right-turn-lane
     * @returns {@code null} if the road doesn't have a right-turn lane, or a road that simulates a right-turn lane otherwise
     */
    public StatRoad simulateRightTurn() {
        if (!this.has_right_turn_lane) { return null; }
        
        StatRoad new_road = new StatRoad(this);
        new_road.lanes = 1;

        for (Cardinal dir : Cardinal.values()) {
            if (!Cardinal.isRightOf(new_road.pos, dir)) {
                new_road.inbound_vph[dir.ordinal()] = 0;
            }
        }

        return new_road;
    }

    /*
     * Creates a road which simulates the rest of the lanes
     * ie: non-left-turn and non-right-turn lanes
     * @returns {@code null} if the road doesn't only has left and right-turn lanes, or a road that simulates a road without left and right-turn lanes otherwise
     */
    public StatRoad simulateRegularRoad() {
        if (this.has_left_turn_lane && this.has_right_turn_lane && this.lanes <= 2
         || this.has_left_turn_lane && !this.has_right_turn_lane && this.lanes <= 1
         || !this.has_left_turn_lane && this.has_right_turn_lane && this.lanes <= 1) {
            return null;
        }

        StatRoad new_road = new StatRoad(this);
        
        if (new_road.has_left_turn_lane) {
            new_road.lanes = Math.max(1, new_road.lanes-1);
            new_road.has_left_turn_lane = false;
        }
        
        if (new_road.has_right_turn_lane) {
            new_road.lanes = Math.max(1, new_road.lanes-1);
            new_road.has_right_turn_lane = false;
        }

        return new_road;
    }

    public double getTotalVph() {
        double sum = 0;
        for (int i=0; i<4; i++) {
            sum += this.inbound_vph[i];
        }
        return sum;
    }
}
