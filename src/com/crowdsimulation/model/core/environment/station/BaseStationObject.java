package com.crowdsimulation.model.core.environment.station;

public abstract class BaseStationObject {
    public BaseStationObject() {
    }

    public abstract static class StationObjectFactory {
        // Create a base station object
        public abstract BaseStationObject create(Object... objects);
    }
}
