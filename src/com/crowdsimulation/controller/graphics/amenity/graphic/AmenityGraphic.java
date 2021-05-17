package com.crowdsimulation.controller.graphics.amenity.graphic;

import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.model.core.environment.Environment;
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

public abstract class AmenityGraphic implements Environment {
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
        stairsGraphic.add("com/crowdsimulation/view/image/amenity/stair/lower/front/stair_lower_front.png");
        stairsGraphic.add("com/crowdsimulation/view/image/amenity/stair/lower/right/stair_lower_right.png");
        stairsGraphic.add("com/crowdsimulation/view/image/amenity/stair/lower/rear/stair_lower_rear.png");
        stairsGraphic.add("com/crowdsimulation/view/image/amenity/stair/lower/left/stair_lower_left.png");
        stairsGraphic.add("com/crowdsimulation/view/image/amenity/stair/upper/front/stair_upper_front.png");
        stairsGraphic.add("com/crowdsimulation/view/image/amenity/stair/upper/right/stair_upper_right.png");
        stairsGraphic.add("com/crowdsimulation/view/image/amenity/stair/upper/rear/stair_upper_rear.png");
        stairsGraphic.add("com/crowdsimulation/view/image/amenity/stair/upper/left/stair_upper_left.png");
        AMENITY_GRAPHICS.put(StairPortal.class, stairsGraphic);

        final List<String> escalatorGraphic = new ArrayList<>();
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/lower/front/up/escalator_lower_front_up.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/lower/front/down/escalator_lower_front_down.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/lower/right/up/escalator_lower_right_up.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/lower/right/down/escalator_lower_right_down.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/lower/rear/up/escalator_lower_rear_up.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/lower/rear/down/escalator_lower_rear_down.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/lower/left/up/escalator_lower_left_up.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/lower/left/down/escalator_lower_left_down.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/upper/front/up/escalator_upper_front_up.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/upper/front/down/escalator_upper_front_down.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/upper/right/up/escalator_upper_right_up.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/upper/right/down/escalator_upper_right_down.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/upper/rear/up/escalator_upper_rear_up.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/upper/rear/down/escalator_upper_rear_down.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/upper/left/up/escalator_upper_left_up.png");
        escalatorGraphic.add("com/crowdsimulation/view/image/amenity/escalator/upper/left/down/escalator_upper_left_down.png");
        AMENITY_GRAPHICS.put(EscalatorPortal.class, escalatorGraphic);

        final List<String> elevatorGraphic = new ArrayList<>();
        elevatorGraphic.add("com/crowdsimulation/view/image/amenity/elevator/front/elevator_front.png");
        elevatorGraphic.add("com/crowdsimulation/view/image/amenity/elevator/right/elevator_right.png");
        elevatorGraphic.add("com/crowdsimulation/view/image/amenity/elevator/rear/elevator_rear.png");
        elevatorGraphic.add("com/crowdsimulation/view/image/amenity/elevator/left/elevator_left.png");
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
        trainDoorGraphic.add("com/crowdsimulation/view/image/amenity/traindoor/nw/traindoor_nw.png");
        trainDoorGraphic.add("com/crowdsimulation/view/image/amenity/traindoor/se/traindoor_se.png");
        AMENITY_GRAPHICS.put(TrainDoor.class, trainDoorGraphic);

        final List<String> wallGraphic = new ArrayList<>();
        wallGraphic.add("com/crowdsimulation/view/image/amenity/obstacle/wall/wall.png");
        wallGraphic.add("com/crowdsimulation/view/image/amenity/obstacle/post/post.png");
        wallGraphic.add("com/crowdsimulation/view/image/amenity/obstacle/beltbarrier/front/beltbarrier_front.png");
        wallGraphic.add("com/crowdsimulation/view/image/amenity/obstacle/beltbarrier/side/beltbarrier_side.png");
        wallGraphic.add("com/crowdsimulation/view/image/amenity/obstacle/metalbarrier/front/metalbarrier_front.png");
        wallGraphic.add("com/crowdsimulation/view/image/amenity/obstacle/metalbarrier/side/metalbarrier_side.png");
        AMENITY_GRAPHICS.put(Wall.class, wallGraphic);
    }

    private final Amenity amenity;

    protected final List<String> graphics;
    protected int graphicIndex;

    // Denotes the rows and columns spanned by this graphic
    private final AmenityGraphicScale amenityGraphicScale;

    // Denotes the offset of this graphic
    private final AmenityGraphicOffset amenityGraphicOffset;

    public AmenityGraphic(Amenity amenity, int rowSpan, int columnSpan, int rowOffset, int columnOffset) {
        this.amenity = amenity;

        this.amenityGraphicScale = new AmenityGraphicScale(rowSpan, columnSpan);
        this.amenityGraphicOffset = new AmenityGraphicOffset(rowOffset, columnOffset);

        this.graphics = AMENITY_GRAPHICS.get(amenity.getClass());
        this.graphicIndex = 0;
    }

    public AmenityGraphicScale getAmenityGraphicScale() {
        return amenityGraphicScale;
    }

    public AmenityGraphicOffset getAmenityGraphicOffset() {
        return amenityGraphicOffset;
    }

    public Amenity getAmenity() {
        return amenity;
    }

    public String getGraphicURL() {
        return this.graphics.get(this.graphicIndex);
    }

    public static class AmenityGraphicScale implements Environment {
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

    public static class AmenityGraphicOffset {
        private int rowOffset;
        private int columnOffset;

        public AmenityGraphicOffset(int rowOffset, int columnOffset) {
            this.rowOffset = rowOffset;
            this.columnOffset = columnOffset;
        }

        public int getRowOffset() {
            return rowOffset;
        }

        public void setRowOffset(int rowOffset) {
            this.rowOffset = rowOffset;
        }

        public int getColumnOffset() {
            return columnOffset;
        }

        public void setColumnOffset(int columnOffset) {
            this.columnOffset = columnOffset;
        }
    }
}
