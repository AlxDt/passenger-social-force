package com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle;

import com.crowdsimulation.model.core.environment.station.patch.Patch;

public class Wall extends Obstacle {
    public Wall(Patch patch) {
        super(patch);
    }

    // Wall factory
    public static class WallFactory extends AmenityFactory {
        @Override
        public Wall create(Object... objects) {
            return new Wall(
                    (Patch) objects[0]
            );
        }
    }

    @Override
    public String toString() {
        return "Wall";
    }
}
