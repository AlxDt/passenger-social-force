package com.crowdsimulation.model.core.environment.station.patch.floorfield.headful;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.Queueable;

public class QueueingFloorField extends HeadfulFloorField {
    public QueueingFloorField() {
        super(null, null);
    }

    public QueueingFloorField(Patch apex, Queueable goal) {
        super(apex, goal);
    }
}
