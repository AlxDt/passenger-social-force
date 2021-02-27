package com.crowdsimulation.model.core.environment.station;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.utility.Coordinates;
import com.crowdsimulation.model.core.environment.station.utility.MatrixPosition;

import java.util.ArrayList;
import java.util.List;

public class Floor extends BaseStation {
    // Denotes the number of rows this floor has
    private final int rows;

    // Denotes the number of columns this floor has
    private final int columns;

    // The row x column containing the patches which constitute this floor
    private final Patch[][] patches;

    // Amenity lists
    private final List<StationGate> stationGates;

/*    private final List<Patch> starts;
    private final List<List<Patch>> goals;
    private final List<Patch> obstacles;
    private final List<Passenger> passengers;*/

    public Floor(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        // Initialize the patches
        this.patches = new Patch[rows][columns];
        this.initializePatches();

        // Initialize the amenity lists
        this.stationGates = new ArrayList<>();
/*        // Initialize empty start and end patches
        this.starts = new ArrayList<>();
        this.goals = new ArrayList<>();

        // Initialize the empty list of obstacles
        this.obstacles = new ArrayList<>();

        // Initialize the passenger list
        this.passengers = new ArrayList<>();*/
    }

    private void initializePatches() {
        MatrixPosition matrixPosition;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                matrixPosition = new MatrixPosition(row, column);

                patches[row][column] = new Patch(matrixPosition);
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Patch getPatch(Coordinates coordinates) {
        return getPatch((int) coordinates.getY(), (int) coordinates.getX());
    }

    public Patch getPatch(int row, int column) {
        return patches[row][column];
    }

    public static List<Patch> get5x5Field(Patch centerPatch, double heading, boolean includeCenterPatch) {
        final double fieldOfViewAngleDegrees = 135.0;

        int truncatedX = (int) centerPatch.getPatchCenterCoordinates().getX();
        int truncatedY = (int) centerPatch.getPatchCenterCoordinates().getY();

        Patch chosenPatch;
        List<Patch> patchesToExplore = new ArrayList<>();

        for (int rowOffset = -2; rowOffset <= 2; rowOffset++) {
            for (int columnOffset = -2; columnOffset <= 2; columnOffset++) {
                boolean xCondition;
                boolean yCondition;

                // Exclude the center patch, unless explicitly specified
                boolean isCenterPatch = rowOffset == 0 && columnOffset == 0;

                if (!includeCenterPatch) {
                    if (isCenterPatch) {
                        continue;
                    }
                }

                // Separate upper and lower rows
                if (rowOffset < 0) {
                    yCondition = truncatedY + rowOffset > 0;
                } else if (rowOffset > 0) {
                    yCondition = truncatedY + rowOffset < Main.simulator.getCurrentFloor().getRows();
                } else {
                    yCondition = true;
                }

                // Separate left and right columns
                if (columnOffset < 0) {
                    xCondition = truncatedX + columnOffset > 0;
                } else if (columnOffset > 0) {
                    xCondition = truncatedX + columnOffset < Main.simulator.getCurrentFloor().getColumns();
                } else {
                    xCondition = true;
                }

                // Insert the patch to the list of patches to be explored if the patches are within the bounds of the
                // walkway
                if (xCondition && yCondition) {
                    chosenPatch = Main.simulator.getCurrentFloor().getPatch(
                            truncatedY + rowOffset,
                            truncatedX + columnOffset
                    );

                    // Make sure that the patch to be added is within the field of view of the passenger which invoked
                    // this method
                    if ((includeCenterPatch && isCenterPatch) || Coordinates.isWithinFieldOfView(
                            centerPatch.getPatchCenterCoordinates(),
                            chosenPatch.getPatchCenterCoordinates(),
                            heading,
                            Math.toRadians(fieldOfViewAngleDegrees))) {
                        patchesToExplore.add(chosenPatch);
                    }
                }
            }
        }

        return patchesToExplore;
    }

    public List<StationGate> getStationGates() {
        return stationGates;
    }

    /*public int getNumGoals() {
        return this.goals.size();
    }*/

/*    public void setType(int row, int column, Patch.Type type, int sequence) {
        Patch patch = patches[row][column];

        int index = -1;

        // Only allow placement on clear patches
        if (patch.getType() == Patch.Type.CLEAR) {
            // Set the patch to its new state
            if (type == Patch.Type.SPAWN) {
                // Add to the starts list
                this.starts.add(patch);
            } else if (type == Patch.Type.TRANSACTION_AREA || type == Patch.Type.DESPAWN) {
                if (type == Patch.Type.TRANSACTION_AREA) {
                    patch.setWaitingTime(Patch.ENTRY_WAITING_TIME);
                }

                // Add to the goals list
                if (sequence == this.goals.size()) {
                    this.goals.add(new ArrayList<>());
                }

                this.goals.get(sequence).add(patch);
                index = this.goals.get(sequence).size() - 1;
            } else if (type == Patch.Type.OBSTACLE || type == Patch.Type.TICKET_BOOTH) {
                // Add to the obstacles list
                this.obstacles.add(patch);
            }

            // Change the state
            patch.setType(type, sequence, index);
        }
    }*/

/*    public void setFloorField(
            Patch patch,
            PassengerMovement.State state,
            Patch associatedGoal,
            double floorFieldValue) {
        // A fix for when the JavaFX slider gives the maximum value as 0.999 and not 1.0
        if (floorFieldValue >= 0.99) {
            floorFieldValue = 1.0;
        }

        FloorField floorField = patch.getFloorFieldValues().get(state);

//        floorField.setValue(floorField.getValue() + 1);
        floorField.setValue(floorFieldValue);

        // Set the goal this patch is associated to
        floorField.setGoal(associatedGoal);

        // Tell that associated goal patch to add this patch to the list of its associated patches, if it's not yet
        // already there
        if (!associatedGoal.getAssociatedPatches().contains(patch)) {
            associatedGoal.getAssociatedPatches().add(patch);
        }

        // If the floor field value is 1, this is the apex patch of the floor field
        if (floorFieldValue == 1.0) {
            // Make sure that the apex doesn't exist yet
//            assert floorField.getApex() == null;

            floorField.setApex(patch);
        }

//        FloorField newFloorField
//                = new FloorField(patch.getFloorFields().get(state).getValue() + 1.0, association);
//
//        patch.getFloorFields().put(state, newFloorField);
    }*/

/*    public double getMaximumFloorFieldValue(PassengerMovement.State state) {
        // Get the maximum floor field value for the chosen state
        double maximumFloorField = 0.0;

        for (int row = 0; row < Main.simulator.getCurrentFloor().patches.length; row++) {
            for (int column = 0; column < Main.simulator.getCurrentFloor().patches[0].length; column++) {
                double currentFloorField
                        = Main.simulator.getCurrentFloor().getPatch(row, column).getFloorFieldValues().get(state)
                        .getValue();

                if (currentFloorField > maximumFloorField) {
                    maximumFloorField = currentFloorField;
                }
            }
        }

        return maximumFloorField;
    }*/

/*    public boolean checkGoal(Passenger passenger) {
        // Get its goal
        Patch goal = passenger.getPassengerMovement().getGoal();

        // TODO: Take into account states other than transacting
        return passenger.getPassengerMovement().getAction() == PassengerMovement.Action.TRANSACTING
                && (int) passenger.getPassengerMovement().getPosition().getX() == goal.getMatrixPosition().getColumn()
                && (int) passenger.getPassengerMovement().getPosition().getY() == goal.getMatrixPosition().getRow();
    }*/

/*    public boolean checkPass(Passenger passenger, boolean trainDoorsOpen) {
        // Get its goal
//        Patch goal = this.goals.get(passenger.getGoalsReached()).get(passenger.getIndexGoalChosen());
        Patch goal = passenger.getPassengerMovement().getGoal();

        if (goal.getType() == Patch.Type.TRANSACTION_AREA) {
            if (goal.getWaitingTime() == 0) {
                goal.setWaitingTime(Patch.ENTRY_WAITING_TIME);

                return true;
            } else {
                goal.setWaitingTime(goal.getWaitingTime() - 1);

                return false;
            }
        } else if (goal.getType() == Patch.Type.DESPAWN) {
            return trainDoorsOpen;
        }

        return true;
    }*/

/*    public List<Patch> getGoalsAtSequence(int sequence) {
        return goals.get(sequence);
    }

    public List<List<Patch>> getGoals() {
        return goals;
    }

    public List<Patch> getGoalsFlattened() {
        List<Patch> flattened = new ArrayList<>();

        for (List<Patch> patches : this.goals) {
            flattened.addAll(patches);
        }

        return flattened;
    }

    public Patch getGoalFromGoalId(String goalId) {
        String[] split = goalId.split("-");

        int sequence = Integer.parseInt(split[0].substring(1));
        int index = Integer.parseInt(split[1]);

        return this.goals.get(sequence).get(index);
    }*/
}
