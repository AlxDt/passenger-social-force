package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.NonObstacle;

import java.util.List;

public abstract class Gate extends NonObstacle implements Drawable {
    protected Gate(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);
    }

    // Spawn a passenger in this position
    public abstract Passenger spawnPassenger();

    // Despawn a passenger in this position
    public void despawnPassenger(Passenger passenger) {
        passenger.getPassengerMovement().despawnPassenger();
    }

    // Gate factory
    public static abstract class GateFactory extends NonObstacleFactory {
    }
}
