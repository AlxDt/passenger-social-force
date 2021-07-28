package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.EscalatorGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.AmenityGraphicLocation;
import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;

import java.util.List;

public class EscalatorPortal extends Portal {
    // Denotes the escalator shaft which contains this escalator portal
    private final EscalatorShaft escalatorShaft;

    // Factory for escalator portal creation
    public static final EscalatorPortalFactory escalatorPortalFactory;

    // Handles how this escalator portal is displayed
    private final EscalatorGraphic escalatorGraphic;

    // Denotes the footprint of this amenity when being drawn
    public static final AmenityFootprint escalatorPortalFootprint;

    static {
        escalatorPortalFactory = new EscalatorPortalFactory();

        // Initialize this amenity's footprints
        escalatorPortalFootprint = new AmenityFootprint();

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
                EscalatorPortal.class,
                true,
                false
        );

        upBlockN10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                -1,
                0,
                EscalatorPortal.class,
                false,
                true
        );

        upBlockN11 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                -1,
                1,
                EscalatorPortal.class,
                false,
                false
        );

        upBlock01 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                0,
                1,
                EscalatorPortal.class,
                true,
                false
        );

        upView.getAmenityBlockTemplates().add(upBlock00);
        upView.getAmenityBlockTemplates().add(upBlockN10);
        upView.getAmenityBlockTemplates().add(upBlockN11);
        upView.getAmenityBlockTemplates().add(upBlock01);

        escalatorPortalFootprint.addRotation(upView);

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
                EscalatorPortal.class,
                true,
                true
        );

        rightBlock01 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                0,
                1,
                EscalatorPortal.class,
                false,
                false
        );

        rightBlock10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                1,
                0,
                EscalatorPortal.class,
                true,
                false
        );

        rightBlock11 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                1,
                1,
                EscalatorPortal.class,
                false,
                false
        );

        rightView.getAmenityBlockTemplates().add(rightBlock00);
        rightView.getAmenityBlockTemplates().add(rightBlock01);
        rightView.getAmenityBlockTemplates().add(rightBlock10);
        rightView.getAmenityBlockTemplates().add(rightBlock11);

        escalatorPortalFootprint.addRotation(rightView);

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
                EscalatorPortal.class,
                true,
                false
        );

        downBlock0N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                0,
                -1,
                EscalatorPortal.class,
                true,
                true
        );

        downBlock1N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                1,
                -1,
                EscalatorPortal.class,
                false,
                false
        );

        downBlock10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                1,
                0,
                EscalatorPortal.class,
                false,
                false
        );

        downView.getAmenityBlockTemplates().add(downBlock00);
        downView.getAmenityBlockTemplates().add(downBlock0N1);
        downView.getAmenityBlockTemplates().add(downBlock1N1);
        downView.getAmenityBlockTemplates().add(downBlock10);

        escalatorPortalFootprint.addRotation(downView);

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
                EscalatorPortal.class,
                true,
                false
        );

        leftBlockN1N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                -1,
                -1,
                EscalatorPortal.class,
                false,
                true
        );

        leftBlockN10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                -1,
                0,
                EscalatorPortal.class,
                true,
                false
        );

        leftBlock0N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                0,
                -1,
                EscalatorPortal.class,
                false,
                false
        );

        leftView.getAmenityBlockTemplates().add(leftBlock00);
        leftView.getAmenityBlockTemplates().add(leftBlockN1N1);
        leftView.getAmenityBlockTemplates().add(leftBlockN10);
        leftView.getAmenityBlockTemplates().add(leftBlock0N1);

        escalatorPortalFootprint.addRotation(leftView);
    }

    protected EscalatorPortal(
            List<AmenityBlock> amenityBlocks,
            boolean enabled,
            Floor floorServed,
            EscalatorShaft escalatorShaft
    ) {
        super(amenityBlocks, enabled, floorServed);

        this.escalatorShaft = escalatorShaft;

        this.escalatorGraphic = new EscalatorGraphic(this);
    }

    public EscalatorShaft getEscalatorShaft() {
        return escalatorShaft;
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.escalatorGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.escalatorGraphic.getGraphicLocation();
    }

    @Override
    public String toString() {
        String string = "Escalator" + ((this.enabled) ? "" : " (disabled)");

        Floor floorServed = this.getPair().getFloorServed();
        int numberFloorServed = Main.simulator.getStation().getFloors().indexOf(floorServed) + 1;

        string += "\n" + "Connects to floor #" + numberFloorServed;
        string += "\n" + this.escalatorShaft.getEscalatorDirection();

        return string;
    }

    @Override
    public Passenger spawnPassenger() {
        return null;
    }

    // Escalator portal block
    public static class EscalatorPortalBlock extends Amenity.AmenityBlock {
        public static EscalatorPortal.EscalatorPortalBlock.EscalatorPortalBlockFactory escalatorPortalBlockFactory;

        static {
            escalatorPortalBlockFactory = new EscalatorPortal.EscalatorPortalBlock.EscalatorPortalBlockFactory();
        }

        private EscalatorPortalBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        // Escalator portal block factory
        public static class EscalatorPortalBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public EscalatorPortal.EscalatorPortalBlock create(
                    Patch patch,
                    boolean attractor,
                    boolean hasGraphic,
                    AmenityFootprint.Rotation.Orientation... orientation
            ) {
                return new EscalatorPortal.EscalatorPortalBlock(
                        patch,
                        attractor,
                        hasGraphic
                );
            }
        }
    }

    // Escalator portal factory
    public static class EscalatorPortalFactory extends PortalFactory {
        public EscalatorPortal create(
                List<AmenityBlock> amenityBlocks,
                boolean enabled,
                Floor floorServed,
                EscalatorShaft escalatorShaft
        ) {
            return new EscalatorPortal(
                    amenityBlocks,
                    enabled,
                    floorServed,
                    escalatorShaft
            );
        }
    }
}
