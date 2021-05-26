package com.crowdsimulation.controller.graphics.amenity.editor;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorShaft;

import java.util.List;

public class EscalatorEditor extends AmenityEditor {
    public EscalatorShaft createShaft(
            boolean enabled,
            int moveTime,
            EscalatorShaft.EscalatorDirection escalatorDirection
    ) {
        // Prepare the provisional escalator shaft
        // If the user chooses not to go through with the elevator, this shaft will
        // simply be discarded
        EscalatorShaft.EscalatorShaftFactory escalatorShaftFactory
                = new EscalatorShaft.EscalatorShaftFactory();

        return escalatorShaftFactory.create(
                enabled,
                moveTime,
                escalatorDirection
        );
    }

    public EscalatorPortal draw(
            Patch currentPatch,
            boolean enabled,
            Floor currentFloor,
            EscalatorShaft escalatorShaft
    ) {
        List<Amenity.AmenityBlock> amenityBlocks
                = Amenity.AmenityBlock.convertToAmenityBlocks(
                currentPatch,
                GraphicsController.currentAmenityFootprint.getCurrentRotation()
                        .getAmenityBlockTemplates()
        );

        // If there are no amenity blocks to be formed from the footprint at all, do nothing
        if (amenityBlocks == null) {
            return null;
        }

        // Only add amenities on patches which are empty and do not have floor field values on them
        // Check if, in each amenity block, there are no floor field values
        boolean patchesClear = true;

        for (Amenity.AmenityBlock amenityBlock : amenityBlocks) {
            if (
                    amenityBlock.getPatch().getAmenityBlock() != null
                            || !amenityBlock.getPatch().getFloorFieldValues().isEmpty()
            ) {
                patchesClear = false;

                break;
            }
        }

        // Otherwise, do nothing
        if (patchesClear) {
            return EscalatorPortal.escalatorPortalFactory.create(
                    amenityBlocks,
                    enabled,
                    currentFloor,
                    escalatorShaft
            );
        } else {
            return null;
        }
    }

    public void edit(
            EscalatorShaft escalatorShaft,
            boolean enabled,
            int moveTime,
            EscalatorShaft.EscalatorDirection escalatorDirection
    ) {
        EscalatorShaft.EscalatorDirection priorEscalatorDirection = escalatorShaft.getEscalatorDirection();

        escalatorShaft.setEnabled(enabled);
        escalatorShaft.setMoveTime(moveTime);
        escalatorShaft.setEscalatorDirection(escalatorDirection);

        // If the direction was changed, take note of it
        escalatorShaft.setChangedDirection(!escalatorDirection.equals(priorEscalatorDirection));
    }

    public void delete(
            EscalatorShaft escalatorShaftToDelete
    ) {
        // Retrieve portal components
        EscalatorPortal upperEscalatorPortal = (EscalatorPortal) escalatorShaftToDelete.getUpperPortal();
        EscalatorPortal lowerEscalatorPortal = (EscalatorPortal) escalatorShaftToDelete.getLowerPortal();

        // Remove the portals from their patches in their respective floors
        if (Main.simulator.getFirstPortal() == null) {
            // Portal drawing completed, deleting portal from portal shaft
            if (upperEscalatorPortal != null) {
                for (Amenity.AmenityBlock amenityBlock : upperEscalatorPortal.getAmenityBlocks()) {
                    amenityBlock.getPatch().setAmenityBlock(null);
                }
            }

            if (lowerEscalatorPortal != null) {
                for (Amenity.AmenityBlock amenityBlock : lowerEscalatorPortal.getAmenityBlocks()) {
                    amenityBlock.getPatch().setAmenityBlock(null);
                }
            }

            // Remove escalator shaft
            Main.simulator.getStation().getEscalatorShafts().remove(
                    escalatorShaftToDelete
            );
        } else {
            // Portal drawing uncompleted, deleting portal from simulator
            EscalatorPortal portal = (EscalatorPortal) Main.simulator.getFirstPortal();

            for (Amenity.AmenityBlock amenityBlock : portal.getAmenityBlocks()) {
                amenityBlock.getPatch().setAmenityBlock(null);
            }
        }
    }
}
