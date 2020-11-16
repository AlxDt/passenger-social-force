package sample;

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

    public int getCols() {
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

    public int getNumGoals() {
        return this.goals.size();
    }

    public void setType(int row, int column, Patch.Type type, int sequence) {
        Patch patch = region[row][column];

        // Only allow placement on clear patches
        if (patch.getType() == Patch.Type.CLEAR) {
            // Set the patch to its new status
            if (type == Patch.Type.START) {
                // Add to the starts list
                this.starts.add(patch);
            } else if (type == Patch.Type.WAYPOINT || type == Patch.Type.GATE || type == Patch.Type.EXIT) {
                if (type == Patch.Type.GATE) {
                    patch.setWaitingTime(Patch.ENTRY_WAITING_TIME);
                }

                // Add to the goals list
                if (sequence == this.goals.size()) {
                    this.goals.add(new ArrayList<>());
                }

                this.goals.get(sequence).add(patch);
            } else if (type == Patch.Type.OBSTACLE) {
                // Add to the obstacles list
                this.obstacles.add(patch);
            }

            // Change the status
            patch.setType(type);
        }
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setFloorField(int row, int column, PassengerMovement.Status status) {
        Patch patch = region[row][column];

        // Increment the current value in that position
        patch.getFloorFields().put(status, patch.getFloorFields().get(status) + 1.0);
    }

    public double getMaximumFloorFieldValue(PassengerMovement.Status status) {
        // Get the maximum floor field value for the chosen status
        double maximumFloorField = 0.0;

        for (int row = 0; row < Main.WALKWAY.region.length; row++) {
            for (int column = 0; column < Main.WALKWAY.region[0].length; column++) {
                double currentFloorField = Main.WALKWAY.getPatch(row, column).getFloorFields().get(status);

                if (currentFloorField > maximumFloorField) {
                    maximumFloorField = currentFloorField;
                }
            }
        }

        return maximumFloorField;
    }

    public void normalizeFloorFields(PassengerMovement.Status status) {
        // Get the maximum floor field value for this status
        double maximumFloorField = getMaximumFloorFieldValue(status);

        // Only perform the normalization pass when there actually is a floor field value other than zero
        // This is to avoid division by zero issues
        if (maximumFloorField > 0.0) {
            // Divide everything else using the maximum value found
            for (int row = 0; row < Main.WALKWAY.region.length; row++) {
                for (int column = 0; column < Main.WALKWAY.region[0].length; column++) {
                    Patch patch = Main.WALKWAY.getPatch(row, column);

                    double currentFloorField = patch.getFloorFields().get(status);
                    patch.getFloorFields().put(status, currentFloorField / maximumFloorField);
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

        // Check if passenger is in its goal
        return (int) passenger.getPassengerMovement().getPosition().getX() == goal.getMatrixPosition().getColumn()
                && (int) passenger.getPassengerMovement().getPosition().getY() == goal.getMatrixPosition().getRow();
    }

    public boolean checkPass(Passenger passenger, boolean trainDoorsOpen) {
        // Get its goal
//        Patch goal = this.goals.get(passenger.getGoalsReached()).get(passenger.getIndexGoalChosen());
        Patch goal = passenger.getPassengerMovement().getGoal();

        if (goal.getType() == Patch.Type.GATE) {
            if (goal.getWaitingTime() == 0) {
                goal.setWaitingTime(Patch.ENTRY_WAITING_TIME);

                return true;
            } else {
                goal.setWaitingTime(goal.getWaitingTime() - 1);

                return false;
            }
        } else if (goal.getType() == Patch.Type.EXIT) {
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
}
