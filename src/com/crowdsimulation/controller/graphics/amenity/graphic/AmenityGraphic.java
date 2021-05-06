package com.crowdsimulation.controller.graphics.amenity.graphic;

import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs.StairPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Turnstile;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AmenityGraphic {
    public static final HashMap<Class<?>, List<Image>> AMENITY_GRAPHICS = new HashMap<>();

    static {
        // TODO: Modify graphics to fit new dimensions
        // The designated graphic of the amenities
        final List<Image> stationGateGraphic = new ArrayList<>();
        stationGateGraphic.add(new Image("com/crowdsimulation/view/image/amenity/stationgate/open/front/stationgate_open_front.png"));
        AMENITY_GRAPHICS.put(StationGate.class, stationGateGraphic);

        final List<Image> securityGraphic = new ArrayList<>();
        securityGraphic.add(new Image("com/crowdsimulation/view/image/amenity/security/front/security_front.png"));
        AMENITY_GRAPHICS.put(Security.class, securityGraphic);

        final List<Image> stairsGraphic = new ArrayList<>();
        stairsGraphic.add(new Image("com/crowdsimulation/view/image/amenity/stair/single/front/stair_single_front.png"));
        stairsGraphic.add(new Image("com/crowdsimulation/view/image/amenity/stair/single/right/stair_single_right.png"));
        stairsGraphic.add(new Image("com/crowdsimulation/view/image/amenity/stair/single/rear/stair_single_rear.png"));
        stairsGraphic.add(new Image("com/crowdsimulation/view/image/amenity/stair/single/left/stair_single_left.png"));
        AMENITY_GRAPHICS.put(StairPortal.class, stairsGraphic);

        final List<Image> escalatorGraphic = new ArrayList<>();
        escalatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/escalator/single/down/front/escalator_down_front.png"));
        escalatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/escalator/single/up/front/escalator_up_front.png"));
        escalatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/escalator/single/down/right/escalator_down_right.png"));
        escalatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/escalator/single/up/right/escalator_up_right.png"));
        escalatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/escalator/single/down/rear/escalator_down_rear.png"));
        escalatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/escalator/single/up/rear/escalator_up_rear.png"));
        escalatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/escalator/single/down/left/escalator_down_left.png"));
        escalatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/escalator/single/up/left/escalator_down_left.png"));
        AMENITY_GRAPHICS.put(EscalatorPortal.class, escalatorGraphic);

        final List<Image> elevatorGraphic = new ArrayList<>();
        elevatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/elevator/front/elevator_front.png"));
        elevatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/elevator/side/elevator_side.png"));
        AMENITY_GRAPHICS.put(ElevatorPortal.class, elevatorGraphic);

        final List<Image> ticketBoothGraphic = new ArrayList<>();
        ticketBoothGraphic.add(new Image("com/crowdsimulation/view/image/amenity/ticketbooth/manual/front/ticketbooth_manual_front.png"));
        ticketBoothGraphic.add(new Image("com/crowdsimulation/view/image/amenity/ticketbooth/automatic/front/ticketbooth_automatic_front.png"));
        ticketBoothGraphic.add(new Image("com/crowdsimulation/view/image/amenity/ticketbooth/manual/right/ticketbooth_manual_right.png"));
        ticketBoothGraphic.add(new Image("com/crowdsimulation/view/image/amenity/ticketbooth/automatic/right/ticketbooth_automatic_right.png"));
        ticketBoothGraphic.add(new Image("com/crowdsimulation/view/image/amenity/ticketbooth/manual/rear/ticketbooth_manual_rear.png"));
        ticketBoothGraphic.add(new Image("com/crowdsimulation/view/image/amenity/ticketbooth/automatic/rear/ticketbooth_automatic_rear.png"));
        ticketBoothGraphic.add(new Image("com/crowdsimulation/view/image/amenity/ticketbooth/manual/left/ticketbooth_manual_left.png"));
        ticketBoothGraphic.add(new Image("com/crowdsimulation/view/image/amenity/ticketbooth/automatic/left/ticketbooth_automatic_left.png"));
        AMENITY_GRAPHICS.put(TicketBooth.class, ticketBoothGraphic);

        final List<Image> turnstileGraphic = new ArrayList<>();
        turnstileGraphic.add(new Image("com/crowdsimulation/view/image/amenity/turnstile/front/turnstile_front.png"));
        turnstileGraphic.add(new Image("com/crowdsimulation/view/image/amenity/turnstile/right/turnstile_right.png"));
        turnstileGraphic.add(new Image("com/crowdsimulation/view/image/amenity/turnstile/rear/turnstile_rear.png"));
        turnstileGraphic.add(new Image("com/crowdsimulation/view/image/amenity/turnstile/left/turnstile_left.png"));
        AMENITY_GRAPHICS.put(Turnstile.class, turnstileGraphic);

        final List<Image> trainDoorGraphic = new ArrayList<>();
        trainDoorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/traindoor/traindoor.png"));
        AMENITY_GRAPHICS.put(TrainDoor.class, trainDoorGraphic);
    }

    private final Amenity amenity;

    protected final List<Image> graphics;
    protected int graphicIndex;
    private Image image;

    public AmenityGraphic(Amenity amenity) {
        this.amenity = amenity;

        this.graphics = AMENITY_GRAPHICS.get(amenity.getClass());
        this.graphicIndex = 0;
        this.image = this.graphics.get(this.graphicIndex);
    }

    public Amenity getAmenity() {
        return amenity;
    }

    public Image getGraphic() {
        return this.graphics.get(this.graphicIndex);
    }
}
