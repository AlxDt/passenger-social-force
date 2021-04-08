package com.crowdsimulation.model.core.environment.station.patch;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.BaseStationObject;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;
import com.crowdsimulation.model.core.environment.station.utility.Coordinates;
import com.crowdsimulation.model.core.environment.station.utility.MatrixPosition;

import java.util.*;

public class Patch extends BaseStationObject {
/*    // TODO: Make entry waiting time adjustable based on UI sliders
    public static final int ENTRY_WAITING_TIME = 10;*/

    // Denotes the position of this patch based on a discrete row x column matrix
    private final MatrixPosition matrixPosition;

    // Denotes the center of this patch in a Cartesian pixel coordinate system
    private final Coordinates patchCenterCoordinates;

    // Denotes the list of passengers that are currently on this patch
    private final List<Passenger> passengers;

    // Denotes the amenity present on this patch
    private Amenity amenity;

    // Denotes the individual floor field value of this patch, given the queueable goal patch and the desired state
    private final Map<Queueable, Map<QueueingFloorField.FloorFieldState, Double>> floorFieldValues;

/*    private boolean obstacle;

    private ArrayDeque<Passenger> passengersQueueing;
    private Passenger passengerTransacting;
    private List<Patch> associatedPatches;
    private String goalId;
    private Type type;
    private int waitingTime;
    private final Integer sequence;
    private final Integer index;*/

    public Patch(MatrixPosition matrixPosition/*, Type type*/) {
        super();

        this.matrixPosition = matrixPosition;
        this.patchCenterCoordinates = Coordinates.patchCenterCoordinates(this);

        this.passengers = new ArrayList<>();
        this.amenity = null;
        this.floorFieldValues = new HashMap<>();

/*        this.matrixPosition = matrixPosition;
        this.type = type;
        this.passengers = new ArrayList<>();

        this.passengersQueueing = null;
        this.passengerTransacting = null;
        this.associatedPatches = null;
        this.goalId = null;

        this.floorFieldValues = new HashMap<>();

        this.floorFieldValues.put(PassengerMovement.State.IN_QUEUE, new FloorField());

        this.obstacle = false;

        this.waitingTime = 0;

        this.patchCenterCoordinates = Coordinates.patchCenterCoordinates(this);

        this.sequence = null;
        this.index = null;*/
    }

    public MatrixPosition getMatrixPosition() {
        return matrixPosition;
    }

    public Coordinates getPatchCenterCoordinates() {
        return patchCenterCoordinates;
    }

/*    public boolean isObstacle() {
        return obstacle;
    }

    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type, int sequence, int index) {
        this.type = type;

        if (type == Type.TRANSACTION_AREA || type == Type.DESPAWN) {
            assert index != -1;

            this.passengersQueueing = new ArrayDeque<>();
            this.associatedPatches = new ArrayList<>();
            this.goalId = (type == Type.TRANSACTION_AREA ? "T" : "E") + sequence + "-" + index;
        } else if (type == Type.TICKET_BOOTH || type == Type.OBSTACLE) {
            this.obstacle = true;
        }
    }*/

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public Map<Queueable, Map<QueueingFloorField.FloorFieldState, Double>> getFloorFieldValues() {
        return floorFieldValues;
    }

    /*    public ArrayDeque<Passenger> getPassengersQueueing() {
        return passengersQueueing;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public Passenger getPassengerTransacting() {
        return passengerTransacting;
    }

    public void setPassengerTransacting(Passenger passengerTransacting) {
        this.passengerTransacting = passengerTransacting;
    }

    public String getGoalId() {
        return goalId;
    }

    public Map<PassengerMovement.State, FloorField> getFloorFieldValues() {
        return floorFieldValues;
    }

    public List<Patch> getAssociatedPatches() {
        return associatedPatches;
    }

    public Integer getSequence() {
        return sequence;
    }

    public Integer getIndex() {
        return index;
    }*/

    public Amenity getAmenity() {
        return amenity;
    }

    public void setAmenity(Amenity amenity) {
        this.amenity = amenity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patch patch = (Patch) o;
        return matrixPosition.equals(patch.matrixPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matrixPosition);
    }
/*    public enum Type {
        CLEAR,
        SPAWN,
        SECURITY_ENTRANCE,
        TICKET_BOOTH,
        TURNSTILE,
        TRANSACTION_AREA,
        DESPAWN,
        OBSTACLE;
    }*/
}
