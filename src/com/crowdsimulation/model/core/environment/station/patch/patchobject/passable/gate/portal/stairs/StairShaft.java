package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.PortalShaft;

public class StairShaft extends PortalShaft {
    protected StairShaft(Patch patch, boolean enabled, int moveTime) {
        super(patch, enabled, moveTime);
    }

    // Factory for stair shaft creation
    public static final StairShaftFactory stairShaftFactory;

    static {
        stairShaftFactory = new StairShaftFactory();
    }

    // Stair shaft factory
    public static class StairShaftFactory extends PortalShaftFactory {
        public StairShaft create(Patch patch, boolean enabled, int moveTime) {
            return new StairShaft(
                    patch,
                    enabled,
                    moveTime
            );
        }
    }
}
