package sample;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Region {
    private final int rows;
    private final int cols;
    private final Patch[][] region;
    private final List<double[][]> gradients;
    private final List<Patch> goals;
    private final double diffusionPercentage;
    private final int diffusionPasses;
    private Patch start;

    public Region(int rows, int cols, double diffusionPercentage, int diffusionPasses) {
        this.rows = rows;
        this.cols = cols;

        this.diffusionPercentage = diffusionPercentage;
        this.diffusionPasses = diffusionPasses;

        // Initialize region
        this.region = new Patch[rows][cols];
        this.initializeRegion();

        // Initialize empty list of gradients
        this.gradients = new ArrayList<>();

        // Initialize empty start and end patches
        this.start = null;
        this.goals = new ArrayList<>();
    }

    private void initializeRegion() {
        MatrixPosition matrixPosition;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                matrixPosition = new MatrixPosition(row, col);

//                if (row == start.getRow() && col == start.getCol()) {
//                    region[row][col] = new Patch(position, Patch.Status.START, new double[]{0.0, 0.0});
//                } else if (row == end1.getRow() && col == end1.getCol()) {
//                    region[row][col] = new Patch(position, Patch.Status.GOAL1, new double[]{1.0, 0.0});
//                } else if (row == end2.getRow() && col == end2.getCol()) {
//                    region[row][col] = new Patch(position, Patch.Status.GOAL2, new double[]{0.0, 1.0});
//                } else if (row == 0 || row == rows - 1 || col == 0 || col == cols - 1) {
//                    region[row][col] = new Patch(position, Patch.Status.OBSTACLE, new double[]{0.0, 0.0});
//                } else {
//                    region[row][col] = new Patch(position, Patch.Status.CLEAR, new double[]{0.0, 0.0});
//                }
                region[row][col] = new Patch(matrixPosition, Patch.Status.CLEAR);
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public double getDiffusionPercentage() {
        return diffusionPercentage;
    }

    public int getDiffusionPasses() {
        return diffusionPasses;
    }

    public Patch getStart() {
        return start;
    }

    public Patch getPatch(int row, int col) {
        return region[row][col];
    }

    public double getAttraction(int layer, int row, int col) {
        return gradients.get(layer)[row][col];
    }

    public int getNumGoals() {
        return this.goals.size();
    }

    public void setStart(int row, int col) {
        // Add start patch
        this.start = region[row][col];
    }

    public void removeStart() {
        // Reset start patch state, if it already existed as a start state
        if (this.start != null) {
            this.start.setStatus(Patch.Status.CLEAR);
        }

        // Forget start patch
        this.start = null;
    }

    public void setStatus(int row, int col, Patch.Status status) {
        Patch patch = region[row][col];

        // Check if the patch to be modified is a start patch
        if (patch.equals(this.start)) {
            // If it is, remove the start
            removeStart();
        } else {
            // Check if the patch to be modified is a goal patch
            // If it is, remove it
            this.goals.remove(patch);
        }

        // Set the patch to its new status
        if (status == Patch.Status.CLEAR) {
            // Change the status
            patch.setStatus(Patch.Status.CLEAR);
        } else if (status == Patch.Status.START) {
            // Remove the region's start patch
            removeStart();

            // Set the new start patch
            setStart(row, col);

            // Change the status
            patch.setStatus(Patch.Status.START);
        } else if (status == Patch.Status.GOAL) {
            // Add to the goals list
            this.goals.add(patch);

            // Change the status
            patch.setStatus(Patch.Status.GOAL);
        } else if (status == Patch.Status.OBSTACLE) {
            // Change the status
            patch.setStatus(Patch.Status.OBSTACLE);
        }
    }

    public void diffuseGoals() {
//        // Add goal patch
//        Patch goal = region[row][col];
//        goal.setStatus(Patch.Status.GOAL);
//
//        this.goals.add(goal);

        // Add a layer for the goal patch
        for (Patch goal : this.goals) {
            this.gradients.add(diffuseN(goal.getMatrixPosition().getRow(), goal.getMatrixPosition().getCol(), this.diffusionPasses));
        }
    }

    private double[][] diffuseN(int goalRow, int goalCol, int n) {
        // Create the gradient layer
        double[][] gradient = new double[rows][cols];

        for (double[] gradientRow : gradient) {
            Arrays.fill(gradientRow, 0.0);
        }

        // Apply the gradient of the goal
        gradient[goalRow][goalCol] = 1.0;

        // Diffuse for the specified number of times
        for (int i = 0; i < n; i++) {
            diffuse(gradient);
        }

        return gradient;
    }

    private void diffuse(double[][] gradient) {
        // Create a solution matrix
        double[][] solution = new double[rows][cols];

        // Fill solution matrix with 0s
        for (double[] row : solution) {
            Arrays.fill(row, 0.0);
        }

        // Diffuse each patch to the solution matrix
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Get current attraction value
                double attraction = gradient[row][col];

                // Get portion to be distributed
                double distributed = diffusionPercentage * attraction;

                // Distribute shares
                int numShares;

                if (row == 0 && col == 0
                        || row == rows - 1 && col == 0
                        || row == 0 && col == cols - 1
                        || row == rows - 1 && col == cols - 1) {
                    numShares = 3;
                } else if (row == 0 || row == rows - 1 || col == 0 || col == cols - 1) {
                    numShares = 5;
                } else {
                    numShares = 8;
                }

                // Update the attraction value
                solution[row][col] += attraction - (1.0 / 8.0) * numShares * distributed;

                // Distribute portions
                if (row > 0) {
                    solution[row - 1][col] += (1.0 / 8.0) * distributed;
                }

                if (row < rows - 1) {
                    solution[row + 1][col] += (1.0 / 8.0) * distributed;
                }

                if (col > 0) {
                    solution[row][col - 1] += (1.0 / 8.0) * distributed;
                }

                if (col < cols - 1) {
                    solution[row][col + 1] += (1.0 / 8.0) * distributed;
                }

                if (row > 0 && col > 0) {
                    solution[row - 1][col - 1] += (1.0 / 8.0) * distributed;
                }

                if (row > 0 && col < cols - 1) {
                    solution[row - 1][col + 1] += (1.0 / 8.0) * distributed;
                }

                if (row < rows - 1 && col > 0) {
                    solution[row + 1][col - 1] += (1.0 / 8.0) * distributed;
                }

                if (row < rows - 1 && col < cols - 1) {
                    solution[row + 1][col + 1] += (1.0 / 8.0) * distributed;
                }
            }
        }

        // Copy the results in the solution matrix to the region
        for (int row = 0; row < rows; row++) {
            if (cols >= 0) System.arraycopy(solution[row], 0, gradient[row], 0, cols);
        }
    }

    public void positionPassenger(Passenger passenger, int row, int col) {
        this.region[row][col].setPassenger(passenger);
    }

    public void movePassenger(Passenger passenger, int destRow, int destCol) {
        // Remove passenger from old patch
        removePassenger(passenger);

        // Place passenger on new patch
        positionPassenger(passenger, destRow, destCol);
        passenger.setXY(destCol, destRow);
    }

    public void removePassenger(Passenger passenger) {
        // Retrieve former patch
        Patch formerPatch = this.region[(int) passenger.getY()][(int) passenger.getX()];

        // Remove passenger from old patch
        formerPatch.setPassenger(null);
    }

    public boolean checkGoal(Passenger passenger) {
        // Get its goal
        Patch goal = this.goals.get(passenger.getGoalsReached());

        // Check if passenger is in its goal
        return (int) passenger.getX() == goal.getMatrixPosition().getCol()
                && (int) passenger.getY() == goal.getMatrixPosition().getRow();
    }

    public void printRegion(boolean showAttraction, int layer) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (!showAttraction) {
                    if (region[row][col].getPassenger() != null) {
                        stringBuilder.append("o");
                    } else {
                        switch (region[row][col].getStatus()) {
                            case CLEAR:
                                stringBuilder.append(".");
                                break;
                            case OBSTACLE:
                                stringBuilder.append("#");
                                break;
                            case START:
                                stringBuilder.append("*");
                                break;
                            case GOAL:
                                stringBuilder.append("+");
                                break;
                        }
                    }

                    stringBuilder.append(" ");
                } else {
//                    if (row == end.getRow() && col == end.getCol()) {
//                        System.out.print("         ");
//                    } else {
//                    System.out.print(new DecimalFormat("0.0000000").format(region[row][col].getAttraction()));
                    stringBuilder.append(new DecimalFormat("0.0000000").format(gradients.get(layer)[row][col]));
//                    }
                }

                stringBuilder.append(" ");
            }

            stringBuilder.append("\n");
        }

        System.out.println(stringBuilder.toString());
    }

    public List<Patch> getGoals() {
        return goals;
    }
}
