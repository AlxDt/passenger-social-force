package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator;

import com.crowdsimulation.controller.graphics.amenity.editor.EscalatorEditor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.PortalShaft;

public class EscalatorShaft extends PortalShaft {
    // Denotes the direction of this escalator
    private EscalatorDirection escalatorDirection;

    // Denotes whether the direction of the escalator shaft has been changed
    private boolean hasChangedDirection;

    // Denotes the editor of this amenity
    public static final EscalatorEditor escalatorEditor;

    static {
        // Initialize the editor
        escalatorEditor = new EscalatorEditor();
    }

    protected EscalatorShaft(
            boolean enabled,
            int moveTime,
            EscalatorDirection escalatorDirection
    ) {
        super(null, enabled, moveTime);

        this.escalatorDirection = escalatorDirection;
        this.hasChangedDirection = false;
    }

    // Escalator shaft factory
    public static class EscalatorShaftFactory extends PortalShaftFactory {
        public EscalatorShaft create(
                boolean enabled,
                int moveTime,
                EscalatorDirection escalatorDirection
        ) {
            return new EscalatorShaft(
                    enabled,
                    moveTime,
                    escalatorDirection
            );
        }
    }

    public EscalatorDirection getEscalatorDirection() {
        return escalatorDirection;
    }

    public void setEscalatorDirection(EscalatorDirection escalatorDirection) {
        this.escalatorDirection = escalatorDirection;
    }

    public boolean hasChangedDirection() {
        return hasChangedDirection;
    }

    public void setChangedDirection(boolean hasChangedDirection) {
        this.hasChangedDirection = hasChangedDirection;
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
