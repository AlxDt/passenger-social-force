package sample;

import java.util.*;

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
    private State state;
    private Action action;
    private boolean stateChanged;

    public PassengerMovement(Passenger parent, double x, double y, int numGoals) {
        this.parent = parent;
        this.position = new Coordinates(x, y);
        this.goalsReached = 0;
        this.goalsLeft = numGoals;
        this.isWaiting = false;
        this.goal = null;

        // All newly generated passengers will face the north by default
        // The heading values shall be in degrees, but have to be converted to radians for the math libraries to process
        // East: 0 degrees
        // North: 90 degrees
        // West: 180 degrees
        // South: 270 degrees
        this.heading = Math.toRadians(90.0);

        // TODO: Walking speed should depend on the passenger's age
        // The walking speed values shall be in m/s
        this.walkingDistance = 0.6;

        // Add this passenger to the start patch
        this.currentPatch = Main.WALKWAY.getPatch((int) y, (int) x);
        currentPatch.getPassengers().add(parent);

        // Assign the initial state and action of this passenger
        this.state = State.WALKING;
        this.action = Action.WILL_QUEUE;

        this.stateChanged = true;
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

    public Patch getGoal() {
        return goal;
    }

    public Patch getCurrentPatch() {
        return currentPatch;
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

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public boolean isStateChanged() {
        return stateChanged;
    }

    public void setStateChanged(boolean stateChanged) {
        this.stateChanged = stateChanged;
    }

    public void reachGoal() {
        this.goalsReached++;
        this.goalsLeft--;
    }

    // Set the nearest goal to this passenger
    // That goal should also have the fewer passengers queueing for it
    // To determine this, for each two passengers in the queue (or fraction thereof), a penalty of one meter is added to
    // the distance to this goal
    public void setChosenGoal() {
        double minScore = Double.MAX_VALUE;
        Patch chosenGoal = null;

        double distance;
        int passengersQueueing;
        double score;

        for (Patch goal : Main.WALKWAY.getGoalsAtSequence(goalsReached)) {
            distance = Coordinates.distance(this.position, goal.getPatchCenterCoordinates());
            passengersQueueing = goal.getPassengersQueueing().size();

            score = distance + passengersQueueing * 1.5;

            if (score < minScore) {
                minScore = score;
                chosenGoal = goal;
            }
        }

        // Set the goal nearest to this passenger
        this.goal = chosenGoal;
    }

    // Get the future position of this passenger given the current goal, current heading, and the current walking
    // distance
    public Coordinates getFuturePosition() {
        return getFuturePosition(this.goal, this.heading, this.walkingDistance);
    }

    // Get the future position of this passenger given the current goal, current heading, and a given walking distance
    public Coordinates getFuturePosition(double walkingDistance) {
        return getFuturePosition(this.goal, this.heading, walkingDistance);
    }

    // Get the future position of this passenger given a goal and a heading
    public Coordinates getFuturePosition(Patch goal, double heading, double walkingDistance) {
        // Check if the distance between this passenger and its goal
        double distanceToGoal = Coordinates.distance(this.position, goal.getPatchCenterCoordinates());

        // If the distance between this passenger and the goal is less than the distance this passenger covers every
        // time it walks, "snap" the position of the passenger to the center of the goal immediately, to avoid
        // overshooting its target
        // If not, compute the next coordinates normally
        if (distanceToGoal < walkingDistance) {
            return new Coordinates(goal.getPatchCenterCoordinates().getX(), goal.getPatchCenterCoordinates().getY());
        } else {
            // Given the current position, the current heading, and the walking speed, the coordinates for the new
            // position of the passenger are
            // (x_current + cos(heading) * walking speed, y_current - sin(heading) * walking_distance)
            double newX = this.position.getX() + Math.cos(heading) * walkingDistance;
            double newY = this.position.getY() - Math.sin(heading) * walkingDistance;

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

    // Make the passenger move given the currently set heading and walking distance
    public void move() {
        this.setPosition(this.getFuturePosition());
    }

    // Make the passenger move given the currently set heading and the modified walking distance
    public void move(double walkingDistance) {
        this.setPosition(this.getFuturePosition(walkingDistance));
    }

    // See if this passenger should move
    // That is, check if a movement considering its current heading would not violate distancing
    public Passenger shouldMove(double minimumDistance, double maximumHeadingChange) {
        // Compile a list of patches which would be explored by this passenger
        List<Patch> patchesToExplore = Walkway.get5x5Field(
                this.getCurrentPatch(),
                this.getHeading(),
                true
        );

        // For each of these compiled patches, see if there is another passenger within this passenger's field of view
        TreeMap<Double, Passenger> passengersWithinFieldOfView = new TreeMap<>();

        for (Patch patch : patchesToExplore) {
            for (Passenger passenger : patch.getPassengers()) {
                // Check if this passenger is within the field of view and within the minimum distance
                double distanceToPassenger = Coordinates.distance(this.position,
                        passenger.getPassengerMovement().getPosition());

                if (Coordinates.isWithinFieldOfView(
                        this.getPosition(),
                        passenger.getPassengerMovement().getPosition(),
                        this.getHeading(),
                        maximumHeadingChange)
                        && distanceToPassenger < minimumDistance) {
                    passengersWithinFieldOfView.put(distanceToPassenger, passenger);
                }
            }
        }

        // For each passenger found to violate the space, return the nearest one to this passenger
        Map.Entry<Double, Passenger> firstEntry = passengersWithinFieldOfView.firstEntry();

        if (firstEntry == null) {
            return null;
        } else {
            return firstEntry.getValue();
        }
    }

    // Attempt to move this passenger
    // Return the nearest passenger within the field of view, given the desired heading, if any
    public Passenger attemptMovement(double heading) {
        final double fieldOfViewAngleDegrees = 45.0;

        // The minimum allowable distance from another passenger at its front before this passenger stops
        final double minimumStopDistance = 0.5;

        // The maximum allowable distance from another passenger at its front before this passenger stops
        double maximumStopDistance = 1.0;

        // The distance to another passenger before this passenger slows down
        final double slowdownDistance = 2.5;

        // Get the relevant patches
        List<Patch> patchesToExplore = Walkway.get5x5Field(this.currentPatch, heading, true);

        // Get the passengers within the current field of view (45 degrees, 2.5 m) in these patches
        // If there are any other passengers within this field of view, this passenger is at least guaranteed to slow
        // down
        TreeMap<Double, Passenger> passengersWithinFieldOfView = new TreeMap<>();

        // Count the number of passengers in the the relevant patches
        int numberOfPassengers = 0;

        for (Patch patch : patchesToExplore) {
            numberOfPassengers += patch.getPassengers().size();

            for (Passenger passenger : patch.getPassengers()) {
                // Check if this passenger is within the field of view and within the slowdown distance
                double distanceToPassenger = Coordinates.distance(
                        this.position,
                        passenger.getPassengerMovement().getPosition()
                );

                if (Coordinates.isWithinFieldOfView(
                        this.position,
                        passenger.getPassengerMovement().getPosition(),
                        heading,
                        Math.toRadians(fieldOfViewAngleDegrees))
                        && distanceToPassenger < slowdownDistance) {
                    passengersWithinFieldOfView.put(distanceToPassenger, passenger);
                }
            }
        }

        // Compute the perceived density of the passengers
        // Assuming the maximum density a passenger sees within its environment is 20 before it thinks the crowd is very
        // dense, rate the perceived density of the surroundings by dividing the number of people by the maximum
        // tolerated number of passengers
        final double maximumDensityTolerated = 15.0;
        final double passengerDensity
                = (numberOfPassengers > maximumDensityTolerated ? maximumDensityTolerated : numberOfPassengers)
                / maximumDensityTolerated;

        // For each passenger found within the slowdown distance, get the nearest one, if there is any
        Map.Entry<Double, Passenger> firstEntry = passengersWithinFieldOfView.firstEntry();

        // If there are no passengers within the field of view, good - move normally
        if (firstEntry == null) {
            PassengerMovement.face(this.parent, null, heading);

            this.move();

            return null;
        } else {
            // If there are passengers within the field of view, get its heading to see whether that passenger is
            // heading towards or away from this passenger
            Passenger nearestPassenger = firstEntry.getValue();

            double headingDifference = Coordinates.headingDifference(
                    heading,
                    nearestPassenger.getPassengerMovement().getHeading()
            );

            // If the heading difference is less than 90 degrees, that means that that passenger is moving away
            // from this passenger
            if (headingDifference < Math.toRadians(90.0)) {
                // If the two passengers are more or less going at the same direction, take the heading to that
                // passenger into account in the final heading
                PassengerMovement.face(this.parent, nearestPassenger, heading);

                // Check the distance of that nearest passenger to this passenger
                double distanceToNearestPassenger = firstEntry.getKey();

                // Modify the maximum stopping distance depending on the density of the environment
                // That is, the denser the surroundings, the less space this passenger will allow between other
                // passengers
                maximumStopDistance -= (maximumStopDistance - minimumStopDistance) * passengerDensity;

                // If the distance to the other passenger is 0.5 or less, immediately stop
                if (distanceToNearestPassenger <= minimumStopDistance) {
                    return nearestPassenger;
                } else if (distanceToNearestPassenger <= maximumStopDistance) {
                    // If the distance to the other passenger is (max) 1.0 m or less, stop
                    return nearestPassenger;
                } else {
                    // Else, just slow down and move towards the direction of that passenger in front
                    // The slowdown factor linearly depends on the distance between this passenger and the other
                    final double slowdownFactor
                            = (distanceToNearestPassenger - maximumStopDistance)
                            / (slowdownDistance - maximumStopDistance);

                    this.move(this.walkingDistance * slowdownFactor);

                    return nearestPassenger;
                }
            } else {
                // If the heading difference is more than 90 degrees, that means that that passenger is moving
                // towards this passenger
                // When this happens, collect the headings of the two nearest passengers in the field of view
                // (and to the limits of the field of view itself), then pick the mean heading of the two headings which
                // form the widest angle
                // A total of three angles will be compared:
                //   - The angle formed between the left edge of the FOV cone and the heading to the left passenger
                //   - The angle between the headings of the left and right passengers
                //   - The angle formed between the heading to the right passenger and the right edge of the FOV cone
                // Basically, have this passenger squeeze itself to the widest gap it can see
                final double leftFieldOfViewEdgeHeading
                        = (this.heading + Math.toRadians(45.0)) % Math.toRadians(360.0);

                final double rightFieldOfViewEdgeHeading
                        = (this.heading - Math.toRadians(45.0)) % Math.toRadians(360.0);

                Passenger leftPassenger;
                Passenger rightPassenger;

                double headingToLeftPassenger;
                Double headingToRightPassenger;

                // Grab the two nearest passengers
                List<Passenger> passengerWithinFieldOfViewList = new ArrayList<>(passengersWithinFieldOfView.values());

                Passenger firstPassenger = passengerWithinFieldOfViewList.get(0);
                Passenger secondPassenger = null;

                // Only get the second nearest passenger when there are more than one passengers in the field of view
                if (passengerWithinFieldOfViewList.size() > 1) {
                    secondPassenger = passengerWithinFieldOfViewList.get(1);
                }

                double headingToFirstPassenger = Coordinates.headingTowards(
                        this.getPosition(),
                        firstPassenger.getPassengerMovement().getPosition()
                );

                // Only get the heading to the second nearest passenger when the second passenger is available in the
                // first place, of course
                Double headingToSecondPassenger = null;

                if (secondPassenger != null) {
                    headingToSecondPassenger = Coordinates.headingTowards(
                            this.getPosition(),
                            secondPassenger.getPassengerMovement().getPosition()
                    );
                }

                // Determine of the two passengers belong to the left and right
                // Whichever is nearer to the left FOV edge is the left passenger, and the other is the right one
                double firstPassengerLeftHeadingDifference = Coordinates.headingDifference(
                        leftFieldOfViewEdgeHeading,
                        headingToFirstPassenger
                );

                // Only get the angle between the second passenger and the left heading when the heading to the second
                // passenger is available
                Double secondPassengerLeftHeadingDifference = null;

                if (headingToSecondPassenger != null) {
                    secondPassengerLeftHeadingDifference = Coordinates.headingDifference(
                            leftFieldOfViewEdgeHeading,
                            headingToSecondPassenger
                    );
                }

                // If there is only one passenger available, no need to decide which passenger is the left and right
                if (secondPassengerLeftHeadingDifference != null) {
                    // Whichever is nearer to the left edge has a smaller heading difference to it, so that passenger
                    // will be the left passenger
                    if (firstPassengerLeftHeadingDifference < secondPassengerLeftHeadingDifference) {
                        leftPassenger = firstPassenger;
                        headingToLeftPassenger = headingToFirstPassenger;

                        rightPassenger = secondPassenger;
                        headingToRightPassenger = headingToSecondPassenger;
                    } else {
                        leftPassenger = secondPassenger;
                        headingToLeftPassenger = headingToSecondPassenger;

                        rightPassenger = firstPassenger;
                        headingToRightPassenger = headingToFirstPassenger;
                    }
                } else {
                    leftPassenger = firstPassenger;
                    headingToLeftPassenger = headingToFirstPassenger;

                    rightPassenger = null;
                    headingToRightPassenger = null;
                }

                // Get the heading difference between the left edge and the left passenger
                double angleBetweenLeftEdgeAndLeftPassenger = Coordinates.headingDifference(
                        leftFieldOfViewEdgeHeading,
                        headingToLeftPassenger
                );

                // If there is only one passenger available, no need to compute for the angle between the left and right
                // passengers
                Double angleBetweenLeftAndRightPassenger = null;

                if (rightPassenger != null) {
                    // Get the heading difference between the left and right passengers
                    angleBetweenLeftAndRightPassenger = Coordinates.headingDifference(
                            headingToLeftPassenger,
                            headingToRightPassenger
                    );
                }

                // If there is only one passenger available, get the angle between the only passenger and the right edge
                // instead
                Double angleBetweenRightPassengerAndLeftEdge = null;
                Double angleBetweenLeftPassengerAndRightEdge = null;

                if (rightPassenger != null) {
                    // Get the heading difference between the right passenger and the right edge
                    angleBetweenRightPassengerAndLeftEdge = Coordinates.headingDifference(
                            headingToRightPassenger,
                            rightFieldOfViewEdgeHeading
                    );
                } else {
                    angleBetweenLeftPassengerAndRightEdge = Coordinates.headingDifference(
                            headingToLeftPassenger,
                            rightFieldOfViewEdgeHeading
                    );
                }

                double widestAngle;
                double meanHeading;

                // Find the widest angle while making sure to consider the case when there is just one passenger
                if (rightPassenger != null) {
                    widestAngle = Math.max(
                            angleBetweenLeftEdgeAndLeftPassenger,
                            Math.max(
                                    angleBetweenLeftAndRightPassenger,
                                    angleBetweenRightPassengerAndLeftEdge
                            )
                    );

                    // Get the mean headings between the two headings that form the widest angle
                    if (widestAngle == angleBetweenLeftEdgeAndLeftPassenger) {
                        meanHeading = Coordinates.meanHeading(
                                leftFieldOfViewEdgeHeading,
                                headingToLeftPassenger
                        );
                    } else if (widestAngle == angleBetweenLeftAndRightPassenger) {
                        meanHeading = Coordinates.meanHeading(
                                headingToLeftPassenger,
                                headingToRightPassenger
                        );
                    } else {
                        meanHeading = Coordinates.meanHeading(
                                headingToRightPassenger,
                                rightFieldOfViewEdgeHeading
                        );
                    }
                } else {
                    widestAngle = Math.max(
                            angleBetweenLeftEdgeAndLeftPassenger,
                            angleBetweenLeftPassengerAndRightEdge
                    );

                    // Get the mean headings between the two headings that form the smallest angle
                    if (widestAngle == angleBetweenLeftEdgeAndLeftPassenger) {
                        meanHeading = Coordinates.meanHeading(
                                leftFieldOfViewEdgeHeading,
                                headingToLeftPassenger
                        );
                    } else {
                        meanHeading = Coordinates.meanHeading(
                                headingToLeftPassenger,
                                rightFieldOfViewEdgeHeading
                        );
                    }
                }

                // Finally, move towards that modified heading at a modified speed
                face(this.parent, null, meanHeading);

                // The slowdown factor linearly depends on the distance between this passenger and the closest passenger
                final double distanceToNearestPassenger = firstEntry.getKey();

                double slowdownFactor
                        = distanceToNearestPassenger / slowdownDistance;

                if (slowdownFactor < 0.1) {
                    slowdownFactor = 0.1;
                }

                this.move(this.walkingDistance * slowdownFactor);

                return nearestPassenger;
            }
        }
    }

    public static void face(Passenger currentPassenger, Passenger leader, double headingGoal) {
//        Passenger currentPassenger = this.parent;

        // If a leader was chosen, face towards the angular mean of the headings toward the leader and the goal
        if (leader != null) {
            // Check if the leader shares the same status
            double headingLeader = Coordinates.headingTowards(
                    currentPassenger.getPassengerMovement().getPosition(),
                    leader.getPassengerMovement().getPosition()
            );

            // Set this passenger's final heading to the angular mean of the two headings
            double meanHeading = Coordinates.meanHeading(headingGoal, headingLeader);

            // Add random perturbations for realistic movement
            meanHeading += new Random().nextGaussian() * Math.toRadians(2);

            currentPassenger.getPassengerMovement().setHeading(meanHeading);
        } else {
            // No leader has been chosen, continue with the passenger's own knowledge of the
            // position of the goal
            // Add random perturbations for realistic movement
            headingGoal += new Random().nextGaussian() * Math.toRadians(2);

            // If a leader has not been chosen, continue moving solo
            currentPassenger.getPassengerMovement().setHeading(headingGoal);
        }
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
            if (patch.getFloorFieldValues().get(State.IN_QUEUE).getValue() > threshold) {
                // Get the distance of that patch from this passenger
                double distanceFromPassenger = Coordinates.distance(this.position, patch.getPatchCenterCoordinates());

                if (distanceFromPassenger < minimumDistance) {
                    minimumDistance = distanceFromPassenger;
                    nearestPatch = patch;
                }
            }
        }

        return nearestPatch;
    }

    // Get the next queueing patch given the current state and the current goal
    public Patch nextQueueingPatch() {
        State state = this.state;

        // Get the patches to explore
        List<Patch> patchesToExplore = Walkway.get5x5Field(this.currentPatch, this.heading, false);

        // Collect the patches with the highest floor field values
        List<Patch> highestPatches = new ArrayList<>();

        double maximumFloorFieldValue = 0.0;

        for (Patch patch : patchesToExplore) {
            FloorField floorField = patch.getFloorFieldValues().get(state);

            // Aside from empty patches, only consider patches whose associated goal is this passenger's goal
            if (floorField == null || floorField.getGoal() == this.goal) {
                double floorFieldValue = patch.getFloorFieldValues().get(state).getValue();

                if (floorFieldValue >= maximumFloorFieldValue) {
                    if (floorFieldValue > maximumFloorFieldValue) {
                        maximumFloorFieldValue = floorFieldValue;

                        highestPatches.clear();
                    }

                    highestPatches.add(patch);
                }
            }
        }

        // If it gets to this point without finding a floor field value greater than zero, return early
        if (maximumFloorFieldValue == 0.0) {
            return null;
        }

        // If there are more than one highest valued-patches, choose the one that is nearest to the passenger
        Patch chosenPatch = highestPatches.get(0);

        List<Double> headingChanges = new ArrayList<>();

        double headingToHighestPatch;
        double headingChangeRequired;

        for (Patch patch : highestPatches) {
            headingToHighestPatch = Coordinates.headingTowards(this.position, patch.getPatchCenterCoordinates());
            headingChangeRequired = Coordinates.headingDifference(this.heading, headingToHighestPatch);

            double headingChangeRequiredDegrees = Math.toDegrees(headingChangeRequired);

            headingChanges.add(headingChangeRequiredDegrees);
        }

        double minimumHeadingChange = Double.MAX_VALUE;

        for (int index = 0; index < highestPatches.size(); index++) {
            double individualScore = headingChanges.get(index);

            if (individualScore < minimumHeadingChange) {
                minimumHeadingChange = individualScore;
                chosenPatch = highestPatches.get(index);
            }
        }

        return chosenPatch;
    }

    public enum State {
        WALKING,
        IN_QUEUE,
        AT_PLATFORM,
        IN_TRAIN,
    }

    public enum Action {
        WILL_QUEUE,
        ASSEMBLING,
        QUEUEING,
        TRANSACTING,
        WAITING_FOR_TRAIN,
        BOARDING,
        RIDING_TRAIN,
        LEAVING
    }
}
