package com.crowdsimulation.controller.graphics.amenity;

import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs.StairPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBoothTransactionArea;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Turnstile;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AmenityGraphic {
    public static final HashMap<Class<?>, List<Image>> AMENITY_GRAPHICS = new HashMap<>();

    static {
        // The designated graphic of the amenities
        final List<Image> stationGateGraphic = new ArrayList<>();
        stationGateGraphic.add(new Image("com/crowdsimulation/view/image/amenity/station_gate.png"));
        AMENITY_GRAPHICS.put(StationGate.class, stationGateGraphic);

        final List<Image> securityGraphic = new ArrayList<>();
        securityGraphic.add(new Image("com/crowdsimulation/view/image/amenity/security.png"));
        AMENITY_GRAPHICS.put(Security.class, securityGraphic);

        final List<Image> stairsGraphic = new ArrayList<>();
        stairsGraphic.add(new Image("com/crowdsimulation/view/image/amenity/stairs-left.png"));
        stairsGraphic.add(new Image("com/crowdsimulation/view/image/amenity/stairs-right.png"));
        AMENITY_GRAPHICS.put(StairPortal.class, stairsGraphic);

        final List<Image> escalatorGraphic = new ArrayList<>();
        escalatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/escalator_left_down.png"));
        escalatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/escalator_left_up.png"));
        escalatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/escalator_right_down.png"));
        escalatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/escalator_right_up.png"));
        AMENITY_GRAPHICS.put(EscalatorPortal.class, escalatorGraphic);

        final List<Image> elevatorGraphic = new ArrayList<>();
        elevatorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/elevator.png"));
        AMENITY_GRAPHICS.put(ElevatorPortal.class, elevatorGraphic);

        final List<Image> ticketBoothGraphic = new ArrayList<>();
        ticketBoothGraphic.add(new Image("com/crowdsimulation/view/image/amenity/ticket_booth_manual.png"));
        ticketBoothGraphic.add(new Image("com/crowdsimulation/view/image/amenity/ticket_booth_automatic.png"));
        AMENITY_GRAPHICS.put(TicketBooth.class, ticketBoothGraphic);

        final List<Image> ticketBoothTransactionAreaGraphic = new ArrayList<>();
        ticketBoothTransactionAreaGraphic.add(
                new Image("com/crowdsimulation/view/image/amenity/ticket_booth_transaction_area.png")
        );
        AMENITY_GRAPHICS.put(TicketBoothTransactionArea.class, ticketBoothTransactionAreaGraphic);

        final List<Image> turnstileGraphic = new ArrayList<>();
        turnstileGraphic.add(new Image("com/crowdsimulation/view/image/amenity/turnstile.png"));
        AMENITY_GRAPHICS.put(Turnstile.class, turnstileGraphic);

        final List<Image> trainDoorGraphic = new ArrayList<>();
        trainDoorGraphic.add(new Image("com/crowdsimulation/view/image/amenity/train_door.png"));
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
