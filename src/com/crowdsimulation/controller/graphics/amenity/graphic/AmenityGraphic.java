package com.crowdsimulation.controller.graphics.amenity.graphic;

import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle.Wall;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs.StairPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Turnstile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AmenityGraphic {
    public static final HashMap<Class<?>, List<String>> AMENITY_GRAPHICS = new HashMap<>();

    static {
        // TODO: Modify graphics to fit new dimensions
        // The designated graphic of the amenities
        final List<String> stationGateGraphic = new ArrayList<>();
        stationGateGraphic.add("com/crowdsimulation/view/image/amenity/stationgate/open/front/stationgate_open_front.png");
        stationGateGraphic.add("com/crowdsimulation/view/image/amenity/stationgate/open/side/stationgate_open_side.png");
        stationGateGraphic.add("com/crowdsimulation/view/image/amenity/stationgate/closed/front/stationgate_closed_front.png");
        stationGateGraphic.add("com/crowdsimulation/view/image/amenity/stationgate/closed/side/stationgate_closed_side.png");
        AMENITY_GRAPHICS.put(StationGate.class, stationGateGraphic);

        final List<String> securityGraphic = new ArrayList<>();
        securityGraphic.add("com/crowdsimulation/view/image/amenity/security/front/security_front.png");
        securityGraphic.add("com/crowdsimulation/view/image/amenity/security/side/security_side.png");
        AMENITY_GRAPHICS.put(Security.class, securityGraphic);

        final List<String> stairsGraphic = new ArrayList<>();
        stairsGraphic.add("com/crowdsimulation/view/image/amenity/stair/single/front/stair_single_front.png");
        stairsGraphic.add("com/crowdsimulation/view/image/amenity/stair/single/right/stair_single_right.png");
        stairsGraphic.add("com/crowdsimulation/view/image/amenity/stair/single/rear/stair_single_rear.png");
        stairsGraphic.add("com/crowdsimulation/view/image/amenity/stair/single/left/stair_single_left.png");
        AMENITY_GRAPHICS.put(StairPortal.class, stairsGraphic);

        final List<String> escalatorGraphic = new ArrayList<>();
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/single/up/front/escalator_up_front.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/single/up/right/escalator_up_right.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/single/up/rear/escalator_up_rear.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/single/up/left/escalator_up_left.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/single/down/front/escalator_down_front.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/single/down/right/escalator_down_right.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/single/down/rear/escalator_down_rear.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/single/down/left/escalator_down_left.png");
        AMENITY_GRAPHICS.put(EscalatorPortal.class, escalatorGraphic);

        final List<String> elevatorGraphic = new ArrayList<>();
        elevatorGraphic.add("com/crowdsimulation/view/image/amenity/elevator/front/elevator_front.png");
        elevatorGraphic.add("com/crowdsimulation/view/image/amenity/elevator/side/elevator_side.png");
        AMENITY_GRAPHICS.put(ElevatorPortal.class, elevatorGraphic);

        final List<String> ticketBoothGraphic = new ArrayList<>();
        ticketBoothGraphic.add("com/crowdsimulation/view/image/amenity/ticketbooth/automatic/front/ticketbooth_automatic_front.png");
        ticketBoothGraphic.add("com/crowdsimulation/view/image/amenity/ticketbooth/automatic/right/ticketbooth_automatic_right.png");
        ticketBoothGraphic.add("com/crowdsimulation/view/image/amenity/ticketbooth/automatic/rear/ticketbooth_automatic_rear.png");
        ticketBoothGraphic.add("com/crowdsimulation/view/image/amenity/ticketbooth/automatic/left/ticketbooth_automatic_left.png");
        ticketBoothGraphic.add("com/crowdsimulation/view/image/amenity/ticketbooth/manual/front/ticketbooth_manual_front.png");
        ticketBoothGraphic.add("com/crowdsimulation/view/image/amenity/ticketbooth/manual/right/ticketbooth_manual_right.png");
        ticketBoothGraphic.add("com/crowdsimulation/view/image/amenity/ticketbooth/manual/rear/ticketbooth_manual_rear.png");
        ticketBoothGraphic.add("com/crowdsimulation/view/image/amenity/ticketbooth/manual/left/ticketbooth_manual_left.png");
        AMENITY_GRAPHICS.put(TicketBooth.class, ticketBoothGraphic);

        final List<String> turnstileGraphic = new ArrayList<>();
        turnstileGraphic.add("com/crowdsimulation/view/image/amenity/turnstile/front/turnstile_front.png");
        turnstileGraphic.add("com/crowdsimulation/view/image/amenity/turnstile/right/turnstile_right.png");
        turnstileGraphic.add("com/crowdsimulation/view/image/amenity/turnstile/rear/turnstile_rear.png");
        turnstileGraphic.add("com/crowdsimulation/view/image/amenity/turnstile/left/turnstile_left.png");
        AMENITY_GRAPHICS.put(Turnstile.class, turnstileGraphic);

        final List<String> trainDoorGraphic = new ArrayList<>();
        trainDoorGraphic.add("com/crowdsimulation/view/image/amenity/traindoor/traindoor.png");
        AMENITY_GRAPHICS.put(TrainDoor.class, trainDoorGraphic);

        final List<String> wallGraphic = new ArrayList<>();
        wallGraphic.add("com/crowdsimulation/view/image/amenity/obstacle/wall/wall.png");
        wallGraphic.add("com/crowdsimulation/view/image/amenity/obstacle/post/post.png");
        wallGraphic.add("com/crowdsimulation/view/image/amenity/obstacle/beltbarrier/front/beltbarrier_front.png");
        wallGraphic.add("com/crowdsimulation/view/image/amenity/obstacle/beltbarrier/side/belt_barrier_side.png");
        wallGraphic.add("com/crowdsimulation/view/image/amenity/obstacle/metalbarrier/front/metalbarrier_front.png");
        wallGraphic.add("com/crowdsimulation/view/image/amenity/obstacle/metalbarrier/side/metalbarrier_side.png");
        AMENITY_GRAPHICS.put(Wall.class, wallGraphic);
    }

    private final Amenity amenity;

    protected final List<String> graphics;
    protected int graphicIndex;

    // Only take note of the URL of the image, as it is expensive to store the image itself
    private String graphicURL;

    private final AmenityGraphicScale amenityGraphicScale;

    public AmenityGraphic(Amenity amenity, int rowSpan, int columnSpan) {
        this.amenity = amenity;

        this.amenityGraphicScale = new AmenityGraphicScale(rowSpan, columnSpan);

        this.graphics = AMENITY_GRAPHICS.get(amenity.getClass());
        this.graphicIndex = 0;
        this.graphicURL = this.graphics.get(this.graphicIndex);
    }

    public AmenityGraphicScale getAmenityGraphicScale() {
        return amenityGraphicScale;
    }

    public Amenity getAmenity() {
        return amenity;
    }

    public String getGraphicURL() {
        return this.graphics.get(this.graphicIndex);
    }

    public void setGraphicURL(String graphicURL) {
        this.graphicURL = graphicURL;
    }

    public static class AmenityGraphicScale {
        private int rowSpan;
        private int columnSpan;

        public AmenityGraphicScale(int rowSpan, int columnSpan) {
            this.rowSpan = rowSpan;
            this.columnSpan = columnSpan;
        }

        public int getRowSpan() {
            return rowSpan;
        }

        public void setRowSpan(int rowSpan) {
            this.rowSpan = rowSpan;
        }

        public int getColumnSpan() {
            return columnSpan;
        }

        public void setColumnSpan(int columnSpan) {
            this.columnSpan = columnSpan;
        }
    }
}
