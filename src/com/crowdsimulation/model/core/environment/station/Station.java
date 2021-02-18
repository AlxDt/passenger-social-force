package com.crowdsimulation.model.core.environment.station;

import com.crowdsimulation.model.core.environment.Environment;

import java.util.ArrayList;
import java.util.List;

public class Station extends BaseStation implements Environment {
    public static final int ROWS = 60;
    public static final int COLUMNS = 106;

    // The name of the station
    private final String name;

    // Each station contains a list of floors
    private final List<Floor> floors;

    public Station() {
        // TODO: Load station from file
        this.name = null;
        this.floors = new ArrayList<>();

        // TODO: Remove ad-hoc adding when implementation is made to load from file
        this.floors.add(new Floor(ROWS, COLUMNS));
    }

    public String getName() {
        return name;
    }

    public List<Floor> getFloors() {
        return floors;
    }
}
