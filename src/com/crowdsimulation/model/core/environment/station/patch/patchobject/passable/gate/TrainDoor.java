package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.controller.graphics.amenity.editor.TrainDoorEditor;
import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.controller.graphics.amenity.graphic.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.GraphicLocation;
import com.crowdsimulation.controller.graphics.amenity.graphic.TrainDoorGraphic;
import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;

import java.util.ArrayList;
import java.util.List;

public class TrainDoor extends Gate implements Queueable {
    // Denotes the platform side served by this train door
    private TrainDoorDirection platform;

    // Denotes the types of carriages supported by this train door
    private final List<TrainDoorCarriage> trainDoorCarriagesSupported;

    // Factory for train door creation
    public static final TrainDoorFactory trainDoorFactory;

    // Denotes the queueing object associated with all goals like this
    private final QueueObject queueObject;

    // Denotes the floor field state needed to access the floor fields of this train door
    private final QueueingFloorField.FloorFieldState trainDoorFloorFieldState;

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
        this.trainDoorCarriagesSupported = new ArrayList<>();

        setTrainDoorCarriagesSupported(trainDoorCarriagesSupported);

        this.queueObject = new QueueObject();

        // Initialize this elevator portal's floor field state
        // A null in the floor field state means that it may accept any direction
        this.trainDoorFloorFieldState = new QueueingFloorField.FloorFieldState(
                PassengerMovement.Direction.BOARDING,
                PassengerMovement.State.IN_QUEUE,
                this
        );

        // Add a blank floor field
        QueueingFloorField queueingFloorField = QueueingFloorField.queueingFloorFieldFactory.create(this);

        // Using the floor field state defined earlier, create the floor field
        this.queueObject.getFloorFields().put(this.trainDoorFloorFieldState, queueingFloorField);

        this.trainDoorGraphic = new TrainDoorGraphic(this);
    }

    public TrainDoorDirection getPlatform() {
        return platform;
    }

    public void setPlatform(TrainDoorDirection platform) {
        this.platform = platform;
    }

    public List<TrainDoorCarriage> getTrainDoorCarriagesSupported() {
        return trainDoorCarriagesSupported;
    }

    public void setTrainDoorCarriagesSupported(List<TrainDoorCarriage> trainDoorCarriagesSupported) {
        this.trainDoorCarriagesSupported.clear();
        this.trainDoorCarriagesSupported.addAll(trainDoorCarriagesSupported);
    }

    public QueueObject getQueueObject() {
        return queueObject;
    }

    public QueueingFloorField.FloorFieldState getTrainDoorFloorFieldState() {
        return trainDoorFloorFieldState;
    }

    @Override
    public List<QueueingFloorField.FloorFieldState> retrieveFloorFieldStates() {
        List<QueueingFloorField.FloorFieldState> floorFieldStates = new ArrayList<>();

        floorFieldStates.add(this.trainDoorFloorFieldState);

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
        QueueingFloorField queueingFloorField = retrieveFloorField(this.trainDoorFloorFieldState);

        // The floor field of this queueable is complete when there are floor field values present with an apex patch
        // that is equal to the number of attractors in this queueable target
        return queueingFloorField.getApices().size() == this.getAttractors().size()
                && !queueingFloorField.getAssociatedPatches().isEmpty();
    }

    @Override
    // Clear all floor fields of the given floor field state in this train door waiting area
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
    public String toString() {
        return "Train boarding area" + ((this.enabled) ? "" : " (disabled)");
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.trainDoorGraphic;
    }

    @Override
    public GraphicLocation getGraphicLocation() {
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
}
