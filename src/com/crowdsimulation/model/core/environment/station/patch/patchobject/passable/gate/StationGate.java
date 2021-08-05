package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.controller.graphics.amenity.editor.StationGateEditor;
import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.controller.graphics.amenity.footprint.GateFootprint;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.AmenityGraphicLocation;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.StationGateGraphic;
import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.simulator.Simulator;

import java.util.ArrayList;
import java.util.List;

public class StationGate extends Gate {
    // Denotes the chance of generating a passenger per second
    private double chancePerSecond;

    // Denotes the mode of this station gate (whether it's entry/exit only, or both)
    private StationGateMode stationGateMode;

    // Denotes the direction of passengers this station gate produces
    private final List<PassengerMovement.TravelDirection> stationGatePassengerTravelDirections;

    // Factory for station gate creation
    public static final StationGateFactory stationGateFactory;

    // Handles how the station gate is displayed
    private final StationGateGraphic stationGateGraphic;

    // Denotes the footprint of this amenity when being drawn
    public static final AmenityFootprint stationGateFootprint;

    // Denotes the editor of this amenity
    public static final StationGateEditor stationGateEditor;

    static {
        stationGateFactory = new StationGateFactory();

        // Initialize this amenity's footprints
        stationGateFootprint = new AmenityFootprint();

        // Up view
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlockN10;
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlockN11;
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlock01;

        AmenityFootprint.Rotation upView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.UP);

        upBlock00 = new GateFootprint.GateRotation.GateBlockTemplate(
                upView.getOrientation(),
                0,
                0,
                StationGate.class,
                false,
                true,
                false
        );

        upBlockN10 = new GateFootprint.GateRotation.GateBlockTemplate(
                upView.getOrientation(),
                -1,
                0,
                StationGate.class,
                false,
                false,
                true
        );

        upBlockN11 = new GateFootprint.GateRotation.GateBlockTemplate(
                upView.getOrientation(),
                -1,
                1,
                StationGate.class,
                false,
                false,
                false
        );

        upBlock01 = new GateFootprint.GateRotation.GateBlockTemplate(
                upView.getOrientation(),
                0,
                1,
                StationGate.class,
                true,
                false,
                false
        );

        upView.getAmenityBlockTemplates().add(upBlock00);
        upView.getAmenityBlockTemplates().add(upBlockN10);
        upView.getAmenityBlockTemplates().add(upBlockN11);
        upView.getAmenityBlockTemplates().add(upBlock01);

        stationGateFootprint.addRotation(upView);

        // Right view
        AmenityFootprint.Rotation.AmenityBlockTemplate rightBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate rightBlock01;
        AmenityFootprint.Rotation.AmenityBlockTemplate rightBlock10;
        AmenityFootprint.Rotation.AmenityBlockTemplate rightBlock11;

