package com.crowdsimulation.controller.graphics.amenity.editor;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;

import java.util.List;

public class SecurityEditor extends AmenityEditor {
    public void draw(
            Patch currentPatch,
            boolean enabled,
            int waitingTime,
            boolean blockPassenger
    ) {
        List<Amenity.AmenityBlock> amenityBlocks
                = Security.SecurityBlock.convertToAmenityBlocks(
                currentPatch,
                GraphicsController.currentAmenityFootprint.getCurrentRotation()
                        .getAmenityBlockTemplates()
        );

        // Only add amenities on patches which do not have floor fields
        // Otherwise, do nothing
        if (currentPatch.getFloorFieldValues().isEmpty()) {
            // Prepare the amenity that will be placed on the station
            Security securityToAdd = Security.securityFactory.create(
                    amenityBlocks,
                    enabled,
                    waitingTime,
                    blockPassenger
            );

            // Add this station gate to the list of all station gates on this floor
            Main.simulator.getCurrentFloor().getSecurities().add(securityToAdd);
        }
    }

    public void edit(
            Security securityToEdit,
            boolean enabled,
            int waitingTime,
            boolean blockPassenger
    ) {
        securityToEdit.setEnabled(
                enabled
        );

        securityToEdit.setBlockEntry(
                blockPassenger
        );

        securityToEdit.setWaitingTime(
                waitingTime
        );
    }

    public void delete(
            Security security
    ) {
        Main.simulator.getCurrentFloor().getSecurities().remove(
                security
        );
    }
}
