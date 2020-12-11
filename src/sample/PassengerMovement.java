package sample;

import java.util.ArrayList;
import java.util.List;

public class PassengerMovement {
    private final Passenger parent;
    private final double walkingDistance;
    private final Coordinates position;
    private double heading;
    private Patch currentPatch;
    private Patch goal;
    private int goalsReached;
    private int goalsLeft;
    private boolean isWaiting;
    private Passenger followed;
    private Passenger leader;
    private State state;

    public PassengerMovement(Passenger parent, double x, double y, int numGoals) {
        this.parent = parent;
        this.position = new Coordinates(x, y);
        this.goalsReached = 0;
        this.goalsLeft = numGoals;
        this.isWaiting = false;
        this.goal = null;
        this.followed = null;
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
        this.walkingDistance = 0.6 /*+ (new Random().nextDouble() - 0.5)*/;

        // Add this passenger to the start patch
        this.currentPatch = Main.WALKWAY.getPatch((int) y, (int) x);
        currentPatch.getPassengers().add(parent);

        this.state = State.WILL_QUEUE;
    }

    // Compute for the angular mean of two headings
    public static double meanHeading(double heading1, double heading2) {
        return Math.atan2(
                (Math.sin(heading1) + Math.sin(heading2)) / 2.0,
                (Math.cos(heading1) + Math.cos(heading2)) / 2.0
        );
    }

    public Coordinates getPosition() {
        return this.position;
    }

    private void setPosition(Coordinates coordinates) {
        double x = coordinates.getX();
        double y = coordinates.getY();

        // Take note of the passenger's new patch
        Patch newPatch = Main.WALKWAY.getPatch((int) y, (int) x);

        // If the current and new patches are different, it means the passenger has moved patches, and both patches
        // should take that into account
        if (this.currentPatch != newPatch) {
            // Remove this passenger from the patch that was left behind
            this.currentPatch.getPassengers().remove(this.parent);

            // Add the passenger to its new patch
            newPatch.getPassengers().add(this.parent);

            // This new patch will now be the current patch
            this.currentPatch = newPatch;
        }

        // Set the new position of this passenger
        this.position.setX(x);
        this.position.setY(y);
    }

    public Passenger getFollowed() {
        return followed;
    }

