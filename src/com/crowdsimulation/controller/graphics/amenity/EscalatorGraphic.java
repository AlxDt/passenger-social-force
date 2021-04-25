package com.crowdsimulation.controller.graphics.amenity;

import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorShaft;

public class EscalatorGraphic extends AmenityGraphic implements Flippable, Changeable {
    public EscalatorGraphic(Amenity amenity) {
        super(amenity);
    }

    public void setGraphicToDirection(EscalatorShaft.EscalatorDirection escalatorDirection) {
        if (escalatorDirection == EscalatorShaft.EscalatorDirection.DOWN) {
            this.graphicIndex = 0;
        } else {
            this.graphicIndex = 1;
        }
    }

    @Override
    public void change() {
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

    @Override
    public void flip() {
        // Cycle through the graphics list in steps of two
        this.graphicIndex = (this.graphicIndex + 2) % this.graphics.size();
    }
}
