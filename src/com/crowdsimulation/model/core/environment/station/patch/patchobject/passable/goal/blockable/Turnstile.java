package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable;

import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;

import java.util.ArrayList;
import java.util.List;

public class Turnstile extends BlockableAmenity {
    // Denotes the current mode of this turnstile
    private TurnstileMode turnstileMode;

    // Denotes the floor field states needed to access the floor fields of this security gate
    private final QueueingFloorField.FloorFieldState turnstileFloorFieldStateBoarding;
    private final QueueingFloorField.FloorFieldState turnstileFloorFieldStateAlighting;

    // Factory for turnstile creation
    public static final TurnstileFactory turnstileFactory;

    static {
        turnstileFactory = new TurnstileFactory();
    }

    protected Turnstile(
            Patch patch,
            boolean enabled,
            int waitingTime,
            boolean blockEntry,
            TurnstileMode turnstileMode
    ) {
        super(
                patch,
                enabled,
                waitingTime,
                new QueueObject(),
                blockEntry
        );

        // Initialize this turnstile's floor field states
        this.turnstileFloorFieldStateBoarding = new QueueingFloorField.FloorFieldState(
                PassengerMovement.Direction.BOARDING,
                PassengerMovement.State.IN_QUEUE,
                this
        );

        this.turnstileFloorFieldStateAlighting = new QueueingFloorField.FloorFieldState(
                PassengerMovement.Direction.ALIGHTING,
                PassengerMovement.State.IN_QUEUE,
                this
        );

        this.turnstileMode = turnstileMode;

        // Add blank floor fields, one for each direction
        QueueingFloorField floorFieldBoarding = QueueingFloorField.queueingFloorFieldFactory.create(this);
        QueueingFloorField floorFieldAlighting = QueueingFloorField.queueingFloorFieldFactory.create(this);

        // Using the floor field states defined earlier, create the floor fields
        this.getQueueObject().getFloorFields().put(this.turnstileFloorFieldStateBoarding, floorFieldBoarding);
        this.getQueueObject().getFloorFields().put(this.turnstileFloorFieldStateAlighting, floorFieldAlighting);
    }

    public TurnstileMode getTurnstileMode() {
        return turnstileMode;
    }

    public void setTurnstileMode(TurnstileMode turnstileMode) {
        this.turnstileMode = turnstileMode;
    }

    public QueueingFloorField.FloorFieldState getTurnstileFloorFieldStateBoarding() {
        return turnstileFloorFieldStateBoarding;
    }

    public QueueingFloorField.FloorFieldState getTurnstileFloorFieldStateAlighting() {
        return turnstileFloorFieldStateAlighting;
    }

    @Override
    public String toString() {
        return "Turnstile";
    }

    @Override
    public List<QueueingFloorField.FloorFieldState> retrieveFloorFieldStates() {
        List<QueueingFloorField.FloorFieldState> floorFieldStates = new ArrayList<>();

        floorFieldStates.add(this.turnstileFloorFieldStateBoarding);
        floorFieldStates.add(this.turnstileFloorFieldStateAlighting);

        return floorFieldStates;
    }

    @Override
    public QueueingFloorField retrieveFloorField(QueueingFloorField.FloorFieldState floorFieldState) {
        return this.getQueueObject().getFloorFields().get(
                floorFieldState
        );
    }

    @Override
    // Denotes whether the floor field for this turnstile is complete
    public boolean isFloorFieldsComplete() {
        QueueingFloorField boardingFloorField;
        QueueingFloorField alightingFloorField;

        boolean boardingCheck;
        boolean alightingCheck;

        boardingFloorField = retrieveFloorField(turnstileFloorFieldStateBoarding);
        alightingFloorField = retrieveFloorField(turnstileFloorFieldStateAlighting);

        // Despite the actual mode of the turnstile, always require that the user fill all (boarding and alighting)
        // floor fields
        boardingCheck = boardingFloorField.getApex() != null && !boardingFloorField.getAssociatedPatches().isEmpty();
        alightingCheck = alightingFloorField.getApex() != null && !alightingFloorField.getAssociatedPatches().isEmpty();

        // The floor field of this queueable is complete when, for both floor fields, there are floor fields present and
        // apex patches are present
        return boardingCheck && alightingCheck;
    }

    // Clear all floor fields of the given floor field state in this turnstile
    @Override
    public void deleteFloorField(QueueingFloorField.FloorFieldState floorFieldState) {
        QueueingFloorField boardingFloorField = retrieveFloorField(floorFieldState);

        QueueingFloorField.clearFloorField(
                boardingFloorField,
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

    // Turnstile factory
    public static class TurnstileFactory extends GoalFactory {
        public Turnstile create(
                Patch patch,
                boolean enabled,
                int waitingTime,
                boolean blockEntry,
                TurnstileMode turnstileMode
        ) {
            return new Turnstile(
                    patch,
                    enabled,
                    waitingTime,
                    blockEntry,
                    turnstileMode
            );
        }
    }

    // Lists the possible modes of this turnstile
    public enum TurnstileMode {
        BOARDING("Boarding"),
        ALIGHTING("Alighting"),
        BIDIRECTIONAL("Bidirectional");

        private final String name;

        private TurnstileMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
