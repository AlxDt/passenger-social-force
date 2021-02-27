package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal;

import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.Queueable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;

public class Elevator extends Portal implements Queueable {
    // Denotes the pair of this portal (where it is connected to)
    private final Elevator pair;

    // Denotes the time this elevator's doors are open to let passengers in and out
    private int doorOpenTime;

    // Denotes the time needed for this elevator to move to another floor
    private int moveTime;

    // The floor this elevator is currently at
    private Floor currentFloor;

    public Elevator() {
        super(null,false, null, null, null);

        this.pair = null;
        this.doorOpenTime = 10;
        this.moveTime = 10;
        this.currentFloor = null;
    }

    public Elevator(
            Patch patch,
            boolean enabled,
            Floor lowerFloor,
            Floor upperFloor,
            PortalLocation portalLocation,
            Elevator pair,
            int doorOpenTime,
            int moveTime,
            Floor currentFloor
    ) {
        super(patch, enabled, lowerFloor, upperFloor, portalLocation);

        this.pair = pair;
        this.doorOpenTime = doorOpenTime;
        this.moveTime = moveTime;
        this.currentFloor = currentFloor;
    }

    public Elevator getPair() {
        return pair;
    }

    public int getDoorOpenTime() {
        return doorOpenTime;
    }

    public void setDoorOpenTime(int doorOpenTime) {
        this.doorOpenTime = doorOpenTime;
    }

    public int getMoveTime() {
        return moveTime;
    }

    public void setMoveTime(int moveTime) {
        this.moveTime = moveTime;
    }

    public Floor getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(Floor currentFloor) {
        this.currentFloor = currentFloor;
    }

    @Override
    public String toString() {
        return "Elevator";
    }

    public enum ElevatorState {
        BOARDING,
        MOVING
    }
}
