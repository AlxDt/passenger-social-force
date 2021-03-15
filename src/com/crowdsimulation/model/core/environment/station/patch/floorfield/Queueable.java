package com.crowdsimulation.model.core.environment.station.patch.floorfield;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.agent.passenger.PassengerMovement;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public interface Queueable {
    // Any amenity that is queueable must contain a hashmap of floor fields
    // Given a goal and a state, a floor field may be retrieved from that goal
    Map<PassengerMovement.State, FloorField> floorFields = new HashMap<PassengerMovement.State, FloorField>();

    // Denotes the list of passengers who are queueing for this goal
    ArrayDeque<Passenger> passengersQueueing = new ArrayDeque<>();
}