    public void setFollowed(Passenger followed) {
        this.followed = followed;
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

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getWalkingDistance() {
        return walkingDistance;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void reachGoal() {
        this.goalsReached++;
        this.goalsLeft--;
    }

    // Set the nearest goal to this passenger
    public void setNearestGoal() {
        double minDistance = Double.MAX_VALUE;
        Patch nearestGoal = null;

        for (Patch goal : Main.WALKWAY.getGoalsAtSequence(goalsReached)) {
            double distance = distanceTo(goal.getPatchCenterCoordinates());

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
    /*public boolean setLeader() {
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
                // Check if there are at most four passengers in this patch
                // If there is more than that, do not allow this passenger to

                // If this passenger is within this passenger's field of view and is in the same state as this
                // passenger, add it to the list of leader candidates
                if (this.goalsReached == passenger.getPassengerMovement().getGoalsReached()
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
                if (truncatedX + 1 < Main.WALKWAY.getColumns()) {
                    chosenPatch = Main.WALKWAY.getPatch(truncatedY, truncatedX + 1);
                }
            } else if (currentHeadingDegrees >= 22.5 && currentHeadingDegrees < 67.5) {
                // Upper right
                if (truncatedX + 1 < Main.WALKWAY.getColumns() && truncatedY > 0) {
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
                if (truncatedX + 1 < Main.WALKWAY.getColumns() && truncatedY + 1 < Main.WALKWAY.getRows()) {
                    chosenPatch = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX + 1);
                }
            }

            // Check the chosen patch to see whether there are passengers that are candidates for being leaders for this
            // passenger
            if (chosenPatch != null && !chosenPatch.getPassengers().isEmpty()) {
                // Check if any of the passengers in the passenger list are within this passenger's field of view
                for (Passenger passenger : this.currentPatch.getPassengers()) {
                    // If this passenger is within this passenger's field of view, add it to the list of leader candidates
                    if (this.goalsReached == passenger.getPassengerMovement().getGoalsReached()
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
    }*/

    // Retrieve the heading of this passenger (in radians) when it faces towards a given position
    public double headingTowards(Coordinates coordinates) {
        double x = coordinates.getX();
        double y = coordinates.getY();

        // Get the differences in the x and y values of the two positions
        double dx = x - this.position.getX();
        double dy = y - this.position.getY();

        // The length of the adjacent side is the difference between the x values of the target position and the
        // passenger
        double adjacentLength = dx;

        // The length of the hypotenuse is the distance between this passenger and the given position
        double hypotenuseLength = distanceTo(coordinates);

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

    // Get the future position of this passenger given the current goal and the current heading
    private Coordinates getFuturePosition() {
        return getFuturePosition(this.goal, this.heading);
    }

    // Get the future position of this passenger given a goal and a heading
    public Coordinates getFuturePosition(Patch goal, double heading) {
        // Check if the distance between this passenger and its goal
        double distanceToGoal = this.distanceTo(goal.getPatchCenterCoordinates());

        // If the distance between this passenger and the goal is less than the distance this passenger covers every
        // time it walks, "snap" the position of the passenger to the center of the goal immediately, to avoid
        // overshooting its target
        // If not, compute the next coordinates normally
        if (distanceToGoal < this.walkingDistance) {
            return new Coordinates(goal.getPatchCenterCoordinates().getX(), goal.getPatchCenterCoordinates().getY());
        } else {
            // Given the current position, the current heading, and the walking speed, the coordinates for the new
            // position of the passenger are
            // (x_current + cos(heading) * walking speed, y_current - sin(heading) * walking speed)
            double newX = this.position.getX() + Math.cos(heading) * this.walkingDistance;
            double newY = this.position.getY() - Math.sin(heading) * this.walkingDistance;

            // Check if the new coordinates are out of bounds
            // If they are, adjust them such that they stay within bounds
            if (newX < 0) {
                newX = 0.0;
            } else if (newX > Main.WALKWAY.getColumns() - 1) {
                newX = Main.WALKWAY.getColumns() - 0.99;
            }

            if (newY < 0) {
                newY = 0.0;
            } else if (newY > Main.WALKWAY.getRows() - 1) {
                newY = Main.WALKWAY.getRows() - 0.99;
            }

            // Then set the position of this passenger to the new coordinates
            return new Coordinates(newX, newY);
        }
    }

    // Make the passenger move given the currently set heading and walking speed
    public void move() {
        this.setPosition(this.getFuturePosition());
    }

    // Compute the distance between the coordinates of this passenger and some other object with coordinates
    private double distanceTo(Coordinates coordinates) {
        double x = coordinates.getX();
        double y = coordinates.getY();

        return Math.sqrt(
                Math.pow(x - this.position.getX(), 2)
                        + Math.pow(y - this.position.getY(), 2)
        );
    }

    // See if the given passenger is within the passenger's field of view
    // TODO: Switch to coordinates
    public boolean isWithinFieldOfView(Passenger passenger, double maximumHeadingChange) {
        // A passenger is within a field of view if the heading change required to face that passenger is within the
        // given maximum heading change
        double headingTowardsPassenger = headingTowards(passenger.getPassengerMovement().getPosition());

        // Compute the difference between the two headings
        double headingDifference = Math.abs(headingTowardsPassenger - this.heading) % Math.toRadians(360.0);

        if (headingDifference > Math.toRadians(180)) {
            headingDifference = Math.toRadians(360) - headingDifference;
        }

        // If the heading difference is within the specified parameter, return true
        // If not, the passenger is outside this passenger's field of view, so return false
        return headingDifference <= maximumHeadingChange;
    }

    public boolean isWithinFieldOfViewObstacle(Patch patch, double maximumHeadingChange) {
        // A passenger is within a field of view if the heading change required to face that passenger is within the
        // given maximum heading change
        double headingTowardsPassenger = headingTowards(patch.getPatchCenterCoordinates());

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
    public boolean shouldMove(double minimumDistance, double maximumHeading) {
/*//        // Get this passenger's current patch
//        Patch currentPatch = Main.WALKWAY.getPatch(this.getCurrentPatch().getPatchCenterCoordinates());
//
//        // Get this passenger's future patch
//        Patch futurePatch = Main.WALKWAY.getPatch(this.getFuturePosition());
//
//        // If there are other passengers on  this patch
//        if (currentPatch.getPassengers().size() > 1) {
//            return false;
//        } else {
//            // If the future patch has passengers on them, do not move
//            // If there are no passengers there, the passenger may safely move
//            return futurePatch.getPassengers().isEmpty();
//        }

        // First, check this patch if there are fellow passengers within it
        if (this.currentPatch.getPassengers().size() >= 1) {
            // Check if any of the passengers in the passenger list are within this passenger's field of view
            for (Passenger passenger : this.currentPatch.getPassengers()) {
                // If this passenger is within this passenger's field of view, check if the distance to this passenger
                // is less than the allowable distance
                // If it is, don't move - too close for comfort
                if (isWithinFieldOfView(passenger, Math.toRadians(45.0))
                        && passenger.getPassengerMovement().distanceTo(this.getPosition()) < distance) {
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
            if (truncatedX + 1 < Main.WALKWAY.getColumns()) {
                chosenPatch = Main.WALKWAY.getPatch(truncatedY, truncatedX + 1);
            }
        } else if (currentHeadingDegrees >= 22.5 && currentHeadingDegrees < 67.5) {
            // Upper right
            if (truncatedX + 1 < Main.WALKWAY.getColumns() && truncatedY > 0) {
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
            if (truncatedX + 1 < Main.WALKWAY.getColumns() && truncatedY + 1 < Main.WALKWAY.getRows()) {
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
                        && passenger.getPassengerMovement().distanceTo(this.getPosition()) < distance) {
                    return false;
                }
            }
        }

        // Finally, check if the future patch has passengers on them
        Patch futurePatch = Main.WALKWAY.getPatch(this.getFuturePosition());

        // If there are passengers in the future patch, and the future patch is different from the current patch,
        // don't move
//        return futurePatch.getPassengers().isEmpty();
//        System.out.println(futurePatch.getPassengers().isEmpty());
        if (futurePatch != this.currentPatch && !futurePatch.getPassengers().isEmpty()) {
            for (Passenger passenger : futurePatch.getPassengers()) {
                if (isWithinFieldOfView(passenger, Math.toRadians(90.0))
                        && passenger.getPassengerMovement().distanceTo(this.getPosition()) < distance) {
                    return false;
                }
            }
        }

        return true;*/
        double currentHeadingDegrees = Math.toDegrees(this.heading);

        // Compile a list of patches which would be explored by this passenger
        List<Patch> patchesToExplore = new ArrayList<>();
        Patch chosenPatch;

        int truncatedX = (int) this.position.getX();
        int truncatedY = (int) this.position.getY();

        // Right
        if (truncatedX + 1 < Main.WALKWAY.getColumns()) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY, truncatedX + 1);
            patchesToExplore.add(chosenPatch);
        }

        // Upper right
        if (truncatedX + 1 < Main.WALKWAY.getColumns() && truncatedY > 0) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX + 1);
            patchesToExplore.add(chosenPatch);
        }

        // Up
        if (truncatedY > 0) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX);
            patchesToExplore.add(chosenPatch);
        }

        // Upper left
        if (truncatedX > 0 && truncatedY > 0) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX - 1);
            patchesToExplore.add(chosenPatch);
        }

        // Left
        if (truncatedX > 0) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY, truncatedX - 1);
            patchesToExplore.add(chosenPatch);
        }

        // Lower left
        if (truncatedX > 0 && truncatedY + 1 < Main.WALKWAY.getRows()) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX - 1);
            patchesToExplore.add(chosenPatch);
        }

        // Down
        if (truncatedY + 1 < Main.WALKWAY.getRows()) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX);
            patchesToExplore.add(chosenPatch);
        }

        // Lower right
        if (truncatedX + 1 < Main.WALKWAY.getColumns() && truncatedY + 1 < Main.WALKWAY.getRows()) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX + 1);
            patchesToExplore.add(chosenPatch);
        }

        // For each of these compiled patches, see if there is another passenger within this passenger's field of view
        for (Patch patch : patchesToExplore) {
            for (Passenger passenger : patch.getPassengers()) {
                // Check if this passenger is within the field of view and within the minimum distance
                if (isWithinFieldOfView(passenger, maximumHeading)
                        && distanceTo(passenger.getPassengerMovement().getPosition()) < minimumDistance) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean shouldMoveObstacle(double minimumDistance, double maximumHeading) {
        double currentHeadingDegrees = Math.toDegrees(this.heading);

        // Compile a list of patches which would be explored by this passenger
        List<Patch> patchesToExplore = new ArrayList<>();
        Patch chosenPatch;

        int truncatedX = (int) this.position.getX();
        int truncatedY = (int) this.position.getY();

        // Right
        if (truncatedX + 1 < Main.WALKWAY.getColumns()) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY, truncatedX + 1);
            patchesToExplore.add(chosenPatch);
        }

        // Upper right
        if (truncatedX + 1 < Main.WALKWAY.getColumns() && truncatedY > 0) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX + 1);
            patchesToExplore.add(chosenPatch);
        }

        // Up
        if (truncatedY > 0) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX);
            patchesToExplore.add(chosenPatch);
        }

        // Upper left
        if (truncatedX > 0 && truncatedY > 0) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY - 1, truncatedX - 1);
            patchesToExplore.add(chosenPatch);
        }

        // Left
        if (truncatedX > 0) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY, truncatedX - 1);
            patchesToExplore.add(chosenPatch);
        }

        // Lower left
        if (truncatedX > 0 && truncatedY + 1 < Main.WALKWAY.getRows()) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX - 1);
            patchesToExplore.add(chosenPatch);
        }

        // Down
        if (truncatedY + 1 < Main.WALKWAY.getRows()) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX);
            patchesToExplore.add(chosenPatch);
        }

        // Lower right
        if (truncatedX + 1 < Main.WALKWAY.getColumns() && truncatedY + 1 < Main.WALKWAY.getRows()) {
            chosenPatch = Main.WALKWAY.getPatch(truncatedY + 1, truncatedX + 1);
            patchesToExplore.add(chosenPatch);
        }

        // For each of these compiled patches, see if there is another passenger within this passenger's field of view
        for (Patch patch : patchesToExplore) {
            if (patch.getType() == Patch.Type.OBSTACLE && isWithinFieldOfViewObstacle(patch, maximumHeading)
                    && distanceTo(patch.getPatchCenterCoordinates()) < minimumDistance) {
                return false;
            }
        }

        return true;
    }

    // From a set of patches associated with a goal, get the nearest patch with a floor field value greater than a
    // certain threshold
    public Patch nearestPatchAboveThreshold(double threshold) {
        // Get the patches associated with the current goal
        List<Patch> associatedPatches = this.goal.getAssociatedPatches();

        double minimumDistance = Double.MAX_VALUE;
        Patch nearestPatch = null;

        // Look for the nearest patch with a floor field value greater than the threshold
        for (Patch patch : associatedPatches) {
            if (patch.getFloorFieldValues().get(State.QUEUEING).getValue() > threshold) {
                // Get the distance of that patch from this passenger
                double distanceFromPassenger = distanceTo(patch.getPatchCenterCoordinates());

                if (distanceFromPassenger < minimumDistance) {
                    minimumDistance = distanceFromPassenger;
                    nearestPatch = patch;
                }
            }
        }

        return nearestPatch;
    }

    public enum State {
        WILL_QUEUE,
        QUEUEING,
        TRANSACTING;
//        IN_TRAIN;
    }
}
