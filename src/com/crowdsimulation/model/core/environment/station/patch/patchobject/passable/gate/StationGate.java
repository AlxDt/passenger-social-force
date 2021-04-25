package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.controller.graphics.amenity.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.SingularGraphic;
import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import javafx.scene.image.Image;

public class StationGate extends Gate {
    // Denotes the chance of generating a passenger per second
    private double chancePerSecond;

    // Denotes the mode of this station gate (whether it's entry/exit only, or both)
    private StationGateMode stationGateMode;

    // Factory for station gate creation
    public static final StationGateFactory stationGateFactory;

    // Handles how the station gate is displayed
    private final SingularGraphic stationGateGraphic;

    static {
        stationGateFactory = new StationGateFactory();
    }

    protected StationGate(
            Patch patch,
            boolean enabled,
            double chancePerSecond,
            StationGateMode stationGateMode
    ) {
        super(patch, enabled);

        this.chancePerSecond = chancePerSecond;
        this.stationGateMode = stationGateMode;

        this.stationGateGraphic = new SingularGraphic(this);
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

    @Override
    public Passenger spawnPassenger() {
        return Passenger.passengerFactory.create(this);
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.stationGateGraphic;
    }

    @Override
    public Image getGraphic() {
        return this.stationGateGraphic.getGraphic();
    }

    // Station gate factory
    public static class StationGateFactory extends GateFactory {
        public StationGate create(
                Patch patch,
                boolean enabled,
                double chancePerSecond,
                StationGateMode stationGateMode
        ) {
            return new StationGate(
                    patch,
                    enabled,
                    chancePerSecond,
                    stationGateMode
            );
        }
    }

    // Lists the mode of this station gate (whether it's entry/exit only, or both)
    public enum StationGateMode {
        ENTRANCE("Entrance"),
        EXIT("Exit"),
        ENTRANCE_AND_EXIT("Entrance and exit");

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
