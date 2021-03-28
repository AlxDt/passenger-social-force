package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.NonObstacle;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;

public class PortalShaft extends NonObstacle {
    // Denotes the lower and upper portals of this shaft
    private Portal lowerPortal;
    private Portal upperPortal;

    // Denotes the time (s) needed to move the passengers from one end of the shaft to another
    private int moveTime;

    public PortalShaft(Patch patch, boolean enabled, int moveTime) {
        super(patch, enabled);

        this.moveTime = moveTime;
    }

    public Portal getLowerPortal() {
        return lowerPortal;
    }

    public void setLowerPortal(Portal lowerPortal) {
        this.lowerPortal = lowerPortal;
    }

    public Portal getUpperPortal() {
        return upperPortal;
    }

    public void setUpperPortal(Portal upperPortal) {
        this.upperPortal = upperPortal;
    }

    public int getMoveTime() {
        return moveTime;
    }

    public void setMoveTime(int moveTime) {
        this.moveTime = moveTime;
    }
}
