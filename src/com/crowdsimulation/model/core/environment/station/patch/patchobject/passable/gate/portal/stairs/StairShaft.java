package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs;

import com.crowdsimulation.controller.graphics.amenity.editor.StairEditor;
import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.PortalShaft;

import java.util.ArrayList;
import java.util.List;

public class StairShaft extends PortalShaft {
    // Denotes the internal queues this staircase maintains
    private final List<List<Passenger>> descendingQueue;
    private final List<List<Passenger>> ascendingQueue;

    private int passengersDescending;
    private int passengersAscending;

    // Denotes the editor of this amenity
    public static final StairEditor stairEditor;

    static {
        // Initialize the editor
        stairEditor = new StairEditor();
    }

    protected StairShaft(boolean enabled, int moveTime, int capacity) {
        super(null, enabled, moveTime, capacity);

        this.descendingQueue = new ArrayList<>(capacity);
        this.ascendingQueue = new ArrayList<>(capacity);

        for (int index = 0; index < capacity; index++) {
            this.descendingQueue.add(new ArrayList<>());
            this.ascendingQueue.add(new ArrayList<>());
        }

        this.passengersDescending = 0;
        this.passengersAscending = 0;
    }

    public List<List<Passenger>> getDescendingQueue() {
        return descendingQueue;
    }

    public List<List<Passenger>> getAscendingQueue() {
        return ascendingQueue;
    }

    public boolean isDescendingQueueAtCapacity() {
        return this.passengersDescending >= this.getCapacity();
    }

    public boolean isAscendingQueueAtCapacity() {
        return this.passengersAscending >= this.getCapacity();
    }

    public void incrementPassengersDescending() {
        this.passengersDescending++;
    }

    public void incrementPassengersAscending() {
        this.passengersAscending++;
    }

    public void decrementPassengersDescending() {
        this.passengersDescending--;
    }

    public void decrementPassengersAscending() {
        this.passengersAscending--;
    }

    @Override
    public void updateQueues() {
        // For each passenger in the descending queue, move the passenger down a bucket, if that bucket is not filled,
        // Do this operation from bottom to top
        for (int index = 0; index < this.descendingQueue.size(); index++) {
            List<Passenger> passengersInBucket = this.descendingQueue.get(index);

            if (!passengersInBucket.isEmpty()) {
                if (index == 0) {
                    // Remove the passengers at the bottom of the queue to spawn them into the new floor, if the
                    // pertinent spawn patch is empty
                    if (passengersInBucket.get(0).getPassengerMovement().exitPortal()) {
                        this.descendingQueue.get(index).clear();
                        this.decrementPassengersDescending();
                    }
                } else {
                    // Only move down if the succeeding bucket is empty
                    if (this.descendingQueue.get(index - 1).isEmpty()) {
                        this.descendingQueue.get(index - 1).addAll(passengersInBucket);
                        this.descendingQueue.get(index).clear();
                    }
                }
            }
        }

        // For each passenger in the ascending queue, move the passenger up a bucket, if that bucket is not filled,
        // Do this operation from top to bottom
        for (int index = this.ascendingQueue.size() - 1; index >= 0; index--) {
            List<Passenger> passengersInBucket = this.ascendingQueue.get(index);

            if (!passengersInBucket.isEmpty()) {
                if (index == this.ascendingQueue.size() - 1) {
                    // Remove the passengers at the top of the queue to spawn them into the new floor, if the pertinent spawn
                    // patch is empty
                    if (passengersInBucket.get(0).getPassengerMovement().exitPortal()) {
                        this.ascendingQueue.get(index).clear();
                        this.decrementPassengersAscending();
                    }
                } else {
                    // Only move up if the succeeding bucket is empty
                    if (this.ascendingQueue.get(index + 1).isEmpty()) {
                        this.ascendingQueue.get(index + 1).addAll(passengersInBucket);
                        this.ascendingQueue.get(index).clear();
                    }
                }
            }
        }

        // Set the last bucket with null
//        this.ascendingQueue.set(this.descendingQueue.size() - 1, null);
//        for (Map.Entry<Passenger, Integer> passengerTimeEntry : this.descendingQueue.entrySet()) {
//            Passenger passenger = passengerTimeEntry.getKey();
//            Integer timeSpent = passengerTimeEntry.getValue();
//
//            this.descendingQueue.put(passenger, timeSpent + 1);
//
//            // If the time spent has exceeded the time set in this shaft, try to spawn the passenger to its new floor
//            if (passengerTimeEntry.getValue() > this.getMoveTime()) {
//                if (passenger.getPassengerMovement().exitPortal()) {
//                    passengersToClear.add(passenger);
//                }
//            }
//        }
//
//        for (Passenger passenger : passengersToClear) {
//            this.descendingQueue.remove(passenger);
//        }
    }

    @Override
    public List<Passenger> clearQueues() {
        List<Passenger> passengersToRemove = new ArrayList<>();

        for (List<Passenger> passengersInBucket : this.descendingQueue) {
            passengersToRemove.addAll(passengersInBucket);
            passengersInBucket.clear();
        }

        for (List<Passenger> passengersInBucket : this.ascendingQueue) {
            passengersToRemove.addAll(passengersInBucket);
            passengersInBucket.clear();
        }

        this.passengersDescending = 0;
        this.passengersAscending = 0;

        return passengersToRemove;
    }

    // Stair shaft factory
    public static class StairShaftFactory extends PortalShaftFactory {
        public StairShaft create(boolean enabled, int moveTime, int capacity) {
            return new StairShaft(
                    enabled,
                    moveTime,
                    capacity
            );
        }
    }
}
