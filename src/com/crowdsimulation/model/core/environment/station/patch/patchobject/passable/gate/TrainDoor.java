package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.controller.graphics.amenity.editor.TrainDoorEditor;
import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.AmenityGraphicLocation;
import com.crowdsimulation.controller.graphics.amenity.graphic.amenity.TrainDoorGraphic;
import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.platform.PlatformFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainDoor extends Gate implements Queueable {
    // Denotes the platform side served by this train door
    private TrainDoorDirection platform;

    // Denotes whether this train door is open and allows entry and exit of passengers
    private boolean open;

    // Denotes the types of carriages supported by this train door
    private final List<TrainDoorCarriage> trainDoorCarriagesSupported;

    // Factory for train door creation
    public static final TrainDoorFactory trainDoorFactory;

    // Denotes the queueing object associated with all goals like this
    private final Map<TrainDoorEntranceLocation, QueueObject> queueObjects;

    // Denotes the floor field states needed to access the floor fields of this train door
    private final PlatformFloorField.PlatformFloorFieldState leftTrainDoorFloorFieldState;
    private final PlatformFloorField.PlatformFloorFieldState rightTrainDoorFloorFieldState;

    // Handles how the train door is displayed
    private final TrainDoorGraphic trainDoorGraphic;

    // Denotes the footprint of this amenity when being drawn
    public static final AmenityFootprint trainDoorFootprint;

    // Denotes the editor of this amenity
    public static final TrainDoorEditor trainDoorEditor;

    static {
        trainDoorFactory = new TrainDoorFactory();

        // Initialize this amenity's footprints
        trainDoorFootprint = new AmenityFootprint();

        // Up view
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlock01;
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlock02;
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlock03;

        AmenityFootprint.Rotation upView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.UP);

        upBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                0,
                0,
                TrainDoor.class,
                true,
                true
        );

        upBlock01 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                0,
                1,
                TrainDoor.class,
                false,
                false
        );

        upBlock02 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                0,
                2,
                TrainDoor.class,
                false,
                false
        );

        upBlock03 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                0,
                3,
                TrainDoor.class,
                true,
                false
        );

        upView.getAmenityBlockTemplates().add(upBlock00);
        upView.getAmenityBlockTemplates().add(upBlock01);
        upView.getAmenityBlockTemplates().add(upBlock02);
        upView.getAmenityBlockTemplates().add(upBlock03);

        trainDoorFootprint.addRotation(upView);

        // Initialize the editor
        trainDoorEditor = new TrainDoorEditor();
    }

    protected TrainDoor(
            List<AmenityBlock> amenityBlocks,
            boolean enabled,
            TrainDoorDirection platform,
            List<TrainDoorCarriage> trainDoorCarriagesSupported
    ) {
        super(amenityBlocks, enabled);

        this.platform = platform;
        this.open = false;
        this.trainDoorCarriagesSupported = new ArrayList<>();

        setTrainDoorCarriagesSupported(trainDoorCarriagesSupported);

        this.queueObjects = new HashMap<>();

        if (platform == TrainDoorDirection.SOUTHBOUND || platform == TrainDoorDirection.EASTBOUND) {
            this.queueObjects.put(TrainDoorEntranceLocation.LEFT, new QueueObject());
            this.queueObjects.put(TrainDoorEntranceLocation.RIGHT, new QueueObject());
        } else {
            this.queueObjects.put(TrainDoorEntranceLocation.RIGHT, new QueueObject());
            this.queueObjects.put(TrainDoorEntranceLocation.LEFT, new QueueObject());
        }

        // Initialize this elevator portal's floor field state
        // A null in the floor field state means that it may accept any direction
        this.leftTrainDoorFloorFieldState = new PlatformFloorField.PlatformFloorFieldState(
                PassengerMovement.Direction.BOARDING,
                PassengerMovement.State.IN_QUEUE,
                this,
                TrainDoorEntranceLocation.LEFT
        );

        this.rightTrainDoorFloorFieldState = new PlatformFloorField.PlatformFloorFieldState(
                PassengerMovement.Direction.BOARDING,
                PassengerMovement.State.IN_QUEUE,
                this,
                TrainDoorEntranceLocation.RIGHT
        );

        // Add a blank floor field
        PlatformFloorField leftPlatformFloorField = PlatformFloorField.platformFloorFieldFactory.create(this);
        PlatformFloorField rightPlatformFloorField = PlatformFloorField.platformFloorFieldFactory.create(this);

        // Using the floor field state defined earlier, create the floor field
        this.queueObjects.get(TrainDoorEntranceLocation.LEFT).getFloorFields().put(
                this.leftTrainDoorFloorFieldState, leftPlatformFloorField
        );

        this.queueObjects.get(TrainDoorEntranceLocation.RIGHT).getFloorFields().put(
                this.rightTrainDoorFloorFieldState, rightPlatformFloorField
        );

        this.trainDoorGraphic = new TrainDoorGraphic(this);
    }

    public TrainDoorDirection getPlatform() {
        return platform;
    }

    public void setPlatform(TrainDoorDirection platform) {
        this.platform = platform;
    }

    public boolean isOpen() {
        return open;
    }

    public void toggleTrainDoor() {
        this.open = !this.open;
    }

    public List<TrainDoorCarriage> getTrainDoorCarriagesSupported() {
        return trainDoorCarriagesSupported;
    }

    public void setTrainDoorCarriagesSupported(List<TrainDoorCarriage> trainDoorCarriagesSupported) {
        this.trainDoorCarriagesSupported.clear();
        this.trainDoorCarriagesSupported.addAll(trainDoorCarriagesSupported);
    }

    // Get whichever queue object has the shorter queue
    public QueueObject getQueueObject() {
        QueueObject leftQueueObject = this.queueObjects.get(TrainDoorEntranceLocation.LEFT);
        QueueObject rightQueueObject = this.queueObjects.get(TrainDoorEntranceLocation.RIGHT);

        if (leftQueueObject.getPassengersQueueing().size() <= rightQueueObject.getPassengersQueueing().size()) {
            return leftQueueObject;
        } else {
            return rightQueueObject;
        }
    }

    public Map<TrainDoorEntranceLocation, QueueObject> getQueueObjects() {
        return queueObjects;
    }

    public PlatformFloorField.PlatformFloorFieldState getLeftTrainDoorFloorFieldState() {
        return leftTrainDoorFloorFieldState;
    }

    public PlatformFloorField.PlatformFloorFieldState getRightTrainDoorFloorFieldState() {
        return rightTrainDoorFloorFieldState;
    }

    public static boolean isTrainDoor(Amenity amenity) {
        return amenity instanceof TrainDoor;
    }

    public static TrainDoor asTrainDoor(Amenity amenity) {
        if (isTrainDoor(amenity)) {
            return (TrainDoor) amenity;
        } else {
            return null;
        }
    }

    @Override
    public List<QueueingFloorField.FloorFieldState> retrieveFloorFieldStates() {
        List<QueueingFloorField.FloorFieldState> floorFieldStates = new ArrayList<>();

        floorFieldStates.add(this.leftTrainDoorFloorFieldState);
        floorFieldStates.add(this.rightTrainDoorFloorFieldState);

        return floorFieldStates;
    }

    @Override
    public QueueingFloorField retrieveFloorField(
            QueueObject queueObject,
            QueueingFloorField.FloorFieldState floorFieldState
    ) {
        PlatformFloorField.PlatformFloorFieldState platformFloorFieldState
                = ((PlatformFloorField.PlatformFloorFieldState) floorFieldState);

        // Get the corresponding queueing floor field
        return queueObject.getFloorFields().get(
                platformFloorFieldState
        );
    }

    @Override
    // Denotes whether the floor field for this elevator portal is complete
    public boolean isFloorFieldsComplete() {
        PlatformFloorField leftPlatformFloorField = (PlatformFloorField) retrieveFloorField(
                this.getQueueObjects().get(TrainDoorEntranceLocation.LEFT),
                this.leftTrainDoorFloorFieldState
        );

        PlatformFloorField rightPlatformFloorField = (PlatformFloorField) retrieveFloorField(
                this.getQueueObjects().get(TrainDoorEntranceLocation.RIGHT),
                this.rightTrainDoorFloorFieldState
        );

        // The floor field of this queueable is complete when there are floor field values present with an apex patch
        // that is equal to the number of attractors in this queueable target
        return leftPlatformFloorField.getApices().size() == 1 && !leftPlatformFloorField.getAssociatedPatches().isEmpty()
                && rightPlatformFloorField.getApices().size() == 1 && !rightPlatformFloorField.getAssociatedPatches().isEmpty();
    }

    @Override
    // Clear all floor fields of the given floor field state in this train door waiting area
    public void deleteFloorField(
            QueueingFloorField.FloorFieldState floorFieldState
    ) {
        PlatformFloorField.PlatformFloorFieldState platformFloorFieldState
                = ((PlatformFloorField.PlatformFloorFieldState) floorFieldState);

        // Get the location of the train door entrance from the given platform floor field state
        TrainDoor.TrainDoorEntranceLocation trainDoorEntranceLocation
                = platformFloorFieldState.getTrainDoorEntranceLocation();

        // Then get the appropriate queue object corresponding to that location
        QueueObject updatedQueueObject = this.getQueueObjects().get(trainDoorEntranceLocation);

        PlatformFloorField platformFloorField
                = (PlatformFloorField) platformFloorFieldState.getTarget().retrieveFloorField(
                updatedQueueObject,
                platformFloorFieldState
        );

        PlatformFloorField.clearFloorField(
                platformFloorField,
                floorFieldState
        );
    }

    @Override
    public void deleteAllFloorFields() {
        // Sweep through each and every floor field and delete them
        List<QueueingFloorField.FloorFieldState> floorFieldStates = retrieveFloorFieldStates();

        for (QueueingFloorField.FloorFieldState queueingFloorFieldState : floorFieldStates) {
            deleteFloorField(queueingFloorFieldState);
        }
    }

    @Override
    public String toString() {
        return "Train boarding area" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.trainDoorGraphic;
    }

    @Override
    public AmenityGraphicLocation getGraphicLocation() {
        return this.trainDoorGraphic.getGraphicLocation();
    }

    // Train door block
    public static class TrainDoorBlock extends Amenity.AmenityBlock {
        public static TrainDoor.TrainDoorBlock.TrainDoorBlockFactory trainDoorBlockFactory;

        static {
            trainDoorBlockFactory = new TrainDoor.TrainDoorBlock.TrainDoorBlockFactory();
        }

        private TrainDoorBlock(Patch patch, boolean attractor, boolean hasGraphic) {
            super(patch, attractor, hasGraphic);
        }

        // Train door block factory
        public static class TrainDoorBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public TrainDoor.TrainDoorBlock create(
                    Patch patch,
                    boolean attractor,
                    boolean hasGraphic,
                    AmenityFootprint.Rotation.Orientation... orientation
            ) {
                return new TrainDoor.TrainDoorBlock(
                        patch,
                        attractor,
                        hasGraphic
                );
            }
        }
    }

    // Train door factory
    public static class TrainDoorFactory extends GateFactory {
        public TrainDoor create(
                List<AmenityBlock> amenityBlocks,
                boolean enabled,
                TrainDoorDirection trainDoorDirection,
                List<TrainDoorCarriage> trainDoorCarriagesSupported
        ) {
            return new TrainDoor(
                    amenityBlocks,
                    enabled,
                    trainDoorDirection,
                    trainDoorCarriagesSupported
            );
        }
    }

    // The platform direction this train door waiting area is at
    public enum TrainDoorDirection {
        NORTHBOUND("Northbound"),
        SOUTHBOUND("Southbound"),
        EASTBOUND("Eastbound"),
        WESTBOUND("Westbound");

        private final String name;

        TrainDoorDirection(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    // The carriages this train door supports
    public enum TrainDoorCarriage {
        LRT_1_FIRST_GENERATION("LRT-1 1G"),
        LRT_1_SECOND_GENERATION("LRT-1 2G"),
        LRT_1_THIRD_GENERATION("LRT-1 3G"),
        LRT_2_FIRST_GENERATION("LRT-2 1G"),
        MRT_3_FIRST_GENERATION("MRT-3 1G"),
        MRT_3_SECOND_GENERATION("MRT-3 2G");

        private final String name;

        TrainDoorCarriage(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    // Denotes the location of a train door entrance
    public enum TrainDoorEntranceLocation {
        LEFT("Left entrance"),
        RIGHT("Right entrance");

        private final String name;

        TrainDoorEntranceLocation(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
