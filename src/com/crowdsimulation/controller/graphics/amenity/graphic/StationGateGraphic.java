package com.crowdsimulation.controller.graphics.amenity.graphic;

import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;

public class StationGateGraphic extends AmenityGraphic implements Cyclable, Changeable {
    private static final int ROW_SPAN_VERTICAL = 1;
    private static final int COLUMN_SPAN_VERTICAL = 1;

    private static final int ROW_SPAN_HORIZONTAL = 1;
    private static final int COLUMN_SPAN_HORIZONTAL = 1;

    public StationGateGraphic(StationGate stationGate) {
        super(
                stationGate,
                GraphicsController.currentAmenityFootprint.getCurrentRotation().isVertical()
                        ? ROW_SPAN_VERTICAL : ROW_SPAN_HORIZONTAL,
                GraphicsController.currentAmenityFootprint.getCurrentRotation().isVertical()
                        ? COLUMN_SPAN_VERTICAL : COLUMN_SPAN_HORIZONTAL
        );

        this.graphicIndex = 0;

        change(stationGate);
    }

    @Override
    public void cycle() {
        // Indices 0 - 1: Open station gate
        if (this.graphicIndex == 0 || this.graphicIndex == 1) {
            if (this.graphicIndex == 0) {
                this.graphicIndex++;
            } else {
                this.graphicIndex--;
            }
        } else {
            // Indices 2 - 3: Closed station gate
            if (this.graphicIndex == 2) {
                this.graphicIndex++;
            } else {
                this.graphicIndex--;
            }
        }
    }

    @Override
    public void change(Drawable drawable) {
        StationGate stationGate = (StationGate) drawable;

        if (stationGate.isEnabled()) {
            if (this.graphicIndex == 2 || this.graphicIndex == 3) {
                if (this.graphicIndex == 2) {
                    this.graphicIndex = 0;
                } else {
                    this.graphicIndex = 1;
                }
            } else {
                this.graphicIndex = 0;
            }
        } else {
            if (this.graphicIndex == 0 || this.graphicIndex == 1) {
                if (this.graphicIndex == 0) {
                    this.graphicIndex = 2;
                } else {
                    this.graphicIndex = 3;
                }
            } else {
                this.graphicIndex = 2;
            }
        }
    }
}
