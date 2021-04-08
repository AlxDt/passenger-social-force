package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.NonObstacle;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;

public abstract class Goal extends NonObstacle implements Queueable {
    // Denotes the textual identifier of this goal
    private String goalId;

    // Denotes the waiting time before this goal lets the passenger pass (s)
    private int waitingTime;

    // Denotes the chronological order of this goal in a passenger's pathway
    private final Integer sequence;

    // Denotes the index of this goal within a sequence number
    // (e.g., this goal could be goal A, B, C, etc. in the 2nd sequence)
    // A passenger can choose between goals with the same sequence number depending on heuristics such as the line on
    // this goal, distance to this goal, etc.
    private final Integer index;

    // Denotes the queueing object associated with all goals like this
    private final QueueObject queueObject;

    public Goal(Patch patch, boolean enabled, int waitingTime, QueueObject queueObject) {
        super(patch, enabled);

        this.goalId = null;
        this.waitingTime = waitingTime;

        this.sequence = null;
        this.index = null;

        this.queueObject = queueObject;
    }

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public Integer getSequence() {
        return sequence;
    }

    public Integer getIndex() {
        return index;
    }

    public QueueObject getQueueObject() {
        return queueObject;
    }
}
