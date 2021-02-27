package com.crowdsimulation.model.core.environment.station.patch.floorfield.headful;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.Queueable;

public class PlatformFloorField extends HeadfulFloorField {
    public PlatformFloorField() {
        super(null, null);
    }

    public PlatformFloorField(Patch apex, Queueable goal) {
        super(apex, goal);
    }
}
