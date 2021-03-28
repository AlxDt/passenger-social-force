package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator;

import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.Queueable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;

public class ElevatorPortal extends Portal implements Queueable {
    // Denotes the elevator shaft which contains this elevator portal
    private final ElevatorShaft elevatorShaft;

    public ElevatorPortal(
            Patch patch,
            boolean enabled,
            Floor floorServed,
            ElevatorShaft elevatorShaft) {
        super(patch, enabled, floorServed);

        this.elevatorShaft = elevatorShaft;
    }

    public ElevatorShaft getElevatorShaft() {
        return elevatorShaft;
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
