package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.PortalShaft;

public class EscalatorShaft extends PortalShaft {
    // Denotes the direction of this escalator
    private EscalatorDirection escalatorDirection;

    public EscalatorShaft(
            Patch patch,
            boolean enabled,
            int moveTime,
            EscalatorDirection escalatorDirection) {
        super(patch, enabled, moveTime);

        this.escalatorDirection = escalatorDirection;
    }

    // Escalator shaft factory
    public static class EscalatorShaftFactory extends AmenityFactory {
        @Override
        public EscalatorShaft create(Object... objects) {
            return new EscalatorShaft(
                    (Patch) objects[0],
                    (boolean) objects[1],
                    (int) objects[2],
                    (EscalatorDirection) objects[3]
            );
        }
    }

    public EscalatorDirection getEscalatorDirection() {
        return escalatorDirection;
    }

    public void setEscalatorDirection(EscalatorDirection escalatorDirection) {
        this.escalatorDirection = escalatorDirection;
    }

    // Denotes the next direction of the escalator
    public enum EscalatorDirection {
        UP("Going up"), // The escalator is about to move up
        DOWN("Going down"); // The escalator is about to move down

        private final String name;

        private EscalatorDirection(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
