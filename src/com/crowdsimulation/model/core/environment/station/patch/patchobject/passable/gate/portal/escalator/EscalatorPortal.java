package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator;

import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.controller.graphics.amenity.graphic.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.EscalatorGraphic;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;
import javafx.scene.image.Image;

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

        AmenityFootprint.Rotation upView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.UP);

        upBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                0,
                0,
                EscalatorPortal.class,
                true,
                false
        );

        upBlockN10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                -1,
                0,
                EscalatorPortal.class,
                false,
                true
        );

        upView.getAmenityBlockTemplates().add(upBlock00);
        upView.getAmenityBlockTemplates().add(upBlockN10);

        escalatorPortalFootprint.addRotation(upView);

        // Right view
        AmenityFootprint.Rotation.AmenityBlockTemplate rightBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate rightBlock01;

        AmenityFootprint.Rotation rightView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.RIGHT);

        rightBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                0,
                0,
                EscalatorPortal.class,
                true,
                true
        );

        rightBlock01 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                0,
                1,
                EscalatorPortal.class,
                false,
                false
        );

        rightView.getAmenityBlockTemplates().add(rightBlock00);
        rightView.getAmenityBlockTemplates().add(rightBlock01);

        escalatorPortalFootprint.addRotation(rightView);

        // Down view
        AmenityFootprint.Rotation.AmenityBlockTemplate downBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate downBlock10;

        AmenityFootprint.Rotation downView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.DOWN);

        downBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                0,
                0,
                EscalatorPortal.class,
                true,
                true
        );

        downBlock10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                1,
                0,
                EscalatorPortal.class,
                false,
                false
        );

        downView.getAmenityBlockTemplates().add(downBlock00);
        downView.getAmenityBlockTemplates().add(downBlock10);

        escalatorPortalFootprint.addRotation(downView);

        // Left view
        AmenityFootprint.Rotation.AmenityBlockTemplate leftBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate leftBlock0N1;

        AmenityFootprint.Rotation leftView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.LEFT);

        leftBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                0,
                0,
                EscalatorPortal.class,
                false,
                true
        );

        leftBlock0N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                0,
                -1,
                EscalatorPortal.class,
                true,
                false
        );

        leftView.getAmenityBlockTemplates().add(leftBlock00);
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
    public Image getGraphic() {
        return this.escalatorGraphic.getGraphic();
    }

    @Override
    public String toString() {
        return "Escalator";
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
                    boolean hasGraphic
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
