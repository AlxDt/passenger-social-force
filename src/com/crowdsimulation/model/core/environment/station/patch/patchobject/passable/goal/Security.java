package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
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
        public Amenity createAmenity(Patch patch, Object... objects) {
            return new Security(
                    patch,
                    (boolean) objects[0],
                    (int) objects[1],
                    (boolean) objects[2]
            );
        }
    }

    @Override
    public String toString() {
        return "Security";
    }
}
