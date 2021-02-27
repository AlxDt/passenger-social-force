package com.crowdsimulation.model.core.environment.station.patch.floorfield.headful;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.FloorField;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.Queueable;

public abstract class HeadfulFloorField extends FloorField {
    // Denotes the patch in this floor field with the highest value
    private Patch apex;

    // Denotes the goal associated with this floor field
    private Queueable goal;

    public HeadfulFloorField(Patch apex, Queueable goal) {
        this.apex = apex;
        this.goal = goal;
    }

    public Patch getApex() {
        return apex;
    }

    public void setApex(Patch apex) {
        this.apex = apex;
    }

    public Queueable getGoal() {
        return goal;
    }

    public void setGoal(Queueable goal) {
        this.goal = goal;
    }
}
