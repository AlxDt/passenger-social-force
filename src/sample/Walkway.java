package sample;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Walkway {
    private final int rows;
    private final int columns;
    private final Patch[][] region;
    //    private final List<List<double[][]>> gradients;
    private final List<Patch> starts;
    private final List<List<Patch>> goals;
    private final List<Patch> obstacles;
    private final List<Passenger> passengers;

    public Walkway(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        // Initialize region
        this.region = new Patch[rows][columns];
        this.initializeRegion();

        // Initialize empty list of gradients
//        this.gradients = new ArrayList<>();

        // Initialize empty start and end patches
        this.starts = new ArrayList<>();
        this.goals = new ArrayList<>();

        // Initialize the empty list of obstacles
        this.obstacles = new ArrayList<>();

        // Initialize the passenger list
        this.passengers = new ArrayList<>();
    }

    private void initializeRegion() {
        MatrixPosition matrixPosition;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                matrixPosition = new MatrixPosition(row, column);

//                if (row == start.getRow() && column == start.getCol()) {
//                    region[row][column] = new Patch(position, Patch.Status.START, new double[]{0.0, 0.0});
//                } else if (row == end1.getRow() && column == end1.getCol()) {
//                    region[row][column] = new Patch(position, Patch.Status.GOAL1, new double[]{1.0, 0.0});
//                } else if (row == end2.getRow() && column == end2.getCol()) {
//                    region[row][column] = new Patch(position, Patch.Status.GOAL2, new double[]{0.0, 1.0});
//                } else if (row == 0 || row == rows - 1 || column == 0 || column == columns - 1) {
//                    region[row][column] = new Patch(position, Patch.Status.OBSTACLE, new double[]{0.0, 0.0});
//                } else {
//                    region[row][column] = new Patch(position, Patch.Status.CLEAR, new double[]{0.0, 0.0});
//                }

                region[row][column] = new Patch(matrixPosition, Patch.Type.CLEAR);
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public List<Patch> getStarts() {
        return starts;
    }

    public Patch getPatch(Coordinates coordinates) {
        return getPatch((int) coordinates.getY(), (int) coordinates.getX());
    }

    public Patch getPatch(int row, int column) {
        return region[row][column];
    }

//    public double getAttraction(int sequence, int index, int row, int column) {
//        return gradients.get(sequence).get(index)[row][column];
//    }

    public Patch chooseBestNeighboringPatch(Patch patch, double heading, PassengerMovement.State state) {
        Patch chosenPatch;

        int truncatedX = (int) patch.getPatchCenterCoordinates().getX();
        int truncatedY = (int) patch.getPatchCenterCoordinates().getY();

        double currentHeadingDegrees = Math.toDegrees(heading);

        // Right
        if (currentHeadingDegrees >= 337.5 && currentHeadingDegrees <= 360.0
                || currentHeadingDegrees >= 0 && currentHeadingDegrees < 22.5) {
            // Candidates are the upper and lower patches
            chosenPatch = chooseHorizontal(patch, state, truncatedX, truncatedY);
        } else if (currentHeadingDegrees >= 22.5 && currentHeadingDegrees < 67.5) {
            // Upper right
            // Candidates are the upper left and lower right patches
            chosenPatch = choosePositiveDiagonal(patch, state, truncatedX, truncatedY);
        } else if (currentHeadingDegrees >= 67.5 && currentHeadingDegrees < 112.5) {
            // Up
            // Candidates are the left and right patches
            chosenPatch = chooseVertical(patch, state, truncatedX, truncatedY);
        } else if (currentHeadingDegrees >= 112.5 && currentHeadingDegrees < 157.5) {
            // Upper left
            // Candidates are the upper right and lower left patches
            chosenPatch = chooseNegativeDiagonal(patch, state, truncatedX, truncatedY);
        } else if (currentHeadingDegrees >= 157.5 && currentHeadingDegrees < 202.5) {
            // Left
            // Candidates are the upper and lower patches
            chosenPatch = chooseHorizontal(patch, state, truncatedX, truncatedY);
        } else if (currentHeadingDegrees >= 202.5 && currentHeadingDegrees < 247.5) {
            // Lower left
            // Candidates are the upper left and lower right patches
            chosenPatch = choosePositiveDiagonal(patch, state, truncatedX, truncatedY);
        } else if (currentHeadingDegrees >= 247.5 && currentHeadingDegrees < 292.5) {
            // Down
            // Candidates are the left and right patches
            chosenPatch = chooseVertical(patch, state, truncatedX, truncatedY);
        } else {
            // Lower right
            // Candidates are the upper right and lower left patches
            chosenPatch = chooseNegativeDiagonal(patch, state, truncatedX, truncatedY);
        }

        return chosenPatch;
    }

    private Patch chooseNegativeDiagonal(Patch patch, PassengerMovement.State state, int truncatedX, int truncatedY) {
        Patch chosenPatch;

        final boolean upperRightCondition = truncatedX + 1 < Main.WALKWAY.getColumns() && truncatedY > 0;
        final boolean lowerLeftCondition = truncatedX > 0 && truncatedY + 1 < Main.WALKWAY.getRows();

        if (upperRightCondition && lowerLeftCondition) {
            Patch upperRight = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX + 1);
            Patch lowerLeft = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX - 1);

            chosenPatch = getPatchWithHighestFloorFieldValue(state, patch, upperRight, lowerLeft);
        } else {
            // One of the required patches are out of bounds
            if (!upperRightCondition && lowerLeftCondition) {
                // Upper right not available, choose lower left
                Patch lowerLeft = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX - 1);
                chosenPatch = getPatchWithHighestFloorFieldValue(state, patch, lowerLeft);
            } else if (upperRightCondition && !lowerLeftCondition) {
                // Lower left not available, choose upper right
                Patch upperRight = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX + 1);
                chosenPatch = getPatchWithHighestFloorFieldValue(state, patch, upperRight);
            } else {
                // Neither are available
                chosenPatch = patch;
            }
        }

        return chosenPatch;
    }

    private Patch choosePositiveDiagonal(Patch patch, PassengerMovement.State state, int truncatedX, int truncatedY) {
        Patch chosenPatch;

        final boolean upperLeftCondition = truncatedX > 0 && truncatedY > 0;
        final boolean lowerRightCondition = truncatedX + 1 < Main.WALKWAY.getColumns() && truncatedY + 1 < Main.WALKWAY.getRows();

        if (upperLeftCondition && lowerRightCondition) {
            Patch upperLeft = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX - 1);
            Patch lowerRight = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX + 1);

            chosenPatch = getPatchWithHighestFloorFieldValue(state, patch, upperLeft, lowerRight);
        } else {
            // One of the required patches are out of bounds
            if (!upperLeftCondition && lowerRightCondition) {
                // Upper left not available, choose lower right
                Patch lowerRight = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX + 1);
                chosenPatch = getPatchWithHighestFloorFieldValue(state, patch, lowerRight);
            } else if (upperLeftCondition && !lowerRightCondition) {
                // Lower right not available, choose upper left
                Patch upperLeft = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX - 1);
                chosenPatch = getPatchWithHighestFloorFieldValue(state, patch, upperLeft);
            } else {
                // Neither are available
                chosenPatch = patch;
            }
        }

        return chosenPatch;
    }

    private Patch chooseVertical(Patch patch, PassengerMovement.State state, int truncatedX, int truncatedY) {
        Patch chosenPatch;

        final boolean leftCondition = truncatedX > 0;
        final boolean rightCondition = truncatedX + 1 < Main.WALKWAY.getColumns();

        if (leftCondition && rightCondition) {
            Patch left = Main.WALKWAY.getPatch(truncatedY, truncatedX - 1);
            Patch right = Main.WALKWAY.getPatch(truncatedY, truncatedX + 1);

            chosenPatch = getPatchWithHighestFloorFieldValue(state, patch, left, right);
        } else {
            // One of the required patches are out of bounds
            if (!leftCondition) {
                // Left not available, choose right
                Patch right = Main.WALKWAY.getPatch(truncatedY, truncatedX + 1);
                chosenPatch = getPatchWithHighestFloorFieldValue(state, patch, right);
            } else {
                // Right not available, choose left
                Patch left = Main.WALKWAY.getPatch(truncatedY, truncatedX - 1);
                chosenPatch = getPatchWithHighestFloorFieldValue(state, patch, left);
            }
        }

        return chosenPatch;
    }

    private Patch chooseHorizontal(Patch patch, PassengerMovement.State state, int truncatedX, int truncatedY) {
        Patch chosenPatch;

        final boolean upperCondition = truncatedY > 0;
        final boolean lowerCondition = truncatedY + 1 < Main.WALKWAY.getRows();

        if (upperCondition && lowerCondition) {
            Patch upper = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX);
            Patch lower = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX);

            chosenPatch = getPatchWithHighestFloorFieldValue(state, patch, upper, lower);
        } else {
            // One of the required patches are out of bounds
            if (!upperCondition) {
                // Upper not available, choose lower
                Patch lower = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX);
                chosenPatch = getPatchWithHighestFloorFieldValue(state, patch, lower);
            } else {
                // Lower not available, choose upper
                Patch upper = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX);
                chosenPatch = getPatchWithHighestFloorFieldValue(state, patch, upper);
            }
        }

        return chosenPatch;
    }

    private Patch getPatchWithHighestFloorFieldValue(PassengerMovement.State state, Patch... patches) {
        if (patches.length == 0) {
            return null;
        }

        Patch patchWithHighestFloorFieldValue = null;
        double highestFloorFieldValue = -Double.MAX_VALUE;

        for (Patch patch : patches) {
            double floorFieldValue = patch.getFloorFieldValues().get(state).getValue();

            if (floorFieldValue > highestFloorFieldValue) {
                highestFloorFieldValue = floorFieldValue;
                patchWithHighestFloorFieldValue = patch;
            }
        }

        return patchWithHighestFloorFieldValue;
    }
    
    public static List<Patch> get5x5Field(Patch centerPatch, double heading) {
        int truncatedX = (int) centerPatch.getPatchCenterCoordinates().getX();
        int truncatedY = (int) centerPatch.getPatchCenterCoordinates().getY();

        Patch chosenPatch;
        List<Patch> patchesToExplore = new ArrayList<>();

        for (int rowOffset = -2; rowOffset <= 2; rowOffset++) {
            for (int columnOffset = -2; columnOffset <= 2; columnOffset++) {
                boolean xCondition;
                boolean yCondition;

                // Separate upper and lower rows
                if (rowOffset < 0) {
                    yCondition = truncatedY + rowOffset > 0;
                } else if (rowOffset > 0) {
                    yCondition = truncatedY + rowOffset < Main.WALKWAY.getRows();
                } else {
                    yCondition = true;
                }

                // Separate left and right columns
                if (columnOffset < 0) {
                    xCondition = truncatedX + columnOffset > 0;
                } else if (columnOffset > 0) {
                    xCondition = truncatedX + columnOffset < Main.WALKWAY.getColumns();
                } else {
                    xCondition = true;
                }

                // Insert the patch to the list of patches to be explored if the patches are within the bounds of the
                // walkway
                if (xCondition && yCondition) {
                    chosenPatch = Main.WALKWAY.getPatch(truncatedY + rowOffset, truncatedX + columnOffset);

                    // Make sure that the patch to be added is within the field of view of the passenger which invoked
                    // this method
                    if (Coordinates.isWithinFieldOfView(
                            centerPatch.getPatchCenterCoordinates(),
                            chosenPatch.getPatchCenterCoordinates(),
                            heading,
                            Math.toRadians(90.0))) {
                        patchesToExplore.add(chosenPatch);
                    }
                }
            }
        }

        return patchesToExplore;
    }

    public int getNumGoals() {
        return this.goals.size();
    }

    public void setType(int row, int column, Patch.Type type, int sequence) {
        Patch patch = region[row][column];

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
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setFloorField(int row, int column, PassengerMovement.State state, Patch associatedGoal) {
        Patch patch = region[row][column];

        // Increment the current value in that position
        FloorField floorField = patch.getFloorFieldValues().get(state);

        floorField.setValue(floorField.getValue() + 1);

        // Set the goal this patch is associated to
        floorField.setAssociation(associatedGoal);

        // Tell that associated goal patch to add this patch to the list of its associated patches, if it's not yet
        // already there
        if (!associatedGoal.getAssociatedPatches().contains(patch)) {
            associatedGoal.getAssociatedPatches().add(patch);
        }

//        FloorField newFloorField
//                = new FloorField(patch.getFloorFields().get(state).getValue() + 1.0, association);
//
//        patch.getFloorFields().put(state, newFloorField);
    }

    public double getMaximumFloorFieldValue(PassengerMovement.State state) {
        // Get the maximum floor field value for the chosen state
        double maximumFloorField = 0.0;

        for (int row = 0; row < Main.WALKWAY.region.length; row++) {
            for (int column = 0; column < Main.WALKWAY.region[0].length; column++) {
                double currentFloorField = Main.WALKWAY.getPatch(row, column).getFloorFieldValues().get(state).getValue();

                if (currentFloorField > maximumFloorField) {
                    maximumFloorField = currentFloorField;
                }
            }
        }

        return maximumFloorField;
    }

    public void normalizeFloorFields(PassengerMovement.State state) {
        // Get the maximum floor field value for this state
        double maximumFloorField = getMaximumFloorFieldValue(state);

        // Only perform the normalization pass when there actually is a floor field value other than zero
        // This is to avoid division by zero issues
        if (maximumFloorField > 0.0) {
            // Divide everything else using the maximum value found
            for (int row = 0; row < Main.WALKWAY.region.length; row++) {
                for (int column = 0; column < Main.WALKWAY.region[0].length; column++) {
                    Patch patch = Main.WALKWAY.getPatch(row, column);

                    FloorField floorField = patch.getFloorFieldValues().get(state);
                    floorField.setValue(floorField.getValue() / maximumFloorField);

//                    patch.getFloorFields().put(state, currentFloorField / maximumFloorField);
                }
            }
        }
    }

//    public void diffuseGoals() {
//        // Add a layer for the goal patch
//        int index = 0;
//
//        for (List<Patch> sequence : this.goals) {
//            this.gradients.add(new ArrayList<>());
//
//            for (Patch goal : sequence) {
//                this.gradients.get(index).add(
//                        diffuseN(
//                                goal.getMatrixPosition().getRow(),
//                                goal.getMatrixPosition().getColumn(),
//                                this.diffusionPasses)
//                );
//            }
//
//            index++;
//        }
//    }

//    private double[][] diffuseN(int goalRow, int goalColumn, int n) {
//        // Create the gradient layer
//        double[][] gradient = new double[rows][columns];
//
//        for (double[] gradientRow : gradient) {
//            Arrays.fill(gradientRow, 0.0);
//        }
//
//        // Apply the attractive effect of the goal
//        gradient[goalRow][goalColumn] = 1.0;
//
//        // Diffuse for the specified number of times
//        for (int i = 0; i < n; i++) {
//            diffuse(gradient);
//        }
//
////        // Apply the repulsive effect of the goals
////        for (Patch obstacle : this.obstacles) {
////            // Up
////            gradient[obstacle.getMatrixPosition().getColumn()][obstacle.getMatrixPosition().getRow() + 1] = -0.5;
////
////            // Down
////            gradient[obstacle.getMatrixPosition().getColumn()][obstacle.getMatrixPosition().getRow() + 1] = -0.5;
////
////            gradient[obstacle.getMatrixPosition().getColumn()][obstacle.getMatrixPosition().getRow()] = -1.0;
////        }
//
//        return gradient;
//    }

//    private void diffuse(double[][] gradient) {
//        // Create a solution matrix
//        double[][] solution = new double[rows][columns];
//
//        // Fill solution matrix with 0s
//        for (double[] row : solution) {
//            Arrays.fill(row, 0.0);
//        }
//
//        // Diffuse each patch to the solution matrix
//        for (int row = 0; row < rows; row++) {
//            for (int column = 0; column < columns; column++) {
//                // Get current attraction value
//                double attraction = gradient[row][column];
//
//                // Get portion to be distributed
//                double distributed = diffusionPercentage * attraction;
//
//                // Distribute shares
//                int numShares;
//
//                if (row == 0 && column == 0
//                        || row == rows - 1 && column == 0
//                        || row == 0 && column == columns - 1
//                        || row == rows - 1 && column == columns - 1) {
//                    numShares = 3;
//                } else if (row == 0 || row == rows - 1 || column == 0 || column == columns - 1) {
//                    numShares = 5;
//                } else {
//                    numShares = 8;
//                }
////                numShares = 8;
//
//                // Update the attraction value
//                solution[row][column] += attraction - (1.0 / 8.0) * numShares * distributed;
//
//                // Distribute portions
//                if (row > 0) {
//                    solution[row - 1][column] += (1.0 / 8.0) * distributed;
//                }
//
//                if (row < rows - 1) {
//                    solution[row + 1][column] += (1.0 / 8.0) * distributed;
//                }
//
//                if (column > 0) {
//                    solution[row][column - 1] += (1.0 / 8.0) * distributed;
//                }
//
//                if (column < columns - 1) {
//                    solution[row][column + 1] += (1.0 / 8.0) * distributed;
//                }
//
//                if (row > 0 && column > 0) {
//                    solution[row - 1][column - 1] += (1.0 / 8.0) * distributed;
//                }
//
//                if (row > 0 && column < columns - 1) {
//                    solution[row - 1][column + 1] += (1.0 / 8.0) * distributed;
//                }
//
//                if (row < rows - 1 && column > 0) {
//                    solution[row + 1][column - 1] += (1.0 / 8.0) * distributed;
//                }
//
//                if (row < rows - 1 && column < columns - 1) {
//                    solution[row + 1][column + 1] += (1.0 / 8.0) * distributed;
//                }
//            }
//        }
//
//        // Copy the results in the solution matrix to the region
//        for (int row = 0; row < rows; row++) {
//            if (columns >= 0) System.arraycopy(solution[row], 0, gradient[row], 0, columns);
//        }
//    }

//    public void positionPassenger(Passenger passenger, int row, int column) {
//        this.region[row][column].getPassengers().add(passenger);
//    }

//    // TODO: Change to double
//    public void movePassenger(Passenger passenger, int destRow, int destColumn) {
//        // Remove passenger from old patch
//        removePassenger(passenger);
//
//        // Place passenger on new patch
//        positionPassenger(passenger, destRow, destColumn);
//        passenger.setPosition(destColumn, destRow);
//    }

//    public void removePassenger(Passenger passenger) {
//        // Retrieve former patch
//        Patch formerPatch = this.region[(int) passenger.getPosition().getY()][(int) passenger.getPosition().getX()];
//
//        // Remove passenger from old patch
//        formerPatch.getPassengers().remove(passenger);
//    }

    public boolean checkGoal(Passenger passenger) {
        // Get its goal
//        Patch goal = this.goals.get(passenger.getGoalsReached()).get(passenger.getIndexGoalChosen());
        Patch goal = passenger.getPassengerMovement().getGoal();

        // TODO: Take into account states other than transacting
        return passenger.getPassengerMovement().getAction() == PassengerMovement.Action.TRANSACTING
                && (int) passenger.getPassengerMovement().getPosition().getX() == goal.getMatrixPosition().getColumn()
                && (int) passenger.getPassengerMovement().getPosition().getY() == goal.getMatrixPosition().getRow();
    }

    public boolean checkPass(Passenger passenger, boolean trainDoorsOpen) {
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
    }

//    public void printRegion(int layer) {
//        StringBuilder stringBuilder = new StringBuilder();
//
//        for (int row = 0; row < rows; row++) {
//            for (int column = 0; column < columns; column++) {
////                    if (row == end.getRow() && column == end.getCol()) {
////                        System.out.print("         ");
////                    } else {
////                    System.out.print(new DecimalFormat("0.0000000").format(region[row][column].getAttraction()));
//                stringBuilder.append(new DecimalFormat("0.0000000").format(gradients.get(layer).get(0)[row][column]));
////                    }
//
//                stringBuilder.append(",");
//            }
//
//            stringBuilder.append("\n");
//        }
//
//        System.out.println(stringBuilder.toString());
//    }

    public List<Patch> getGoalsAtSequence(int sequence) {
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
    }
}
