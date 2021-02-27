package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal;

import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;

public class StairPortal extends Portal {
    // Denotes the pair of this portal (where it is connected to)
    private final StairPortal pair;

    public StairPortal() {
        super(null,true, null, null, null);

        this.pair = null;
    }

    public StairPortal(
            Patch patch,
            boolean enabled,
            Floor lowerFloor,
            Floor upperFloor,
            Portal.PortalLocation portalLocation,
            StairPortal pair
    ) {
        super(patch, enabled, lowerFloor, upperFloor, portalLocation);

        this.pair = pair;
    }

    public StairPortal getPair() {
        return pair;
    }

    @Override
    public String toString() {
        return "Stair portal";
    }
}
