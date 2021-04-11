package com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBoothTransactionArea;

public class TicketBooth extends Obstacle {
    // Denotes the transaction area of this ticket booth
    private TicketBoothTransactionArea ticketBoothTransactionArea;

    // Factory for ticket booth creation
    public static final TicketBoothFactory ticketBoothFactory;

    static {
        ticketBoothFactory = new TicketBoothFactory();
    }

    protected TicketBooth(Patch patch) {
        super(patch);
    }

    public TicketBoothTransactionArea getTicketBoothTransactionArea() {
        return ticketBoothTransactionArea;
    }

    public void setTicketBoothTransactionArea(TicketBoothTransactionArea ticketBoothTransactionArea) {
        this.ticketBoothTransactionArea = ticketBoothTransactionArea;
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
