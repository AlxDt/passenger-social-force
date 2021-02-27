package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal;

import com.crowdsimulation.model.core.environment.station.patch.Patch;

public class Security extends Goal {
    // Denotes whether to block passengers from passing through
    private boolean blockEntry;

    public Security() {
        super(null,true, 0);

        this.blockEntry = false;
    }

    public Security(
            Patch patch,
            boolean enabled,
            int waitingTime,
            boolean blockPassengers
    ) {
        super(patch, enabled, waitingTime);

        this.blockEntry = blockPassengers;
    }

    public boolean isBlockEntry() {
        return blockEntry;
    }

    public void setBlockEntry(boolean blockEntry) {
        this.blockEntry = blockEntry;
    }

    @Override
    public String toString() {
        return "Security";
    }
}
