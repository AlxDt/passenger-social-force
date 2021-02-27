package com.crowdsimulation.model.simulator;

import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.Station;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import javafx.beans.property.SimpleObjectProperty;

// The simulator has total control over the aspects of the crowd simulation
public class Simulator {
    // State variables
    // Denotes the current mode of operation in the program
    private final SimpleObjectProperty<OperationMode> operationMode;

    // Denotes the current category within the build operation
    private BuildCategory buildCategory;

    // Denotes the current build category within the current build category
    private BuildSubcategory buildSubcategory;

    // Denotes the current state mostly while the program is in building mode
    private final SimpleObjectProperty<BuildState> buildState;

    // Context variables
    // Denotes the current station loaded
    private Station station;

    // Denotes the index of the floor displayed
    private int currentFloorIndex;

    // Denotes the floor currently displayed
    private Floor currentFloor;

    private final SimpleObjectProperty<Amenity> currentAmenity;
    private final SimpleObjectProperty<Class> currentClass;

    public Simulator() {
        // The program is initially in the building mode
        this.operationMode = new SimpleObjectProperty<>(OperationMode.BUILDING);

        // The program is initially in the entrances/exits tab
        this.buildCategory = BuildCategory.ENTRANCES_AND_EXITS;

        // The program initially does not have a build subcategory
        this.buildSubcategory = BuildSubcategory.NONE;

        // The program is initially in the drawing state
        this.buildState = new SimpleObjectProperty<>(BuildState.DRAWING);

        this.station = null;
        this.currentFloorIndex = -1;
        this.currentFloor = null;

        this.currentAmenity = new SimpleObjectProperty<>(null);
        this.currentClass = new SimpleObjectProperty<>(null);

        // TODO: Remove ad-hoc constants
        this.station = new Station();
        this.currentFloorIndex = 0;
        this.currentFloor = station.getFloors().get(this.currentFloorIndex);
    }

    public SimpleObjectProperty<OperationMode> getOperationMode() {
        return operationMode;
    }

    public SimpleObjectProperty<OperationMode> operationModeProperty() {
        return operationMode;
    }

    public BuildCategory getBuildCategory() {
        return buildCategory;
    }

    public void setBuildCategory(BuildCategory buildCategory) {
        this.buildCategory = buildCategory;
    }

    public BuildSubcategory getBuildSubcategory() {
        return buildSubcategory;
    }

    public void setBuildSubcategory(BuildSubcategory buildSubcategory) {
        this.buildSubcategory = buildSubcategory;
    }

    public SimpleObjectProperty<BuildState> getBuildState() {
        return buildState;
    }

    public SimpleObjectProperty<BuildState> buildStateProperty() {
        return buildState;
    }

    public void setBuildState(BuildState buildState) {
        this.buildState.set(buildState);
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

    public SimpleObjectProperty<Amenity> getCurrentAmenity() {
        return currentAmenity;
    }

    public void setCurrentAmenity(Amenity currentAmenity) {
        this.currentAmenity.set(currentAmenity);
    }

    public SimpleObjectProperty<Class> getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(Class currentClass) {
        this.currentClass.set(currentClass);
    }

    // Describes the modes of operation in this program
    public enum OperationMode {
        BUILDING, // The user is building the station
        TESTING // The user is testing the station - no building features available when this is the mode (except edit)
    }

    // Describes the categories within the build operation
    public enum BuildCategory {
        ENTRANCES_AND_EXITS,
        STAIRS_AND_ELEVATORS,
        CONCOURSE_AMENITIES,
        PLATFORM_AMENITIES,
        FLOOR_FIELDS
    }

    // Describes the subcategories within the current build category
    public enum BuildSubcategory {
        NONE,

        // Entrances and exits
        STATION_ENTRANCE_EXIT,
        SECURITY,

        // Stairs and elevators
        STAIRS,
        ESCALATOR,
        ELEVATOR,

        // Concourse amenities
        TICKET_BOOTH,
        TURNSTILE,

        // Platform amenities
        TRAIN_BOARDING_AREA,

        // Floor fields
        QUEUEING_FLOOR_FIELD,
        PLATFORM_FLOOR_FIELD,
        STAIR_FLOOR_FIELD,
        ELEVATOR_FLOOR_FIELD
    }

    // Describes the states within the build mode in the program
    public enum BuildState {
        DRAWING("Draw"), // The user is drawing more amenities
        EDITING_ONE("Edit one"), // The user is editing parameters of an existing amenity
        EDITING_ALL("Edit all"); // The user is editing parameters of all existing amenities

        private final String name;

        private BuildState(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
