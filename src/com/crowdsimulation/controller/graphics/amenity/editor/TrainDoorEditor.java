package com.crowdsimulation.controller.graphics.amenity.editor;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;

import java.util.List;

public class TrainDoorEditor {
    public void draw(
            Patch currentPatch,
            boolean enabled,
            TrainDoor.TrainDoorDirection trainDoorDirection,
            List<TrainDoor.TrainDoorCarriage> trainDoorCarriages
    ) {
        List<Amenity.AmenityBlock> amenityBlocks
                = Amenity.AmenityBlock.convertToAmenityBlocks(
                currentPatch,
                GraphicsController.currentAmenityFootprint.getCurrentRotation()
                        .getAmenityBlockTemplates()
        );

        // If there are no amenity blocks to be formed from the footprint at all, do nothing
        if (amenityBlocks == null) {
            return;
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
            // Prepare the amenity that will be placed on the station
            TrainDoor trainDoorToAdd = TrainDoor.trainDoorFactory.create(
                    amenityBlocks,
                    enabled,
                    trainDoorDirection,
                    trainDoorCarriages
            );

            // Add this station gate to the list of all train doors on this floor
            Main.simulator.getCurrentFloor().getTrainDoors().add(trainDoorToAdd);

            amenityBlocks.forEach(
                    amenityBlock -> amenityBlock.getPatch().getFloor().getAmenityPatchSet().add(
                            amenityBlock.getPatch()
                    )
            );
        }
    }

    public void edit(
            TrainDoor trainDoorToEdit,
            boolean enabled,
            TrainDoor.TrainDoorDirection trainDoorDirection,
            List<TrainDoor.TrainDoorCarriage> trainDoorCarriages
    ) {
        trainDoorToEdit.setEnabled(
                enabled
        );

        trainDoorToEdit.setPlatform(
                trainDoorDirection
        );

        trainDoorToEdit.setTrainDoorCarriagesSupported(
                trainDoorCarriages
        );
    }

    public void delete(
            TrainDoor trainDoorToDelete
    ) {
        Main.simulator.getCurrentFloor().getTrainDoors().remove(
                trainDoorToDelete
        );

        trainDoorToDelete.getAmenityBlocks().forEach(amenityBlock -> {
            Floor amenityFloor = amenityBlock.getPatch().getFloor();
            Patch currentPatch = amenityBlock.getPatch();

            amenityFloor.getAmenityPatchSet().remove(currentPatch);
        });
    }
}
