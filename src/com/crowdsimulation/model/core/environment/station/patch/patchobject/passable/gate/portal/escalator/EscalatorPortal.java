package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator;

import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;

public class EscalatorPortal extends Portal {
    // Denotes the escalator shaft which contains this escalator portal
    private final EscalatorShaft escalatorShaft;

    public EscalatorPortal(
            Patch patch,
            boolean enabled,
            Floor floorServed,
            EscalatorShaft escalatorShaft) {
        super(patch, enabled, floorServed);

        this.escalatorShaft = escalatorShaft;
    }

    public EscalatorShaft getEscalatorShaft() {
        return escalatorShaft;
    }

    // Escalator portal factory
    public static class EscalatorPortalFactory extends AmenityFactory {
        @Override
        public EscalatorPortal create(Object... objects) {
            return new EscalatorPortal(
                    (Patch) objects[0],
                    (boolean) objects[1],
                    (Floor) objects[2],
                    (EscalatorShaft) objects[3]
            );
        }
    }

    @Override
    public String toString() {
        return "Escalator portal";
    }
}
