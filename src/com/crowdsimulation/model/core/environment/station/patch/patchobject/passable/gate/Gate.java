package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Passable;

public abstract class Gate extends Passable {
    public Gate(Patch patch, boolean enabled) {
        super(patch, enabled);
    }
}
