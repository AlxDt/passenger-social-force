package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.model.core.agent.passenger.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;

import java.util.ArrayList;
import java.util.List;

public class TrainDoor extends Gate implements Queueable {
    private TrainDoorPlatform platform;
    private final List<TrainDoorCarriage> trainDoorCarriagesSupported;

    // Denotes the queueing object associated with all goals like this
    private final QueueObject queueObject;

    // Denotes the floor field state needed to access the floor fields of this security gate
    private final QueueingFloorField.FloorFieldState trainDoorFloorFieldState;

    public TrainDoor(
            Patch patch,
            boolean enabled,
            TrainDoorPlatform platform,
            List<TrainDoorCarriage> trainDoorCarriagesSupported
    ) {
        super(patch, enabled);

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
        QueueingFloorField queueingFloorField = new QueueingFloorField(this);

        // Using the floor field state defined earlier, create the floor field
        this.queueObject.getFloorFields().put(this.trainDoorFloorFieldState, queueingFloorField);
    }

    public TrainDoorPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(TrainDoorPlatform platform) {
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
    public List<QueueingFloorField.FloorFieldState> retrieveFloorFieldState() {
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

        // The floor field of this queueable is complete when there are floor fields present and it has an apex patch
        return queueingFloorField.getApex() != null && !queueingFloorField.getAssociatedPatches().isEmpty();
    }

    @Override
    // Clear all floor fields of this train door waiting area
    public void clearFloorFields() {
        QueueingFloorField queueingFloorField = retrieveFloorField(this.trainDoorFloorFieldState);

        QueueingFloorField.clearFloorField(
                queueingFloorField,
                this.trainDoorFloorFieldState
        );
    }

    @Override
    public String toString() {
        return "Train boarding area";
    }

    // Train door factory
    public static class TrainDoorFactory extends AmenityFactory {
        @Override
        public TrainDoor
        create(Object... objects) {
            return new TrainDoor(
                    (Patch) objects[0],
                    (boolean) objects[1],
                    (TrainDoorPlatform) objects[2],
                    (List<TrainDoorCarriage>) objects[3]
            );
        }
    }

    // The platform direction this train door waiting area is at
    public enum TrainDoorPlatform {
        NORTHBOUND("Northbound"),
        SOUTHBOUND("Southbound"),
        EASTBOUND("Eastbound"),
        WESTBOUND("Westbound");

        private final String name;

        private TrainDoorPlatform(String name) {
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

        private TrainDoorCarriage(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
