package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.Queueable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;

import java.util.ArrayList;
import java.util.List;

public class TrainDoor extends Gate implements Queueable {
    private TrainDoorPlatform platform;
    private final List<TrainDoorCarriage> trainDoorCarriagesSupported;

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

    @Override
    public String toString() {
        return "Train boarding area";
    }

    // Train door factory
    public static class TrainDoorFactory extends AmenityFactory {
        @Override
        public Amenity createAmenity(Patch patch, Object... objects) {
            return new TrainDoor(
                    patch,
                    (boolean) objects[0],
                    (TrainDoorPlatform) objects[1],
                    (List<TrainDoorCarriage>) objects[2]
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
