package com.crowdsimulation.model.core.environment.station.patch;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.agent.passenger.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.BaseStation;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.FloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.PatchObject;
import com.crowdsimulation.model.core.environment.station.utility.Coordinates;
import com.crowdsimulation.model.core.environment.station.utility.MatrixPosition;

import java.util.*;

public class Patch extends BaseStation {
    // TODO: Make entry waiting time adjustable based on UI sliders
    public static final int ENTRY_WAITING_TIME = 10;

    // Denotes the position of this patch based on a discrete row x column matrix
    private final MatrixPosition matrixPosition;

    // Denotes the center of this patch in a Cartesian pixel coordinate system
    private final Coordinates patchCenterCoordinates;

    // Denotes the list of passengers that are currently on this patch
    private final List<Passenger> passengers;

    ////

    // Denotes the amenity present on this patch
    private PatchObject patchObject;

    // Denotes layers of floor fields that this patch has
    // Each layer corresponds to a goal
    private final Map<PassengerMovement.State, FloorField> floorFieldValues;

    private boolean obstacle;

    private ArrayDeque<Passenger> passengersQueueing;
    private Passenger passengerTransacting;
    private List<Patch> associatedPatches;
    private String goalId;
    private Type type;
    private int waitingTime;
    private final Integer sequence;
    private final Integer index;

    public Patch(MatrixPosition matrixPosition, Type type) {
        this.matrixPosition = matrixPosition;
        this.type = type;
        this.passengers = new ArrayList<>();

        this.passengersQueueing = null;
        this.passengerTransacting = null;
        this.associatedPatches = null;
        this.goalId = null;

        this.floorFieldValues = new HashMap<>();

        // TODO: Add all available statuses to all floor fields, not just queueing
        this.floorFieldValues.put(PassengerMovement.State.IN_QUEUE, new FloorField());

        this.obstacle = false;

        this.waitingTime = 0;

        this.patchCenterCoordinates = Coordinates.patchCenterCoordinates(this);

        this.sequence = null;
        this.index = null;
    }

    public MatrixPosition getMatrixPosition() {
        return matrixPosition;
    }

    public Coordinates getPatchCenterCoordinates() {
        return patchCenterCoordinates;
    }

    public boolean isObstacle() {
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
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public ArrayDeque<Passenger> getPassengersQueueing() {
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
    }

    public PatchObject getPatchObject() {
        return patchObject;
    }

    public void setPatchObject(PatchObject patchObject) {
        this.patchObject = patchObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patch patch = (Patch) o;
        return matrixPosition.equals(patch.matrixPosition) &&
                type == patch.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(matrixPosition, type);
    }

    public enum Type {
        CLEAR,
        SPAWN,
        SECURITY_ENTRANCE,
        TICKET_BOOTH,
        TURNSTILE,
        TRANSACTION_AREA,
        DESPAWN,
        OBSTACLE;
    }
}
