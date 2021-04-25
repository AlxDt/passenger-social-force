package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator;

import com.crowdsimulation.controller.graphics.amenity.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.SingularGraphic;
import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class ElevatorPortal extends Portal implements Queueable {
    // Denotes the elevator shaft which contains this elevator portal
    private final ElevatorShaft elevatorShaft;

    // Denotes the queueing object associated with all goals like this
    private final QueueObject queueObject;

    // Denotes the floor field state needed to access the floor fields of this security gate
    private final QueueingFloorField.FloorFieldState elevatorPortalFloorFieldState;

    // Factory for elevator portal creation
    public static final ElevatorPortalFactory elevatorPortalFactory;

    // Handles how this elevator portal is displayed
    private SingularGraphic elevatorPortalGraphic;

    static {
        elevatorPortalFactory = new ElevatorPortalFactory();
    }

    protected ElevatorPortal(
            Patch patch,
            boolean enabled,
            Floor floorServed,
            ElevatorShaft elevatorShaft
    ) {
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
        QueueingFloorField queueingFloorField = QueueingFloorField.queueingFloorFieldFactory.create(this);

        // Using the floor field state defined earlier, create the floor field
        this.queueObject.getFloorFields().put(this.elevatorPortalFloorFieldState, queueingFloorField);

        this.elevatorPortalGraphic = new SingularGraphic(this);
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
    public List<QueueingFloorField.FloorFieldState> retrieveFloorFieldStates() {
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
    // Calling this method should work in either portal
    public boolean isFloorFieldsComplete() {
        QueueingFloorField queueingFloorField = retrieveFloorField(this.elevatorPortalFloorFieldState);
        QueueingFloorField queueingFloorFieldOther;

        ElevatorPortal otherPortal = (ElevatorPortal) this.getPair();

        queueingFloorFieldOther = otherPortal.retrieveFloorField(
                otherPortal.getElevatorPortalFloorFieldState()
        );

        boolean thisFloorFieldCheck;
        boolean otherFloorFieldCheck;

        thisFloorFieldCheck
                = queueingFloorField.getApex() != null
                && !queueingFloorField.getAssociatedPatches().isEmpty();

        otherFloorFieldCheck
                = queueingFloorFieldOther.getApex() != null
                && !queueingFloorFieldOther.getAssociatedPatches().isEmpty();

        // The floor field of this queueable is complete when, for both portals in this elevator shaft, there are floor\
        // fields present and it has an apex patch
        return thisFloorFieldCheck && otherFloorFieldCheck;
    }

    @Override
    // Clear all floor fields of this given floor field state in this elevator portal
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

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.elevatorPortalGraphic;
    }

    @Override
    public Image getGraphic() {
        return this.elevatorPortalGraphic.getGraphic();
    }

    // Elevator portal factory
    public static class ElevatorPortalFactory extends PortalFactory {
        public ElevatorPortal create(
                Patch patch,
                boolean enabled,
                Floor floorServed,
                ElevatorShaft elevatorShaft
        ) {
            return new ElevatorPortal(
                    patch,
                    enabled,
                    floorServed,
                    elevatorShaft
            );
        }
    }

    @Override
    public String toString() {
        return "Elevator";
    }
}
