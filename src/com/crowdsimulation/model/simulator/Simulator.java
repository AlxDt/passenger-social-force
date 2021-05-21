package com.crowdsimulation.model.simulator;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.Station;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.miscellaneous.Track;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.miscellaneous.Wall;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Gate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.PortalShaft;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs.StairPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Turnstile;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

// The simulator has total control over the aspects of the crowd simulation
public class Simulator {
    // State variables
    // Denotes the current mode of operation in the program
    private final SimpleObjectProperty<OperationMode> operationMode;

    // Denotes the current category within the build operation
    private final SimpleObjectProperty<BuildCategory> buildCategory;

    // Denotes the current build category within the current build category
    private final SimpleObjectProperty<BuildSubcategory> buildSubcategory;
    private final SimpleObjectProperty<Class<? extends Amenity>> buildSubcategoryClass;

    // Denotes the current state mostly while the program is in building mode
    private final SimpleObjectProperty<BuildState> buildState;

    // Context variables
    // Denotes the current station loaded
    private Station station;

    // Denotes the index of the floor displayed
    private final SimpleIntegerProperty currentFloorIndex;

    // Denotes the floor currently displayed
    private Floor currentFloor;

    // Denotes the current amenity and classes being drawn or edited
    private final SimpleObjectProperty<Amenity> currentAmenity;
    private final SimpleObjectProperty<Class<? extends Amenity>> currentClass;

    // Miscellaneous variables
    private final SimpleBooleanProperty portalDrawing;
    private final SimpleBooleanProperty firstPortalDrawn;

    private Portal firstPortal;
    private PortalShaft provisionalPortalShaft;

    private final SimpleBooleanProperty floorFieldDrawing;

    private Queueable currentFloorFieldTarget;
    private QueueingFloorField.FloorFieldState currentFloorFieldState;

    // Simulation variables
    private final AtomicBoolean running;
    // TODO: Replace with SimulationTime object
    private int timeElapsed;
    private final Semaphore playSemaphore;

    private final Random randomNumberGenerator;

