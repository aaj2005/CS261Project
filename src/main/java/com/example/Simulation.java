package com.example;

public class Simulation {
    private String simName;

    // North lane parameters
    private Integer north_num_lanes = 1;
    private Boolean north_bus_lane = false;
    private Integer north_south_vph = 0, north_east_vph = 0, north_west_vph = 0;

    // East lane parameters
    private Integer east_num_lanes = 1;
    private Boolean east_bus_lane = false;
    private Integer east_north_vph = 0, east_west_vph = 0, east_south_vph = 0;

    // South lane parameters
    private Integer south_num_lanes = 1;
    private Boolean south_bus_lane = false;
    private Integer south_east_vph = 0, south_north_vph = 0, south_west_vph = 0;

    // West lane parameters
    private Integer west_num_lanes = 1;
    private Boolean west_bus_lane = false;
    private Integer west_north_vph = 0, west_south_vph = 0, west_east_vph = 0;

    // Pedestrian crossing parameters
    private Boolean pedestrian_crossings = false;
    private Integer duration_of_crossings = 0;
    private Integer requests_per_hour = 0;

    private SimulationData resultsData;

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
            Integer west_west_vph,
            Integer duration_of_crossings,
            Integer requests_per_hour)
    {
        this.north_south_vph = north_north_vph;
        this.north_east_vph = north_east_vph;
        this.north_west_vph = north_west_vph;
        this.east_north_vph = east_north_vph;
        this.east_west_vph = east_east_vph;
        this.east_south_vph = east_south_vph;
        this.south_east_vph = south_east_vph;
        this.south_north_vph = south_south_vph;
        this.south_west_vph = south_west_vph;
        this.west_north_vph = west_north_vph;
        this.west_south_vph = west_south_vph;
        this.west_east_vph = west_west_vph;
        this.duration_of_crossings = duration_of_crossings;
        this.requests_per_hour = requests_per_hour;
    }

    // Getters and setters
    public void setNorthNumLanes(Integer north_num_lanes) {
        this.north_num_lanes = north_num_lanes;
    }
    public void setNorthBusLane(Boolean north_bus_lane) {
        this.north_bus_lane = north_bus_lane;
    }

    public void setEastNumLanes(Integer east_num_lanes) {
        this.east_num_lanes = east_num_lanes;
    }
    public void setEastBusLane(Boolean east_bus_lane) {
        this.east_bus_lane = east_bus_lane;
    }

    public void setSouthNumLanes(Integer south_num_lanes) {
        this.south_num_lanes = south_num_lanes;
    }
    public void setSouthBusLane(Boolean south_bus_lane) {
        this.south_bus_lane = south_bus_lane;
    }

    public void setWestNumLanes(Integer west_num_lanes) {
        this.west_num_lanes = west_num_lanes;
    }
    public void setWestBusLane(Boolean west_bus_lane) {
        this.west_bus_lane = west_bus_lane;
    }

    public void setPedestrianCrossings(Boolean pedestrian_crossings) {
        this.pedestrian_crossings = pedestrian_crossings;
    }

    public String getSimName() {
        return simName;
    }

    public void setSimName(String simName) {
        this.simName = simName;
        if (resultsData != null) {
            resultsData.setName(simName);
        }
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

    public Integer getNorth_south_vph() {
        return north_south_vph;
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

    public Integer getEast_north_vph() {
        return east_north_vph;
    }

    public Integer getEast_west_vph() {
        return east_west_vph;
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

    public Integer getSouth_east_vph() {
        return south_east_vph;
    }

    public Integer getSouth_north_vph() {
        return south_north_vph;
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

    public Integer getWest_north_vph() {
        return west_north_vph;
    }

    public Integer getWest_south_vph() {
        return west_south_vph;
    }

    public Integer getWest_east_vph() {
        return west_east_vph;
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

    public void setResultsData(SimulationData data) {
        this.resultsData = data;
    }

    public SimulationData getResultsData() { return this.resultsData; }

    public void setNorth_south_vph(Integer north_south_vph) {
        this.north_south_vph = north_south_vph;
    }

    public void setNorth_east_vph(Integer north_east_vph) {
        this.north_east_vph = north_east_vph;
    }

    public void setNorth_west_vph(Integer north_west_vph) {
        this.north_west_vph = north_west_vph;
    }

    public void setEast_north_vph(Integer east_north_vph) {
        this.east_north_vph = east_north_vph;
    }

    public void setEast_west_vph(Integer east_west_vph) {
        this.east_west_vph = east_west_vph;
    }

    public void setEast_south_vph(Integer east_south_vph) {
        this.east_south_vph = east_south_vph;
    }

    public void setSouth_east_vph(Integer south_east_vph) {
        this.south_east_vph = south_east_vph;
    }

    public void setSouth_north_vph(Integer south_north_vph) {
        this.south_north_vph = south_north_vph;
    }

    public void setSouth_west_vph(Integer south_west_vph) {
        this.south_west_vph = south_west_vph;
    }

    public void setWest_north_vph(Integer west_north_vph) {
        this.west_north_vph = west_north_vph;
    }

    public void setWest_south_vph(Integer west_south_vph) {
        this.west_south_vph = west_south_vph;
    }

    public void setWest_east_vph(Integer west_east_vph) {
        this.west_east_vph = west_east_vph;
    }
}
