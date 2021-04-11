package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.agent.passenger.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.FloorField;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;

import java.util.ArrayList;
import java.util.List;

public class TicketBoothTransactionArea extends Goal {
    // Denotes the ticket booth associated with this transaction area
    private TicketBooth ticketBooth;

    // Denotes the type of tickets the ticket booth this transaction area belongs to dispenses
    private TicketBoothType ticketBoothType;

    // Takes note of the passenger currently transacting in the ticket booth
    private Passenger passengerTransacting;

    // Denotes the floor field state needed to access the floor fields of this ticket booth transaction area
    private final QueueingFloorField.FloorFieldState ticketBoothTransactionAreaFloorFieldState;

    // Factory for ticket booth transaction area creation
    public static final TicketBoothTransactionAreaFactory ticketBoothTransactionAreaFactory;

    static {
        ticketBoothTransactionAreaFactory = new TicketBoothTransactionAreaFactory();
    }

    protected TicketBoothTransactionArea(
            Patch patch,
            boolean enabled,
            int waitingTime,
            TicketBooth ticketBooth,
            TicketBoothType ticketBoothType
    ) {
        super(
                patch,
                enabled,
                waitingTime,
                new QueueObject()
        );

        this.ticketBooth = ticketBooth;
        this.ticketBoothType = ticketBoothType;
        this.passengerTransacting = null;

        // Initialize this ticket booth transaction area's floor field state
        this.ticketBoothTransactionAreaFloorFieldState = new QueueingFloorField.FloorFieldState(
                PassengerMovement.Direction.BOARDING,
                PassengerMovement.State.IN_QUEUE,
                this
        );

        // Add a blank floor field
        QueueingFloorField queueingFloorField = QueueingFloorField.queueingFloorFieldFactory.create(this);

        // Using the floor field state defined earlier, create the floor field
        this.getQueueObject().getFloorFields().put(this.ticketBoothTransactionAreaFloorFieldState, queueingFloorField);
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

    public QueueingFloorField.FloorFieldState getTicketBoothTransactionAreaFloorFieldState() {
        return ticketBoothTransactionAreaFloorFieldState;
    }

    @Override
    public String toString() {
        return "Ticket booth transaction area";
    }

    @Override
    public List<QueueingFloorField.FloorFieldState> retrieveFloorFieldStates() {
        List<QueueingFloorField.FloorFieldState> floorFieldStates = new ArrayList<>();

        floorFieldStates.add(this.ticketBoothTransactionAreaFloorFieldState);

        return floorFieldStates;
    }

    @Override
    public QueueingFloorField retrieveFloorField(QueueingFloorField.FloorFieldState floorFieldState) {
        return this.getQueueObject().getFloorFields().get(
                floorFieldState
        );
    }

    @Override
    // Denotes whether the floor field for this ticket booth transaction area is complete
    public boolean isFloorFieldsComplete() {
        QueueingFloorField queueingFloorField = retrieveFloorField(this.ticketBoothTransactionAreaFloorFieldState);

        // The floor field of this queueable is complete when there are floor fields present and it has an apex patch
        return queueingFloorField.getApex() != null && !queueingFloorField.getAssociatedPatches().isEmpty();
    }

    @Override
    // Clear all floor fields of the given floor field state in this ticket booth transaction area
    public void deleteFloorField(QueueingFloorField.FloorFieldState floorFieldState) {
        QueueingFloorField queueingFloorField = retrieveFloorField(floorFieldState);

        QueueingFloorField.clearFloorField(
                queueingFloorField,
                floorFieldState
        );
    }

    @Override
    public void deleteAllFloorFields() {
        // Sweep through each and every floor field and delete them
        List<QueueingFloorField.FloorFieldState> floorFieldStates = retrieveFloorFieldStates();

        for (QueueingFloorField.FloorFieldState floorFieldState : floorFieldStates) {
            deleteFloorField(floorFieldState);
        }
    }

    // Ticket booth transaction area factory
    public static class TicketBoothTransactionAreaFactory extends GoalFactory {
        public TicketBoothTransactionArea create(
                Patch patch,
                boolean enabled,
                int waitingTime,
                TicketBooth ticketBooth,
                TicketBoothType ticketBoothType
        ) {
            return new TicketBoothTransactionArea(
                    patch,
                    enabled,
                    waitingTime,
                    ticketBooth,
                    ticketBoothType
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
