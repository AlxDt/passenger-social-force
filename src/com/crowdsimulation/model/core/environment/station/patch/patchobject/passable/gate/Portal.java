package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;

import java.util.List;

public abstract class Portal extends Gate {
    // Denotes the floor that this portal serves
    private final Floor floorServed;

    // Denotes the location of this portal
    private PortalLocation portalLocation;

    // Denotes the pair (other end) of this portal
    private Portal pair;

    protected Portal(List<AmenityBlock> amenityBlocks, boolean enabled, Floor floorServed) {
        super(amenityBlocks, enabled);

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

    public Portal getPair() {
        return pair;
    }

    public void setPair(Portal pair) {
        this.pair = pair;
    }

    // Portal factory
    public static abstract class PortalFactory extends GateFactory {
    }

    // Denotes the two locations that the portal may be in
    protected enum PortalLocation {
        LOWER,
        UPPER
    }
}
