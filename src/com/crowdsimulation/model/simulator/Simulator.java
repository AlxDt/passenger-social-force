package com.crowdsimulation.model.simulator;

import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.Station;

// The simulator has total control over the aspects of the crowd simulation
public class Simulator {
    // Denotes the current station loaded
    public Station station;

    // Denotes the index of the floor displayed
    private int currentFloorIndex;

    // Denotes the floor currently displayed
    private Floor currentFloor;

    public Simulator() {
        this.station = null;
        this.currentFloorIndex = -1;
        this.currentFloor = null;

        // TODO: Remove ad-hoc constants
        this.station = new Station();
        this.currentFloorIndex = 0;
        this.currentFloor = station.getFloors().get(this.currentFloorIndex);
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public int getCurrentFloorIndex() {
        return currentFloorIndex;
    }

    public void setCurrentFloorIndex(int currentFloorIndex) {
        this.currentFloorIndex = currentFloorIndex;
    }

    public Floor getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(Floor currentFloor) {
        this.currentFloor = currentFloor;
    }
}
