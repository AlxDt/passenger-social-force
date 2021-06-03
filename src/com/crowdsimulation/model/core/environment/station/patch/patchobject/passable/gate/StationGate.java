package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.controller.graphics.amenity.editor.StationGateEditor;
import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.AmenityGraphicLocation;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.StationGateGraphic;
import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;

import java.util.List;

public class StationGate extends Gate {
    // Denotes the chance of generating a passenger per second
    private double chancePerSecond;

    // Denotes the mode of this station gate (whether it's entry/exit only, or both)
    private StationGateMode stationGateMode;

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
            StationGateMode stationGateMode
    ) {
        super(amenityBlocks, enabled);

        this.chancePerSecond = chancePerSecond;
        this.stationGateMode = stationGateMode;

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

    @Override
    public String toString() {
        return "Station entrance/exit" + ((this.enabled) ? "" : " (disabled)");
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
    public AmenityGraphicLocation getGraphicLocation() {
        return this.stationGateGraphic.getGraphicLocation();
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
                StationGateMode stationGateMode
        ) {
            return new StationGate(
                    amenityBlocks,
                    enabled,
                    chancePerSecond,
                    stationGateMode
            );
        }
    }
}
