package sample;

import java.util.*;

public class Patch {
    public static final int ENTRY_WAITING_TIME = 20;

    private final MatrixPosition matrixPosition;
    private final Coordinates patchCenterCoordinates;
    private final List<Passenger> passengers;
    private final Map<PassengerMovement.State, FloorField> floorFieldValues;
    private ArrayDeque<Passenger> passengersQueueing;
    private List<Patch> associatedPatches;
    private String goalId;
    private Type type;
    private int waitingTime;

    public Patch(MatrixPosition matrixPosition, Type type) {
        this.matrixPosition = matrixPosition;
        this.type = type;
        this.passengers = new ArrayList<>();
//        this.passengersQueueing = (type == Type.GATE || type == Type.EXIT) ? new ArrayDeque<>() : null;

        this.passengersQueueing = null;
        this.associatedPatches = null;
        this.goalId = null;

        this.floorFieldValues = new HashMap<>();

        // TODO: Add all available statuses to all floor fields, not just queueing
        this.floorFieldValues.put(PassengerMovement.State.QUEUEING, new FloorField());

        this.waitingTime = 0;

        this.patchCenterCoordinates = Coordinates.patchCenterCoordinates(this);
    }

    public MatrixPosition getMatrixPosition() {
        return matrixPosition;
    }

    public Coordinates getPatchCenterCoordinates() {
        return patchCenterCoordinates;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type, int sequence, int index) {
        this.type = type;

        if (type == Type.GATE || type == Type.EXIT) {
            assert index != -1;

            this.passengersQueueing = new ArrayDeque<>();
            this.associatedPatches = new ArrayList<>();
            this.goalId = (type == Type.GATE ? "G" : "E") + sequence + "-" + index;
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

    public String getGoalId() {
        return goalId;
    }

    public Map<PassengerMovement.State, FloorField> getFloorFieldValues() {
        return floorFieldValues;
    }

    public List<Patch> getAssociatedPatches() {
        return associatedPatches;
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
        START,
        //        WAYPOINT,
        GATE,
        EXIT,
        OBSTACLE
    }
}
