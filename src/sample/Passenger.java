package sample;

import javafx.scene.paint.Color;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Passenger {
    private final Color color;
    private double x;
    private double y;
    private int goalsReached;
    private int indexGoalChosen;
    private int goalsLeft;
    private boolean isWaiting;

    public Passenger(double x, double y, int numGoals) {
        this.x = x;
        this.y = y;
        this.goalsReached = 0;
        this.goalsLeft = numGoals;
        this.isWaiting = false;
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

    public boolean isWaiting() {
        return isWaiting;
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
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
