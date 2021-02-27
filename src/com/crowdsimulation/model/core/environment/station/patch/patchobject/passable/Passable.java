package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;

public abstract class Passable extends Amenity {
    // Denotes whether this passable patch object is enabled or not (passengers cannot pass through it)
    private boolean enabled;

    public Passable(Patch patch, boolean enabled) {
        super(patch);

        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
