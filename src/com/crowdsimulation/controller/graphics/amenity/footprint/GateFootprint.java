package com.crowdsimulation.controller.graphics.amenity.footprint;

import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;

public class GateFootprint extends AmenityFootprint {
    public static class GateRotation extends AmenityFootprint.Rotation {
        public GateRotation(Orientation orientation) {
            super(orientation);
        }

        public static class GateBlockTemplate extends Rotation.AmenityBlockTemplate {
            private final boolean spawner;

            public GateBlockTemplate(
                    Rotation.Orientation orientation,
                    int rowOffset,
                    int columnOffset,
                    Class<? extends Amenity> amenityClass,
                    boolean attractor,
                    boolean spawner,
                    boolean hasGraphic
            ) {
                super(orientation, rowOffset, columnOffset, amenityClass, attractor, hasGraphic);

                this.spawner = spawner;
            }

            public boolean isSpawner() {
                return spawner;
            }
        }
    }
}
