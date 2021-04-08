package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator;

import com.crowdsimulation.model.core.agent.passenger.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;

import java.util.ArrayList;
import java.util.List;

public class ElevatorPortal extends Portal implements Queueable {
    // Denotes the elevator shaft which contains this elevator portal
    private final ElevatorShaft elevatorShaft;

    // Denotes the queueing object associated with all goals like this
    private final QueueObject queueObject;

    // Denotes the floor field state needed to access the floor fields of this security gate
    private final QueueingFloorField.FloorFieldState elevatorPortalFloorFieldState;

    public ElevatorPortal(
            Patch patch,
            boolean enabled,
            Floor floorServed,
            ElevatorShaft elevatorShaft) {
        super(patch, enabled, floorServed);

        this.elevatorShaft = elevatorShaft;

        this.queueObject = new QueueObject();

        // Initialize this elevator portal's floor field state
        // A null in the floor field state means that it may accept any direction
        this.elevatorPortalFloorFieldState = new QueueingFloorField.FloorFieldState(
                null,
                PassengerMovement.State.IN_QUEUE,
                this
        );

        // Add a blank floor field
        QueueingFloorField queueingFloorField = new QueueingFloorField(this);

        // Using the floor field state defined earlier, create the floor field
        this.queueObject.getFloorFields().put(this.elevatorPortalFloorFieldState, queueingFloorField);
    }

    public ElevatorShaft getElevatorShaft() {
        return elevatorShaft;
    }

    public QueueObject getQueueObject() {
        return queueObject;
    }

    public QueueingFloorField.FloorFieldState getElevatorPortalFloorFieldState() {
        return elevatorPortalFloorFieldState;
    }

    @Override
    public List<QueueingFloorField.FloorFieldState> retrieveFloorFieldState() {
        List<QueueingFloorField.FloorFieldState> floorFieldStates = new ArrayList<>();

        floorFieldStates.add(this.elevatorPortalFloorFieldState);

        return floorFieldStates;
    }

    @Override
    public QueueingFloorField retrieveFloorField(QueueingFloorField.FloorFieldState floorFieldState) {
        return this.getQueueObject().getFloorFields().get(
                floorFieldState
        );
    }

    @Override
    // Denotes whether the floor field for this elevator portal is complete
    public boolean isFloorFieldsComplete() {
        QueueingFloorField queueingFloorField = retrieveFloorField(this.elevatorPortalFloorFieldState);

        // The floor field of this queueable is complete when there are floor fields present and it has an apex patch
        return queueingFloorField.getApex() != null && !queueingFloorField.getAssociatedPatches().isEmpty();
    }

    @Override
    // Clear all floor fields of this elevator portal
    public void clearFloorFields() {
        QueueingFloorField queueingFloorField = retrieveFloorField(this.elevatorPortalFloorFieldState);

        QueueingFloorField.clearFloorField(
                queueingFloorField,
                this.elevatorPortalFloorFieldState
        );
    }

    // Elevator portal factory
    public static class ElevatorPortalFactory extends AmenityFactory {
        @Override
        public ElevatorPortal create(Object... objects) {
            return new ElevatorPortal(
                    (Patch) objects[0],
                    (boolean) objects[1],
                    (Floor) objects[2],
                    (ElevatorShaft) objects[3]
            );
        }
    }

    @Override
    public String toString() {
        return "Elevator";
    }
}
