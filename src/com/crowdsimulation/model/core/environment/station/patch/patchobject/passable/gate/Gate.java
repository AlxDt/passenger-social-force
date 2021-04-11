package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.NonObstacle;

public abstract class Gate extends NonObstacle {
    protected Gate(Patch patch, boolean enabled) {
        super(patch, enabled);
    }

    // Gate factory
    public static abstract class GateFactory extends AmenityFactory {
    }
}
