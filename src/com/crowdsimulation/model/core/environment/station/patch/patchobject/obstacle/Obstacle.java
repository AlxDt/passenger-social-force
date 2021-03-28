package com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;

public abstract class Obstacle extends Amenity {
    public Obstacle(Patch patch) {
        super(patch);
    }
}