package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle.TicketBooth;

public class TicketBoothTransactionArea extends Goal {
    // Denotes the ticket booth associated with this transaction area
    private TicketBooth ticketBooth;

    // Denotes the type of tickets the ticket booth this transaction area belongs to dispenses
    private TicketBoothType ticketBoothType;

    // Takes note of the passenger currently transacting in the ticket booth
    private Passenger passengerTransacting;

    public TicketBoothTransactionArea() {
        super(null,true, 0);

        this.ticketBooth = null;
        this.ticketBoothType = null;
        this.passengerTransacting = null;
    }

    public TicketBoothTransactionArea(
            Patch patch,
            boolean enabled,
            int waitingTime,
            TicketBooth ticketBooth,
            TicketBoothType ticketBoothType
    ) {
        super(patch, enabled, waitingTime);

        this.ticketBooth = ticketBooth;
        this.ticketBoothType = ticketBoothType;
        this.passengerTransacting = null;
    }

    public TicketBooth getTicketBooth() {
        return ticketBooth;
    }

    public void setTicketBooth(TicketBooth ticketBooth) {
        this.ticketBooth = ticketBooth;
    }

    public TicketBoothType getTicketBoothType() {
        return ticketBoothType;
    }

    public void setTicketBoothType(TicketBoothType ticketBoothType) {
        this.ticketBoothType = ticketBoothType;
    }

    public Passenger getPassengerTransacting() {
        return passengerTransacting;
    }

    public void setPassengerTransacting(Passenger passengerTransacting) {
        this.passengerTransacting = passengerTransacting;
    }

    @Override
    public String toString() {
        return "Ticket booth transaction area";
    }

    // Lists the types of tickets the ticket booth this transaction area belongs to dispenses
    public enum TicketBoothType {
        SINGLE_JOURNEY,
        STORED_VALUE,
        ALL_TICKET_TYPES
    }
}
