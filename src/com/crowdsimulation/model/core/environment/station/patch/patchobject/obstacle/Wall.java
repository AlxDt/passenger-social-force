package com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle;

import com.crowdsimulation.model.core.environment.station.patch.Patch;

public class Wall extends Obstacle {
    public Wall(Patch patch) {
        super(patch);
    }

    @Override
    public String toString() {
        return "Wall";
    }
}
