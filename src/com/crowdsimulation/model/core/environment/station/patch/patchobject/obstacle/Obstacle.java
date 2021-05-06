package com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;

import java.util.List;

public abstract class Obstacle extends Amenity {
    protected Obstacle(List<AmenityBlock> amenityBlocks) {
        super(amenityBlocks);
    }

    // Obstacle factory template
    public static abstract class ObstacleFactory extends AmenityFactory {
    }
}
