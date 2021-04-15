package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable;

import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;

import java.util.ArrayList;
import java.util.List;

public class Security extends BlockableAmenity {
    // Denotes whether to block passengers from passing through
    private boolean blockEntry;

    // Denotes the floor field state needed to access the floor fields of this security gate
    private final QueueingFloorField.FloorFieldState securityFloorFieldState;

    // Factory for security gate creation
    public static final SecurityFactory securityFactory;

    static {
        securityFactory = new SecurityFactory();
    }

    protected Security(
            Patch patch,
            boolean enabled,
            int waitingTime,
            boolean blockPassengers
    ) {
        super(
                patch,
                enabled,
                waitingTime,
                new QueueObject(),
                blockPassengers
        );

        // Initialize this security gate's floor field state
        this.securityFloorFieldState = new QueueingFloorField.FloorFieldState(
                PassengerMovement.Direction.BOARDING,
                PassengerMovement.State.IN_QUEUE,
                this
        );

        // Add a blank floor field
        QueueingFloorField queueingFloorField = QueueingFloorField.queueingFloorFieldFactory.create(this);

        // Using the floor field state defined earlier, create the floor field
        this.getQueueObject().getFloorFields().put(this.securityFloorFieldState, queueingFloorField);
    }

    public boolean isBlockEntry() {
        return blockEntry;
    }

    public void setBlockEntry(boolean blockEntry) {
        this.blockEntry = blockEntry;
    }

    public QueueingFloorField.FloorFieldState getSecurityFloorFieldState() {
        return securityFloorFieldState;
    }

    @Override
    public String toString() {
        return "Security";
    }

    @Override
    public List<QueueingFloorField.FloorFieldState> retrieveFloorFieldStates() {
        List<QueueingFloorField.FloorFieldState> floorFieldStates = new ArrayList<>();

        floorFieldStates.add(this.securityFloorFieldState);

        return floorFieldStates;
    }

    @Override
    public QueueingFloorField retrieveFloorField(QueueingFloorField.FloorFieldState floorFieldState) {
        return this.getQueueObject().getFloorFields().get(
                floorFieldState
        );
    }

    @Override
    // Denotes whether the floor field for this security gate is complete
    public boolean isFloorFieldsComplete() {
        QueueingFloorField queueingFloorField = retrieveFloorField(this.securityFloorFieldState);

        // The floor field of this queueable is complete when there are floor fields present and it has an apex patch
        return queueingFloorField.getApex() != null && !queueingFloorField.getAssociatedPatches().isEmpty();
    }

    // Clear all floor fields of the given floor field state in this security gate
    @Override
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

    // Security factory
    public static class SecurityFactory extends GoalFactory {
        public Security create(
                Patch patch,
                boolean enabled,
                int waitingTime,
                boolean blockPassengers
        ) {
            return new Security(
                    patch,
                    enabled,
                    waitingTime,
                    blockPassengers
            );
        }
    }
}
