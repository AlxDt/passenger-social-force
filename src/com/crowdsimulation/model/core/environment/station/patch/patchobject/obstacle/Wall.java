package com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle;

import com.crowdsimulation.model.core.environment.station.patch.Patch;

public class Wall extends Obstacle {
    // Factory for wall creation
    public static final WallFactory wallFactory;

    static {
        wallFactory = new WallFactory();
    }

    protected Wall(Patch patch) {
        super(patch);
    }

    // Wall factory
    public static class WallFactory extends ObstacleFactory {
        public Wall create(Patch patch) {
            return new Wall(
                    patch
            );
        }
    }

    @Override
    public String toString() {
        return "Wall";
    }
}
