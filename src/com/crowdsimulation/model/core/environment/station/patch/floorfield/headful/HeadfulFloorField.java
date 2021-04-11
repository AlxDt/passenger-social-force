package com.crowdsimulation.model.core.environment.station.patch.floorfield.headful;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.FloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;

public abstract class HeadfulFloorField extends FloorField {
    // Denotes the patch in this floor field with the highest value
    private Patch apex;

    // Denotes the target associated with this floor field
    private final Queueable target;

    protected HeadfulFloorField(Queueable target) {
        this.target = target;
    }

    public Patch getApex() {
        return apex;
    }

    public void setApex(Patch apex) {
        this.apex = apex;
    }

    public Queueable getTarget() {
        return target;
    }

    public static abstract class HeadfulFloorFieldFactory {

    }
}
