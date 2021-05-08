package com.crowdsimulation.controller.graphics.amenity.graphic;

import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;

public class GenericGraphic extends AmenityGraphic {
    private static final int ROW_SPAN_HORIZONTAL = 1;
    private static final int COLUMN_SPAN_HORIZONTAL = 1;

    private static final int ROW_SPAN_VERTICAL = 1;
    private static final int COLUMN_SPAN_VERTICAL = 1;

    public GenericGraphic(Amenity amenity) {
        super(
                amenity,
                GraphicsController.currentAmenityFootprint.getCurrentRotation().isVertical()
                        ? ROW_SPAN_VERTICAL : ROW_SPAN_HORIZONTAL,
                GraphicsController.currentAmenityFootprint.getCurrentRotation().isVertical()
                        ? COLUMN_SPAN_VERTICAL : COLUMN_SPAN_HORIZONTAL
        );
    }
}
