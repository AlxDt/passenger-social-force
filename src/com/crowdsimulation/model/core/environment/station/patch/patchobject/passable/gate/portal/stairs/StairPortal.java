package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs;

import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;

public class StairPortal extends Portal {
    // Denotes the stair shaft which contains this stair portal
    private final StairShaft stairShaft;

    public StairPortal(
            Patch patch,
            boolean enabled,
            Floor floorServed,
            StairShaft stairShaft) {
        super(patch, enabled, floorServed);

        this.stairShaft = stairShaft;
    }

    public StairShaft getStairShaft() {
        return stairShaft;
    }

    // Stair portal factory
    public static class StairPortalFactory extends AmenityFactory {
        @Override
        public StairPortal create(Object... objects) {
            return new StairPortal(
                    (Patch) objects[0],
                    (boolean) objects[1],
                    (Floor) objects[2],
                    (StairShaft) objects[3]
            );
        }
    }

    @Override
    public String toString() {
        return "Stair portal";
    }
}
