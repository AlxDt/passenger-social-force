package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs;

import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.controller.graphics.amenity.graphic.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.StairGraphic;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;

import java.util.List;

public class StairPortal extends Portal {
    // Denotes the stair shaft which contains this stair portal
    private final StairShaft stairShaft;

    // Factory for stair portal creation
    public static final StairPortalFactory stairPortalFactory;

    // Handles how this stair portal is displayed
    private final StairGraphic stairGraphic;

    // Denotes the footprint of this amenity when being drawn
    public static final AmenityFootprint stairPortalFootprint;

    static {
        stairPortalFactory = new StairPortalFactory();

        // Initialize this amenity's footprints
        stairPortalFootprint = new AmenityFootprint();

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
                StairPortal.class,
                true,
                false
        );

        upBlockN10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                -1,
                0,
                StairPortal.class,
                false,
                true
        );

        upBlockN11 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                -1,
                1,
                StairPortal.class,
                false,
                false
        );

        upBlock01 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                0,
                1,
                StairPortal.class,
                true,
                false
        );

        upView.getAmenityBlockTemplates().add(upBlock00);
        upView.getAmenityBlockTemplates().add(upBlockN10);
        upView.getAmenityBlockTemplates().add(upBlockN11);
        upView.getAmenityBlockTemplates().add(upBlock01);

        stairPortalFootprint.addRotation(upView);

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
                StairPortal.class,
                true,
                true
        );

        rightBlock01 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                0,
                1,
                StairPortal.class,
                false,
                false
        );

        rightBlock10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                1,
                0,
                StairPortal.class,
                true,
                false
        );

        rightBlock11 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                1,
                1,
                StairPortal.class,
                false,
                false
        );

        rightView.getAmenityBlockTemplates().add(rightBlock00);
        rightView.getAmenityBlockTemplates().add(rightBlock01);
        rightView.getAmenityBlockTemplates().add(rightBlock10);
        rightView.getAmenityBlockTemplates().add(rightBlock11);

        stairPortalFootprint.addRotation(rightView);

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
                StairPortal.class,
                true,
                false
        );

        downBlock0N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                0,
                -1,
                StairPortal.class,
                true,
                true
        );

        downBlock1N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                1,
                -1,
                StairPortal.class,
                false,
                false
        );

        downBlock10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                1,
                0,
                StairPortal.class,
                false,
                false
        );

        downView.getAmenityBlockTemplates().add(downBlock00);
        downView.getAmenityBlockTemplates().add(downBlock0N1);
        downView.getAmenityBlockTemplates().add(downBlock1N1);
        downView.getAmenityBlockTemplates().add(downBlock10);

        stairPortalFootprint.addRotation(downView);

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
                StairPortal.class,
                true,
                false
        );

        leftBlockN1N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                -1,
                -1,
                StairPortal.class,
                false,
                true
        );

        leftBlockN10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                -1,
                0,
                StairPortal.class,
                true,
                false
        );

        leftBlock0N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                0,
                -1,
                StairPortal.class,
                false,
                false
        );

        leftView.getAmenityBlockTemplates().add(leftBlock00);
        leftView.getAmenityBlockTemplates().add(leftBlockN1N1);
        leftView.getAmenityBlockTemplates().add(leftBlockN10);
        leftView.getAmenityBlockTemplates().add(leftBlock0N1);

        stairPortalFootprint.addRotation(leftView);
    }

    protected StairPortal(
            List<AmenityBlock> amenityBlocks,
            boolean enabled,
            Floor floorServed,
            StairShaft stairShaft
    ) {
        super(amenityBlocks, enabled, floorServed);

        this.stairShaft = stairShaft;

        this.stairGraphic = new StairGraphic(this);
    }

    public StairShaft getStairShaft() {
        return stairShaft;
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.stairGraphic;
    }

    @Override
    public String getGraphicURL() {
        return this.stairGraphic.getGraphicURL();
    }

    @Override
    public String toString() {
        return "Stair";
    }

    // Stair portal block
    public static class StairPortalBlock extends Amenity.AmenityBlock {
        public static StairPortal.StairPortalBlock.StairPortalBlockFactory stairPortalBlockFactory;

        static {
            stairPortalBlockFactory = new StairPortal.StairPortalBlock.StairPortalBlockFactory();
        }

        private StairPortalBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        // Stair portal block factory
        public static class StairPortalBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public StairPortal.StairPortalBlock create(
                    Patch patch,
                    boolean attractor,
                    boolean hasGraphic,
                    AmenityFootprint.Rotation.Orientation... orientation
            ) {
                return new StairPortal.StairPortalBlock(
                        patch,
                        attractor,
                        hasGraphic
                );
            }
        }
    }

    // Stair portal factory
    public static class StairPortalFactory extends PortalFactory {
        public StairPortal create(
                List<AmenityBlock> amenityBlocks,
                boolean enabled,
                Floor floorServed,
                StairShaft stairShaft
        ) {
            return new StairPortal(
                    amenityBlocks,
                    enabled,
                    floorServed,
                    stairShaft
            );
        }
    }
}
