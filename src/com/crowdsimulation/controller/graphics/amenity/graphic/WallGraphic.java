package com.crowdsimulation.controller.graphics.amenity.graphic;

import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle.Wall;

public class WallGraphic extends AmenityGraphic implements Cyclable, Changeable {
    private static final int ROW_SPAN_VERTICAL = 1;
    private static final int COLUMN_SPAN_VERTICAL = 1;

    private static final int ROW_SPAN_HORIZONTAL = 1;
    private static final int COLUMN_SPAN_HORIZONTAL = 1;

    public WallGraphic(Wall wall) {
        super(
                wall,
                GraphicsController.currentAmenityFootprint.getCurrentRotation().isVertical()
                        ? ROW_SPAN_VERTICAL : ROW_SPAN_HORIZONTAL,
                GraphicsController.currentAmenityFootprint.getCurrentRotation().isVertical()
                        ? COLUMN_SPAN_VERTICAL : COLUMN_SPAN_HORIZONTAL
        );

        this.graphicIndex = 0;

        change(wall);
    }

    @Override
    public void cycle() {
        switch (this.graphicIndex) {
            case 2:
            case 4:
                this.graphicIndex++;

                break;
            case 3:
            case 5:
                this.graphicIndex--;

                break;
        }
    }

    @Override
    public void change(Drawable drawable) {
        Wall wall = (Wall) drawable;

        switch (wall.getWallType()) {
            case WALL:
                this.graphicIndex = 0;

                break;
            case BUILDING_COLUMN:
                this.graphicIndex = 1;

                break;
            case BELT_BARRIER:
                if (this.graphicIndex == 4 || this.graphicIndex == 5) {
                    if (this.graphicIndex == 4) {
                        this.graphicIndex = 2;
                    } else {
                        this.graphicIndex = 3;
                    }
                } else {
                    this.graphicIndex = 2;
                }

                break;
            case METAL_BARRIER:
                if (this.graphicIndex == 3 || this.graphicIndex == 4) {
                    if (this.graphicIndex == 3) {
                        this.graphicIndex = 4;
                    } else {
                        this.graphicIndex = 5;
                    }
                } else {
                    this.graphicIndex = 4;
                }

                break;
        }
    }
}