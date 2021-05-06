package com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle;

import com.crowdsimulation.controller.graphics.amenity.graphic.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.TicketBoothGraphic;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBooth;
import javafx.scene.image.Image;

public class OldTicketBooth/* extends Obstacle implements Drawable*/ {
/*    // Denotes the transaction area of this ticket booth
    private TicketBooth ticketBoothTransactionArea;

    // Factory for ticket booth creation
    public static final OldTicketBoothFactory ticketBoothFactory;

    // Handles how this ticket booth is displayed
    private final TicketBoothGraphic ticketBoothGraphic;

    static {
        ticketBoothFactory = new OldTicketBoothFactory();
    }

    protected OldTicketBooth(Patch patch) {
        super(patch);

        this.ticketBoothGraphic = new TicketBoothGraphic(this);
    }

    public TicketBooth getTicketBoothTransactionArea() {
        return ticketBoothTransactionArea;
    }

    public void setTicketBoothTransactionArea(TicketBooth ticketBoothTransactionArea) {
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
    public static class OldTicketBoothFactory extends ObstacleFactory {
        public OldTicketBooth create(Patch patch) {
            return new OldTicketBooth(
                    patch
            );
        }
    }

    @Override
    public String toString() {
        return "Ticket booth";
    }*/
}
