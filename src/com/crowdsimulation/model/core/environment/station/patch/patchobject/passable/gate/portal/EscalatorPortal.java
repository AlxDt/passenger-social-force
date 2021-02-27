package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal;

import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;

public class EscalatorPortal extends Portal {
    // Denotes the pair of this portal (where it is connected to)
    private final EscalatorPortal pair;

    public EscalatorPortal() {
        super(null,true, null, null, null);

        this.pair = null;
    }

    public EscalatorPortal(
            Patch patch,
            boolean enabled,
            Floor lowerFloor,
            Floor upperFloor,
            PortalLocation portalLocation,
            EscalatorPortal pair
    ) {
        super(patch, enabled, lowerFloor, upperFloor, portalLocation);

        this.pair = pair;
    }

    public EscalatorPortal getPair() {
        return pair;
    }

    @Override
    public String toString() {
        return "Escalator portal";
    }
}
