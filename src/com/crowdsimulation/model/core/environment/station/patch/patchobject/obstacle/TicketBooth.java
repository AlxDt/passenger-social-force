package com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle;

import com.crowdsimulation.model.core.environment.station.patch.Patch;

public class TicketBooth extends Obstacle {
    public TicketBooth(Patch patch) {
        super(patch);
    }

    @Override
    public String toString() {
        return "Ticket booth";
    }
}
