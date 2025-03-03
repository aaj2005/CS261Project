package com.example;

public class Simulation {
    private String simName;
    private Integer north_num_lanes = 0;
    private Boolean north_bus_lane, north_left_turn = false;
    private Integer north_north_vph, north_east_vph, north_west_vph = 0;

    private Integer east_num_lanes = 0;
    private Boolean east_bus_lane, east_left_turn = false;
    private Integer east_north_vph, east_east_vph, east_south_vph = 0;

    private Integer south_num_lanes = 0;
    private Boolean south_bus_lane, south_left_turn = false;
    private Integer south_east_vph, south_south_vph, south_west_vph = 0;

    private Integer west_num_lanes = 0;
    private Boolean west_bus_lane, west_left_turn = false;
    private Integer west_north_vph, west_south_vph, west_west_vph = 0;

    private Boolean pedestrian_crossings = false;
    private Integer duration_of_crossings = 0;
    private Integer requests_per_hour = 0;


    public Simulation(String simName) {
        this.simName = simName;
    }

    public void setNumberParameters(
            Integer north_north_vph,
            Integer north_east_vph,
            Integer north_west_vph,
            Integer east_north_vph,
            Integer east_east_vph,
            Integer east_south_vph,
            Integer south_east_vph,
            Integer south_south_vph,
            Integer south_west_vph,
            Integer west_north_vph,
            Integer west_south_vph,
            Integer west_west_vph)
    {
        this.north_north_vph = north_north_vph;
        this.north_east_vph = north_east_vph;
        this.north_west_vph = north_west_vph;
        this.east_north_vph = east_north_vph;
        this.east_east_vph = east_east_vph;
        this.east_south_vph = east_south_vph;
        this.south_east_vph = south_east_vph;
        this.south_south_vph = south_south_vph;
        this.south_west_vph = south_west_vph;
        this.west_north_vph = west_north_vph;
        this.west_south_vph = west_south_vph;
        this.west_west_vph = west_west_vph;

    }

    public void setNorthNumLanes(Integer north_num_lanes) {
        this.north_num_lanes = north_num_lanes;
    }
    public void setNorthBusLane(Boolean north_bus_lane) {
        this.north_bus_lane = north_bus_lane;
    }
    public void setNorthLeftTurn(Boolean north_left_turn) {
        this.north_left_turn = north_left_turn;
    }

    public void setEastNumLanes(Integer east_num_lanes) {
        this.east_num_lanes = east_num_lanes;
    }
    public void setEastBusLane(Boolean east_bus_lane) {
        this.east_bus_lane = east_bus_lane;
    }
    public void setEastLeftTurn(Boolean east_left_turn) {
        this.east_left_turn = east_left_turn;
    }

    public void setSouthNumLanes(Integer south_num_lanes) {
        this.south_num_lanes = south_num_lanes;
    }
    public void setSouthBusLane(Boolean south_bus_lane) {
        this.south_bus_lane = south_bus_lane;
    }
    public void setSouthLeftTurn(Boolean south_left_turn) {
        this.south_left_turn = south_left_turn;
    }

    public void setWestNumLanes(Integer west_num_lanes) {
        this.west_num_lanes = west_num_lanes;
    }
    public void setWestBusLane(Boolean west_bus_lane) {
        this.west_bus_lane = west_bus_lane;
    }
    public void setWestLeftTurn(Boolean west_left_turn) {
        this.west_left_turn = west_left_turn;
    }

    public void setPedestrianCrossings(Boolean pedestrian_crossings) {
        this.pedestrian_crossings = pedestrian_crossings;
    }

    public String getSimName() {
        return simName;
    }

    public void setSimName(String simName) {
        this.simName = simName;
    }

    @Override
    public String toString() {
        return simName;
    }

    public Integer getNorth_num_lanes() {
        return north_num_lanes;
    }

    public Boolean getNorth_bus_lane() {
        return north_bus_lane;
    }

    public Boolean getNorth_left_turn() {
        return north_left_turn;
    }

    public Integer getNorth_north_vph() {
        return north_north_vph;
    }

    public Integer getNorth_east_vph() {
        return north_east_vph;
    }

    public Integer getNorth_west_vph() {
        return north_west_vph;
    }

    public Integer getEast_num_lanes() {
        return east_num_lanes;
    }

    public Boolean getEast_bus_lane() {
        return east_bus_lane;
    }

    public Boolean getEast_left_turn() {
        return east_left_turn;
    }

    public Integer getEast_north_vph() {
        return east_north_vph;
    }

    public Integer getEast_east_vph() {
        return east_east_vph;
    }

    public Integer getEast_south_vph() {
        return east_south_vph;
    }

    public Integer getSouth_num_lanes() {
        return south_num_lanes;
    }

    public Boolean getSouth_bus_lane() {
        return south_bus_lane;
    }

    public Boolean getSouth_left_turn() {
        return south_left_turn;
    }

    public Integer getSouth_east_vph() {
        return south_east_vph;
    }

    public Integer getSouth_south_vph() {
        return south_south_vph;
    }

    public Integer getSouth_west_vph() {
        return south_west_vph;
    }

    public Integer getWest_num_lanes() {
        return west_num_lanes;
    }

    public Boolean getWest_bus_lane() {
        return west_bus_lane;
    }

    public Boolean getWest_left_turn() {
        return west_left_turn;
    }

    public Integer getWest_north_vph() {
        return west_north_vph;
    }

    public Integer getWest_south_vph() {
        return west_south_vph;
    }

    public Integer getWest_west_vph() {
        return west_west_vph;
    }

    public Boolean getPedestrian_crossings() {
        return pedestrian_crossings;
    }

    public Integer getDuration_of_crossings() {
        return duration_of_crossings;
    }

    public Integer getRequests_per_hour() {
        return requests_per_hour;
    }
}
