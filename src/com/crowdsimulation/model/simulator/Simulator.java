package com.crowdsimulation.model.simulator;

import com.crowdsimulation.controller.Main;
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
import com.crowdsimulation.model.core.environment.station.patch.position.Coordinates;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.*;
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

    // Simulator variables
    private final AtomicBoolean running;

    // Denotes the current time in the simulation
    private final SimulationTime time;
    private final Semaphore playSemaphore;

    // Random number generator for all purposes in the simulation
    public static final Random RANDOM_NUMBER_GENERATOR;

    // Simulation variables
    private final List<Passenger> passengersToDespawn;
    public static final HashMap<Coordinates.PatchPair, Double> DISTANCE_CACHE;

    static {
        RANDOM_NUMBER_GENERATOR = new Random();

        DISTANCE_CACHE = new HashMap<>();
    }

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

        this.time = new SimulationTime(0, 0, 0);
        this.playSemaphore = new Semaphore(0);

        this.passengersToDespawn = Collections.synchronizedList(new ArrayList<>());

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

    public AtomicBoolean getRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running.set(running);
    }

    public boolean isRunning() {
        return running.get();
    }

    public SimulationTime getSimulationTime() {
        return time;
    }

    public Semaphore getPlaySemaphore() {
        return playSemaphore;
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

        this.time.reset();
        this.running.set(false);
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

    // Start the simulation thread
    private void start() {
        // Run this on a thread so it won't choke the JavaFX UI thread
        new Thread(() -> {
            // For times shorter than this, speed awareness will be implemented
            final int speedAwarenessLimitMilliseconds = 10;

            while (true) {
                try {
                    // Wait until the play button has been pressed
                    playSemaphore.acquire();

                    // Keep looping until paused
                    while (this.isRunning()) {
                        // Update the pertinent variables when ticking

                        // Draw all agents in each floor
                        for (Floor floor : Main.simulator.station.getFloors()) {
                            updateFloor(floor);
                        }

                        // Redraw the visualization
                        // If the refreshes are frequent enough, update the visualization in a speed-aware manner
                        // That is, avoid having too many refreshes within a short period of time
                        Main.mainScreenController.drawStationViewFloorForeground(
                                Main.simulator.getCurrentFloor(),
                                SimulationTime.SLEEP_TIME_MILLISECONDS.get()
                                        < speedAwarenessLimitMilliseconds
                        );

                        // Increment (tick) the clock
                        this.time.tick();

                        // TODO: Rest for an amount of time depending on the interface slider
                        Thread.sleep(SimulationTime.SLEEP_TIME_MILLISECONDS.get());
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    // Reset the simulation
    public void reset() {
        // Reset the simulation time
        this.time.reset();
    }

    // Make all agents tick (move once in a one-second time frame) in the given floor
    private void updateFloor(Floor floor) {
        // Make all station gates in this floor spawn passengers depending on their spawn frequency
        // Generate a number from 0.0 to 1.0
        double randomNumber;

        for (StationGate stationGate : floor.getStationGates()) {
            randomNumber = RANDOM_NUMBER_GENERATOR.nextDouble();

            // Only deal with station gates which have entrances
            if (stationGate.getStationGateMode() != StationGate.StationGateMode.EXIT) {
                // Spawn passengers depending on the spawn frequency of the station gate
                if (stationGate.getChancePerSecond() > randomNumber) {
//                    if (floor.getPassengersInFloor().size() <= 15) {
                    spawnPassenger(floor, stationGate);
//                    }
                }
            }
        }

        // Make each passenger move
        for (Passenger passenger : floor.getPassengersInFloor()) {
            movePassenger(passenger);

            // Also update the graphic of the passenger
            passenger.getPassengerGraphic().change();
        }

        // Remove all passengers that are marked for removal
        for (Passenger passengerToDespawn : this.passengersToDespawn) {
            // Get the passenger's gate
            Gate gate = (Gate) passengerToDespawn.getPassengerMovement().getCurrentAmenity();

            // Have the gate despawn that passenger
            gate.despawnPassenger(passengerToDespawn);
        }

        passengersToDespawn.clear();
    }

    private void movePassenger(Passenger passenger) {
        PassengerMovement passengerMovement = passenger.getPassengerMovement();

        // Get the three passenger movement states
        PassengerMovement.Direction direction = passengerMovement.getDirection();
        PassengerMovement.State state = passengerMovement.getState();
        PassengerMovement.Action action = passengerMovement.getAction();

        switch (direction) {
            case BOARDING:
                // The passenger has entered the station and is heading towards the platform to board the train
                switch (state) {
                    case WALKING:
                        if (action == PassengerMovement.Action.WILL_QUEUE) {
                            // Look for the goal nearest to this passenger
                            passengerMovement.chooseGoal();

                            if (
                                    passengerMovement.getParent().getTicketType() == TicketBooth.TicketType.SINGLE_JOURNEY
                                            || passengerMovement.getParent().getTicketType() == TicketBooth.TicketType.STORED_VALUE && !passengerMovement.willPathFind()
                            ) {
                                // Make this passenger face the set goal, its queueing area, or the passenger at the tail of
                                // the queue
                                passengerMovement.faceNextPosition();

                                // Move towards that direction
                                passengerMovement.moveSocialForce();

                                if (passengerMovement.hasEncounteredPassengerToFollow()) {
                                    // If the passenger did not move, and there is someone blocking it while queueing,
                                    // transition into the "in queue" state and the "assembling" action
                                    passengerMovement.joinQueue();

                                    passengerMovement.setState(PassengerMovement.State.IN_QUEUE);
                                    state = PassengerMovement.State.IN_QUEUE;

                                    passengerMovement.setAction(PassengerMovement.Action.ASSEMBLING);
                                    action = PassengerMovement.Action.ASSEMBLING;

                                    break;
                                }
//
                                // Check whether the passenger's next amenity is queueable
                                // If it is, check whether the passenger has reached its floor field
                                if (passengerMovement.isNextAmenityQueueable()) {
                                    // If the passenger has reached the patch with the nearest floor field value, transition
                                    // into the "in queue" state and the "queueing" action
                                    if (passengerMovement.hasReachedQueueingFloorField()) {
                                        // Mark this passenger as the latest one to join its queue
                                        passengerMovement.joinQueue();

                                        passengerMovement.setState(PassengerMovement.State.IN_QUEUE);
                                        state = PassengerMovement.State.IN_QUEUE;

                                        passengerMovement.setAction(PassengerMovement.Action.QUEUEING);
                                        action = PassengerMovement.Action.QUEUEING;
                                    }
                                } else {
                                    // If the passenger has reached its non-queueable goal, transition into the appropriate
                                    // state and action
                                    if (passengerMovement.hasReachedGoal()) {
                                        passengerMovement.setState(PassengerMovement.State.IN_NONQUEUEABLE);
                                        state = PassengerMovement.State.IN_NONQUEUEABLE;

                                        if (passengerMovement.getGoalAmenity() instanceof StationGate) {
                                            passengerMovement.setAction(PassengerMovement.Action.EXITING_STATION);
                                            action = PassengerMovement.Action.EXITING_STATION;
                                        } else if (passengerMovement.getGoalAmenity() instanceof Portal) {
                                            // This only covers stairs and escalators, as elevators are queueable
                                            // TODO: Set the appropriate passenger direction (ascending/descending)
                                        }
                                    }
                                }

                                // if the passenger is stuck, switch to the "rerouting" action except if the passenger
                                // is a stored value ticket holder
                                if (
                                        passengerMovement.isStuck()
                                                && passengerMovement.getState() != PassengerMovement.State.IN_QUEUE
                                                && passengerMovement.getParent().getTicketType() != TicketBooth.TicketType.STORED_VALUE
                                ) {
                                    passengerMovement.setAction(PassengerMovement.Action.REROUTING);
                                    action = PassengerMovement.Action.REROUTING;
                                }
                            } else {
                                // This passenger is a stored value ticket holder so generate a path, if one hasn't been
                                // generated yet, then follow it until the passenger reaches its goal
                                // Get the next path
                                if (passengerMovement.chooseNextPatchInPath()) {
                                    // Make this passenger face that patch
                                    passengerMovement.faceNextPosition();

                                    // Move towards that patch
                                    passengerMovement.moveSocialForce();

                                    if (
                                            passengerMovement.hasEncounteredPassengerToFollow()/*
                                                    || passengerMovement.hasEncounteredAnyQueueingPassenger()*/
                                    ) {
                                        // If the passenger did not move, and there is someone blocking it while queueing,
                                        // transition into the "in queue" state and the "assembling" action
                                        passengerMovement.joinQueue();

                                        passengerMovement.setState(PassengerMovement.State.IN_QUEUE);
                                        state = PassengerMovement.State.IN_QUEUE;

                                        passengerMovement.setAction(PassengerMovement.Action.ASSEMBLING);
                                        action = PassengerMovement.Action.ASSEMBLING;

                                        passengerMovement.endStoredValuePathfinding();

                                        break;
                                    }

                                    if (passengerMovement.hasReachedNextPatchInPath()) {
                                        // The passenger has reached the next patch in the path, so remove this from this
                                        // passenger's current path
                                        passengerMovement.reachPatchInPath();

                                        // Check if there are still patches left in the path
                                        // If there are no more patches left, revert back to the "will queue" action
                                        if (passengerMovement.hasPassengerReachedFinalPatchInPath()) {
                                            passengerMovement.setState(PassengerMovement.State.WALKING);
                                            state = PassengerMovement.State.WALKING;

                                            passengerMovement.setAction(PassengerMovement.Action.WILL_QUEUE);
                                            action = PassengerMovement.Action.WILL_QUEUE;

                                            passengerMovement.endStoredValuePathfinding();
                                        }

                                        break;
                                    }
                                } else {
                                    passengerMovement.setState(PassengerMovement.State.WALKING);
                                    state = PassengerMovement.State.WALKING;

                                    passengerMovement.setAction(PassengerMovement.Action.WILL_QUEUE);
                                    action = PassengerMovement.Action.WILL_QUEUE;

                                    passengerMovement.endStoredValuePathfinding();

                                    break;
                                }
                            }
                        } else {
                            // This passenger is stuck, so generate a path, if one hasn't been generated yet, then
                            // follow it until the passenger is not stuck anymore
                            // Get the next path
                            if (passengerMovement.chooseNextPatchInPath()) {
                                // Make this passenger face that patch
                                passengerMovement.faceNextPosition();

                                // Move towards that patch
                                passengerMovement.moveSocialForce();

                                if (passengerMovement.isReadyToFree()) {
                                    // If the passenger has been moving again for a consistent period of time, free the
                                    // passenger and don't follow the path anymore
                                    passengerMovement.setState(PassengerMovement.State.WALKING);
                                    state = PassengerMovement.State.WALKING;

                                    passengerMovement.setAction(PassengerMovement.Action.WILL_QUEUE);
                                    action = PassengerMovement.Action.WILL_QUEUE;

                                    // Then this passenger will not be stuck anymore
                                    passengerMovement.free();

                                    break;
                                }

                                if (
                                        passengerMovement.hasEncounteredPassengerToFollow()/*
                                                || passengerMovement.hasEncounteredAnyQueueingPassenger()*/
                                ) {
                                    // If the passenger did not move, and there is someone blocking it while queueing,
                                    // transition into the "in queue" state and the "assembling" action
                                    passengerMovement.joinQueue();

                                    passengerMovement.setState(PassengerMovement.State.IN_QUEUE);
                                    state = PassengerMovement.State.IN_QUEUE;

                                    passengerMovement.setAction(PassengerMovement.Action.ASSEMBLING);
                                    action = PassengerMovement.Action.ASSEMBLING;

                                    // Then this passenger will not be stuck anymore
                                    passengerMovement.free();

                                    break;
                                }

                                if (passengerMovement.hasReachedNextPatchInPath()) {
                                    // The passenger has reached the next patch in the path, so remove this from this
                                    // passenger's current path
                                    passengerMovement.reachPatchInPath();

                                    // Check if there are still patches left in the path
                                    // If there are no more patches left, revert back to the "will queue" action
                                    if (passengerMovement.hasPassengerReachedFinalPatchInPath()) {
                                        passengerMovement.setState(PassengerMovement.State.WALKING);
                                        state = PassengerMovement.State.WALKING;

                                        passengerMovement.setAction(PassengerMovement.Action.WILL_QUEUE);
                                        action = PassengerMovement.Action.WILL_QUEUE;

                                        // Then this passenger will not be stuck anymore
                                        passengerMovement.free();
                                    }

                                    break;
                                }
                            } else {
                                passengerMovement.setState(PassengerMovement.State.WALKING);
                                state = PassengerMovement.State.WALKING;

                                passengerMovement.setAction(PassengerMovement.Action.WILL_QUEUE);
                                action = PassengerMovement.Action.WILL_QUEUE;

                                // Then this passenger will not be stuck anymore
                                passengerMovement.free();

                                break;
                            }
                        }
                    case IN_NONQUEUEABLE:
                        // TODO: Like in IN_QUEUEABLE
                        break;
                    case IN_QUEUE:
                        if (action == PassengerMovement.Action.ASSEMBLING) {
                            // The passenger is not yet in the queueing area, but is already queueing
                            // So keep following the end of the queue until the queueing area is reached
                            // Make this passenger face the set goal, its queueing area, or the passenger at the tail of
                            // the queue
                            passengerMovement.faceNextPosition();

                            // Move towards that direction
                            passengerMovement.moveSocialForce();

                            if (passengerMovement.isReadyToFree()) {
                                // Then this passenger will not be stuck anymore
                                passengerMovement.free();
                            }

                            // Check whether the passenger has reached its floor field
                            // If the passenger has reached the patch with the nearest floor field value, transition
                            // into the "queueing" action
                            if (passengerMovement.hasReachedQueueingFloorField()) {
                                passengerMovement.setAction(PassengerMovement.Action.QUEUEING);
                                action = PassengerMovement.Action.QUEUEING;
                            }

                            // Check if this passenger has not encountered a queueing passenger anymore
                            if (!passengerMovement.hasEncounteredPassengerToFollow()) {
                                // If the passenger did not move, and there is someone blocking it while queueing,
                                // transition into the "in queue" state and the "assembling" action
                                passengerMovement.leaveQueue();

                                passengerMovement.setState(PassengerMovement.State.WALKING);
                                state = PassengerMovement.State.WALKING;

                                passengerMovement.setAction(PassengerMovement.Action.WILL_QUEUE);
                                action = PassengerMovement.Action.WILL_QUEUE;

                                break;
                            }
                        } else if (action == PassengerMovement.Action.QUEUEING) {
                            // The passenger is still queueing, so follow the path set by the floor field and its values
                            // Only move if the passenger is not waiting for the amenity to be vacant
                            if (!passengerMovement.isWaitingOnAmenity()) {
                                // In its neighboring patches, look for the patch with the highest floor field
                                passengerMovement.chooseBestQueueingPatch();

                                // Make this passenger face that patch
                                passengerMovement.faceNextPosition();

                                // Move towards that patch
                                passengerMovement.moveSocialForce();
                            }

                            if (passengerMovement.isReadyToFree()) {
                                // Then this passenger will not be stuck anymore
                                passengerMovement.free();
                            }

                            // Check if the passenger is on one of the current floor field's apices
                            // If not, keep following the floor field until it is reached
                            if (passengerMovement.hasReachedQueueingFloorFieldApex()) {
                                // Have the passenger waiting for the amenity to be vacant
                                // TODO: Add waiting for turn state
                                passengerMovement.beginWaitingOnAmenity();

                                // Check first if the goal of this passenger is not currently serving other passengers
                                // If it is, the passenger will now transition into the "heading to queueable" action
                                // Do nothing if there is another passenger still being serviced
                                if (passengerMovement.isGoalFree()/* && passengerMovement.isAtQueueFront()*/) {
                                    // The amenity is vacant, so no need to wait anymore
                                    passengerMovement.endWaitingOnAmenity();

                                    // Have the amenity mark this passenger as the one to be served next
                                    passengerMovement.beginServicingThisPassenger();

                                    passengerMovement.setAction(PassengerMovement.Action.HEADING_TO_QUEUEABLE);
                                    action = PassengerMovement.Action.HEADING_TO_QUEUEABLE;

                                    // Then this passenger will not be stuck anymore
                                    passengerMovement.free();
                                } else {
                                    // Just stop and wait
                                    passengerMovement.stop();
                                }
                            }
                        } else if (action == PassengerMovement.Action.HEADING_TO_QUEUEABLE) {
                            // Check if the passenger is now in the goal
                            if (passengerMovement.hasReachedGoal()) {
                                // Check if the passenger is in a pure goal (an amenity with waiting time variables)
                                if (passengerMovement.isNextAmenityGoal()) {
                                    // Transition into the "in queueable" state and the appropriate action
                                    passengerMovement.setState(PassengerMovement.State.IN_QUEUEABLE);
                                    state = PassengerMovement.State.IN_NONQUEUEABLE;

                                    if (passengerMovement.getGoalAmenity() instanceof Security) {
                                        passengerMovement.setAction(PassengerMovement.Action.SECURITY_CHECKING);
                                        action = PassengerMovement.Action.SECURITY_CHECKING;
                                    } else if (passengerMovement.getGoalAmenity() instanceof TicketBooth) {
                                        passengerMovement.setAction(PassengerMovement.Action.TRANSACTING_TICKET);
                                        action = PassengerMovement.Action.TRANSACTING_TICKET;
                                    } else if (passengerMovement.getGoalAmenity() instanceof Turnstile) {
                                        passengerMovement.setAction(PassengerMovement.Action.USING_TICKET);
                                        action = PassengerMovement.Action.USING_TICKET;
                                    }
                                } else {
                                    // Either the next goal is a train door, or an elevator
                                    if (passengerMovement.getGoalAmenity() instanceof TrainDoor) {
                                        // TODO: Wait until open train door button is set
                                        // Transition into the "in queueable" state and the appropriate action
                                        passengerMovement.setState(PassengerMovement.State.IN_QUEUEABLE);
                                        state = PassengerMovement.State.IN_NONQUEUEABLE;

                                        passengerMovement.setAction(PassengerMovement.Action.BOARDING_TRAIN);
                                        action = PassengerMovement.Action.BOARDING_TRAIN;
                                    } else if (passengerMovement.getGoalAmenity() instanceof ElevatorPortal) {
                                        // TODO: The next goal is an elevator, so change to the appropriate actions and
                                        // states
                                    }
                                }
                            } else {
                                // The passenger has exited its goal's floor field and is now headed to the goal itself
                                passengerMovement.chooseGoal();

                                // Make this passenger face the set goal, or its queueing area
                                passengerMovement.faceNextPosition();

                                // Then make the passenger move towards that goal
//                        passengerMovement.attemptMovement();
                                passengerMovement.moveSocialForce();
                            }
                        }
                    case IN_QUEUEABLE:
                        if (action == PassengerMovement.Action.BOARDING_TRAIN) {
                            // Check if the passenger is willing to enter the train doors
                            // If it is, despawn the passenger
                            // If not, wait for an additional second
                            if (passengerMovement.willEnterTrain()) {
                                // Have the passenger set its current goal
                                passengerMovement.reachGoal();

                                // Have this passenger's goal wrap up serving this passenger
                                passengerMovement.endServicingThisPassenger();

                                // Leave the queue
                                passengerMovement.leaveQueue();

                                // Then have this passenger marked for despawning
                                this.passengersToDespawn.add(passenger);
                            }
                        } else if (
                                action == PassengerMovement.Action.ASCENDING
                                        || action == PassengerMovement.Action.DESCENDING
                        ) {
                            // Have the passenger set its current goal
                            passengerMovement.reachGoal();

                            // Leave the queue
                            passengerMovement.leaveQueue();
                        } else if (
                                action == PassengerMovement.Action.SECURITY_CHECKING
                                        || action == PassengerMovement.Action.TRANSACTING_TICKET
                                        || action == PassengerMovement.Action.USING_TICKET
                        ) {
                            // Have the passenger set its current goal
                            passengerMovement.reachGoal();

                            // Check if the passenger is allowed passage by the goal
                            // If it is, proceed to the next state
                            // If not, wait for an additional second
                            if (passengerMovement.isAllowedPass()) {
                                // Have this passenger's goal wrap up serving this passenger
                                passengerMovement.endServicingThisPassenger();

                                // Leave the queue
                                passengerMovement.leaveQueue();

                                // Move forward and go looking for the
                                // next one
                                passengerMovement.getRoutePlan().setNextAmenityClass();

                                // Reset the current goal of the passenger
                                // The passenger is set to step forward initially if the passenger is coming from a
                                // security entrance or a ticket booth
                                passengerMovement.resetGoal(
                                        action == PassengerMovement.Action.SECURITY_CHECKING
                                                || action == PassengerMovement.Action.USING_TICKET
                                );

                                // Transition back into the "walking" state, and the "will queue" action
                                passengerMovement.setState(PassengerMovement.State.WALKING);
                                state = PassengerMovement.State.WALKING;

                                passengerMovement.setAction(PassengerMovement.Action.WILL_QUEUE);
                                action = PassengerMovement.Action.WILL_QUEUE;
                            } else {
                                // Just stop and wait
                                passengerMovement.stop();
                            }
                        }

                        break;
                }

                break;
            case RIDING_TRAIN:
                // The passenger is riding the train
                switch (state) {
                    case IN_TRAIN:
                        break;
                }

                break;
            case ALIGHTING:
                // The passenger has alighted the train and is heading for the station exit
                switch (state) {
                    case WALKING:
                        break;
                    case IN_QUEUEABLE:
                        break;
                    case IN_QUEUE:
                        break;
                    case IN_NONQUEUEABLE:
                        break;
                }

                break;
        }
    }

    // Spawn a passenger from a gate in the given floor
    private void spawnPassenger(Floor floor, Gate gate) {
        // Generate the passenger
        Passenger passenger = gate.spawnPassenger();

        if (passenger != null) {
            // Add the newly created passenger to the list of passengers in the floor, station, and simulation
            floor.getStation().getPassengersInStation().add(passenger);
            floor.getPassengersInFloor().add(passenger);

            // Add the passenger's patch position to its current floor's patch set as well
            floor.getPassengerPatchSet().add(
                    passenger.getPassengerMovement().getCurrentPatch()
            );
        }
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

        BuildState(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
