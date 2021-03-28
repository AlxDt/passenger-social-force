package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;

public abstract class Portal extends Gate {
    // Denotes the floor that this portal serves
    private final Floor floorServed;

    // Denotes the location of this portal
    private PortalLocation portalLocation;

    public Portal(Patch patch, boolean enabled, Floor floorServed) {
        super(patch, enabled);

        this.floorServed = floorServed;
    }

    public Floor getFloorServed() {
        return floorServed;
    }

    public PortalLocation getPortalLocation() {
        return portalLocation;
    }

    public void setPortalLocation(PortalLocation portalLocation) {
        this.portalLocation = portalLocation;
    }

    // Denotes the two locations that the portal may be in
    protected enum PortalLocation {
        LOWER,
        UPPER
    }
}
