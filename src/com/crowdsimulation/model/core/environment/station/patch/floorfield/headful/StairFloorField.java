package com.crowdsimulation.model.core.environment.station.patch.floorfield.headful;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.Queueable;

public class StairFloorField extends HeadfulFloorField {
    // Denotes the speed multiplier for the passengers using the stairs or escalators
    private double speedMultiplier;

    public StairFloorField() {
        super(null, null);

        this.speedMultiplier = 0.5;
    }

    public StairFloorField(Patch apex, Queueable goal, double speedMultiplier) {
        super(apex, goal);

        this.speedMultiplier = speedMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }
}