    public Simulator() {
        // The program is initially in the building mode
        this.operationMode = new SimpleObjectProperty<>(OperationMode.BUILDING);

        // The program is initially in the entrances/exits tab
        this.buildCategory = new SimpleObjectProperty<>(BuildCategory.ENTRANCES_AND_EXITS);

        // The program initially does not have a build subcategory
        this.buildSubcategory = new SimpleObjectProperty<>(BuildSubcategory.NONE);
        this.buildSubcategoryClass = new SimpleObjectProperty<>(null);

        // The program is initially in the drawing state
        this.buildState = new SimpleObjectProperty<>(BuildState.DRAWING);

        this.currentAmenity = new SimpleObjectProperty<>(null);
        this.currentClass = new SimpleObjectProperty<>(null);

        this.station = null;
        this.currentFloorIndex = new SimpleIntegerProperty(0);
        this.currentFloor = null;

        this.portalDrawing = new SimpleBooleanProperty(false);
        this.firstPortalDrawn = new SimpleBooleanProperty(false);

        this.firstPortal = null;
        this.provisionalPortalShaft = null;

        this.floorFieldDrawing = new SimpleBooleanProperty(false);
        this.currentFloorFieldTarget = null;

        this.running = new AtomicBoolean(false);
        this.timeElapsed = 0;
        this.playSemaphore = new Semaphore(0);

        this.randomNumberGenerator = new Random();

        // Start the simulation thread, but in reality it would be activated much later
        this.start();
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

    public Class<? extends Amenity> getBuildSubcategoryClass() {
        return buildSubcategoryClass.get();
    }

    public SimpleObjectProperty<Class<? extends Amenity>> buildSubcategoryClassProperty() {
        return buildSubcategoryClass;
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

        // Also set the class equivalent of this build subcategory
        this.buildSubcategoryClass.set(Simulator.buildSubcategoryToClass(this.buildSubcategory.get()));
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

    public SimpleObjectProperty<Class<? extends Amenity>> currentClassProperty() {
        return currentClass;
    }

    public Class<? extends Amenity> getCurrentClass() {
        return currentClass.get();
    }

    public void setCurrentClass(Class<? extends Amenity> currentClass) {
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

    public boolean isFloorFieldDrawing() {
        return floorFieldDrawing.get();
    }

    public SimpleBooleanProperty floorFieldDrawingProperty() {
        return floorFieldDrawing;
    }

    public void setFloorFieldDrawing(boolean floorFieldDrawing) {
        this.floorFieldDrawing.set(floorFieldDrawing);
    }

    public Queueable getCurrentFloorFieldTarget() {
        return currentFloorFieldTarget;
    }

    public void setCurrentFloorFieldTarget(Queueable currentFloorFieldTarget) {
        this.currentFloorFieldTarget = currentFloorFieldTarget;
    }

    public QueueingFloorField.FloorFieldState getCurrentFloorFieldState() {
        return currentFloorFieldState;
    }

    public void setCurrentFloorFieldState(QueueingFloorField.FloorFieldState currentFloorFieldState) {
        this.currentFloorFieldState = currentFloorFieldState;
    }

    public void resetToDefaultConfiguration(Station station) {
        // The program is initially in the building mode
        this.operationMode.set(OperationMode.BUILDING);

        // The program is initially in the entrances/exits tab
        this.buildCategory.set(BuildCategory.ENTRANCES_AND_EXITS);

        // The program initially does not have a build subcategory
        this.buildSubcategory.set(BuildSubcategory.NONE);
        this.buildSubcategoryClass.set(null);

        // The program is initially in the drawing state
        this.buildState.set(BuildState.DRAWING);

        this.currentAmenity.set(null);
        this.currentClass.set(null);

        this.station = station;
        this.currentFloorIndex.set(0);
        this.currentFloor = station.getFloors().get(this.currentFloorIndex.get());

        this.portalDrawing.set(false);
        this.firstPortalDrawn.set(false);

        this.firstPortal = null;
        this.provisionalPortalShaft = null;

        this.floorFieldDrawing.set(false);
        this.currentFloorFieldTarget = null;

        this.running.set(false);
        this.timeElapsed = 0;
    }

    // Convert a build subcategory to its corresponding class
    private static Class<? extends Amenity> buildSubcategoryToClass(BuildSubcategory buildSubcategory) {
        switch (buildSubcategory) {
            case STATION_ENTRANCE_EXIT:
                return StationGate.class;
            case SECURITY:
                return Security.class;
            case STAIRS:
                return StairPortal.class;
            case ESCALATOR:
                return EscalatorPortal.class;
            case ELEVATOR:
                return ElevatorPortal.class;
            case TICKET_BOOTH:
                return TicketBooth.class;
            case TURNSTILE:
                return Turnstile.class;
            case TRAIN_BOARDING_AREA:
                return TrainDoor.class;
            case TRAIN_TRACK:
                return Track.class;
            case OBSTACLE:
                return Wall.class;
        }

        return null;
    }

    public AtomicBoolean getRunning() {
        return running;
    }

    public Semaphore getPlaySemaphore() {
        return playSemaphore;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    // Start the simulation thread
    private void start() {
        // Run this on a thread so it won't choke the JavaFX UI thread
        new Thread(() -> {
            while (true) {
                try {
                    // Wait until the play button has been pressed
                    playSemaphore.acquire();

                    // Keep looping until paused
                    while (this.running.get()) {
                        // Update the pertinent variables when ticking

                        // Update all agents in each floor
                        for (Floor floor : Main.simulator.station.getFloors()) {
                            updateFloor(floor);
                        }

                        GraphicsController.requestDrawStationView(
                                Main.mainScreenController.getInterfaceStackPane(),
                                Main.simulator.getCurrentFloor(),
                                false
                        );
                        Thread.sleep(100);
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    // Make all agents tick (move once in a one-second time frame) in the given floor
    private void updateFloor(Floor floor) {
        // Make all station gates in this floor spawn passengers depending on their spawn frequency
        // Generate a number from 0.0 to 1.0
        double randomNumber = randomNumberGenerator.nextDouble();

        for (StationGate stationGate : floor.getStationGates()) {
            // Only deal with station gates which have entrances
            if (stationGate.getStationGateMode() != StationGate.StationGateMode.EXIT) {
                // Spawn passengers depending on the spawn frequency of the station gate
                if (stationGate.getChancePerSecond() > randomNumber) {
                    spawnPassenger(floor, stationGate);
                }
            }
        }

        // Make each passenger move
        for (Passenger passenger : Main.simulator.getStation().getPassengersInStation()) {
            PassengerMovement passengerMovement = passenger.getPassengerMovement();

            // Look for train doors in this floor
            // TODO: change
            TrainDoor trainDoor = floor.getTrainDoors().get(0);

            passengerMovement.setGoalAmenity(trainDoor);
            passengerMovement.move();
        }
    }

    // Spawn a passenger from a gate in the given floor
    private void spawnPassenger(Floor floor, Gate gate) {
        // Generate the passenger
        Passenger passenger = gate.spawnPassenger();

        // Add the newly created passenger to the list of passengers in the floor, station, and simulation
        floor.getStation().getPassengersInStation().add(passenger);
        floor.getPassengersInFloor().add(passenger);
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
        MISCELLANEOUS
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
        TRAIN_TRACK,

        // Miscellaneous
        OBSTACLE
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
