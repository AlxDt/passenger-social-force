package com.crowdsimulation.model.core.environment.station.patch.patchobject;

import com.crowdsimulation.controller.graphics.amenity.graphic.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.GraphicLocation;

public interface Drawable {
    AmenityGraphic getGraphicObject();

    GraphicLocation getGraphicLocation();
}
