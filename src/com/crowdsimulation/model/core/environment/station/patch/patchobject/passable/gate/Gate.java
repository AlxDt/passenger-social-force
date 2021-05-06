package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.NonObstacle;

import java.util.List;

public abstract class Gate extends NonObstacle implements Drawable {
    protected Gate(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);
    }

    // Spawn a passenger in this position
    public Passenger spawnPassenger() {
        return Passenger.passengerFactory.create(this);
    }

    // Gate factory
    public static abstract class GateFactory extends NonObstacleFactory {
    }
}
