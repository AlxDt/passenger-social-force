package sample;

import javafx.scene.paint.Color;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Passenger {
    private final Color color;
    //    private final List<Main.Direction> directions;
    private double x;
    private double y;
    private int goalsReached;
    private int indexGoalChosen;
    private int goalsLeft;

    public Passenger(double x, double y, int numGoals) {
        this.x = x;
        this.y = y;
        this.goalsReached = 0;
        this.goalsLeft = numGoals;
//        this.directions = new ArrayList<>();

        Random rng = new Random();

        this.color = Color.color(rng.nextDouble(), rng.nextDouble(), rng.nextDouble());
    }

    public Color getColor() {
        return color;
    }

    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getGoalsReached() {
        return goalsReached;
    }

    public int getGoalsLeft() {
        return goalsLeft;
    }

    public void reachGoal() {
        this.goalsReached++;
        this.goalsLeft--;
    }

    public Patch choosePatch(int rowNumber, int colNumber, boolean random) {
        Patch candidate;

        double maximumGradient = Double.MIN_VALUE;
        Patch chosen = null;
        double attraction;

        // Get the next set of goals, if available
        List<Patch> nextGoals = Main.region.getGoalsAtSequence(goalsReached);

        // Get the nearest goal to the passenger
        double minDistance = Double.MAX_VALUE;
        int indexNearestGoal = 0;

        int index = 0;

        for (Patch goal : nextGoals) {
            double distance = Math.sqrt(
                    Math.pow(goal.getMatrixPosition().getCol() - this.getX(), 2)
                            + Math.pow(goal.getMatrixPosition().getRow() - this.getY(), 2)
            );

            if (distance < minDistance) {
                minDistance = distance;
                indexNearestGoal = index;
            }

            index++;
        }

        this.indexGoalChosen = indexNearestGoal;

        if (!random) {
            // Left
            if (x > 0) {
                candidate = Main.region.getPatch((int) y, (int) x - 1);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.region.getAttraction(goalsReached, indexNearestGoal, (int) y, (int) x - 1);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.region.getPatch((int) y, (int) x - 1);
                    }
                }
            }

            // Right
            if (x < colNumber - 1) {
                candidate = Main.region.getPatch((int) y, (int) x + 1);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.region.getAttraction(goalsReached, indexNearestGoal, (int) y, (int) x + 1);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.region.getPatch((int) y, (int) x + 1);
                    }
                }
            }

            // Up
            if (y > 0) {
                candidate = Main.region.getPatch((int) y - 1, (int) x);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.region.getAttraction(goalsReached, indexNearestGoal, (int) y - 1, (int) x);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.region.getPatch((int) y - 1, (int) x);
                    }
                }
            }

            // Down
            if (y < rowNumber - 1) {
                candidate = Main.region.getPatch((int) y + 1, (int) x);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.region.getAttraction(goalsReached, indexNearestGoal, (int) y + 1, (int) x);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.region.getPatch((int) y + 1, (int) x);
                    }
                }
            }

            // Upper left
            if (x > 0 && y > 0) {
                candidate = Main.region.getPatch((int) y - 1, (int) x - 1);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.region.getAttraction(goalsReached, indexNearestGoal, (int) y - 1, (int) x - 1);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.region.getPatch((int) y - 1, (int) x - 1);
                    }
                }
            }

            // Lower left
            if (x > 0 && y < rowNumber - 1) {
                candidate = Main.region.getPatch((int) y + 1, (int) x - 1);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.region.getAttraction(goalsReached, indexNearestGoal, (int) y + 1, (int) x - 1);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.region.getPatch((int) y + 1, (int) x - 1);
                    }
                }
            }

            // Upper right
            if (x < colNumber - 1 && y > 0) {
                candidate = Main.region.getPatch((int) y - 1, (int) x + 1);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.region.getAttraction(goalsReached, indexNearestGoal, (int) y - 1, (int) x + 1);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.region.getPatch((int) y - 1, (int) x + 1);
                    }
                }
            }

            // Lower right
            if (x < colNumber - 1 && y < rowNumber - 1) {
                candidate = Main.region.getPatch((int) y + 1, (int) x + 1);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.region.getAttraction(goalsReached, indexNearestGoal, (int) y + 1, (int) x + 1);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.region.getPatch((int) y + 1, (int) x + 1);
                    }
                }
            }
        } else {
            switch (new Random().nextInt(8)) {
                case 0:
                    if (x > 0) {
                        candidate = Main.region.getPatch((int) y, (int) x - 1);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.region.getPatch((int) y, (int) x - 1);
                        }
                    }

                    break;
                case 1:
                    if (x < colNumber - 1) {
                        candidate = Main.region.getPatch((int) y, (int) x + 1);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.region.getPatch((int) y, (int) x + 1);
                        }
                    }

                    break;
                case 2:
                    if (y > 0) {
                        candidate = Main.region.getPatch((int) y - 1, (int) x);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.region.getPatch((int) y - 1, (int) x);
                        }
                    }

                    break;
                case 3:
                    if (y < rowNumber - 1) {
                        candidate = Main.region.getPatch((int) y + 1, (int) x);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.region.getPatch((int) y + 1, (int) x);
                        }
                    }

                    break;
                case 4:
                    if (x > 0 && y > 0) {
                        candidate = Main.region.getPatch((int) y - 1, (int) x - 1);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.region.getPatch((int) y - 1, (int) x - 1);
                        }
                    }

                    break;
                case 5:
                    if (x > 0 && y < rowNumber - 1) {
                        candidate = Main.region.getPatch((int) y + 1, (int) x - 1);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.region.getPatch((int) y + 1, (int) x - 1);
                        }
                    }

                    break;
                case 6:
                    if (x < colNumber - 1 && y > 0) {
                        candidate = Main.region.getPatch((int) y - 1, (int) x + 1);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.region.getPatch((int) y - 1, (int) x + 1);
                        }
                    }

                    break;
                case 7:
                    if (x < colNumber - 1 && y < rowNumber - 1) {
                        candidate = Main.region.getPatch((int) y + 1, (int) x + 1);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.region.getPatch((int) y + 1, (int) x + 1);
                        }
                    }

                    break;
            }
        }

        return chosen;
    }

