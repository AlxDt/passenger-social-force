package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;

public abstract class Portal extends Gate {
    // Denotes the lower of the two floors that this portal serves
    private final Floor lowerFloor;

    // Denotes the upper of the two floors that this portal serves
    private final Floor upperFloor;

    // Denotes the location of this portal
    private final PortalLocation portalLocation;

    public Portal(
            Patch patch,
            boolean enabled,
            Floor lowerFloor,
            Floor upperFloor,
            PortalLocation portalLocation
    ) {
        super(patch, enabled);

        this.lowerFloor = lowerFloor;
        this.upperFloor = upperFloor;
        this.portalLocation = portalLocation;
    }

    public Floor getLowerFloor() {
        return lowerFloor;
    }

    public Floor getUpperFloor() {
        return upperFloor;
    }

    public PortalLocation getPortalLocation() {
        return portalLocation;
    }

    // Denotes the two locations that the portal may be in
    protected enum PortalLocation {
        LOWER,
        UPPER
    }
}
