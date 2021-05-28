package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal;

import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.NonObstacle;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;

import java.util.List;

public abstract class Goal extends NonObstacle implements Queueable, Drawable {
    // Denotes the waiting time before this goal lets the passenger pass (s)
    private int waitingTime;

    // Denotes the queueing object associated with all goals like this
    private final QueueObject queueObject;

    protected Goal(List<AmenityBlock> amenityBlocks, boolean enabled, int waitingTime, QueueObject queueObject) {
        super(amenityBlocks, enabled);

        this.waitingTime = waitingTime;

        this.queueObject = queueObject;
    }


    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    @Override
    public QueueObject getQueueObject() {
        return this.queueObject;
    }

    // Goal factory
    public static abstract class GoalFactory extends AmenityFactory {
    }
}
