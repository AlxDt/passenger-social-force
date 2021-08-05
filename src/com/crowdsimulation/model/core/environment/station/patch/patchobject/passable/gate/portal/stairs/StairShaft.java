package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs;

import com.crowdsimulation.controller.graphics.amenity.editor.StairEditor;
import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.PortalShaft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StairShaft extends PortalShaft {
    // Denotes the editor of this amenity
    public static final StairEditor stairEditor;

    // Denotes the internal queues this staircase maintain, containing a passenger in the stairs and the time it has
    // spent in it
    private final HashMap<Passenger, Integer> descendingQueue;
    private final HashMap<Passenger, Integer> ascendingQueue;

    static {
        // Initialize the editor
        stairEditor = new StairEditor();
    }

    protected StairShaft(boolean enabled, int moveTime) {
        super(null, enabled, moveTime);

        this.descendingQueue = new HashMap<>();
        this.ascendingQueue = new HashMap<>();
    }

    public HashMap<Passenger, Integer> getDescendingQueue() {
        return descendingQueue;
    }

    public HashMap<Passenger, Integer> getAscendingQueue() {
        return ascendingQueue;
    }

    @Override
    public void updateQueues() {
        List<Passenger> passengersToClear = new ArrayList<>();

        // For each internal queue, update the passenger's time spent in this portal
        for (Map.Entry<Passenger, Integer> passengerTimeEntry : this.ascendingQueue.entrySet()) {
            Passenger passenger = passengerTimeEntry.getKey();
            Integer timeSpent = passengerTimeEntry.getValue();

            this.ascendingQueue.put(passenger, timeSpent + 1);

            // If the time spent has exceeded the time set in this shaft, try to spawn the passenger to its new floor
            if (passengerTimeEntry.getValue() > this.getMoveTime()) {
                if (passenger.getPassengerMovement().exitPortal()) {
                    passengersToClear.add(passenger);
                }
            }
        }

        for (Passenger passenger : passengersToClear) {
            this.ascendingQueue.remove(passenger);
        }

        passengersToClear.clear();

        for (Map.Entry<Passenger, Integer> passengerTimeEntry : this.descendingQueue.entrySet()) {
            Passenger passenger = passengerTimeEntry.getKey();
            Integer timeSpent = passengerTimeEntry.getValue();

            this.descendingQueue.put(passenger, timeSpent + 1);

            // If the time spent has exceeded the time set in this shaft, try to spawn the passenger to its new floor
            if (passengerTimeEntry.getValue() > this.getMoveTime()) {
                if (passenger.getPassengerMovement().exitPortal()) {
                    passengersToClear.add(passenger);
                }
            }
        }

        for (Passenger passenger : passengersToClear) {
            this.descendingQueue.remove(passenger);
        }
    }

    // Stair shaft factory
    public static class StairShaftFactory extends PortalShaftFactory {
        public StairShaft create(boolean enabled, int moveTime) {
            return new StairShaft(
                    enabled,
                    moveTime
            );
        }
    }
}
