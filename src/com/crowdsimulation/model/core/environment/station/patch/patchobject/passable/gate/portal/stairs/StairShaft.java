package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs;

import com.crowdsimulation.controller.graphics.amenity.editor.StairEditor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.PortalShaft;

public class StairShaft extends PortalShaft {
    protected StairShaft(boolean enabled, int moveTime) {
        super(null, enabled, moveTime);
    }

    // Factory for stair shaft creation
    public static final StairShaftFactory stairShaftFactory;

    // Denotes the editor of this amenity
    public static final StairEditor stairEditor;

    static {
        stairShaftFactory = new StairShaftFactory();

        // Initialize the editor
        stairEditor = new StairEditor();
    }

    // Stair shaft factory
    public static class StairShaftFactory extends PortalShaftFactory {
        public StairShaft create(boolean enabled, int moveTime) {
            return new StairShaft(
                    enabled,
                    moveTime
            );
        }
    }
}