//    public Patch choosePatch() {
//        // Go to the patch specified by the next direction
//        Main.Direction direction = this.directions.remove(0);
//
//        switch (direction) {
//            case U:
//                return Main.region.getPatch((int) y - 1, (int) x);
//            case D:
//                return Main.region.getPatch((int) y + 1, (int) x);
//            case L:
//                return Main.region.getPatch((int) y, (int) x - 1);
//            case R:
//                return Main.region.getPatch((int) y, (int) x + 1);
//            case UL:
//                return Main.region.getPatch((int) y - 1, (int) x - 1);
//            case UR:
//                return Main.region.getPatch((int) y - 1, (int) x + 1);
//            case DL:
//                return Main.region.getPatch((int) y + 1, (int) x - 1);
//            case DR:
//                return Main.region.getPatch((int) y + 1, (int) x + 1);
//        }
//
//        return null;
//    }

//    public void generateDirections(int startRow, int startCol, int endRow, int endCol) {
//        List<Main.Direction> directions = new ArrayList<>();
//
//        // Generate sets
//        List<Result> open = new ArrayList<>();
//        List<Result> closed = new ArrayList<>();
//
//        // Add starting node to open set
//        Result startResult = new Result(Main.region.getPatch(startRow, startCol), null, null, 0, 0);
//        open.add(startResult);
//
//        // Get the goal patch
//        Patch goal = Main.region.getPatch(endRow, endCol);
//        Result endResult = new Result(goal, null, null, 0, 0);
//        Result foundEndResult = null;
//
//        // While there are still patches left to explore...
//        while (!open.isEmpty()) {
//            // Find the patch with the lowest f value
//            double minimumF = Double.MAX_VALUE;
//            Result currentResult = open.get(0);
//
//            for (Result result : open) {
//                if (result.getF() < minimumF) {
//                    currentResult = result;
//                }
//            }
//
//            // Remove this result from the open patch
//            open.remove(currentResult);
//
//            // Add this result to the closed list
//            closed.add(currentResult);
//
//            // Check if this patch is the goal
//            if (currentResult.equals(endResult)) {
//                foundEndResult = currentResult;
//
//                break;
//            }
//
//            // Generate the neighbors of this patch
//            List<Result> neighbors = currentResult.generateNeighbors(goalsReached);
//
//            // Explore each neighbor
//            for (Result neighbor : neighbors) {
//                // Check if this neighbor is scheduled to be opened, but has a lower cost than the one in the open list
//                if (open.contains(neighbor)) {
//                    int index = open.indexOf(neighbor);
//                    Result neighborInOpen = open.get(index);
//
//                    if (neighbor.getF() < neighborInOpen.getF()) {
//                        // If it is, remove that neighbor from the open list - the path through this neighbor is better
//                        open.remove(neighborInOpen);
//                        open.add(neighbor);
//                    }
//                } else if (closed.contains(neighbor)) {
//                    // Check if this neighbor has already been closed, but has a lower cost than the one in the closed
//                    // list
//                    int index = closed.indexOf(neighbor);
//                    Result neighborInClosed = closed.get(index);
//
//                    if (neighbor.getF() < neighborInClosed.getF()) {
//                        // If it is, remove that neighbor from the closed list
//                        closed.remove(neighborInClosed);
//                        closed.add(neighbor);
//                    }
//                } else {
//                    // If the neighbor is not in either open or closed lists, just add it to the open list
//                    open.add(neighbor);
//                }
//            }
//        }
//
//        // Retrace the directions toward this goal
//        if (foundEndResult != null) {
//            Result result = foundEndResult;
//
//            do {
//                directions.add(directions.size(), result.getDirection());
//                result = result.getParent();
//            } while (result != null);
//        }
//
//        this.directions.clear();
//        this.directions.addAll(directions);
//    }

//    public void prepareToNextGoal(int startRow, int startCol) {
//        Patch goal = Main.region.getCheckpoints().get(goalsReached);
//
//        int endRow = goal.getMatrixPosition().getRow();
//        int endCol = goal.getMatrixPosition().getCol();
//
//        generateDirections(startRow, startCol, endRow, endCol);
//    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return Double.compare(passenger.x, x) == 0 &&
                Double.compare(passenger.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int getIndexGoalChosen() {
        return indexGoalChosen;
    }
}
