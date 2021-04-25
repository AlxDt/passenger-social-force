package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.NonObstacle;

public abstract class Gate extends NonObstacle implements Drawable {
    protected Gate(Patch patch, boolean enabled) {
        super(patch, enabled);
    }

    // Spawn a passenger in this position
    public Passenger spawnPassenger() {
        return Passenger.passengerFactory.create(this);
    }

    // Gate factory
    public static abstract class GateFactory extends AmenityFactory {
    }
}
