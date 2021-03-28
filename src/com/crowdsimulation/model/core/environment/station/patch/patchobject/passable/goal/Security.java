package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.BlockableAmenity;

public class Security extends BlockableAmenity {
    // Denotes whether to block passengers from passing through
    private boolean blockEntry;

    public Security(
            Patch patch,
            boolean enabled,
            int waitingTime,
            boolean blockPassengers
    ) {
        super(patch, enabled, waitingTime, blockPassengers);
    }

    public boolean isBlockEntry() {
        return blockEntry;
    }

    public void setBlockEntry(boolean blockEntry) {
        this.blockEntry = blockEntry;
    }

    // Security factory
    public static class SecurityFactory extends AmenityFactory {
        @Override
        public Security create(Object... objects) {
            return new Security(
                    (Patch) objects[0],
                    (boolean) objects[1],
                    (int) objects[2],
                    (boolean) objects[3]
            );
        }
    }

    @Override
    public String toString() {
        return "Security";
    }
}
