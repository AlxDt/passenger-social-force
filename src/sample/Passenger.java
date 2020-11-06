package sample;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Passenger {
    // Keep track of the number of passengers generated
    private static int passengerCount = 0;
    private final int identifier;
    private final Color color;
    private final double walkingDistance;
    private final Coordinate position;
    private double heading;
    private Patch currentPatch;
    private Patch goal;
    private int goalsReached;
    private int goalsLeft;
    private boolean isWaiting;
    private Passenger leader;

    public Passenger(double x, double y, int numGoals) {
        this.position = new Coordinate(x, y);
        this.goalsReached = 0;
        this.goalsLeft = numGoals;
        this.isWaiting = false;
        this.goal = null;
        this.leader = null;

        // All newly generated passengers will face the north by default
        // The heading values shall be in degrees, but have to be converted to radians for the math libraries to process
        // East: 0 degrees
        // North: 90 degrees
        // West: 180 degrees
        // South: 270 degrees
        this.heading = Math.toRadians(90.0);

        // TODO: Walking speed should depend on the passenger's age
        // The walking speed values shall be in m/s
        this.walkingDistance = 0.6 + (new Random().nextDouble() - 0.5);

        Random randomColor = new Random();

        this.color = Color.color(randomColor.nextDouble(), randomColor.nextDouble(), randomColor.nextDouble());

        // The identifier of this passenger is its serial number (based on the number of passengers generated)
        this.identifier = passengerCount;

        // Add this passenger to the start patch
        this.currentPatch = Main.WALKWAY.getPatch((int) y, (int) x);
        currentPatch.getPassengers().add(this);

        // Increment the number of passengers made by one
        Passenger.passengerCount++;
    }

    // Compute for the average of two headings
    public static double meanHeading(double heading1, double heading2) {
        return Math.atan2(
                (Math.sin(heading1) + Math.sin(heading2)) / 2.0,
                (Math.cos(heading1) + Math.cos(heading2)) / 2.0
        );
    }

    public Color getColor() {
        return color;
    }

    private void setPosition(double x, double y) {
        // Take note of the passenger's new patch
        Patch newPatch = Main.WALKWAY.getPatch((int) y, (int) x);

        // If the current and new patches are different, it means the passenger has moved patches, and both patches
        // should take that into account
        if (this.currentPatch != newPatch) {
            // Remove this passenger from the patch that was left behind
            this.currentPatch.getPassengers().remove(this);

            // Add the passenger to its new patch
            newPatch.getPassengers().add(this);

            // This new patch will now be the current patch
            this.currentPatch = newPatch;
        }

        // Set the new position of this passenger
        this.position.setX(x);
        this.position.setY(y);
    }

    public Coordinate getPosition() {
        return this.position;
    }

    public Patch getGoal() {
        return goal;
    }

    public void setGoal(Patch goal) {
        this.goal = goal;
    }

    public Patch getCurrentPatch() {
        return currentPatch;
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

    public Passenger getLeader() {
        return leader;
    }

    public void reachGoal() {
        this.goalsReached++;
        this.goalsLeft--;
    }

    public Patch choosePatch(int rowNumber, int colNumber, boolean random) {
        Patch candidate;

        double maximumGradient = -Double.MAX_VALUE;
        Patch chosen = null;
        double attraction;

        // Get the next set of goals
        List<Patch> nextGoals = Main.WALKWAY.getGoalsAtSequence(goalsReached);

        // Take note of the index of the goal chosen
        int indexNearestGoal = nextGoals.indexOf(this.goal);

        // Normally, the patch with the highest gradient is chosen
        double x = this.position.getX();
        double y = this.position.getY();

        if (!random) {
            // Left
            if ((int) x > 0) {
                candidate = Main.WALKWAY.getPatch((int) y, (int) x - 1);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.WALKWAY.getAttraction(goalsReached, indexNearestGoal, (int) y, (int) x - 1);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.WALKWAY.getPatch((int) y, (int) x - 1);
                    }
                }
            }

            // Right
            if ((int) x < colNumber - 1) {
                candidate = Main.WALKWAY.getPatch((int) y, (int) x + 1);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.WALKWAY.getAttraction(goalsReached, indexNearestGoal, (int) y, (int) x + 1);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.WALKWAY.getPatch((int) y, (int) x + 1);
                    }
                }
            }

            // Up
            if ((int) y > 0) {
                candidate = Main.WALKWAY.getPatch((int) y - 1, (int) x);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.WALKWAY.getAttraction(goalsReached, indexNearestGoal, (int) y - 1, (int) x);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.WALKWAY.getPatch((int) y - 1, (int) x);
                    }
                }
            }

            // Down
            if ((int) y < rowNumber - 1) {
                candidate = Main.WALKWAY.getPatch((int) y + 1, (int) x);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.WALKWAY.getAttraction(goalsReached, indexNearestGoal, (int) y + 1, (int) x);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.WALKWAY.getPatch((int) y + 1, (int) x);
                    }
                }
            }

            // Upper left
            if ((int) x > 0 && (int) y > 0) {
                candidate = Main.WALKWAY.getPatch((int) y - 1, (int) x - 1);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.WALKWAY.getAttraction(goalsReached, indexNearestGoal, (int) y - 1, (int) x - 1);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.WALKWAY.getPatch((int) y - 1, (int) x - 1);
                    }
                }
            }

            // Lower left
            if ((int) x > 0 && (int) y < rowNumber - 1) {
                candidate = Main.WALKWAY.getPatch((int) y + 1, (int) x - 1);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.WALKWAY.getAttraction(goalsReached, indexNearestGoal, (int) y + 1, (int) x - 1);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.WALKWAY.getPatch((int) y + 1, (int) x - 1);
                    }
                }
            }

            // Upper right
            if ((int) x < colNumber - 1 && (int) y > 0) {
                candidate = Main.WALKWAY.getPatch((int) y - 1, (int) x + 1);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.WALKWAY.getAttraction(goalsReached, indexNearestGoal, (int) y - 1, (int) x + 1);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.WALKWAY.getPatch((int) y - 1, (int) x + 1);
                    }
                }
            }

            // Lower right
            if ((int) x < colNumber - 1 && (int) y < rowNumber - 1) {
                candidate = Main.WALKWAY.getPatch((int) y + 1, (int) x + 1);

                if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                    attraction = Main.WALKWAY.getAttraction(goalsReached, indexNearestGoal, (int) y + 1, (int) x + 1);

                    if (attraction > maximumGradient) {
                        maximumGradient = attraction;

                        chosen = Main.WALKWAY.getPatch((int) y + 1, (int) x + 1);
                    }
                }
            }
        } else {
            // If it was decided that a random choice was to be made, pick one patch randomly
            switch (new Random().nextInt(8)) {
                case 0:
                    if ((int) x > 0) {
                        candidate = Main.WALKWAY.getPatch((int) y, (int) x - 1);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.WALKWAY.getPatch((int) y, (int) x - 1);
                        }
                    }

                    break;
                case 1:
                    if ((int) x < colNumber - 1) {
                        candidate = Main.WALKWAY.getPatch((int) y, (int) x + 1);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.WALKWAY.getPatch((int) y, (int) x + 1);
                        }
                    }

                    break;
                case 2:
                    if ((int) y > 0) {
                        candidate = Main.WALKWAY.getPatch((int) y - 1, (int) x);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.WALKWAY.getPatch((int) y - 1, (int) x);
                        }
                    }

                    break;
                case 3:
                    if ((int) y < rowNumber - 1) {
                        candidate = Main.WALKWAY.getPatch((int) y + 1, (int) x);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.WALKWAY.getPatch((int) y + 1, (int) x);
                        }
                    }

                    break;
                case 4:
                    if ((int) x > 0 && (int) y > 0) {
                        candidate = Main.WALKWAY.getPatch((int) y - 1, (int) x - 1);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.WALKWAY.getPatch((int) y - 1, (int) x - 1);
                        }
                    }

                    break;
                case 5:
                    if ((int) x > 0 && (int) y < rowNumber - 1) {
                        candidate = Main.WALKWAY.getPatch((int) y + 1, (int) x - 1);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.WALKWAY.getPatch((int) y + 1, (int) x - 1);
                        }
                    }

                    break;
                case 6:
                    if ((int) x < colNumber - 1 && (int) y > 0) {
                        candidate = Main.WALKWAY.getPatch((int) y - 1, (int) x + 1);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.WALKWAY.getPatch((int) y - 1, (int) x + 1);
                        }
                    }

                    break;
                case 7:
                    if ((int) x < colNumber - 1 && (int) y < rowNumber - 1) {
                        candidate = Main.WALKWAY.getPatch((int) y + 1, (int) x + 1);

                        if (candidate.getStatus() != Patch.Status.OBSTACLE) {
                            chosen = Main.WALKWAY.getPatch((int) y + 1, (int) x + 1);
                        }
                    }

                    break;
            }
        }

        if (chosen == null) {
            System.out.println("oops");
        }

        return chosen;
    }

    // Set the nearest goal to this passenger
    public void setNearestGoal() {
        double minDistance = Double.MAX_VALUE;
        Patch nearestGoal = null;

        for (Patch goal : Main.WALKWAY.getGoalsAtSequence(goalsReached)) {
            double distance = distanceTo(
                    goal.getPatchCenterCoordinates().getX(),
                    goal.getPatchCenterCoordinates().getY()
            );

            if (distance < minDistance) {
                minDistance = distance;
                nearestGoal = goal;
            }
        }

        // Set the goal nearest to this passenger
        this.goal = nearestGoal;
    }

    public void clearLeader() {
        this.leader = null;
    }

    // Set the leader of this passenger
    // The leader, which the passenger will roughly follow, will be chosen from the nearest fellow passenger to this
    // passenger
    public boolean setLeader() {
        // Choose the patch where a leader shall be searched for - this would depend on the current heading of the
        // passenger
        // Reference (in degrees):
        // 337.5 to 22.5 - right
        // 22.5 to 67.5 - upper right
        // 67.5 to 112.5 - up
        // 112.5 to 157.5 - upper left
        // 157.5 to 202.5 - left
        // 202.5 to 247.5 - lower left
        // 247.5 to 292.5 - down
        // 292.5 to 337.5 - lower right

        List<Passenger> leaderCandidates = new ArrayList<>();

        double currentHeadingDegrees = Math.toDegrees(this.heading);

        // Also, check the current patch first to see whether there are passengers within the current patch that are
        // candidates for being leaders for this passenger because if it does, there is no need to check beyond the
        // current patch anymore, as a leader may be chosen from within
        if (!this.currentPatch.getPassengers().isEmpty()) {
            // Check if any of the passengers in the passenger list are within this passenger's field of view
            for (Passenger passenger : this.currentPatch.getPassengers()) {
                // If this passenger is within this passenger's field of view and is in the same state as this
                // passenger, add it to the list of leader candidates
                if (this.goalsReached == passenger.getGoalsReached()
                        && isWithinFieldOfView(passenger, Math.toRadians(20.0))) {
                    leaderCandidates.add(passenger);
                }
            }
        }

        // If the leader candidates list isn't empty, choose a random leader from such list
        if (!leaderCandidates.isEmpty()) {
            int randomIndex = new Random().nextInt(leaderCandidates.size());

            this.leader = leaderCandidates.get(randomIndex);

            return true;
        } else {
            // If no leader candidates were found, try to select one from the neighboring patches
            Patch chosenPatch = null;

            int truncatedX = (int) this.position.getX();
            int truncatedY = (int) this.position.getY();

            // Right
            if (currentHeadingDegrees >= 337.5 && currentHeadingDegrees <= 360.0
                    || currentHeadingDegrees >= 0 && currentHeadingDegrees < 22.5) {
                if (truncatedX + 1 < Main.WALKWAY.getCols()) {
                    chosenPatch = Main.WALKWAY.getPatch(truncatedY, truncatedX + 1);
                }
            } else if (currentHeadingDegrees >= 22.5 && currentHeadingDegrees < 67.5) {
                // Upper right
                if (truncatedX + 1 < Main.WALKWAY.getCols() && truncatedY > 0) {
                    chosenPatch = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX + 1);
                }
            } else if (currentHeadingDegrees >= 67.5 && currentHeadingDegrees < 112.5) {
                // Up
                if (truncatedY > 0) {
                    chosenPatch = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX);
                }
            } else if (currentHeadingDegrees >= 112.5 && currentHeadingDegrees < 157.5) {
                // Upper left
                if (truncatedX > 0 && truncatedY > 0) {
                    chosenPatch = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX - 1);
                }
            } else if (currentHeadingDegrees >= 157.5 && currentHeadingDegrees < 202.5) {
                // Left
                if (truncatedX > 0) {
                    chosenPatch = Main.WALKWAY.getPatch(truncatedY, truncatedX - 1);
                }
            } else if (currentHeadingDegrees >= 202.5 && currentHeadingDegrees < 247.5) {
                // Lower left
                if (truncatedX > 0 && truncatedY + 1 < Main.WALKWAY.getRows()) {
                    chosenPatch = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX - 1);
                }
            } else if (currentHeadingDegrees >= 247.5 && currentHeadingDegrees < 292.5) {
                // Down
                if (truncatedY + 1 < Main.WALKWAY.getRows()) {
                    chosenPatch = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX);
                }
            } else {
                // Lower right
                if (truncatedX + 1 < Main.WALKWAY.getCols() && truncatedY + 1 < Main.WALKWAY.getRows()) {
                    chosenPatch = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX + 1);
                }
            }

            // Check the chosen patch to see whether there are passengers that are candidates for being leaders for this
            // passenger
            if (chosenPatch != null && !chosenPatch.getPassengers().isEmpty()) {
                // Check if any of the passengers in the passenger list are within this passenger's field of view
                for (Passenger passenger : this.currentPatch.getPassengers()) {
                    // If this passenger is within this passenger's field of view, add it to the list of leader candidates
                    if (this.goalsReached == passenger.getGoalsReached()
                            && isWithinFieldOfView(passenger, Math.toRadians(20.0))) {
                        leaderCandidates.add(passenger);
                    }
                }
            }

            // If the leader candidates list isn't empty, choose a random leader from such list
            // If it is, return with no leader selected
            if (!leaderCandidates.isEmpty()) {
                int randomIndex = new Random().nextInt(leaderCandidates.size());

                this.leader = leaderCandidates.get(randomIndex);

                return true;
            } else {
                return false;
            }

