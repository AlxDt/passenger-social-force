package com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;

public abstract class Obstacle extends Amenity {
    protected Obstacle(Patch patch) {
        super(patch);
    }

    // Obstacle factory template
    public static abstract class ObstacleFactory extends AmenityFactory {

    }
}
