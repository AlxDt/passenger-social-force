package com.crowdsimulation.model.simulator;

import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.Station;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.PortalShaft;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

// The simulator has total control over the aspects of the crowd simulation
public class Simulator {
    // State variables
    // Denotes the current mode of operation in the program
    private final SimpleObjectProperty<OperationMode> operationMode;

    // Denotes the current category within the build operation
    private final SimpleObjectProperty<BuildCategory> buildCategory;

    // Denotes the current build category within the current build category
    private final SimpleObjectProperty<BuildSubcategory> buildSubcategory;

    // Denotes the current state mostly while the program is in building mode
    private final SimpleObjectProperty<BuildState> buildState;

    // Context variables
    // Denotes the current station loaded
    private Station station;

    // Denotes the index of the floor displayed
    private SimpleIntegerProperty currentFloorIndex;

    // Denotes the floor currently displayed
    private Floor currentFloor;

    // Denotes the current amenity and classes being drawn or edited
    private final SimpleObjectProperty<Amenity> currentAmenity;
    private final SimpleObjectProperty<Class> currentClass;

    // Miscellaneous variables
    private final SimpleBooleanProperty portalDrawing;
    private final SimpleBooleanProperty firstPortalDrawn;

    private Portal firstPortal;
    private PortalShaft provisionalPortalShaft;

    public Simulator() {
        // The program is initially in the building mode
        this.operationMode = new SimpleObjectProperty<>(OperationMode.BUILDING);

        // The program is initially in the entrances/exits tab
        this.buildCategory = new SimpleObjectProperty<>(BuildCategory.ENTRANCES_AND_EXITS);

        // The program initially does not have a build subcategory
        this.buildSubcategory = new SimpleObjectProperty<>(BuildSubcategory.NONE);

        // The program is initially in the drawing state
        this.buildState = new SimpleObjectProperty<>(BuildState.DRAWING);

/*        this.station = null;
        this.currentFloorIndex = new SimpleIntegerProperty(-1);
        this.currentFloor = null;*/

        this.currentAmenity = new SimpleObjectProperty<>(null);
        this.currentClass = new SimpleObjectProperty<>(null);

        // TODO: Remove ad-hoc constants
        this.station = new Station();
        this.currentFloorIndex = new SimpleIntegerProperty(0);
        this.currentFloor = station.getFloors().get(this.currentFloorIndex.get());

        this.portalDrawing = new SimpleBooleanProperty(false);
        this.firstPortalDrawn = new SimpleBooleanProperty(false);

        this.firstPortal = null;
        this.provisionalPortalShaft = null;
    }

    public SimpleObjectProperty<OperationMode> operationModeProperty() {
        return operationMode;
    }

    public OperationMode getOperationMode() {
        return operationMode.get();
    }

    public SimpleObjectProperty<BuildCategory> buildCategoryProperty() {
        return buildCategory;
    }

    public BuildCategory getBuildCategory() {
        return buildCategory.get();
    }

    public SimpleObjectProperty<BuildSubcategory> buildSubcategoryProperty() {
        return buildSubcategory;
    }

    public BuildSubcategory getBuildSubcategory() {
        return buildSubcategory.get();
    }

    public SimpleObjectProperty<BuildState> buildStateProperty() {
        return buildState;
    }

    public BuildState getBuildState() {
        return buildState.get();
    }

    public void setOperationMode(OperationMode operationMode) {
        this.operationMode.set(operationMode);
    }

    public void setBuildCategory(BuildCategory buildCategory) {
        this.buildCategory.set(buildCategory);
    }

    public void setBuildSubcategory(BuildSubcategory buildSubcategory) {
        this.buildSubcategory.set(buildSubcategory);
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

    public SimpleIntegerProperty currentFloorIndexProperty() {
        return currentFloorIndex;
    }

    public int getCurrentFloorIndex() {
        return currentFloorIndex.get();
    }

    public void setCurrentFloorIndex(int currentFloorIndex) {
        this.currentFloorIndex.set(currentFloorIndex);
    }

    public Floor getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(Floor currentFloor) {
        this.currentFloor = currentFloor;
    }

    public SimpleObjectProperty<Amenity> currentAmenityProperty() {
        return currentAmenity;
    }

    public Amenity getCurrentAmenity() {
        return currentAmenity.get();
    }

    public void setCurrentAmenity(Amenity currentAmenity) {
        this.currentAmenity.set(currentAmenity);
    }

    public SimpleObjectProperty<Class> currentClassProperty() {
        return currentClass;
    }

    public Class getCurrentClass() {
        return currentClass.get();
    }

    public void setCurrentClass(Class currentClass) {
        this.currentClass.set(currentClass);
    }

    public boolean isPortalDrawing() {
        return portalDrawing.get();
    }

    public SimpleBooleanProperty portalDrawingProperty() {
        return portalDrawing;
    }

    public void setPortalDrawing(boolean portalDrawing) {
        this.portalDrawing.set(portalDrawing);
    }

    public boolean isFirstPortalDrawn() {
        return firstPortalDrawn.get();
    }

    public SimpleBooleanProperty firstPortalDrawnProperty() {
        return firstPortalDrawn;
    }

    public void setFirstPortalDrawn(boolean firstPortalDrawn) {
        this.firstPortalDrawn.set(firstPortalDrawn);
    }

    public Portal getFirstPortal() {
        return firstPortal;
    }

    public void setFirstPortal(Portal firstPortal) {
        this.firstPortal = firstPortal;
    }

    public PortalShaft getProvisionalPortalShaft() {
        return provisionalPortalShaft;
    }

    public void setProvisionalPortalShaft(PortalShaft provisionalPortalShaft) {
        this.provisionalPortalShaft = provisionalPortalShaft;
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
        FLOORS_AND_FLOOR_FIELDS,
        WALLS
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

        // Floors and floor fields
        QUEUEING_FLOOR_FIELD,

        // Walls
        WALL
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
