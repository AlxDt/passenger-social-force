package com.crowdsimulation.model.core.environment.station.patch.floorfield;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class QueueObject extends AbstractFloorFieldObject {
    // Any amenity that is queueable must contain a hashmap of floor fields
    // Given a passenger state, a floor field may be retrieved from that goal
    private final Map<QueueingFloorField.FloorFieldState, QueueingFloorField> floorFields = new HashMap<>();

    // Denotes the list of passengers who are queueing for this goal
    private final ArrayDeque<Passenger> passengersQueueing = new ArrayDeque<>();

    public Map<QueueingFloorField.FloorFieldState, QueueingFloorField> getFloorFields() {
        return floorFields;
    }

    public ArrayDeque<Passenger> getPassengersQueueing() {
        return passengersQueueing;
    }
}
