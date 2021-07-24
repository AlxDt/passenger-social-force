package com.crowdsimulation.model.core.agent.passenger.movement;

import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Turnstile;

import java.util.*;

public class RoutePlan {
    // Contains the list of pattern plans
    public static final Map<PassengerMovement.Direction, List<Class<? extends Amenity>>> DIRECTION_ROUTE_MAP;

    // Denotes the current route plan of the passenger which owns this
    private Iterator<Class<? extends Amenity>> currentRoutePlan;

    // Denotes the current class of the amenity in the route plan
    private Class<? extends Amenity> currentAmenityClass;

    static {
        // Prepare the structure that maps directions to the plans
        DIRECTION_ROUTE_MAP = new HashMap<>();

        // Prepare the plans
        final List<Class<? extends Amenity>> boardingPlanList = new ArrayList<>();

        boardingPlanList.add(StationGate.class);
        boardingPlanList.add(Security.class);
        boardingPlanList.add(TicketBooth.class);
        boardingPlanList.add(Turnstile.class);
        boardingPlanList.add(TrainDoor.class);

        final List<Class<? extends Amenity>> alightingPlanList = new ArrayList<>();

        alightingPlanList.add(TrainDoor.class);
        alightingPlanList.add(Turnstile.class);
        alightingPlanList.add(StationGate.class);

        DIRECTION_ROUTE_MAP.put(PassengerMovement.Direction.BOARDING, boardingPlanList);
        DIRECTION_ROUTE_MAP.put(PassengerMovement.Direction.RIDING_TRAIN, null);
        DIRECTION_ROUTE_MAP.put(PassengerMovement.Direction.ALIGHTING, alightingPlanList);
    }

    public RoutePlan(boolean isStoredValueCardHolder) {
        // All newly-spawned passengers will have a boarding route plan
        setNextRoutePlan(PassengerMovement.Direction.BOARDING, isStoredValueCardHolder);

        // Burn off the first amenity class in the route plan, as the passenger will have already spawned there
        setNextAmenityClass();
        setNextAmenityClass();
    }

    // Set the next route plan
    public void setNextRoutePlan(PassengerMovement.Direction direction, boolean isStoredValueCardHolder) {
        List<Class<? extends Amenity>> routePlan = new ArrayList<>(DIRECTION_ROUTE_MAP.get(direction));

        // If the passenger is a stored value card holder, remove the ticket booth from its route plan
        if (direction == PassengerMovement.Direction.BOARDING && isStoredValueCardHolder) {
            routePlan.remove(TicketBooth.class);
        }

        this.currentRoutePlan = routePlan.iterator();
    }

    // Set the next amenity class in the route plan
    public void setNextAmenityClass() {
        this.currentAmenityClass = this.currentRoutePlan.next();
    }

    public Iterator<Class<? extends Amenity>> getCurrentRoutePlan() {
        return currentRoutePlan;
    }

    public Class<? extends Amenity> getCurrentAmenityClass() {
        return currentAmenityClass;
    }
}
