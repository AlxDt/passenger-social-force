package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Patch {
    public static final int ENTRY_WAITING_TIME = 20;

    private final MatrixPosition matrixPosition;
    private final Coordinate patchCenterCoordinates;
    private Status status;
    private final List<Passenger> passengers;
    private int waitingTime;

    public Patch(MatrixPosition matrixPosition, Status status) {
        this.matrixPosition = matrixPosition;
        this.status = status;
        this.passengers = new ArrayList<>();

        this.waitingTime = 0;

        this.patchCenterCoordinates = Coordinate.patchCenterCoordinates(this);
    }

    public MatrixPosition getMatrixPosition() {
        return matrixPosition;
    }

    public Coordinate getPatchCenterCoordinates() {
        return patchCenterCoordinates;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patch patch = (Patch) o;
        return matrixPosition.equals(patch.matrixPosition) &&
                status == patch.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(matrixPosition, status);
    }

    public enum Status {
        //        OBSTACLE,
//        CLEAR,
//        START,
//        GOAL
        CLEAR,
        START,
        WAYPOINT,
        GATE,
        EXIT,
        OBSTACLE
    }
}
