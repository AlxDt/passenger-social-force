package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs;

import com.crowdsimulation.controller.graphics.amenity.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.StairGraphic;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;
import javafx.scene.image.Image;

public class StairPortal extends Portal {
    // Denotes the stair shaft which contains this stair portal
    private final StairShaft stairShaft;

    // Factory for stair portal creation
    public static final StairPortalFactory stairPortalFactory;

    // Handles how this stair portal is displayed
    private StairGraphic stairGraphic;

    static {
        stairPortalFactory = new StairPortalFactory();
    }

    protected StairPortal(
            Patch patch,
            boolean enabled,
            Floor floorServed,
            StairShaft stairShaft
    ) {
        super(patch, enabled, floorServed);

        this.stairShaft = stairShaft;

        this.stairGraphic = new StairGraphic(this);
    }

    public StairShaft getStairShaft() {
        return stairShaft;
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.stairGraphic;
    }

    @Override
    public Image getGraphic() {
        return this.stairGraphic.getGraphic();
    }

    // Stair portal factory
    public static class StairPortalFactory extends PortalFactory {
        public StairPortal create(
                Patch patch,
                boolean enabled,
                Floor floorServed,
                StairShaft stairShaft
        ) {
            return new StairPortal(
                    patch,
                    enabled,
                    floorServed,
                    stairShaft
            );
        }
    }

    @Override
    public String toString() {
        return "Stair";
    }
}
