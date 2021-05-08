package com.crowdsimulation.model.core.environment.station.patch.patchobject;

import com.crowdsimulation.controller.graphics.amenity.graphic.AmenityGraphic;
import javafx.scene.image.Image;

public interface Drawable {
    AmenityGraphic getGraphicObject();

    String getGraphicURL();
}
