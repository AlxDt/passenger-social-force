package com.crowdsimulation.model.core.environment.station.patch.patchobject;

import com.crowdsimulation.model.core.environment.station.BaseStationObject;
import com.crowdsimulation.model.core.environment.station.patch.Patch;

public abstract class Amenity extends PatchObject {
    // Denotes the patch which contains this amenity
    private final Patch patch;

    public Amenity(Patch patch) {
        this.patch = patch;
    }

    public Patch getPatch() {
        return patch;
    }

    // Create an amenity
    public abstract static class AmenityFactory extends BaseStationObject.StationObjectFactory {
        @Override
        public abstract Amenity create(Object... objects);
    }
}
