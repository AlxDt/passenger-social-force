package com.crowdsimulation.model.core.environment.station.patch.patchobject;

import com.crowdsimulation.controller.graphics.amenity.AmenityGraphic;
import javafx.scene.image.Image;

public interface Drawable {
    AmenityGraphic getGraphicObject();

    Image getGraphic();
}
