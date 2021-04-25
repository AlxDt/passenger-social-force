package com.crowdsimulation.controller.graphics.amenity;

import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;

public class TicketBoothGraphic extends AmenityGraphic implements Flippable {
    public TicketBoothGraphic(Amenity amenity) {
        super(amenity);
    }

    @Override
    public void flip() {
        // Get the next image in the list of graphics
        if (this.graphicIndex + 1 == this.graphics.size()) {
            this.graphicIndex = 0;
        } else {
            this.graphicIndex++;
        }
    }
}
