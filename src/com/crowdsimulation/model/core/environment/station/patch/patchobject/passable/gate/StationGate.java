package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.controller.graphics.amenity.editor.StationGateEditor;
import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.AmenityGraphicLocation;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.StationGateGraphic;
import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.simulator.Simulator;

import java.util.ArrayList;
import java.util.List;

public class StationGate extends Gate {
    // Denotes the chance of generating a passenger per second
    private double chancePerSecond;

    // Denotes the mode of this station gate (whether it's entry/exit only, or both)
    private StationGateMode stationGateMode;

    // Denotes the direction of passengers this station gate produces
    private StationGatePassengerTravelDirection stationGatePassengerTravelDirection;

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

        upBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                0,
                0,
                StationGate.class,
                true,
                false
        );

        upBlockN10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                -1,
                0,
                StationGate.class,
                false,
                true
        );

        upBlockN11 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                -1,
                1,
                StationGate.class,
                false,
                false
        );

        upBlock01 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                0,
                1,
                StationGate.class,
                true,
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

        rightBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                0,
                0,
                StationGate.class,
                true,
                true
        );

        rightBlock01 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                0,
                1,
                StationGate.class,
                false,
                false
        );

        rightBlock10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                1,
                0,
                StationGate.class,
                true,
                false
        );

        rightBlock11 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                1,
                1,
                StationGate.class,
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

        downBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                0,
                0,
                StationGate.class,
                true,
                false
        );

        downBlock0N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                0,
                -1,
                StationGate.class,
                true,
                true
        );

        downBlock1N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                1,
                -1,
                StationGate.class,
                false,
                false
        );

        downBlock10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                1,
                0,
                StationGate.class,
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

        leftBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                0,
                0,
                StationGate.class,
                true,
                false
        );

        leftBlockN1N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                -1,
                -1,
                StationGate.class,
                false,
                true
        );

        leftBlockN10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                -1,
                0,
                StationGate.class,
                true,
                false
        );

        leftBlock0N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                0,
                -1,
                StationGate.class,
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
            StationGatePassengerTravelDirection stationGatePassengerTravelDirection
    ) {
        super(amenityBlocks, enabled);

        this.chancePerSecond = chancePerSecond;
        this.stationGateMode = stationGateMode;
        this.stationGatePassengerTravelDirection = stationGatePassengerTravelDirection;

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

    public StationGatePassengerTravelDirection getStationGatePassengerTravelDirection() {
        return stationGatePassengerTravelDirection;
    }

    public void setStationGatePassengerTravelDirection(
            StationGatePassengerTravelDirection stationGatePassengerTravelDirection
    ) {
        this.stationGatePassengerTravelDirection = stationGatePassengerTravelDirection;
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
        // Check if all attractors in this amenity have no passengers
        for (AmenityBlock attractor : this.getAttractors()) {
            if (!attractor.getPatch().getPassengers().isEmpty()) {
                return null;
            }
        }

        // TODO: Consider if entrance/exit only
        AmenityBlock attractor = this.getAttractors().get(0);

        // Get the pool of possible travel directions of the passengers to be spawned, depending on the settings of this
        // passenger gate
        List<TrainDoor.TravelDirection> travelDirections = new ArrayList<>();

        if (this.stationGatePassengerTravelDirection == StationGatePassengerTravelDirection.NORTHBOUND) {
            travelDirections.add(TrainDoor.TravelDirection.NORTHBOUND);
        } else if (this.stationGatePassengerTravelDirection == StationGatePassengerTravelDirection.SOUTHBOUND) {
            travelDirections.add(TrainDoor.TravelDirection.SOUTHBOUND);
        } else if (
                this.stationGatePassengerTravelDirection
                        == StationGatePassengerTravelDirection.NORTHBOUND_AND_SOUTHBOUND
        ) {
            travelDirections.add(TrainDoor.TravelDirection.NORTHBOUND);
            travelDirections.add(TrainDoor.TravelDirection.SOUTHBOUND);
        } else if (this.stationGatePassengerTravelDirection == StationGatePassengerTravelDirection.EASTBOUND) {
            travelDirections.add(TrainDoor.TravelDirection.EASTBOUND);
        } else if (this.stationGatePassengerTravelDirection == StationGatePassengerTravelDirection.WESTBOUND) {
            travelDirections.add(TrainDoor.TravelDirection.WESTBOUND);
        } else if (
                this.stationGatePassengerTravelDirection == StationGatePassengerTravelDirection.EASTBOUND_AND_WESTBOUND
        ) {
            travelDirections.add(TrainDoor.TravelDirection.EASTBOUND);
            travelDirections.add(TrainDoor.TravelDirection.WESTBOUND);
        }

        // From this pool of travel directions, pick a random one
        int randomIndex = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(travelDirections.size());
        TrainDoor.TravelDirection travelDirectionChosen = travelDirections.get(randomIndex);

        // If that random attractor is free from passengers, generate one
        if (attractor.getPatch().getPassengers().isEmpty()) {
            return Passenger.passengerFactory.create(attractor.getPatch(), travelDirectionChosen);
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
    public static class StationGateBlock extends Amenity.AmenityBlock {
        public static StationGateBlockFactory stationGateBlockFactory;

        static {
            stationGateBlockFactory = new StationGateBlockFactory();
        }

        private StationGateBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        // Station gate block factory
        public static class StationGateBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public StationGateBlock create(
                    Patch patch,
                    boolean attractor,
                    boolean hasGraphic,
                    AmenityFootprint.Rotation.Orientation... orientation
            ) {
                return new StationGateBlock(
                        patch,
                        attractor,
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
                StationGatePassengerTravelDirection stationGatePassengerTravelDirection
        ) {
            return new StationGate(
                    amenityBlocks,
                    enabled,
                    chancePerSecond,
                    stationGateMode,
                    stationGatePassengerTravelDirection
            );
        }
    }

    // The platform direction this train door waiting area is at
    public enum StationGatePassengerTravelDirection {
        NORTHBOUND("Northbound"),
        SOUTHBOUND("Southbound"),
        NORTHBOUND_AND_SOUTHBOUND("Northbound and southbound"),
        EASTBOUND("Eastbound"),
        WESTBOUND("Westbound"),
        EASTBOUND_AND_WESTBOUND("Eastbound and westbound"),
        ALL_DIRECTIONS("All directions");

        private final String name;

        StationGatePassengerTravelDirection(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
