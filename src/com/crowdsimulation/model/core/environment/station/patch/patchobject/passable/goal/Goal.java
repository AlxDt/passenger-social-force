package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.Queueable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Passable;

public abstract class Goal extends Passable implements Queueable {
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

    public Goal(Patch patch, boolean enabled, int waitingTime) {
        super(patch, enabled);

        this.goalId = null;
        this.waitingTime = waitingTime;

        this.sequence = null;
        this.index = null;
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
}
