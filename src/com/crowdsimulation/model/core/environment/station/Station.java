package com.crowdsimulation.model.core.environment.station;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.agent.passenger.movement.RoutePlan;
import com.crowdsimulation.model.core.environment.Environment;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.PortalShaft;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorShaft;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorShaft;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs.StairShaft;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Turnstile;
import com.crowdsimulation.model.simulator.cache.DistanceCache;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Station extends BaseStationObject implements Environment {
    // Default station name
    private static final String DEFAULT_STATION_NAME = "New station";

    // Contains binding values for the number of floors this station has
    // The name of the station
    private String name;

    // Denotes the dimensions of this station
    private final int rows;
    private final int columns;

    // Each station contains a list of floors
    private final List<Floor> floors;

    // And a list of portal shafts associated with this station
    // These lists belong here as portal shafts transcend single floors
    private final List<StairShaft> stairShafts;
    private final List<EscalatorShaft> escalatorShafts;
    private final List<ElevatorShaft> elevatorShafts;

    // The list of passengers in this station
    private final CopyOnWriteArrayList<Passenger> passengersInStation;

    // Caches for optimized performance
    private final DistanceCache distanceCache;

    public Station(int rows, int columns) {
        this.name = DEFAULT_STATION_NAME;
        this.floors = Collections.synchronizedList(new ArrayList<>());

        this.rows = rows;
        this.columns = columns;

        this.stairShafts = Collections.synchronizedList(new ArrayList<>());
        this.escalatorShafts = Collections.synchronizedList(new ArrayList<>());
        this.elevatorShafts = Collections.synchronizedList(new ArrayList<>());

        this.passengersInStation = new CopyOnWriteArrayList<>();

        this.distanceCache = new DistanceCache();

        // Initially, the station has one floor
        Floor.addFloor(this, this.floors, 0, rows, columns);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public List<StairShaft> getStairShafts() {
        return stairShafts;
    }

    public List<EscalatorShaft> getEscalatorShafts() {
        return escalatorShafts;
    }

    public List<ElevatorShaft> getElevatorShafts() {
        return elevatorShafts;
    }

    public CopyOnWriteArrayList<Passenger> getPassengersInStation() {
        return passengersInStation;
    }

    public DistanceCache getDistanceCache() {
        return distanceCache;
    }

    // Validate both the station layout and its floor fields as one
    public static boolean validateStation(Station station) {
        return validateStationLayout(station) && validateFloorFieldsInStation(station);
    }

    // Validate the floor fields of each amenity in the station
    public static boolean validateFloorFieldsInStation(Station station) {
        // For each floor in the station, validate the amenities in there which have floor fields
        List<Security> securities;
        List<TicketBooth> ticketBooths;
        List<Turnstile> turnstiles;
        List<TrainDoor> trainDoors;

        for (Floor floor : station.getFloors()) {
            securities = floor.getSecurities();
            ticketBooths = floor.getTicketBooths();
            turnstiles = floor.getTurnstiles();
            trainDoors = floor.getTrainDoors();

            for (Security security : securities) {
                if (!security.isFloorFieldsComplete()) {
                    return false;
                }
            }

            for (TicketBooth ticketBooth : ticketBooths) {
                if (!ticketBooth.isFloorFieldsComplete()) {
                    return false;
                }
            }

            for (Turnstile turnstile : turnstiles) {
                if (!turnstile.isFloorFieldsComplete()) {
                    return false;
                }
            }

            for (TrainDoor trainDoor : trainDoors) {
                if (!trainDoor.isFloorFieldsComplete()) {
                    return false;
                }
            }
        }

        // Then validate the elevator portals in this station
        for (ElevatorShaft elevatorShaft : station.getElevatorShafts()) {
            if (!((ElevatorPortal) elevatorShaft.getUpperPortal()).isFloorFieldsComplete()) {
                return false;
            }
        }

        // This part will only be reached once we pass every test
        return true;
    }

    // Validate the layout of the station
    public static boolean validateStationLayout(Station station) {
        boolean boardingValid = true;
        boolean alightingValid = true;

        // For each floor in the station, collect a single station gate
        Map<Floor, StationGate> floorStationGatesMap = new HashMap<>();

        for (Floor floor : station.getFloors()) {
            // Just sample the first station gate in the list, if any
            if (!floor.getStationGates().isEmpty()) {
                // TODO: Only consider station gates with entrances
                floorStationGatesMap.put(floor, floor.getStationGates().get(0));
            }
        }

        // If there are no station gates at all, this layout is instantly invalid
        if (floorStationGatesMap.isEmpty()) {
            return false;
        }

        // For each station gate, check if there exists a complete path from start to end (for both boarding and
        // alighting passengers)
        List<Class<? extends Amenity>> boardingPlan
                = RoutePlan.DIRECTION_ROUTE_MAP.get(PassengerMovement.Disposition.BOARDING);

        List<Class<? extends Amenity>> alightingPlan
                = RoutePlan.DIRECTION_ROUTE_MAP.get(PassengerMovement.Disposition.ALIGHTING);

        Class<? extends Amenity> currentAmenity;
        Class<? extends Amenity> nextAmenity;

        List<AmenityFloorPair> boardingNotFoundList = new ArrayList<>();
        List<AmenityFloorPair> boardingFoundList = new ArrayList<>();

        List<AmenityFloorPair> alightingNotFoundList = new ArrayList<>();
        List<AmenityFloorPair> alightingFoundList = new ArrayList<>();

        for (Floor floor : floorStationGatesMap.keySet()) {
            currentAmenity = boardingPlan.get(0);
            nextAmenity = boardingPlan.get(1);

            boardingValid = boardingValid && validatePath(
                    boardingPlan,
                    1,
                    floor,
                    boardingNotFoundList,
                    boardingFoundList,
                    currentAmenity,
                    nextAmenity
            );

            if (!boardingValid) {
                return false;
            }

            currentAmenity = alightingPlan.get(0);
            nextAmenity = alightingPlan.get(1);

            alightingValid = alightingValid && validatePath(
                    alightingPlan,
                    1,
                    floor,
                    alightingNotFoundList,
                    alightingFoundList,
                    currentAmenity,
                    nextAmenity
            );

            if (!alightingValid) {
                return false;
            }
        }

        return true;
    }

    private static boolean validatePath(
            List<Class<? extends Amenity>> routePlan,
            int index,
            Floor currentFloor,
            List<AmenityFloorPair> notFoundList,
            List<AmenityFloorPair> foundList,
            Class<? extends Amenity> currentAmenity,
            Class<? extends Amenity> nextAmenity
    ) {
        AmenityFloorPair amenityFloorPair = new AmenityFloorPair(
                nextAmenity,
                currentFloor
        );

        // If this pair has already been found in the not found list, no need to look for that amenity again - return
        // false
        if (notFoundList.contains(amenityFloorPair)) {
            return false;
        }

        // Get the necessary amenity list
        List<Amenity> amenityList = new ArrayList<>();
        if (nextAmenity == StationGate.class) {
            amenityList.addAll(currentFloor.getStationGates());
        } else if (nextAmenity == Security.class) {
            amenityList.addAll(currentFloor.getSecurities());
        } else if (nextAmenity == TicketBooth.class) {
            amenityList.addAll(currentFloor.getTicketBooths());
        } else if (nextAmenity == Turnstile.class) {
            amenityList.addAll(currentFloor.getTurnstiles());
        } else if (nextAmenity == TrainDoor.class) {
            amenityList.addAll(currentFloor.getTrainDoors());
        } else {
            amenityList = null;
        }

        assert amenityList != null;

        // Using the acquired amenity list, check if this floor contains such amenity
        if (foundList.contains(amenityFloorPair) || !amenityList.isEmpty()) {
            // Add this amenity-floor pair to the found list
            foundList.add(amenityFloorPair);

            // Get the next amenity
            Class<? extends Amenity> newCurrentAmenity;
            Class<? extends Amenity> newNextAmenity;

            // If there is no more next amenity, return true
            if (index + 1 == routePlan.size()) {
                return true;
            } else {
                // Else, keep recursing
                int newIndex = index + 1;

                newCurrentAmenity = nextAmenity;
                newNextAmenity = routePlan.get(newIndex);

                return validatePath(
                        routePlan,
                        newIndex,
                        currentFloor,
                        notFoundList,
                        foundList,
                        newCurrentAmenity,
                        newNextAmenity
                );
            }
        } else {
            // Add this amenity-floor pair to the not found list
            notFoundList.add(amenityFloorPair);

            boolean portalsValid = false;

            // Gather all portals in this floor
            List<PortalShaft> portalsInFloor = new ArrayList<>();

            // TODO: Consider directions
            for (StairShaft stairShaft : currentFloor.getStation().getStairShafts()) {
                if (stairShaft.getLowerPortal().getFloorServed() == currentFloor
                        || stairShaft.getUpperPortal().getFloorServed() == currentFloor) {
                    if (!portalsInFloor.contains(stairShaft)) {
                        portalsInFloor.add(stairShaft);
                    }
                }
            }

            for (EscalatorShaft escalatorShaft : currentFloor.getStation().getEscalatorShafts()) {
                if (escalatorShaft.getLowerPortal().getFloorServed() == currentFloor
                        || escalatorShaft.getUpperPortal().getFloorServed() == currentFloor) {
                    if (!portalsInFloor.contains(escalatorShaft)) {
                        portalsInFloor.add(escalatorShaft);
                    }
                }
            }

            for (ElevatorShaft elevatorShaft : currentFloor.getStation().getElevatorShafts()) {
                if (elevatorShaft.getLowerPortal().getFloorServed() == currentFloor
                        || elevatorShaft.getUpperPortal().getFloorServed() == currentFloor) {
                    if (!portalsInFloor.contains(elevatorShaft)) {
                        portalsInFloor.add(elevatorShaft);
                    }
                }
            }

            // For each portal, go to the floor it connects to
            for (PortalShaft portalShaft : portalsInFloor) {
                Floor connectedFloor;

                if (portalShaft.getLowerPortal().getFloorServed() == currentFloor) {
                    connectedFloor = portalShaft.getUpperPortal().getFloorServed();
                } else {
                    connectedFloor = portalShaft.getLowerPortal().getFloorServed();
                }

                // Go to that floor and validate it
                // At least one portal should lead to the next amenity
                // Else, return false
                portalsValid = portalsValid || validatePath(
                        routePlan,
                        index,
                        connectedFloor,
                        notFoundList,
                        foundList,
                        currentAmenity,
                        nextAmenity
                );
            }

            // No other portals, so just return false
            return portalsValid;
        }
    }

    private static class AmenityFloorPair {
        private final Class<? extends Amenity> amenity;
        private final Floor floor;

        public AmenityFloorPair(Class<? extends Amenity> amenity, Floor floor) {
            this.amenity = amenity;
            this.floor = floor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AmenityFloorPair that = (AmenityFloorPair) o;
            return amenity.equals(that.amenity) && floor.equals(that.floor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(amenity, floor);
        }
    }
}
