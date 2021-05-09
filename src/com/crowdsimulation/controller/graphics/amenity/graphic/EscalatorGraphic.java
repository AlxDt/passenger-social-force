package com.crowdsimulation.controller.graphics.amenity.graphic;

import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorShaft;

public class EscalatorGraphic extends AmenityGraphic implements Cyclable, Changeable {
    private static final int ROW_SPAN_VERTICAL = 2;
    private static final int COLUMN_SPAN_VERTICAL = 1;

    private static final int ROW_SPAN_HORIZONTAL = 1;
    private static final int COLUMN_SPAN_HORIZONTAL = 2;

    public EscalatorGraphic(EscalatorPortal escalatorPortal) {
        super(
                escalatorPortal,
                GraphicsController.currentAmenityFootprint.getCurrentRotation().isVertical()
                        ? ROW_SPAN_VERTICAL : ROW_SPAN_HORIZONTAL,
                GraphicsController.currentAmenityFootprint.getCurrentRotation().isVertical()
                        ? COLUMN_SPAN_VERTICAL : COLUMN_SPAN_HORIZONTAL
        );
    }

    public void setGraphicToDirection(EscalatorShaft.EscalatorDirection escalatorDirection) {
        if (escalatorDirection == EscalatorShaft.EscalatorDirection.DOWN) {
            this.graphicIndex = 0;
        } else {
            this.graphicIndex = 1;
        }
    }


    @Override
    public void cycle() {
        // Cycle through the graphics list in steps of two
        this.graphicIndex = (this.graphicIndex + 2) % this.graphics.size();
    }

    @Override
    public void change(Drawable drawable) {
        // Indices 0 - 1: left-facing escalator
        if (this.graphicIndex == 0 || this.graphicIndex == 1) {
            if (this.graphicIndex == 0) {
                this.graphicIndex++;
            } else {
                this.graphicIndex--;
            }
        } else {
            // Indices 2 - 3: right-facing escalator
            if (this.graphicIndex == 2) {
                this.graphicIndex++;
            } else {
                this.graphicIndex--;
            }
        }
    }
}
