package com.crowdsimulation.model.core.environment.station.patch;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.Environment;
import com.crowdsimulation.model.core.environment.station.BaseStationObject;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;
import com.crowdsimulation.model.core.environment.station.patch.location.Coordinates;
import com.crowdsimulation.model.core.environment.station.patch.location.MatrixPosition;

import java.util.*;

public class Patch extends BaseStationObject implements Environment {
    // Denotes the position of this patch based on a discrete row x column matrix
    private final MatrixPosition matrixPosition;

    // Denotes the center of this patch in a Cartesian pixel coordinate system
    private final Coordinates patchCenterCoordinates;

    // Denotes the list of passengers that are currently on this patch
    private final List<Passenger> passengers;

    // Denotes the amenity block present on this patch
    private Amenity.AmenityBlock amenityBlock;

    // Denotes the floor which contains this patch
    private final Floor floor;

    // Denotes the individual floor field value of this patch, given the queueable goal patch and the desired state
    private final Map<Queueable, Map<QueueingFloorField.FloorFieldState, Double>> floorFieldValues;

    public Patch(Floor floor, MatrixPosition matrixPosition) {
        super();

        this.matrixPosition = matrixPosition;
        this.patchCenterCoordinates = Coordinates.patchCenterCoordinates(this);

        this.passengers = new ArrayList<>();
        this.amenityBlock = null;
        this.floor = floor;

        this.floorFieldValues = new HashMap<>();
    }

    public MatrixPosition getMatrixPosition() {
        return matrixPosition;
    }

    public Coordinates getPatchCenterCoordinates() {
        return patchCenterCoordinates;
    }

    public List<Passenger> getPassengers() {
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
}
