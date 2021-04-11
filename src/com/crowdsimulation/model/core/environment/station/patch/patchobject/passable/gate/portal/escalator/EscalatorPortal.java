package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator;

import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;

import java.util.List;

public class EscalatorPortal extends Portal {
    // Denotes the escalator shaft which contains this escalator portal
    private final EscalatorShaft escalatorShaft;

    // Factory for escalator portal creation
    public static final EscalatorPortalFactory escalatorPortalFactory;

    static {
        escalatorPortalFactory = new EscalatorPortalFactory();
    }

    protected EscalatorPortal(
            Patch patch,
            boolean enabled,
            Floor floorServed,
            EscalatorShaft escalatorShaft
    ) {
        super(patch, enabled, floorServed);

        this.escalatorShaft = escalatorShaft;
    }

    public EscalatorShaft getEscalatorShaft() {
        return escalatorShaft;
    }

    // Escalator portal factory
    public static class EscalatorPortalFactory extends PortalFactory {
        public EscalatorPortal create(
                Patch patch,
                boolean enabled,
                Floor floorServed,
                EscalatorShaft escalatorShaft
        ) {
            return new EscalatorPortal(
                    patch,
                    enabled,
                    floorServed,
                    escalatorShaft
            );
        }
    }

    @Override
    public String toString() {
        return "Escalator portal";
    }
}
