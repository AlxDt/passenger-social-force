package com.crowdsimulation.controller.graphics.amenity.graphic;

import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs.StairPortal;

public class StairGraphic extends AmenityGraphic implements Cyclable {
    private static final int ROW_SPAN_VERTICAL = 2;
    private static final int COLUMN_SPAN_VERTICAL = 1;

    private static final int ROW_SPAN_HORIZONTAL = 1;
    private static final int COLUMN_SPAN_HORIZONTAL = 2;

    public StairGraphic(StairPortal stairPortal) {
        super(
                stairPortal,
                GraphicsController.currentAmenityFootprint.getCurrentRotation().isVertical()
                        ? ROW_SPAN_VERTICAL : ROW_SPAN_HORIZONTAL,
                GraphicsController.currentAmenityFootprint.getCurrentRotation().isVertical()
                        ? COLUMN_SPAN_VERTICAL : COLUMN_SPAN_HORIZONTAL
        );
    }

    @Override
    public void cycle() {
        // Get the next image in the list of graphics
        if (this.graphicIndex + 1 == this.graphics.size()) {
            this.graphicIndex = 0;
        } else {
            this.graphicIndex++;
        }
    }
}