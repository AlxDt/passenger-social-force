package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator;

import com.crowdsimulation.controller.graphics.amenity.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.EscalatorGraphic;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;
import javafx.scene.image.Image;

public class EscalatorPortal extends Portal {
    // Denotes the escalator shaft which contains this escalator portal
    private final EscalatorShaft escalatorShaft;

    // Factory for escalator portal creation
    public static final EscalatorPortalFactory escalatorPortalFactory;

    // Handles how this escalator portal is displayed
    private final EscalatorGraphic escalatorGraphic;

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

        this.escalatorGraphic = new EscalatorGraphic(this);
    }

    public EscalatorShaft getEscalatorShaft() {
        return escalatorShaft;
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.escalatorGraphic;
    }

    @Override
    public Image getGraphic() {
        return this.escalatorGraphic.getGraphic();
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
        return "Escalator";
    }
}
