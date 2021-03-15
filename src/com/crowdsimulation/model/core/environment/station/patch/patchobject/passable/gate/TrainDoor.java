package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.Queueable;

public class TrainDoor extends Gate implements Queueable {
    public TrainDoor(
            Patch patch,
            boolean enabled
    ) {
        super(patch, enabled);
    }

    @Override
    public String toString() {
        return "Train boarding area";
    }

    // Lists the mode of this train door (whether it's entry/exit only, or both)
    public enum TrainDoorMode {
        BOARD,
        ALIGHT,
        BOARD_AND_ALIGHT
    }
}
