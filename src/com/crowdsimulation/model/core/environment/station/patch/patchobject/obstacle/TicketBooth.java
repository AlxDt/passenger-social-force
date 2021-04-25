package com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle;

import com.crowdsimulation.controller.graphics.amenity.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.TicketBoothGraphic;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBoothTransactionArea;
import javafx.scene.image.Image;

public class TicketBooth extends Obstacle implements Drawable {
    // Denotes the transaction area of this ticket booth
    private TicketBoothTransactionArea ticketBoothTransactionArea;

    // Factory for ticket booth creation
    public static final TicketBoothFactory ticketBoothFactory;

    // Handles how this ticket booth is displayed
    private final TicketBoothGraphic ticketBoothGraphic;

    static {
        ticketBoothFactory = new TicketBoothFactory();
    }

    protected TicketBooth(Patch patch) {
        super(patch);

        this.ticketBoothGraphic = new TicketBoothGraphic(this);
    }

    public TicketBoothTransactionArea getTicketBoothTransactionArea() {
        return ticketBoothTransactionArea;
    }

    public void setTicketBoothTransactionArea(TicketBoothTransactionArea ticketBoothTransactionArea) {
        this.ticketBoothTransactionArea = ticketBoothTransactionArea;
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.ticketBoothGraphic;
    }

    @Override
    public Image getGraphic() {
        return this.ticketBoothGraphic.getGraphic();
    }

    // Ticket booth factory
    public static class TicketBoothFactory extends ObstacleFactory {
        public TicketBooth create(Patch patch) {
            return new TicketBooth(
                    patch
            );
        }
    }

    @Override
    public String toString() {
        return "Ticket booth";
    }
}
