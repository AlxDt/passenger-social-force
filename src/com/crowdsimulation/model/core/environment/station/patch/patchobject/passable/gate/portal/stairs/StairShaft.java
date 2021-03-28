package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.PortalShaft;

public class StairShaft extends PortalShaft {
    public StairShaft(Patch patch, boolean enabled, int moveTime) {
        super(patch, enabled, moveTime);
    }

    // Stair shaft factory
    public static class StairShaftFactory extends AmenityFactory {
        @Override
        public StairShaft create(Object... objects) {
            return new StairShaft(
                    (Patch) objects[0],
                    (boolean) objects[1],
                    (int) objects[2]
            );
        }
    }
}
