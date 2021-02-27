package com.crowdsimulation.model.core.environment.station.patch.floorfield.headful;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.Queueable;

public class ElevatorFloorField extends HeadfulFloorField {
    public ElevatorFloorField() {
        super(null, null);
    }

    public ElevatorFloorField(Patch apex, Queueable goal) {
        super(apex, goal);
    }
}
