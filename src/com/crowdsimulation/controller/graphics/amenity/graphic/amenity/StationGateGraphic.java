package com.crowdsimulation.controller.graphics.amenity.graphic.amenity;

import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;

public class StationGateGraphic extends AmenityGraphic implements Cyclable, Changeable {
    public static final int ROW_SPAN_VERTICAL = 1;
    public static final int COLUMN_SPAN_VERTICAL = 1;

    public static final int ROW_SPAN_HORIZONTAL = 1;
    public static final int COLUMN_SPAN_HORIZONTAL = 1;

    private static final int ROW_OFFSET = 0;
    private static final int COLUMN_OFFSET = 0;

    public StationGateGraphic(StationGate stationGate) {
        super(
                stationGate,
                GraphicsController.currentAmenityFootprint.getCurrentRotation().isVertical()
                        ? ROW_SPAN_VERTICAL : ROW_SPAN_HORIZONTAL,
                GraphicsController.currentAmenityFootprint.getCurrentRotation().isVertical()
                        ? COLUMN_SPAN_VERTICAL : COLUMN_SPAN_HORIZONTAL,
                ROW_OFFSET,
                COLUMN_OFFSET
        );
    }

    @Override
    public void cycle() {
        if (this.graphicIndex >= 0 && this.graphicIndex <= 2) {
            this.graphicIndex = (this.graphicIndex + 1) % 3;
        } else {
            this.graphicIndex = (this.graphicIndex + 1) % 3 + 3;
        }
    }

    @Override
    public void change() {
        if (this.graphicIndex >= 0 && this.graphicIndex <= 2) {
            this.graphicIndex += 3;
        } else {
            this.graphicIndex -= 3;
        }
    }
}
