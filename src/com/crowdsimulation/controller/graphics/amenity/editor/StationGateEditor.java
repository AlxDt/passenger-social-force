package com.crowdsimulation.controller.graphics.amenity.editor;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;

import java.util.List;

public class StationGateEditor extends AmenityEditor {
    public void draw(
            Patch currentPatch,
            boolean enabled,
            double chancePerSecond,
            StationGate.StationGateMode mode
    ) {
        List<Amenity.AmenityBlock> amenityBlocks
                = StationGate.StationGateBlock.convertToAmenityBlocks(
                currentPatch,
                GraphicsController.currentAmenityFootprint.getCurrentRotation()
                        .getAmenityBlockTemplates()
        );

        // Only add amenities on patches which do not have floor fields
        // Otherwise, do nothing
        if (currentPatch.getFloorFieldValues().isEmpty()) {
            // Prepare the amenity that will be placed on the station
            StationGate stationGateToAdd = StationGate.stationGateFactory.create(
                    amenityBlocks,
                    enabled,
                    chancePerSecond,
                    mode
            );

            // Add this station gate to the list of all station gates on this floor
            Main.simulator.getCurrentFloor().getStationGates().add(stationGateToAdd);
        }
    }

    public void edit(
            StationGate stationGateToEdit,
            boolean stationGateEnabled,
            double chancePerSecond,
            StationGate.StationGateMode stationGateMode
    ) {
        stationGateToEdit.setEnabled(
                stationGateEnabled
        );

        stationGateToEdit.setChancePerSecond(
                chancePerSecond
        );

        stationGateToEdit.setStationGateMode(
                stationGateMode
        );
    }

    public void delete(
            StationGate stationGate
    ) {
        Main.simulator.getCurrentFloor().getStationGates().remove(
                stationGate
        );
    }
}
