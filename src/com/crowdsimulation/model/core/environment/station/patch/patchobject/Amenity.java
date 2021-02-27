package com.crowdsimulation.model.core.environment.station.patch.patchobject;

import com.crowdsimulation.model.core.environment.station.patch.Patch;

public class Amenity extends PatchObject {
    // Denotes the patch which contains this amenity
    private final Patch patch;

    public Amenity(Patch patch) {
        this.patch = patch;
    }

    public Patch getPatch() {
        return patch;
    }
}
