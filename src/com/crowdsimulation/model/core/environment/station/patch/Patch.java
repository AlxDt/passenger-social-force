package com.crowdsimulation.model.core.environment.station.patch;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.Environment;
import com.crowdsimulation.model.core.environment.station.BaseStationObject;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;
import com.crowdsimulation.model.core.environment.station.patch.position.Coordinates;
import com.crowdsimulation.model.core.environment.station.patch.position.MatrixPosition;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Patch extends BaseStationObject implements Environment, Comparable<Patch> {
    // Denotes the size of the path in square meters
    public static final double PATCH_SIZE_IN_SQUARE_METERS = 0.6;

    // Denotes the position of this patch based on a discrete row x column matrix
    private final MatrixPosition matrixPosition;

    // Denotes the center of this patch in a Cartesian pixel coordinate system
    private final Coordinates patchCenterCoordinates;

    // Denotes the list of passengers that are currently on this patch
    private final CopyOnWriteArrayList<Passenger> passengers;

    // Denotes the amenity block present on this patch
    private Amenity.AmenityBlock amenityBlock;

    // Denotes the floor which contains this patch
    private final Floor floor;

    // Denotes the positions of the neighbors of this patch (because the serializer can't handle references to the
    // patch itself)
    private final List<MatrixPosition> neighborIndices;

    // Denotes the individual floor field value of this patch, given the queueable goal patch and the desired state
    private final Map<Queueable, Map<QueueingFloorField.FloorFieldState, Double>> floorFieldValues;

    public Patch(Floor floor, MatrixPosition matrixPosition) {
        super();

        this.matrixPosition = matrixPosition;
        this.patchCenterCoordinates = Coordinates.getPatchCenterCoordinates(this);

        this.passengers = new CopyOnWriteArrayList<>();
        this.amenityBlock = null;
        this.floor = floor;

        this.neighborIndices = this.computeNeighboringPatches();

        this.floorFieldValues = new HashMap<>();
    }

    public MatrixPosition getMatrixPosition() {
        return matrixPosition;
    }

    public Coordinates getPatchCenterCoordinates() {
        return patchCenterCoordinates;
    }

    public CopyOnWriteArrayList<Passenger> getPassengers() {
        return passengers;
    }

    public Map<Queueable, Map<QueueingFloorField.FloorFieldState, Double>> getFloorFieldValues() {
        return floorFieldValues;
    }

    public Amenity.AmenityBlock getAmenityBlock() {
        return amenityBlock;
    }

    public void setAmenityBlock(Amenity.AmenityBlock amenityBlock) {
        this.amenityBlock = amenityBlock;
    }

    public Floor getFloor() {
        return floor;
    }

    private List<MatrixPosition> computeNeighboringPatches() {
        int patchRow = this.matrixPosition.getRow();
        int patchColumn = this.matrixPosition.getColumn();

        List<MatrixPosition> neighboringPatchIndices = new ArrayList<>();

        if (patchRow - 1 >= 0 && patchColumn - 1 >= 0) {
            neighboringPatchIndices.add(new MatrixPosition(patchRow - 1, patchColumn - 1));
        }

        if (patchRow - 1 >= 0) {
            neighboringPatchIndices.add(new MatrixPosition(patchRow - 1, patchColumn));
        }

        if (patchRow - 1 >= 0 && patchColumn + 1 < this.getFloor().getColumns()) {
            neighboringPatchIndices.add(new MatrixPosition(patchRow - 1, patchColumn + 1));
        }

        if (patchColumn - 1 >= 0) {
            neighboringPatchIndices.add(new MatrixPosition(patchRow, patchColumn - 1));
        }

        if (patchColumn + 1 < this.getFloor().getColumns()) {
            neighboringPatchIndices.add(new MatrixPosition(patchRow, patchColumn + 1));
        }

        if (patchRow + 1 < this.getFloor().getRows() && patchColumn - 1 >= 0) {
            neighboringPatchIndices.add(new MatrixPosition(patchRow + 1, patchColumn - 1));
        }

        if (patchRow + 1 < this.getFloor().getRows()) {
            neighboringPatchIndices.add(new MatrixPosition(patchRow + 1, patchColumn));
        }

        if (patchRow + 1 < this.getFloor().getRows() && patchColumn + 1 < this.getFloor().getColumns()) {
            neighboringPatchIndices.add(new MatrixPosition(patchRow + 1, patchColumn + 1));
        }

        return neighboringPatchIndices;
    }

    public List<Patch> getNeighbors() {
        List<Patch> neighboringPatches = new ArrayList<>();

        for (MatrixPosition neighboringPatchIndex : this.neighborIndices) {
            neighboringPatches.add(
                    this.getFloor().getPatch(neighboringPatchIndex.getRow(), neighboringPatchIndex.getColumn())
            );
        }

        return neighboringPatches;
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

    @Override
    public int compareTo(Patch patch) {
        int thisRow = this.getMatrixPosition().getRow();
        int patchRow = patch.getMatrixPosition().getRow();

        int thisColumn = this.getMatrixPosition().getColumn();
        int patchColumn = patch.getMatrixPosition().getColumn();

        if (thisRow > patchRow) {
            return 1;
        } else if (thisRow == patchRow) {
            return Integer.compare(thisColumn, patchColumn);
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "[" + this.getMatrixPosition().getRow() + ", " + this.getMatrixPosition().getColumn() + "]";
    }
}
