package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable;

import com.crowdsimulation.model.core.agent.passenger.PassengerMovement;
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

    public Security(
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
        QueueingFloorField queueingFloorField = new QueueingFloorField(this);

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
    public void clearFloorFields(QueueingFloorField.FloorFieldState floorFieldState) {
        QueueingFloorField queueingFloorField = retrieveFloorField(floorFieldState);

        QueueingFloorField.clearFloorField(
                queueingFloorField,
                floorFieldState
        );
    }

    // Security factory
    public static class SecurityFactory extends AmenityFactory {
        @Override
        public Security create(Object... objects) {
            return new Security(
                    (Patch) objects[0],
                    (boolean) objects[1],
                    (int) objects[2],
                    (boolean) objects[3]
            );
        }
    }
}
