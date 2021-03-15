package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.Goal;

public abstract class BlockableAmenity extends Goal {
    // Denotes whether passengers are able to pass through this amenity
    private boolean blockEntry;

    public BlockableAmenity(Patch patch, boolean enabled, int waitingTime, boolean blockEntry) {
        super(patch, enabled, waitingTime);

        this.blockEntry = blockEntry;
    }

    public boolean isBlockEntry() {
        return blockEntry;
    }

    public void setBlockEntry(boolean blockEntry) {
        this.blockEntry = blockEntry;
    }
}
