package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.environment.Environment;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public abstract class Portal extends Gate {
    // Denotes the floor that this portal serves
    private final Floor floorServed;

    // Denotes the location of this portal
    private PortalLocation portalLocation;

    // Denotes the pair (other end) of this portal
    private Portal pair;

    // Denotes ths amenity classes accessible from this portal
    private final HashSet<DirectoryItem> directory;

    protected Portal(List<AmenityBlock> amenityBlocks, boolean enabled, Floor floorServed) {
        super(amenityBlocks, enabled);

        this.floorServed = floorServed;

        this.directory = new HashSet<>();
    }

    public static boolean isPortal(Amenity amenity) {
        return amenity instanceof Portal;
    }

    public static Portal asPortal(Amenity amenity) {
        if (isPortal(amenity)) {
            return (Portal) amenity;
        } else {
            return null;
        }
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

    public HashSet<DirectoryItem> getDirectory() {
        return directory;
    }

    // Have a passenger use this portal
    public abstract void absorb(Passenger passenger);

    public abstract Patch emit();

    // Portal factory
    public static abstract class PortalFactory extends GateFactory {
    }

    // Denotes the two locations that the portal may be in
    protected enum PortalLocation {
        LOWER,
        UPPER
    }

    public static class DirectoryItem implements Environment {
        private final PassengerMovement.TravelDirection travelDirection;
        private final Class<? extends Amenity> amenityClass;
        private final Amenity previousAmenity;

        public DirectoryItem(
                PassengerMovement.TravelDirection travelDirection,
                Class<? extends Amenity> amenityClass,
                Amenity previousAmenity
        ) {
            this.travelDirection = travelDirection;
            this.amenityClass = amenityClass;
            this.previousAmenity = previousAmenity;
        }

        public PassengerMovement.TravelDirection getTravelDirection() {
            return travelDirection;
        }

        public Class<? extends Amenity> getAmenityClass() {
            return amenityClass;
        }

        public Amenity getPreviousAmenity() {
            return previousAmenity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DirectoryItem that = (DirectoryItem) o;

            if (previousAmenity == null && that.previousAmenity == null) {
                return
                        travelDirection == that.travelDirection && amenityClass.equals(that.amenityClass);
            } else {
                if (previousAmenity != null && that.previousAmenity != null) {
                    return
                            travelDirection == that.travelDirection
                                    && amenityClass.equals(that.amenityClass)
                                    && previousAmenity.equals(that.previousAmenity);
                } else {
                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(travelDirection, amenityClass, previousAmenity);
        }
    }
}
