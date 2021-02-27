package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.model.core.environment.station.patch.Patch;

public class StationGate extends Gate {
    // Denotes the chance of generating a passenger per second
    private double chancePerSecond;

    // Denotes the mode of this station gate (whether it's entry/exit only, or both)
    private StationGateMode stationGateMode;

    public StationGate() {
        super(null,true);

        this.chancePerSecond = 50;
        this.stationGateMode = StationGateMode.ENTRANCE_AND_EXIT;
    }

    public StationGate(
            Patch patch,
            boolean enabled,
            double chancePerSecond,
            StationGateMode stationGateMode
    ) {
        super(patch, enabled);

        this.chancePerSecond = chancePerSecond;
        this.stationGateMode = stationGateMode;
    }

    public double getChancePerSecond() {
        return chancePerSecond;
    }

    public void setChancePerSecond(double chancePerSecond) {
        this.chancePerSecond = chancePerSecond;
    }

    public StationGateMode getStationGateMode() {
        return stationGateMode;
    }

    public void setStationGateMode(StationGateMode stationGateMode) {
        this.stationGateMode = stationGateMode;
    }

    @Override
    public String toString() {
        return "Station entrance/exit";
    }

    // Lists the mode of this station gate (whether it's entry/exit only, or both)
    public enum StationGateMode {
        ENTRANCE ("Entrance"),
        EXIT ("Exit"),
        ENTRANCE_AND_EXIT ("Entrance and exit");

        private final String name;

        private StationGateMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
