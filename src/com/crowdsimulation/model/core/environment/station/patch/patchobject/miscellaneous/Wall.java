package com.crowdsimulation.model.core.environment.station.patch.patchobject.miscellaneous;

import com.crowdsimulation.controller.graphics.amenity.editor.WallEditor;
import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.controller.graphics.amenity.graphic.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.WallGraphic;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;

import java.util.List;

public class Wall extends Obstacle {
    // Denotes the type of this wall
    private WallType wallType;

    // Factory for wall creation
    public static final WallFactory wallFactory;

    // Handles how the wall is displayed
    private final WallGraphic wallGraphic;

    // Denotes the footprint of this amenity when being drawn
    public static final AmenityFootprint wallFootprint;

    // Denotes the editor of this amenity
    public static final WallEditor wallEditor;

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
                Wall.class,
                false,
                true
        );

        upView.getAmenityBlockTemplates().add(block00);

        wallFootprint.addRotation(upView);

        // Initialize the editor
        wallEditor = new WallEditor();
    }

    protected Wall(List<AmenityBlock> amenityBlocks, WallType wallType) {
        super(amenityBlocks);

        this.wallType = wallType;

        this.wallGraphic = new WallGraphic(this);
    }

    public WallType getWallType() {
        return wallType;
    }

    public void setWallType(WallType wallType) {
        this.wallType = wallType;
    }

    @Override
    public String toString() {
        return this.wallType.toString();
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.wallGraphic;
    }

    @Override
    public String getGraphicURL() {
        return this.wallGraphic.getGraphicURL();
    }

    // The different types this wall has
    public enum WallType {
        WALL("Wall"),
        BUILDING_COLUMN("Building column"),
        BELT_BARRIER("Belt barrier"),
        METAL_BARRIER("Metal barrier");

        private final String name;

        WallType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
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
        public Wall create(List<AmenityBlock> amenityBlocks, WallType wallType) {
            return new Wall(
                    amenityBlocks,
                    wallType
            );
        }
    }
}
