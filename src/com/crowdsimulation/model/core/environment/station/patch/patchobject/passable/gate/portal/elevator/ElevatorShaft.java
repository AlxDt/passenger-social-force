package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator;

import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.PortalShaft;

public class ElevatorShaft extends PortalShaft {
    // Denotes the time it takes between the elevator reaching its destination and the elevator having its doors fully
    // opened
    private int openDelayTime;

    // Denotes the time this portal's elevator doors are open to let passengers in and out
    private int doorOpenTime;

    // Denotes the floor the elevator is currently at
    private Floor currentFloor;

    // Denotes whether the elevator is boarding passengers or moving between floors
    private ElevatorState elevatorState;

    // Denotes the direction the elevator is going
    private ElevatorDirection elevatorDirection;

    public ElevatorShaft(
            Patch patch,
            boolean enabled,
            int moveTime,
            int openDelayTime,
            int doorOpenTime,
            ElevatorDirection initialElevatorDirection) {
        super(patch, enabled, moveTime);

        this.openDelayTime = openDelayTime;
        this.doorOpenTime = doorOpenTime;
        this.elevatorState = ElevatorState.IDLE;
        this.elevatorDirection = initialElevatorDirection;
    }

    public int getOpenDelayTime() {
        return openDelayTime;
    }

    public int getDoorOpenTime() {
        return doorOpenTime;
    }

    public void setOpenDelayTime(int openDelayTime) {
        this.openDelayTime = openDelayTime;
    }

    public void setDoorOpenTime(int doorOpenTime) {
        this.doorOpenTime = doorOpenTime;
    }

    public Floor getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(Floor currentFloor) {
        this.currentFloor = currentFloor;
    }

    public ElevatorState getElevatorState() {
        return elevatorState;
    }

    public void setElevatorState(ElevatorState elevatorState) {
        this.elevatorState = elevatorState;
    }

    public ElevatorDirection getElevatorDirection() {
        return elevatorDirection;
    }

    public void setElevatorDirection(ElevatorDirection elevatorDirection) {
        this.elevatorDirection = elevatorDirection;
    }

    // Elevator shaft factory
    public static class ElevatorShaftFactory extends AmenityFactory {
        @Override
        public ElevatorShaft create(Object... objects) {
            return new ElevatorShaft(
                    (Patch) objects[0],
                    (boolean) objects[1],
                    (int) objects[2],
                    (int) objects[3],
                    (int) objects[4],
                    (ElevatorDirection) objects[5]
            );
        }
    }

    // Denotes the current state of the elevator
    public enum ElevatorState {
        BOARDING, // The elevator is open and allowing passengers to move in and out of its cab
        MOVING, // The elevator is closed and in transit from one floor to another
        IDLE // The elevator is closed, but is not moving as there are no passengers
    }

    // Denotes the next direction of the elevator
    public enum ElevatorDirection {
        UP("Bottom (to go up)"), // The elevator is about to move up
        DOWN("Top (to go down)"); // The elevator is above to move down

        private final String name;

        private ElevatorDirection(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