//            return false;
        }
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getWalkingDistance() {
        return walkingDistance;
    }

    // Retrieve the heading of this passenger (in radians) when it faces towards a given position
    public double headingTowards(double x, double y) {
        // Get the differences in the x and y values of the two positions
        double dx = x - this.position.getX();
        double dy = y - this.position.getY();

        // The length of the adjacent side is the difference between the x values of the target position and the
        // passenger
        double adjacentLength = dx;

        // The length of the hypotenuse is the distance between this passenger and the given position
        double hypotenuseLength = distanceTo(x, y);

        // The included angle between the adjacent and the hypotenuse is given by the arccosine of the ratio of the
        // length of the adjacent and the length of the hypotenuse
        double angle = Math.acos(adjacentLength / hypotenuseLength);

        // If the difference between the y values of the target position and this passenger is positive (meaning the
        // target patch is below the passenger), get the supplement of the angle
        if (dy > 0) {
            angle = 2.0 * Math.PI - angle;
        }

        return angle;
    }

    // Make the passenger move given the currently set heading and walking speed
    public void move() {
        // Check if the distance between this passenger and its goal
        double distanceToGoal = this.distanceTo(
                this.goal.getPatchCenterCoordinates().getX(),
                this.goal.getPatchCenterCoordinates().getY()
        );

        // If the distance between this passenger and the goal is less than the distance this passenger covers every
        // time it walks, "snap" the position of the passenger to the center of the goal immediately, to avoid
        // overshooting its target
        // If not, compute the next coordinates normally
        if (distanceToGoal < this.walkingDistance) {
            this.setPosition(
                    this.goal.getPatchCenterCoordinates().getX(),
                    this.goal.getPatchCenterCoordinates().getY()
            );
        } else {
            // Given the current position, the current heading, and the walking speed, the coordinates for the new
            // position of the passenger are
            // (x_current + cos(heading) * walking speed, y_current - sin(heading) * walking speed)
            double newX = this.position.getX() + Math.cos(this.heading) * walkingDistance;
            double newY = this.position.getY() - Math.sin(this.heading) * walkingDistance;

            // Check if the new coordinates are out of bounds
            // If they are, adjust them such that they stay within bounds
            if (newX < 0) {
                newX = 0.0;
            } else if (newX > Main.WALKWAY.getCols() - 1) {
                newX = Main.WALKWAY.getCols() - 0.5;
            }

            if (newY < 0) {
                newY = 0.0;
            } else if (newY > Main.WALKWAY.getRows() - 1) {
                newY = Main.WALKWAY.getCols() - 0.5;
            }

            // Then set the position of this passenger to the new coordinates
            this.setPosition(newX, newY);
        }
    }

    // Compute the distance between the coordinates of this passenger and some other object with coordinates
    private double distanceTo(double x, double y) {
        return Math.sqrt(
                Math.pow(x - this.position.getX(), 2)
                        + Math.pow(y - this.position.getY(), 2)
        );
    }

    // See if the given passenger is within the passenger's field of view
    public boolean isWithinFieldOfView(Passenger passenger, double maximumHeadingChange) {
        // A passenger is within a field of view if the heading change required to face that passenger is within the
        // given maximum heading change
        double headingTowardsPassenger = headingTowards(passenger.getPosition().getX(), passenger.getPosition().getY());

        // Compute the difference between the two headings
        double headingDifference = Math.abs(headingTowardsPassenger - this.heading) % Math.toRadians(360.0);

        if (headingDifference > Math.toRadians(180)) {
            headingDifference = Math.toRadians(360) - headingDifference;
        }

        // If the heading difference is within the specified parameter, return true
        // If not, the passenger is outside this passenger's field of view, so return false
        return headingDifference <= maximumHeadingChange;
    }

    // See if this passenger should move
    // That is, check if a movement considering its current heading would not violate distancing
    public boolean shouldMove(double distance) {
        // First, check this patch if there are fellow passengers within it
        if (!this.currentPatch.getPassengers().isEmpty()) {
            // Check if any of the passengers in the passenger list are within this passenger's field of view
            for (Passenger passenger : this.currentPatch.getPassengers()) {
                // If this passenger is within this passenger's field of view, check if the distance to this passenger
                // is less than the allowable distance
                // If it is, don't move - too close for comfort
                if (isWithinFieldOfView(passenger, Math.toRadians(45.0))
                        && passenger.distanceTo(this.getPosition().getX(), this.position.getY()) < distance) {
                    return false;
                }
            }
        }

        // If there are no passengers in the current patch, or if the passengers in the current patch are far enough,
        // start checking in the neighboring patches
        double currentHeadingDegrees = Math.toDegrees(this.heading);

        Patch chosenPatch = null;

        int truncatedX = (int) this.position.getX();
        int truncatedY = (int) this.position.getY();

        // Right
        if (currentHeadingDegrees >= 337.5 && currentHeadingDegrees <= 360.0
                || currentHeadingDegrees >= 0 && currentHeadingDegrees < 22.5) {
            if (truncatedX + 1 < Main.WALKWAY.getCols()) {
                chosenPatch = Main.WALKWAY.getPatch(truncatedY, truncatedX + 1);
            }
        } else if (currentHeadingDegrees >= 22.5 && currentHeadingDegrees < 67.5) {
            // Upper right
            if (truncatedX + 1 < Main.WALKWAY.getCols() && truncatedY > 0) {
                chosenPatch = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX + 1);
            }
        } else if (currentHeadingDegrees >= 67.5 && currentHeadingDegrees < 112.5) {
            // Up
            if (truncatedY > 0) {
                chosenPatch = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX);
            }
        } else if (currentHeadingDegrees >= 112.5 && currentHeadingDegrees < 157.5) {
            // Upper left
            if (truncatedX > 0 && truncatedY > 0) {
                chosenPatch = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX - 1);
            }
        } else if (currentHeadingDegrees >= 157.5 && currentHeadingDegrees < 202.5) {
            // Left
            if (truncatedX > 0) {
                chosenPatch = Main.WALKWAY.getPatch(truncatedY, truncatedX - 1);
            }
        } else if (currentHeadingDegrees >= 202.5 && currentHeadingDegrees < 247.5) {
            // Lower left
            if (truncatedX > 0 && truncatedY + 1 < Main.WALKWAY.getRows()) {
                chosenPatch = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX - 1);
            }
        } else if (currentHeadingDegrees >= 247.5 && currentHeadingDegrees < 292.5) {
            // Down
            if (truncatedY + 1 < Main.WALKWAY.getRows()) {
                chosenPatch = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX);
            }
        } else {
            // Lower right
            if (truncatedX + 1 < Main.WALKWAY.getCols() && truncatedY + 1 < Main.WALKWAY.getRows()) {
                chosenPatch = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX + 1);
            }
        }

        // Check the chosen patch to see whether there are passengers that are candidates for being leaders for this
        // passenger
        if (chosenPatch != null && !chosenPatch.getPassengers().isEmpty()) {
            // Check if any of the passengers in the passenger list are within this passenger's field of view
            for (Passenger passenger : this.currentPatch.getPassengers()) {
                // If this passenger is within this passenger's field of view, check if the distance to this passenger
                // is less than the allowable distance
                // If it is, don't move - too close for comfort
                if (isWithinFieldOfView(passenger, Math.toRadians(45.0))
                        && passenger.distanceTo(this.getPosition().getX(), this.position.getY()) < distance) {
                    return false;
                }
            }
        }

        // If it reaches this point, the passenger may move safely
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return identifier == passenger.identifier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
