package com.crowdsimulation.model.core.agent.passenger.movement;

import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBoothTransactionArea;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Turnstile;

import java.util.*;

public class RoutePlan {
    // Contains the list of pattern plans
    public static final Map<PassengerMovement.Direction, List<Class<? extends Amenity>>> DIRECTION_ROUTE_MAP;

    // Denotes the current route plan of the passenger which owns this
    private Iterator<? extends Amenity> currentRoutePlan;

    static {
        // Prepare the structure that maps directions to the plans
        DIRECTION_ROUTE_MAP = new HashMap<>();

        // Prepare the plans
        final List<Class<? extends Amenity>> boardingPlanList = new ArrayList<>();

        boardingPlanList.add(StationGate.class);
        boardingPlanList.add(Security.class);
        boardingPlanList.add(TicketBoothTransactionArea.class);
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

}
