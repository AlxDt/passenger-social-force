package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator;

import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.controller.graphics.amenity.graphic.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.GenericGraphic;
import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
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

    // Factory for elevator portal creation
    public static final ElevatorPortalFactory elevatorPortalFactory;

    // Handles how this elevator portal is displayed
    private final GenericGraphic elevatorPortalGraphic;

    // Denotes the footprint of this amenity when being drawn
    public static final AmenityFootprint elevatorPortalFootprint;

    static {
        elevatorPortalFactory = new ElevatorPortalFactory();

        // Initialize this amenity's footprints
        elevatorPortalFootprint = new AmenityFootprint();

        // Up view
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlockN10;
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlockN11;
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlock01;

        AmenityFootprint.Rotation upView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.UP);

        upBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                0,
                0,
                ElevatorPortal.class,
                true,
                false
        );

        upBlockN10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                -1,
                0,
                ElevatorPortal.class,
                false,
                true
        );

        upBlockN11 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                -1,
                1,
                ElevatorPortal.class,
                false,
                false
        );

        upBlock01 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                0,
                1,
                ElevatorPortal.class,
                true,
                false
        );

        upView.getAmenityBlockTemplates().add(upBlock00);
        upView.getAmenityBlockTemplates().add(upBlockN10);
        upView.getAmenityBlockTemplates().add(upBlockN11);
        upView.getAmenityBlockTemplates().add(upBlock01);

        elevatorPortalFootprint.addRotation(upView);

        // Right view
        AmenityFootprint.Rotation.AmenityBlockTemplate rightBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate rightBlock01;
        AmenityFootprint.Rotation.AmenityBlockTemplate rightBlock10;
        AmenityFootprint.Rotation.AmenityBlockTemplate rightBlock11;

        AmenityFootprint.Rotation rightView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.RIGHT);

        rightBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                0,
                0,
                ElevatorPortal.class,
                true,
                true
        );

        rightBlock01 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                0,
                1,
                ElevatorPortal.class,
                false,
                false
        );

        rightBlock10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                1,
                0,
                ElevatorPortal.class,
                true,
                false
        );

        rightBlock11 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                1,
                1,
                ElevatorPortal.class,
                false,
                false
        );

        rightView.getAmenityBlockTemplates().add(rightBlock00);
        rightView.getAmenityBlockTemplates().add(rightBlock01);
        rightView.getAmenityBlockTemplates().add(rightBlock10);
        rightView.getAmenityBlockTemplates().add(rightBlock11);

        elevatorPortalFootprint.addRotation(rightView);

        // Down view
        AmenityFootprint.Rotation.AmenityBlockTemplate downBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate downBlock0N1;
        AmenityFootprint.Rotation.AmenityBlockTemplate downBlock1N1;
        AmenityFootprint.Rotation.AmenityBlockTemplate downBlock10;

        AmenityFootprint.Rotation downView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.DOWN);

        downBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                0,
                0,
                ElevatorPortal.class,
                true,
                false
        );

        downBlock0N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                0,
                -1,
                ElevatorPortal.class,
                true,
                true
        );

        downBlock1N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                1,
                -1,
                ElevatorPortal.class,
                false,
                false
        );

        downBlock10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                1,
                0,
                ElevatorPortal.class,
                false,
                false
        );

        downView.getAmenityBlockTemplates().add(downBlock00);
        downView.getAmenityBlockTemplates().add(downBlock0N1);
        downView.getAmenityBlockTemplates().add(downBlock1N1);
        downView.getAmenityBlockTemplates().add(downBlock10);

        elevatorPortalFootprint.addRotation(downView);

        // Left view
        AmenityFootprint.Rotation.AmenityBlockTemplate leftBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate leftBlockN1N1;
        AmenityFootprint.Rotation.AmenityBlockTemplate leftBlockN10;
        AmenityFootprint.Rotation.AmenityBlockTemplate leftBlock0N1;

        AmenityFootprint.Rotation leftView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.LEFT);

        leftBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                0,
                0,
                ElevatorPortal.class,
                true,
                false
        );

        leftBlockN1N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                -1,
                -1,
                ElevatorPortal.class,
                false,
                true
        );

        leftBlockN10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                -1,
                0,
                ElevatorPortal.class,
                true,
                false
        );

        leftBlock0N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                0,
                -1,
                ElevatorPortal.class,
                false,
                false
        );

        leftView.getAmenityBlockTemplates().add(leftBlock00);
        leftView.getAmenityBlockTemplates().add(leftBlockN1N1);
        leftView.getAmenityBlockTemplates().add(leftBlockN10);
        leftView.getAmenityBlockTemplates().add(leftBlock0N1);

        elevatorPortalFootprint.addRotation(leftView);
    }

    protected ElevatorPortal(
            List<AmenityBlock> amenityBlocks,
            boolean enabled,
            Floor floorServed,
            ElevatorShaft elevatorShaft
    ) {
        super(amenityBlocks, enabled, floorServed);

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

        this.elevatorPortalGraphic = new GenericGraphic(this);
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
    public String getGraphicURL() {
        return this.elevatorPortalGraphic.getGraphicURL();
    }

    @Override
    public String toString() {
        return "Elevator";
    }

    // Elevator portal block
    public static class ElevatorPortalBlock extends Amenity.AmenityBlock {
        public static ElevatorPortal.ElevatorPortalBlock.ElevatorPortalBlockFactory elevatorPortalBlockFactory;

        static {
            elevatorPortalBlockFactory = new ElevatorPortal.ElevatorPortalBlock.ElevatorPortalBlockFactory();
        }

        private ElevatorPortalBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        // Elevator portal block factory
        public static class ElevatorPortalBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public ElevatorPortal.ElevatorPortalBlock create(
                    Patch patch,
                    boolean attractor,
                    boolean hasGraphic,
                    AmenityFootprint.Rotation.Orientation... orientation
            ) {
                return new ElevatorPortal.ElevatorPortalBlock(
                        patch,
                        attractor,
                        hasGraphic
                );
            }
        }
    }

    // Elevator portal factory
    public static class ElevatorPortalFactory extends PortalFactory {
        public ElevatorPortal create(
                List<AmenityBlock> amenityBlocks,
                boolean enabled,
                Floor floorServed,
                ElevatorShaft elevatorShaft
        ) {
            return new ElevatorPortal(
                    amenityBlocks,
                    enabled,
                    floorServed,
                    elevatorShaft
            );
        }
    }
}
