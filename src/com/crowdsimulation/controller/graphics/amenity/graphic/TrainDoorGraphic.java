package com.crowdsimulation.controller.graphics.amenity.graphic;

import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;

public class TrainDoorGraphic extends AmenityGraphic implements Changeable {
    private static final int ROW_SPAN_VERTICAL = 1;
    private static final int COLUMN_SPAN_VERTICAL = 4;

    public TrainDoorGraphic(TrainDoor trainDoor) {
        super(
                trainDoor,
                ROW_SPAN_VERTICAL,
                COLUMN_SPAN_VERTICAL
        );

        this.graphicIndex = 0;

        change(trainDoor);
    }

    @Override
    public void change(Drawable amenity) {
        TrainDoor trainDoor = (TrainDoor) amenity;

        switch (trainDoor.getPlatform()) {
            case NORTHBOUND:
            case WESTBOUND:
                this.graphicIndex = 0;

                break;
            case SOUTHBOUND:
            case EASTBOUND:
                this.graphicIndex = 1;

                break;
        }
    }
}
