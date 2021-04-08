package com.crowdsimulation.model.core.environment.station.patch.floorfield.headful;

import com.crowdsimulation.model.core.agent.passenger.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueingFloorField extends HeadfulFloorField {
    public QueueingFloorField(Queueable target) {
        super(target);
    }

/*    // Given the build subcategory, the patch that will have possession of the floor field, and the patch to draw on,
    // draw an individual floor field value
    public static void drawFloorField(
            Simulator.BuildSubcategory buildSubcategory,
            QueueObject queueable,
            Patch chosenPatch) {
        switch (buildSubcategory) {
            case SECURITY:

                break;
            case STAIRS:
                break;
            case ESCALATOR:
                break;
            case ELEVATOR:
                break;
            case TICKET_BOOTH:
                break;
            case TURNSTILE:
                break;
            case TRAIN_BOARDING_AREA:
                break;
        }
    }*/

    // Adds an individual floor field to a patch and its associated floor field
    public static boolean addFloorFieldValue(
            Patch patch,
            Queueable target,
            FloorFieldState floorFieldState,
            double value) {
        // When adding a floor field value, these things have to happen:
        //   1) Register the patch where the floor field value is to be drawn to the target queueable's floor field
        //   2) Add the floor field value to the patch itself
        // In the target queueable, register the patch into the target's list of floor fields, if possible
        if (QueueingFloorField.registerPatch(patch, target, floorFieldState, value)) {
            // Add the floor field value to the patch itself
            // If the patch still doesn't have an entry for the target, add one and put the map there
            if (!patch.getFloorFieldValues().containsKey(target)) {
                // Prepare the floor field state and value map
                Map<FloorFieldState, Double> map = new HashMap<>();
                map.put(floorFieldState, value);

                // Put the target and the map into the patch
                patch.getFloorFieldValues().put(target, map);
            } else {
                // If it already has, just put the floor field state and the value there
                patch.getFloorFieldValues().get(target).put(floorFieldState, value);
            }

            // Patch registration successful, return true
            return true;
        } else {
            // If patch registration as unsuccessful, return false
            return false;
        }
    }

    // Add the patch to the floor fields kept tracked by the target queueable
    private static boolean registerPatch(
            Patch patch,
            Queueable target,
            FloorFieldState floorFieldState,
            double value) {
        final double EPSILON = 1E-6;

        boolean apexNull = false;

        QueueingFloorField queueingFloorField = target.retrieveFloorField(floorFieldState);
        List<Patch> associatedPatches = queueingFloorField.getAssociatedPatches();

        // If the floor field value is one, check if this floor field already has a value of one
        // This is to make sure that there is only one apex in the floor field
        if (Math.abs(value - 1.0) < EPSILON) {
            // If it does, refuse to register the patch
            if (queueingFloorField.getApex() != null) {
                return false;
            } else {
                // If it hasn't yet, set the patch as the apex
                queueingFloorField.setApex(patch);
            }
        }

        // Check if the floor field already contains the patch
        if (associatedPatches.contains(patch)) {
            // If it already does, just modify the value that's already there
            // Take note of the patch that's already in there
            Patch patchPresent = associatedPatches.get(associatedPatches.indexOf(patch));

            // Get the value in the patch present
            double valuePresent = patchPresent.getFloorFieldValues().get(target).get(floorFieldState);

            // If the present value is 1.0, and the value to replace it isn't 1.0, indicate that this patch doesn't have
            // an apex anymore as it was replaced with another value
            if (Math.abs(valuePresent - 1.0) < EPSILON && value < 1.0 - EPSILON) {
                queueingFloorField.setApex(null);
            }
        } else {
            // If it doesn't contain the patch yet, add it
            associatedPatches.add(patch);
        }

        return true;
    }

    // Remove the patch from the floor fields kept tracked on by the target queueable
    private static void unregisterPatch(
            Patch patch,
            Queueable target,
            FloorFieldState floorFieldState,
            double value
    ) {
        final double EPSILON = 1E-6;

        QueueingFloorField queueingFloorField = target.retrieveFloorField(floorFieldState);

        // Unregister the patch from this target
        queueingFloorField.getAssociatedPatches().remove(patch);

        // If the value being removed is 1.0, this means this floor field won't have an apex anymore
        if (Math.abs(value - 1.0) < EPSILON) {
            queueingFloorField.setApex(null);
        }
    }

    // In a given patch, delete an individual floor field value in a floor field owned by a given target
    public static void deleteFloorFieldValue(
            Patch patch,
            Queueable target,
            FloorFieldState floorFieldState
    ) {
        // When deleting a floor field value, these things have to happen:
        //   1) Unregister the patch where the floor field value to be deleted is from the target queueable's floor
        //      field
        //   2) Remove the floor field value from the patch itself
        // Unregister the patch from the target's list of floor fields
        // Get the value of the floor field value to be removed as well
        Map<FloorFieldState, Double> map = patch.getFloorFieldValues().get(target);

        // Only perform deletion when there definitely is a floor field value in this patch
        // Else, do nothing
        if (map != null) {
            double value = map.get(floorFieldState);

            QueueingFloorField.unregisterPatch(patch, target, floorFieldState, value);

            // In the given patch, remove the entry with the reference to the queueable target
            map.remove(floorFieldState);

            // If the previous deletion has left the floor field state and value map empty for that target queueable, delete
            // the map from the target
            if (map.isEmpty()) {
                patch.getFloorFieldValues().remove(target);
            }
        }
    }

    // Clear the given floor field
    public static void clearFloorField(
            QueueingFloorField queueingFloorField,
            FloorFieldState floorFieldState
    ) {
        // In each patch in the floor field to be deleted, delete the reference to its target
        // This deletes the value within that patch
        List<Patch> associatedPatches = queueingFloorField.getAssociatedPatches();
        Queueable target = queueingFloorField.getTarget();

        ArrayList<Patch> associatedPatchesCopy = new ArrayList<>(associatedPatches);

        for (Patch patch : associatedPatchesCopy) {
            QueueingFloorField.deleteFloorFieldValue(
                    patch,
                    target,
                    floorFieldState
            );
        }

        associatedPatchesCopy.clear();
    }

    // A combination of a passenger's direction, state, and current target, this object is used for the differentiation
    // of floor fields
    public static class FloorFieldState {
        private final PassengerMovement.Direction direction;
        private final PassengerMovement.State state;
        private final Queueable target;

        public FloorFieldState(
                PassengerMovement.Direction direction,
                PassengerMovement.State state,
                Queueable target) {
            this.direction = direction;
            this.state = state;
            this.target = target;
        }

        public PassengerMovement.Direction getDirection() {
            return direction;
        }

        public PassengerMovement.State getState() {
            return state;
        }

        public Queueable getTarget() {
            return target;
        }
    }
}
