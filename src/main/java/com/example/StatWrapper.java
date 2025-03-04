package com.example;

public class StatWrapper {
    private StatRoad[] roads = new StatRoad[4];

    public StatWrapper(Road[] roads) {
        for (int i=0; i<4; i++) {
            this.roads[i] = new StatRoad(roads[i]);
        }
    }

    public Stats run() throws InvalidParametersException, InvalidMethodCallException {
        Stats stats = new Stats(0, 0, 0, 0);
        for (int i=0; i<4; i++) {
            StatRoad[] tempRoads = this.roads.clone();
            
            // first handle the case of the left-turn-lane
            tempRoads[i] = this.roads[i].simulateLeftTurn();
            if (tempRoads[i] != null) {
                Stats currentStats = new StatCalculator(tempRoads, i).run();
                stats = Stats.combine(stats, currentStats);
            }

            // then the right-turn-lane
            tempRoads[i] = this.roads[i].simulateRightTurn();
            if (tempRoads[i] != null) {
                Stats currentStats = new StatCalculator(tempRoads, i).run();
                stats = Stats.combine(stats, currentStats);
            }

            // then all the other lanes
            tempRoads[i] = this.roads[i].simulateRegularRoad();
            if (tempRoads[i] != null) {
                Stats currentStats = new StatCalculator(tempRoads, i).run();
                stats = Stats.combine(stats, currentStats);
            }
        }

        return stats;
    }
}
