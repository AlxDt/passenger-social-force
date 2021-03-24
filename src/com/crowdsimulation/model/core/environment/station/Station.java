package com.crowdsimulation.model.core.environment.station;

import com.crowdsimulation.model.core.environment.Environment;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Station extends BaseStationObject implements Environment {
    // Denotes the dimensions of this station
    public static final int ROWS = 60;
    public static final int COLUMNS = 106;

    // Contains binding values for the number of floors this station has
    // The name of the station
    private final String name;

    // Each station contains a list of floors
    private final List<Floor> floors;

    // TODO: Revise temporary constructor
    public Station() {
        // TODO: Load station from file
        this.name = "Test Station";
        this.floors = new ArrayList<>();

        // TODO: Remove ad-hoc adding when implementation is made to load from file
        // Initially, the station has one floor
        Floor.addFloor(this.floors, 0, Station.ROWS, Station.COLUMNS);
    }

    public String getName() {
        return name;
    }

    public List<Floor> getFloors() {
        return floors;
    }
}
