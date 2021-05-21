package com.crowdsimulation.controller.graphics.amenity.editor;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.miscellaneous.Track;

import java.util.ArrayList;
import java.util.List;

public class TrackEditor extends AmenityEditor {
    public void drawTrackLine(
            Patch currentPatch,
            Track.TrackDirection trackDirection
    ) {
        // Get the two row values of the current patch
        // Use these as the basis for drawing the tracks
        int lowerRow = currentPatch.getMatrixPosition().getRow();
        int columns = Main.simulator.getCurrentFloor().getColumns();

        // Create the track line that will potentially be drawn
        List<Track> trackLine = new ArrayList<>();
        Main.simulator.getCurrentFloor().getTrackLines().add(trackLine);

        // For the entire span of the floor, draw tracks one by one
        // But if along the way, there is an obstruction, delete all tracks then stop trying to draw as the entire span
        // needs to be free of obstruction for the tracks to be drawn
        for (int currentColumn = 0; currentColumn < columns; currentColumn++) {
            Patch currentPatchInSpan = Main.simulator.getCurrentFloor().getPatch(lowerRow, currentColumn);

            List<Amenity.AmenityBlock> amenityBlocks
                    = Amenity.AmenityBlock.convertToAmenityBlocks(
                    currentPatchInSpan,
                    GraphicsController.currentAmenityFootprint.getCurrentRotation()
                            .getAmenityBlockTemplates()
            );

            // If there are no amenity blocks to be formed from the footprint at all, delete currently drawn tracks
            if (amenityBlocks == null) {
                Main.mainScreenController.deleteSingleAmenityInFloor(
                        trackLine.get(0),
                        Main.simulator.getBuildSubcategory()
                );

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

            // Otherwise, delete currently drawn tracks
            if (patchesClear) {
                // Prepare the amenity that will be placed on the station
                Track trackToAdd = Track.trackFactory.create(
                        amenityBlocks,
                        trackDirection
                );

                // Add this track to the list of all tracks on this floor
                trackLine.add(trackToAdd);
            } else {
                Main.mainScreenController.deleteSingleAmenityInFloor(
                        trackLine.get(0),
                        Main.simulator.getBuildSubcategory()
                );

                return;
            }
        }
    }

    public void editTrackLine(
            Track trackToEdit,
            Track.TrackDirection trackDirection
    ) {
        // Get the track line that contains this track
        List<Track> trackLineToEdit = TrackEditor.getTrackLine(trackToEdit);

        if (trackLineToEdit != null) {
            // Edit each track to have that property
            for (Track track : trackLineToEdit) {
                track.setTrackDirection(trackDirection);
            }
        }
    }

    public void deleteTrackLine(
            Track trackToDelete
    ) {
        // Get the track line that contains this track
        List<Track> trackLineToDelete = TrackEditor.getTrackLine(trackToDelete);

        if (trackLineToDelete != null) {
            // Clear that entire track line
            Main.simulator.getCurrentFloor().getTrackLines().remove(
                    trackLineToDelete
            );
        }
    }

    public static List<Track> getTrackLine(Track trackToDelete) {
        List<Track> trackLineToDelete = null;

        for (List<Track> trackLine : Main.simulator.getCurrentFloor().getTrackLines()) {
            if (trackLine.contains(trackToDelete)) {
                trackLineToDelete = trackLine;
            }
        }

        return trackLineToDelete;
    }
}
