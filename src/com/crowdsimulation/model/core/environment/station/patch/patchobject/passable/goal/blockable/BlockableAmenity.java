package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.Goal;

import java.util.List;

public abstract class BlockableAmenity extends Goal {
    // Denotes whether passengers are able to pass through this amenity
    private boolean blockEntry;

    public BlockableAmenity(
            List<AmenityBlock> amenityBlocks,
            boolean enabled,
            int waitingTime,
            QueueObject queueObject,
            boolean blockEntry) {
        super(amenityBlocks, enabled, waitingTime, queueObject);

        this.blockEntry = blockEntry;
    }

    public boolean isBlockEntry() {
        return blockEntry;
    }

    public void setBlockEntry(boolean blockEntry) {
        this.blockEntry = blockEntry;
    }
}
