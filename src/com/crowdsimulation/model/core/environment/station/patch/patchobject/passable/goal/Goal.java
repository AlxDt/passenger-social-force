package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal;

import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.NonObstacle;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;
import com.crowdsimulation.model.simulator.Simulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Goal extends NonObstacle implements Queueable, Drawable {
    // Denotes the waiting time before this goal lets the passenger pass (s)
    private int waitingTime;

    // Denotes the time left to wait until this goal allows a passenger to pass
    private int waitingTimeLeft;

    // Maps queue objects into their corresponding attractors
    private final Map<QueueObject, AmenityBlock> queueObjectAmenityBlockMap;

    protected Goal(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime) {
        super(amenityBlocks, enabled);

        this.waitingTime = waitingTime;

        this.queueObjectAmenityBlockMap = new HashMap<>();

        resetWaitingTime();
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public Map<QueueObject, AmenityBlock> getQueueObjectAmenityBlockMap() {
        return queueObjectAmenityBlockMap;
    }

    // Check if this goal will now allow a passenger to pass
    public boolean allowPass() {
        // Reduce the waiting time by one
        this.waitingTimeLeft--;

        // Check if there is no more waiting time left, in which case a passenger will be allowed to pass
        return this.waitingTimeLeft <= 0;
    }

    public void resetWaitingTime() {
        final int minimumWaitingTime = 1;

        // TODO: Calibrate
        int waitingTimeLeft
                = (int)
                (this.waitingTime + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * (this.waitingTime * 0.05));

        if (waitingTimeLeft <= minimumWaitingTime) {
            waitingTimeLeft = minimumWaitingTime;
        }

        this.waitingTimeLeft = waitingTimeLeft;
    }

    public static boolean isGoal(Amenity amenity) {
        return amenity instanceof Goal;
    }

    public static Goal toGoal(Amenity amenity) {
        if (isGoal(amenity)) {
            return (Goal) amenity;
        } else {
            return null;
        }
    }

    // Goal factory
    public static abstract class GoalFactory extends AmenityFactory {
    }
}
