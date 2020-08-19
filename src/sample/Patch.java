package sample;

import java.util.Objects;

public class Patch {
    private final MatrixPosition matrixPosition;
    private Status status;
    private Passenger passenger;

    public Patch(MatrixPosition matrixPosition, Status status) {
        this.matrixPosition = matrixPosition;
        this.status = status;
        this.passenger = null;
    }

    public MatrixPosition getMatrixPosition() {
        return matrixPosition;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
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
