package com.crowdsimulation.model.core.environment.station.patch.patchobject;

import com.crowdsimulation.model.core.environment.Environment;
import com.crowdsimulation.model.core.environment.station.BaseStationObject;
import com.crowdsimulation.model.core.environment.station.patch.Patch;

public abstract class Amenity extends PatchObject implements Environment {
    // Denotes the patch which contains this amenity
    private final Patch patch;

    protected Amenity(Patch patch) {
        this.patch = patch;
    }

    public Patch getPatch() {
        return patch;
    }

    // Template class for amenity factories
    public static class AmenityFactory extends BaseStationObject.StationObjectFactory {
    }
}
