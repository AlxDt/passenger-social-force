package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;

public class TicketBoothTransactionArea extends Goal {
    // Denotes the ticket booth associated with this transaction area
    private TicketBooth ticketBooth;

    // Denotes the type of tickets the ticket booth this transaction area belongs to dispenses
    private TicketBoothType ticketBoothType;

    // Takes note of the passenger currently transacting in the ticket booth
    private Passenger passengerTransacting;

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

    // Ticket booth transaction area factory
    public static class TicketBoothTransactionAreaFactory extends AmenityFactory {
        @Override
        public TicketBoothTransactionArea create(Object... objects) {
            return new TicketBoothTransactionArea(
                    (Patch) objects[0],
                    (boolean) objects[1],
                    (int) objects[2],
                    (TicketBooth) objects[3],
                    (TicketBoothType) objects [4]
            );
        }
    }

    // Lists the types of tickets the ticket booth this transaction area belongs to dispenses
    public enum TicketBoothType {
        SINGLE_JOURNEY ("Single journey"),
        STORED_VALUE ("Stored value"),
        ALL_TICKET_TYPES ("All ticket types");

        private final String name;

        TicketBoothType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    // The drawing orientation of this ticket booth
    public enum DrawOrientation {
        UP,
        RIGHT,
        DOWN,
        LEFT
    }
}