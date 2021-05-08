package com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle;

import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;

import java.util.List;

public class Wall extends Obstacle {
    // Factory for wall creation
    public static final WallFactory wallFactory;

    // Denotes the footprint of this amenity when being drawn
    public static final AmenityFootprint wallFootprint;

    static {
        wallFactory = new WallFactory();

        // Initialize this amenity's footprints
        wallFootprint = new AmenityFootprint();

        // Up view
        AmenityFootprint.Rotation upView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.UP);

        AmenityFootprint.Rotation.AmenityBlockTemplate block00
                = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                0,
                0,
                Security.class,
                true,
                true
        );

        upView.getAmenityBlockTemplates().add(block00);

        wallFootprint.addRotation(upView);
    }

    protected Wall(List<AmenityBlock> amenityBlocks) {
        super(amenityBlocks);
    }

    @Override
    public String toString() {
        return "Wall";
    }

    // Wall block
    public static class WallBlock extends Amenity.AmenityBlock {
        public static Wall.WallBlock.WallBlockFactory wallBlockFactory;

        static {
            wallBlockFactory = new Wall.WallBlock.WallBlockFactory();
        }

        private WallBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        // Wall block factory
        public static class WallBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Wall.WallBlock create(
                    Patch patch,
                    boolean attractor,
                    boolean hasGraphic,
                    AmenityFootprint.Rotation.Orientation... orientation
            ) {
                return new Wall.WallBlock(
                        patch,
                        attractor,
                        hasGraphic
                );
            }
        }
    }

    // Wall factory
    public static class WallFactory extends ObstacleFactory {
        public Wall create(List<AmenityBlock> amenityBlocks) {
            return new Wall(
                    amenityBlocks
            );
        }
    }
}