        AmenityFootprint.Rotation rightView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.RIGHT);

        rightBlock00 = new GateFootprint.GateRotation.GateBlockTemplate(
                rightView.getOrientation(),
                0,
                0,
                StationGate.class,
                false,
                true,
                true
        );

        rightBlock01 = new GateFootprint.GateRotation.GateBlockTemplate(
                rightView.getOrientation(),
                0,
                1,
                StationGate.class,
                false,
                false,
                false
        );

        rightBlock10 = new GateFootprint.GateRotation.GateBlockTemplate(
                rightView.getOrientation(),
                1,
                0,
                StationGate.class,
                true,
                false,
                false
        );

        rightBlock11 = new GateFootprint.GateRotation.GateBlockTemplate(
                rightView.getOrientation(),
                1,
                1,
                StationGate.class,
                false,
                false,
                false
        );

        rightView.getAmenityBlockTemplates().add(rightBlock00);
        rightView.getAmenityBlockTemplates().add(rightBlock01);
        rightView.getAmenityBlockTemplates().add(rightBlock10);
        rightView.getAmenityBlockTemplates().add(rightBlock11);

        stationGateFootprint.addRotation(rightView);

        // Down view
        AmenityFootprint.Rotation.AmenityBlockTemplate downBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate downBlock0N1;
        AmenityFootprint.Rotation.AmenityBlockTemplate downBlock1N1;
        AmenityFootprint.Rotation.AmenityBlockTemplate downBlock10;

        AmenityFootprint.Rotation downView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.DOWN);

        downBlock00 = new GateFootprint.GateRotation.GateBlockTemplate(
                downView.getOrientation(),
                0,
                0,
                StationGate.class,
                false,
                true,
                false
        );

        downBlock0N1 = new GateFootprint.GateRotation.GateBlockTemplate(
                downView.getOrientation(),
                0,
                -1,
                StationGate.class,
                true,
                false,
                true
        );

        downBlock1N1 = new GateFootprint.GateRotation.GateBlockTemplate(
                downView.getOrientation(),
                1,
                -1,
                StationGate.class,
                false,
                false,
                false
        );

        downBlock10 = new GateFootprint.GateRotation.GateBlockTemplate(
                downView.getOrientation(),
                1,
                0,
                StationGate.class,
                false,
                false,
                false
        );

        downView.getAmenityBlockTemplates().add(downBlock00);
        downView.getAmenityBlockTemplates().add(downBlock0N1);
        downView.getAmenityBlockTemplates().add(downBlock1N1);
        downView.getAmenityBlockTemplates().add(downBlock10);

        stationGateFootprint.addRotation(downView);

        // Left view
        AmenityFootprint.Rotation.AmenityBlockTemplate leftBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate leftBlockN1N1;
        AmenityFootprint.Rotation.AmenityBlockTemplate leftBlockN10;
        AmenityFootprint.Rotation.AmenityBlockTemplate leftBlock0N1;

        AmenityFootprint.Rotation leftView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.LEFT);

        leftBlock00 = new GateFootprint.GateRotation.GateBlockTemplate(
                leftView.getOrientation(),
                0,
                0,
                StationGate.class,
                false,
                true,
                false
        );

        leftBlockN1N1 = new GateFootprint.GateRotation.GateBlockTemplate(
                leftView.getOrientation(),
                -1,
                -1,
                StationGate.class,
                false,
                false,
                true
        );

        leftBlockN10 = new GateFootprint.GateRotation.GateBlockTemplate(
                leftView.getOrientation(),
                -1,
                0,
                StationGate.class,
                true,
                false,
                false
        );

        leftBlock0N1 = new GateFootprint.GateRotation.GateBlockTemplate(
                leftView.getOrientation(),
                0,
                -1,
                StationGate.class,
                false,
                false,
                false
        );

        leftView.getAmenityBlockTemplates().add(leftBlock00);
        leftView.getAmenityBlockTemplates().add(leftBlockN1N1);
        leftView.getAmenityBlockTemplates().add(leftBlockN10);
        leftView.getAmenityBlockTemplates().add(leftBlock0N1);

        stationGateFootprint.addRotation(leftView);

        // Initialize the editor
        stationGateEditor = new StationGateEditor();
    }

    protected StationGate(
            List<AmenityBlock> amenityBlocks,
            boolean enabled,
            double chancePerSecond,
            StationGateMode stationGateMode,
            List<PassengerMovement.TravelDirection> stationGatePassengerTravelDirections
    ) {
        super(amenityBlocks, enabled);

        this.chancePerSecond = chancePerSecond;
        this.stationGateMode = stationGateMode;
        this.stationGatePassengerTravelDirections = new ArrayList<>();

        setPassengerTravelDirectionsSpawned(stationGatePassengerTravelDirections);

        this.stationGateGraphic = new StationGateGraphic(this);
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

    public List<PassengerMovement.TravelDirection> getStationGatePassengerTravelDirections() {
        return stationGatePassengerTravelDirections;
    }

    public void setPassengerTravelDirectionsSpawned(List<PassengerMovement.TravelDirection> travelDirections) {
        this.stationGatePassengerTravelDirections.clear();
        this.stationGatePassengerTravelDirections.addAll(travelDirections);
    }

    @Override
    public String toString() {
        return "Station entrance/exit" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.stationGateGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.stationGateGraphic.getGraphicLocation();
    }

    @Override
    // Spawn a passenger in this position
    public Passenger spawnPassenger() {
        // Check if all attractors and spawners in this amenity have no passengers
        for (AmenityBlock attractor : this.getAttractors()) {
            if (!attractor.getPatch().getPassengers().isEmpty()) {
                return null;
            }
        }

        for (GateBlock spawner : this.getSpawners()) {
            if (!spawner.getPatch().getPassengers().isEmpty()) {
                return null;
            }
        }

        // TODO: Consider if entrance/exit only
        GateBlock spawner = this.getSpawners().get(0);

        // Get the pool of possible travel directions of the passengers to be spawned, depending on the settings of this
        // passenger gate
        // From this pool of travel directions, pick a random one
        int randomIndex = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(this.stationGatePassengerTravelDirections.size());
        PassengerMovement.TravelDirection travelDirectionChosen = this.stationGatePassengerTravelDirections.get(randomIndex);

        // If that spawner is free from passengers, generate one
        if (spawner.getPatch().getPassengers().isEmpty()) {
            return Passenger.passengerFactory.create(spawner.getPatch(), travelDirectionChosen, true);
        } else {
            // Else, do nothing, so return null
            return null;
        }
    }

    // Lists the mode of this station gate (whether it's entry/exit only, or both)
    public enum StationGateMode {
        ENTRANCE("Entrance"),
        EXIT("Exit"),
        ENTRANCE_AND_EXIT("Entrance and exit");

        private final String name;

        StationGateMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    // Station gate block
    public static class StationGateBlock extends Gate.GateBlock {
        public static StationGateBlockFactory stationGateBlockFactory;

        static {
            stationGateBlockFactory = new StationGateBlockFactory();
        }

        private StationGateBlock(Patch patch, boolean attractor, boolean spawner, boolean hasGraphic) {
            super(patch, attractor, spawner, hasGraphic);
        }

        // Station gate block factory
        public static class StationGateBlockFactory extends Gate.GateBlock.GateBlockFactory {
            @Override
            public StationGate.StationGateBlock create(
                    Patch patch,
                    boolean attractor,
                    boolean hasGraphic,
                    AmenityFootprint.Rotation.Orientation... orientation
            ) {
                return new StationGate.StationGateBlock(
                        patch,
                        attractor,
                        false,
                        hasGraphic
                );
            }

            @Override
            public StationGate.StationGateBlock create(
                    Patch patch,
                    boolean attractor,
                    boolean spawner,
                    boolean hasGraphic,
                    AmenityFootprint.Rotation.Orientation... orientation
            ) {
                return new StationGate.StationGateBlock(
                        patch,
                        attractor,
                        spawner,
                        hasGraphic
                );
            }
        }
    }

    // Station gate factory
    public static class StationGateFactory extends GateFactory {
        public StationGate create(
                List<AmenityBlock> amenityBlocks,
                boolean enabled,
                double chancePerSecond,
                StationGateMode stationGateMode,
                List<PassengerMovement.TravelDirection> stationGatePassengerTravelDirections
        ) {
            return new StationGate(
                    amenityBlocks,
                    enabled,
                    chancePerSecond,
                    stationGateMode,
                    stationGatePassengerTravelDirections
            );
        }
    }
}
