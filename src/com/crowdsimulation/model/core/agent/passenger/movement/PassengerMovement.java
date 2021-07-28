package com.crowdsimulation.model.core.agent.passenger.movement;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.platform.PlatformFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Gate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.Goal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Turnstile;
import com.crowdsimulation.model.core.environment.station.patch.position.Coordinates;
import com.crowdsimulation.model.core.environment.station.patch.position.Vector;
import com.crowdsimulation.model.simulator.Simulator;
import com.crowdsimulation.model.simulator.cache.PathCache;

import java.util.*;

public class PassengerMovement {
    // Denotes the owner of this passenger movement object
    private final Passenger parent;

    // Denotes the positional and navigational variables of the current passenger
    // Denotes the position of the passenger
    private final Coordinates position;

    // Denotes the distance (m) the passenger walks in one second
    private final double preferredWalkingDistance;
    private double currentWalkingDistance;

    // Denotes the proposed and actual heading of the passenger in degrees where
    // E = 0 degrees
    // N = 90 degrees
    // W = 180 degrees
    // S = 270 degrees
    private double proposedHeading;
    private double heading;

    // Denotes this passenger's previous heading
    private double previousHeading;

    // Denotes the patch the passenger is currently in
    private Patch currentPatch;

    // Denotes the amenity the passenger is currently in, if any
    private Amenity currentAmenity;

    // Denotes the patch of the passenger's goal
    private Patch goalPatch;

    // Denotes the amenity the passenger is aiming for
    private Amenity goalAmenity;

    // Denote the queue object the passenger is aiming for
    private QueueObject goalQueueObject;

    // Denotes the attractor the passenger is aiming for
    private Amenity.AmenityBlock goalAttractor;

    // Denotes the chosen train door entrance location, if any
    private TrainDoor.TrainDoorEntranceLocation goalTrainDoorEntranceLocation;

    // Denotes the state of this passenger's floor field
    private QueueingFloorField.FloorFieldState goalFloorFieldState;

    // Denotes the floor field of the passenger goal
    private QueueingFloorField goalFloorField;

    // Denotes the patch with the nearest queueing patch
    private Patch goalNearestQueueingPatch;

    // Denotes the route plan of this passenger
    private RoutePlan routePlan;

    // Denotes the current path followed by this passenger, if any
    private PassengerPath currentPath;

    // Get the floor where this passenger currently is
    private Floor currentFloor;

    // Denotes the direction of travel of the passenger
    private TrainDoor.TravelDirection travelDirection;

    // Denotes the disposition of the passenger - whether the passenger is about to ride a train, or the passenger is
    // about to depart the station (macroscopic state)
    private Disposition disposition;

    // Denotes the state of the passenger - the current disposition of the passenger (macroscopic state)
    private State state;

    // Denotes the action of the passenger - the low-level description of what the passenger is doing (microscopic
    // state)
    private Action action;

    // Denotes whether the passenger is temporarily waiting on an amenity to be vacant
    private boolean isWaitingOnAmenity;

    // Denotes whether this passenger has encountered the passenger to be followed in the queue
    private boolean hasEncounteredPassengerToFollow;

    // Denotes whether this passenger has encountered any queueing passenger
    private boolean hasEncounteredAnyQueueingPassenger;

    // Denotes the passenger this passenger is currently following while assembling
    private Passenger passengerFollowedWhenAssembling;

    // Denotes the distance moved by this passenger in the previous tick
    private double distanceMovedInTick;

    // Counts the ticks this passenger moved a distance under a certain threshold
    private int noMovementCounter;

    // Counts the ticks this passenger has spent moving - this will reset when stopping
    private int movementCounter;

    // Counts the ticks this passenger has seen less than the defined number of patches
    private int noNewPatchesSeenCounter;

    // Counts the ticks this passenger has spent seeing new patches - this will reset otherwise
    private int newPatchesSeenCounter;

    // Denotes whether the passenger is stuck
    private boolean isStuck;

    // Counts the ticks this passenger has spent being stuck - this will reset when a condition is reached
    private int stuckCounter;

    // Denotes the time since the passenger left its previous goal
    private int timeSinceLeftPreviousGoal;

    // Denotes the time until the passenger accelerates fully from non-movement
    final int ticksUntilFullyAccelerated;

    // Denotes the time the passenger has spent accelerating or moving at a constant speed so far without slowing down
    // or stopping
    private int ticksAcceleratedOrMaintainedSpeed;

    // Denotes the field of view angle of the passenger
    private final double fieldOfViewAngle;

    // Denotes whether the passenger is ready to be freed from being stuck
    private boolean isReadyToFree;

    // Denotes whether the passenger as a stored value card holder is ready to pathfind
    private boolean willPathfind;

    // Denotes whether this passenger as a stored value card holder has already pathfound
    private boolean hasPathfound;

    // Denotes whether this passenger should take a step forward after it left its goal
    private boolean shouldStepForward;

    // Denotes the patches to explore for obstacles or passengers
    private List<Patch> toExplore;

    // Denotes the recent patches this passenger has been in
    private final HashMap<Patch, Integer> recentPatches;

    // The vectors of this passenger
    private final List<Vector> repulsiveForceFromPassengers;
    private final List<Vector> repulsiveForcesFromObstacles;
    private Vector attractiveForce;
    private Vector motivationForce;

    public PassengerMovement(
            Gate gate,
            Passenger parent,
            Coordinates coordinates,
            TrainDoor.TravelDirection travelDirection
    ) {
        this.parent = parent;

        this.position = new Coordinates(
                coordinates.getX(),
                coordinates.getY()
        );

        // TODO: Walking speed should depend on the passenger's age
        // TODO: Adjust to actual, realistic values
        // The walking speed values shall be in m/s
        this.preferredWalkingDistance = 0.6;
        this.currentWalkingDistance = preferredWalkingDistance;

        // All newly generated passengers will face the north by default
        // The heading values shall be in degrees, but have to be converted to radians for the math libraries to process
        // East: 0 degrees
        // North: 90 degrees
        // West: 180 degrees
        // South: 270 degrees
        this.proposedHeading = Math.toRadians(90.0);
        this.heading = Math.toRadians(90.0);

        this.previousHeading = Math.toRadians(90.0);

        // Set the passenger's field of view
        this.fieldOfViewAngle = Math.toRadians(90.0);

        // Add this passenger to the start patch
        this.currentPatch = Main.simulator.getCurrentFloor().getPatch(coordinates);
        this.currentPatch.getPassengers().add(parent);

        // Set the passenger's time until it fully accelerates
        this.ticksUntilFullyAccelerated = 10;
        this.ticksAcceleratedOrMaintainedSpeed = 0;

        // Take note of the amenity where this passenger was spawned
        this.currentAmenity = gate;

        // Assign the route plan of this passenger
        this.routePlan = new RoutePlan(
                this.parent.getTicketType() == TicketBooth.TicketType.STORED_VALUE
        );

        // Assign the floor of this passenger
        this.currentFloor = gate.getAmenityBlocks().get(0).getPatch().getFloor();

        // Assign the initial direction, disposition, state, action of this passenger
        this.travelDirection = travelDirection;
        this.disposition = Disposition.BOARDING;
        this.state = State.WALKING;
        this.action = Action.WILL_QUEUE;

        this.toExplore = new ArrayList<>();
        this.recentPatches = new HashMap<>();

        repulsiveForceFromPassengers = new ArrayList<>();
        repulsiveForcesFromObstacles = new ArrayList<>();

        // Set the passenger goal
        resetGoal(false);
    }

    public Passenger getParent() {
        return parent;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates coordinates) {
        final int timeElapsedExpiration = 10;

        Patch previousPatch = this.currentPatch;

        this.position.setX(coordinates.getX());
        this.position.setY(coordinates.getY());

        // Get the patch of the new position
        Patch newPatch = this.currentFloor.getPatch(new Coordinates(coordinates.getX(), coordinates.getY()));

        // If the newer position is on a different patch, remove the passenger from its old patch, then
        // add it to the new patch
        if (!previousPatch.equals(newPatch)) {
            previousPatch.getPassengers().remove(this.parent);
            newPatch.getPassengers().add(this.parent);

            // Remove this passenger from the patch set of the previous patch
            SortedSet<Patch> previousPatchSet = previousPatch.getFloor().getPassengerPatchSet();
            SortedSet<Patch> newPatchSet = newPatch.getFloor().getPassengerPatchSet();

            if (
                    previousPatchSet.contains(previousPatch)
                            && previousPatch.getPassengers().isEmpty()
            ) {
                previousPatchSet.remove(previousPatch);
            }

            // Then add this passenger to the patch set of the next patch
            newPatchSet.add(newPatch);

            // Then set the new current patch
            this.currentPatch = newPatch;

            // Update the recent patch list
            updateRecentPatches(this.currentPatch, timeElapsedExpiration);
        } else {
            // Update the recent patch list
            updateRecentPatches(null, timeElapsedExpiration);
        }
    }

    public double getCurrentWalkingDistance() {
        return currentWalkingDistance;
    }

    public double getProposedHeading() {
        return proposedHeading;
    }

    public double getHeading() {
        return heading;
    }

    public Patch getCurrentPatch() {
        return currentPatch;
    }

    public void setCurrentPatch(Patch currentPatch) {
        this.currentPatch = currentPatch;
    }

    public Amenity getCurrentAmenity() {
        return currentAmenity;
    }

    public Amenity.AmenityBlock getGoalAttractor() {
        return goalAttractor;
    }

    public Patch getGoalPatch() {
        return goalPatch;
    }

    public Amenity getGoalAmenity() {
        return goalAmenity;
    }

    public QueueingFloorField.FloorFieldState getGoalFloorFieldState() {
        return goalFloorFieldState;
    }

    public QueueingFloorField getGoalFloorField() {
        return goalFloorField;
    }

    public Patch getGoalNearestQueueingPatch() {
        return goalNearestQueueingPatch;
    }

    public RoutePlan getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(RoutePlan routePlan) {
        this.routePlan = routePlan;
    }

    public PassengerPath getCurrentPath() {
        return currentPath;
    }

    public Floor getCurrentFloor() {
        return currentFloor;
    }

    public Disposition getDirection() {
        return disposition;
    }

    public void setDirection(Disposition disposition) {
        this.disposition = disposition;
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

    public boolean isWaitingOnAmenity() {
        return isWaitingOnAmenity;
    }

    public Passenger getPassengerFollowedWhenAssembling() {
        return passengerFollowedWhenAssembling;
    }

    public boolean hasEncounteredPassengerToFollow() {
        return hasEncounteredPassengerToFollow;
    }

    public boolean hasEncounteredAnyQueueingPassenger() {
        return hasEncounteredAnyQueueingPassenger;
    }

    public List<Patch> getToExplore() {
        return toExplore;
    }

    public HashMap<Patch, Integer> getRecentPatches() {
        return recentPatches;
    }

    public double getDistanceMovedInTick() {
        return distanceMovedInTick;
    }

    public int getNoMovementCounter() {
        return noMovementCounter;
    }

    public int getMovementCounter() {
        return movementCounter;
    }

    public int getNoNewPatchesSeenCounter() {
        return noNewPatchesSeenCounter;
    }

    public int getNewPatchesSeenCounter() {
        return newPatchesSeenCounter;
    }

    public int getStuckCounter() {
        return stuckCounter;
    }

    public int getTimeSinceLeftPreviousGoal() {
        return timeSinceLeftPreviousGoal;
    }

    public boolean isStuck() {
        return isStuck;
    }

    public boolean isReadyToFree() {
        return isReadyToFree;
    }

    public boolean willPathFind() {
        return willPathfind;
    }

    public List<Vector> getRepulsiveForceFromPassengers() {
        return repulsiveForceFromPassengers;
    }

    public List<Vector> getRepulsiveForcesFromObstacles() {
        return repulsiveForcesFromObstacles;
    }

    public Vector getAttractiveForce() {
        return attractiveForce;
    }

    public Vector getMotivationForce() {
        return motivationForce;
    }

    //

    public Queueable getGoalAmenityAsQueueable() {
        return Queueable.toQueueable(this.goalAmenity);
    }

    public Goal getGoalAmenityAsGoal() {
        return Goal.toGoal(this.goalAmenity);
    }

    public TrainDoor getGoalAmenityAsTrainDoor() {
        return TrainDoor.asTrainDoor(this.goalAmenity);
    }

    // Use the A* algorithm (with Euclidian distance to compute the f-score) to find the shortest path to the given goal
    // patch
    public PassengerPath computePath(
            Patch startingPatch,
            Patch goalPatch,
            boolean includeStartingPatch,
            boolean includeGoalPatch
    ) {
        // Check the cache first if the path from one patch to another has already been computed beforehand
        PathCache pathCache = this.currentFloor.getPathCache();

        Patch.PatchPair patchPair = new Patch.PatchPair(startingPatch, goalPatch);
        PassengerPath cachedPath = pathCache.get(patchPair);

        // If the path connecting these patches have already been computed, use that instead
        if (cachedPath != null) {
            return cachedPath;
        }

        HashSet<Patch> openSet = new HashSet<>();

        HashMap<Patch, Double> gScores = new HashMap<>();
        HashMap<Patch, Double> fScores = new HashMap<>();

        HashMap<Patch, Patch> cameFrom = new HashMap<>();

        for (Patch[] patchRow : startingPatch.getFloor().getPatches()) {
            for (Patch patch : patchRow) {
                gScores.put(patch, Double.MAX_VALUE);
                fScores.put(patch, Double.MAX_VALUE);
            }
        }

        gScores.put(startingPatch, 0.0);
        fScores.put(
                startingPatch,
                Coordinates.distance(
                        this.currentFloor.getStation(),
                        startingPatch,
                        goalPatch
                )
        );

        openSet.add(startingPatch);

        while (!openSet.isEmpty()) {
            Patch patchToExplore;

            double minimumDistance = Double.MAX_VALUE;
            Patch patchWithMinimumDistance = null;

            for (Patch patchInQueue : openSet) {
                double fScore = fScores.get(patchInQueue);

                if (fScore < minimumDistance) {
                    minimumDistance = fScore;
                    patchWithMinimumDistance = patchInQueue;
                }
            }

            patchToExplore = patchWithMinimumDistance;

            if (patchToExplore.equals(goalPatch)) {
                Stack<Patch> path = new Stack<>();
                double length = 0.0;

                Patch currentPatch = goalPatch;

                // Exclude the final patch, if the goal is not to be included
                if (!includeGoalPatch) {
                    currentPatch = cameFrom.get(currentPatch);
                }

                while (cameFrom.containsKey(currentPatch)) {
                    Patch previousPatch = cameFrom.get(currentPatch);

                    length += Coordinates.distance(
                            previousPatch.getPatchCenterCoordinates(),
                            currentPatch.getPatchCenterCoordinates()
                    );

                    currentPatch = previousPatch;

                    path.push(currentPatch);
                }

                PassengerPath passengerPath = new PassengerPath(path, length);

                pathCache.put(patchPair, passengerPath);

                return passengerPath;
            }

            openSet.remove(patchToExplore);

            List<Patch> patchToExploreNeighbors = patchToExplore.getNeighbors();

            for (Patch patchToExploreNeighbor : patchToExploreNeighbors) {
                if (
                        patchToExploreNeighbor.getAmenityBlock() == null
                                || patchToExploreNeighbor.getAmenityBlock() != null
                                && (
                                !includeStartingPatch && patchToExplore.equals(startingPatch)
                                        || !includeGoalPatch && patchToExploreNeighbor.equals(goalPatch)
                        )
                ) {
                    double tentativeGScore
                            = gScores.get(patchToExplore)
                            + Coordinates.distance(
                            this.currentFloor.getStation(),
                            patchToExplore,
                            patchToExploreNeighbor
                    );

                    if (tentativeGScore < gScores.get(patchToExploreNeighbor)) {
                        cameFrom.put(patchToExploreNeighbor, patchToExplore);

                        gScores.put(patchToExploreNeighbor, tentativeGScore);
                        fScores.put(
                                patchToExploreNeighbor,
                                gScores.get(patchToExploreNeighbor)
                                        + Coordinates.distance(
                                        this.currentFloor.getStation(),
                                        patchToExploreNeighbor,
                                        goalPatch)
                        );

                        openSet.add(patchToExploreNeighbor);
                    }
                }
            }
        }

        // There are no paths from the origin to the destination patches
        pathCache.put(patchPair, null);

        return null;
    }

    // Check whether the current goal amenity is a queueable or not
    public boolean isNextAmenityQueueable() {
        return Queueable.isQueueable(this.goalAmenity);
    }

    // Check whether the current goal amenity is a goal or not
    public boolean isNextAmenityGoal() {
        return Goal.isGoal(this.goalAmenity);
    }

    // Check whether the passenger has just left the goal (if the passenger is at a certain number of ticks since
    // leaving the goal)
    public boolean hasJustLeftGoal() {
        final int hasJustLeftGoalThreshold = 3;

        return this.timeSinceLeftPreviousGoal <= hasJustLeftGoalThreshold;
    }

    // Reset the passenger's goal
    public void resetGoal(boolean shouldStepForwardFirst) {
        // Take note of the passenger's goal patch, amenity (on that goal patch), and that amenity's attractor
        this.goalPatch = null;
        this.goalAmenity = null;
        this.goalQueueObject = null;
        this.goalAttractor = null;
        this.goalTrainDoorEntranceLocation = null;

        // Take note of the floor field state of this passenger
        this.goalFloorFieldState = null;

        // Take note of the floor field of the passenger's goal
        this.goalFloorField = null;

        // Take note of the passenger's nearest queueing patch
        this.goalNearestQueueingPatch = null;

        // No passengers have been encountered yet
        this.hasEncounteredPassengerToFollow = false;
        this.hasEncounteredAnyQueueingPassenger = false;

        // This passenger is not yet waiting
        this.isWaitingOnAmenity = false;

        // Set whether this passenger is set to step forward
        this.shouldStepForward = shouldStepForwardFirst;

        // This passenger is not following anyone yet
        this.passengerFollowedWhenAssembling = null;

        // This passenger hasn't moved yet
        this.distanceMovedInTick = 0.0;

        this.noMovementCounter = 0;
        this.movementCounter = 0;

        this.noNewPatchesSeenCounter = 0;
        this.newPatchesSeenCounter = 0;

        this.timeSinceLeftPreviousGoal = 0;

        // This passenger hasn't pathfound yet
        this.willPathfind = false;
        this.hasPathfound = false;

        // This passenger has no recent patches yet
        this.recentPatches.clear();

        // This passenger is not yet stuck
        this.free();
    }

    // Set the nearest goal to this passenger
    // That goal should also have the fewer passengers queueing for it
    // To determine this, for each two passengers in the queue (or fraction thereof), a penalty of one tile is added to
    // the distance to this goal
    public void chooseGoal() {
        // Only check the queue a certain percentage of the time
        // Proceed anyway if no goals have been set yet
        if (this.goalAmenity == null && this.goalAttractor == null) {
            // TODO: consider amenities in next floor
            // Based on the passenger's current direction and route plan, get the next amenity class to be sought
            Class<? extends Amenity> nextAmenityClass = this.routePlan.getCurrentAmenityClass();
            List<? extends Amenity> amenityListInFloor = this.currentFloor.getAmenityList(nextAmenityClass);

            Amenity chosenAmenity = null;
            QueueObject chosenQueueObject = null;
            Amenity.AmenityBlock chosenAttractor = null;
            TrainDoor.TrainDoorEntranceLocation chosenTrainDoorEntranceLocation = null;

            // Compile all attractors from each amenity in the amenity list
            HashMap<Amenity.AmenityBlock, Double> distancesToAttractors = new HashMap<>();

            for (Amenity amenity : amenityListInFloor) {
                if (amenity instanceof TrainDoor) {
                    // Only consider train doors which match this passenger's travel direction
                    TrainDoor trainDoor = ((TrainDoor) amenity);

                    if (trainDoor.getPlatform() != this.travelDirection) {
                        continue;
                    }
                }

                for (Amenity.AmenityBlock attractor : amenity.getAttractors()) {
                    // Only consider attractors in amenities which are accessible from the current position
                    PassengerPath path = computePath(
                            this.currentPatch,
                            attractor.getPatch(),
                            false,
                            false
                    );

                    if (path != null) {
                        distancesToAttractors.put(attractor, path.getLength());
                    }
                }
            }

            double minimumAttractorScore = Double.MAX_VALUE;
            Amenity.AmenityBlock nearestAttractor = null;

            for (
                    Map.Entry<Amenity.AmenityBlock, Double> distancesToAttractorEntry
                    : distancesToAttractors.entrySet()
            ) {
                Amenity.AmenityBlock candidateAttractor = distancesToAttractorEntry.getKey();
                Double candidateDistance = distancesToAttractorEntry.getValue();

                Amenity currentAmenity;
                QueueObject currentQueueObject;
                TrainDoor.TrainDoorEntranceLocation currentTrainDoorEntranceLocation;

                currentAmenity = candidateAttractor.getParent();

                if (currentAmenity instanceof TrainDoor) {
                   TrainDoor trainDoor = ((TrainDoor) currentAmenity);

                    currentTrainDoorEntranceLocation
                            = trainDoor.getTrainDoorEntranceLocationFromAttractor(candidateAttractor);
                    currentQueueObject
                            = trainDoor.getQueueObjectFromTrainDoorEntranceLocation(currentTrainDoorEntranceLocation);
                } else {
                    Queueable queueable = ((Queueable) currentAmenity);

                    currentTrainDoorEntranceLocation = null;
                    currentQueueObject = queueable.getQueueObject();
                }

                double attractorScore;

                if (!(currentAmenity instanceof Security)) {
                    final double passengerPenalty = 1.5;

                    attractorScore
                            = candidateDistance + currentQueueObject.getPassengersQueueing().size() * passengerPenalty;
                } else {
                    attractorScore = candidateDistance;
                }

                if (attractorScore < minimumAttractorScore) {
                    minimumAttractorScore = attractorScore;

                    chosenAmenity = currentAmenity;
                    chosenQueueObject = currentQueueObject;
                    chosenAttractor = candidateAttractor;
                    chosenTrainDoorEntranceLocation = currentTrainDoorEntranceLocation;
                }
            }

            //
//
//            for (Amenity amenity : amenityListInFloor) {
//                QueueObject currentQueueObject;
//                TrainDoor.TrainDoorEntranceLocation currentTrainDoorEntranceLocation;
//
//                // Within the amenity itself, see which attractor is closer to this passenger
//                double minimumAttractorDistance = Double.MAX_VALUE;
//                Amenity.AmenityBlock nearestAttractor = null;
//
//                double attractorDistance;
//
//                for (Amenity.AmenityBlock attractor : amenity.getAttractors()) {
//                    attractorDistance = Coordinates.distance(
//                            this.position,
//                            attractor.getPatch().getPatchCenterCoordinates()
//                    );
//
//                    if (attractorDistance < minimumAttractorDistance) {
//                        minimumAttractorDistance = attractorDistance;
//                        nearestAttractor = attractor;
//                    }
//                }
//
//                if (amenity instanceof TrainDoor) {
//                    TrainDoor trainDoor = ((TrainDoor) amenity);
//
//                    currentTrainDoorEntranceLocation
//                            = trainDoor.getTrainDoorEntranceLocationFromAttractor(nearestAttractor);
//                    currentQueueObject
//                            = trainDoor.getQueueObjectFromTrainDoorEntranceLocation(currentTrainDoorEntranceLocation);
//                } else {
//                    Queueable queueable = ((Queueable) amenity);
//
//                    currentTrainDoorEntranceLocation = null;
//                    currentQueueObject = queueable.getQueueObject();
//                }
//
//                // Then measure the distance from the nearest attractor to this passenger
//                if (amenity instanceof Queueable) {
//                    passengersQueueing
//                            = currentQueueObject.getPassengersQueueing().size();
//
//                    score = minimumAttractorDistance;
//
//                    if (
//                            amenity instanceof TicketBooth
//                                    || amenity instanceof Turnstile
//                                    || amenity instanceof TrainDoor
//                    ) {
//                        score += passengersQueueing * 1.5;
//                    }
//                } else {
//                    score = minimumAttractorDistance;
//                }
//
//                if (score < minimumScore) {
//                    minimumScore = score;
//
//                    chosenAmenity = amenity;
//                    chosenQueueObject = currentQueueObject;
//                    chosenAmenityBlock = nearestAttractor;
//                    chosenTrainDoorEntranceLocation = currentTrainDoorEntranceLocation;
//                }
//            }

            // Set the goal nearest to this passenger
            this.goalAmenity = chosenAmenity;
            this.goalQueueObject = chosenQueueObject;
            this.goalAttractor = chosenAttractor;
            this.goalPatch = chosenAttractor.getPatch();
            this.goalTrainDoorEntranceLocation = chosenTrainDoorEntranceLocation;
        }
    }

    // Get the future position of this passenger given the current goal, current heading, and the current walking
    // distance
    private Coordinates getFuturePosition() {
        return getFuturePosition(this.goalAmenity, this.proposedHeading, this.preferredWalkingDistance);
    }

    // Get the future position of this passenger given the current goal, current heading, and a given walking distance
    private Coordinates getFuturePosition(double walkingDistance) {
        return getFuturePosition(this.goalAmenity, this.proposedHeading, walkingDistance);
    }

    public Coordinates getFuturePosition(Coordinates startingPosition, double heading, double magnitude) {
        return Coordinates.computeFuturePosition(startingPosition, heading, magnitude);
    }

    // Get the future position of this passenger given a goal and a heading
    public Coordinates getFuturePosition(Amenity goal, double heading, double walkingDistance) {
        // Get the goal's floor
        Floor goalFloor = goal.getAmenityBlocks().get(0).getPatch().getFloor();

        // Get the nearest attractor to this passenger
        double minimumDistance = Double.MAX_VALUE;
        double distance;

        Amenity.AmenityBlock nearestAttractor = null;

        for (Amenity.AmenityBlock attractor : goal.getAttractors()) {
            distance = Coordinates.distance(this.position, attractor.getPatch().getPatchCenterCoordinates());

            if (distance < minimumDistance) {
                minimumDistance = distance;
                nearestAttractor = attractor;
            }
        }

        assert nearestAttractor != null;

        // If the distance between this passenger and the goal is less than the distance this passenger covers every
        // time it walks, "snap" the position of the passenger to the center of the goal immediately, to avoid
        // overshooting its target
        // If not, compute the next coordinates normally
        if (minimumDistance < walkingDistance) {
            return new Coordinates(
                    nearestAttractor.getPatch().getPatchCenterCoordinates().getX(),
                    nearestAttractor.getPatch().getPatchCenterCoordinates().getY()
            );
        } else {
            Coordinates futurePosition = this.getFuturePosition(
                    this.position,
                    heading,
                    walkingDistance
            );

            double newX = futurePosition.getX();
            double newY = futurePosition.getY();

            // Check if the new coordinates are out of bounds
            // If they are, adjust them such that they stay within bounds
            if (newX < 0) {
                newX = 0.0;
            } else if (newX > goalFloor.getColumns() - 1) {
                newX = goalFloor.getColumns() - 0.5;
            }

            if (newY < 0) {
                newY = 0.0;
            } else if (newY > goalFloor.getRows() - 1) {
                newY = goalFloor.getRows() - 0.5;
            }

            return new Coordinates(newX, newY);
        }
    }

    // Make the passenger move in accordance with social forces
    public boolean moveSocialForce() {
        // The smallest repulsion a passenger may inflict on another
        final double minimumPassengerRepulsion = 0.01 * this.preferredWalkingDistance;

        // The smallest repulsion an obstacle may inflict to a passenger
//        final double minimumObstacleRepulsion = 0.01 * this.preferredWalkingDistance;

        // If the passenger has not moved a sufficient distance for more than this number of ticks, the passenger
        // will be considered stuck
        final int noMovementTicksThreshold = (this.getGoalAmenityAsGoal() != null) ? this.getGoalAmenityAsGoal().getWaitingTime() : 10;

        // If the passenger has not seen new patches for more than this number of ticks, the passenger will be considered
        // stuck
        final int noNewPatchesSeenTicksThreshold = 10;

        // If the passenger has been moving a sufficient distance for at least this number of ticks, this passenger will
        // be out of the stuck state, if it was
        final int unstuckTicksThreshold = 10;

        // If the distance the passenger moves per tick is less than this distance, this passenger is considered to not
        // have moved
        final double noMovementThreshold = 0.01 * this.preferredWalkingDistance;

        // If the size of the passenger's memory of recent patches is less than this number, the passenger is considered
        // to not have moved
        final double noNewPatchesSeenThreshold = 5;

        // The distance to another passenger before this passenger slows down
        final double slowdownStartDistance = 2.0;

        // The minimum allowable distance from another passenger at its front before this passenger stops
        final double minimumStopDistance = 0.6;

        // The maximum allowable distance from another passenger at its front before this passenger stops
        double maximumStopDistance = 1.0;

        // Count the number of passengers and obstacles in the the relevant patches
        int numberOfPassengers = 0;
        int numberOfObstacles = 0;

        // The distance from the passenger's center by which repulsive effects from passengers start to occur
        double maximumPassengerStopDistance = 1.0;

        // The distance from the passenger's center by which repulsive effects from passengers are at a maximum
        final double minimumPassengerStopDistance = 0.6;

        // The distance from the passenger's center by which repulsive effects from obstacles start to occur
        double maximumObstacleStopDistance = 1.0;

        // The distance from the passenger's center by which repulsive effects from obstacles are at a maximum
        final double minimumObstacleStopDistance = 0.6;

        // Get the relevant patches
        List<Patch> patchesToExplore
                = Floor.get7x7Field(this.currentPatch, this.proposedHeading, true, Math.toRadians(360.0));

        this.toExplore = patchesToExplore;

        // Clear vectors from the previous computations
        this.repulsiveForceFromPassengers.clear();
        this.repulsiveForcesFromObstacles.clear();
        this.attractiveForce = null;
        this.motivationForce = null;

        // Add the repulsive effects from nearby passengers and obstacles
        TreeMap<Double, Amenity.AmenityBlock> obstaclesEncountered = new TreeMap<>();

        // This will contain the final motivation vector
        List<Vector> vectorsToAdd = new ArrayList<>();

        // Get the current heading, which will be the previous heading later
        this.previousHeading = this.heading;

        // Compute the proposed future position
        Coordinates proposedNewPosition;

/*        double accelerationFactor;

//        if (this.timeSinceLeftPreviousGoal <= this.ticksUntilFullyAccelerated) {
        accelerationFactor = Math.sqrt(this.ticksAcceleratedOrMaintainedSpeed + 1) / (Math.sqrt(this.ticksUntilFullyAccelerated));
*//*        } else {
            accelerationFactor = Math.sqrt(this.newPatchesSeenCounter + 1) / (Math.sqrt(ticksUntilFullyAccelerated));
        }*//*

        System.out.println("this.currentWalkingDistance < previousWalkingDistance: " + this.currentWalkingDistance + ", " + this.ticksAcceleratedOrMaintainedSpeed + ": " + accelerationFactor);

        accelerationFactor = Math.min(accelerationFactor, 1.0);

        proposedNewPosition = this.getFuturePosition(
                accelerationFactor
                        * this.preferredWalkingDistance
        );*/

        // Check if the passenger is set to take one initial step forward
        if (!this.shouldStepForward) {
            // Compute for the proposed future position
            proposedNewPosition = this.getFuturePosition(this.preferredWalkingDistance);

            // If this passenger is queueing, the only social forces that apply are attractive forces to passengers
            // and obstacles (if not in queueing action)
            if (this.state == State.IN_QUEUE) {
                // Do not check for stuckness when already heading to the queueable
                if (this.action != Action.HEADING_TO_QUEUEABLE) {
                    // If the passenger hasn't already been moving for a while, consider the passenger stuck, and implement some
                    // measures to free this passenger
                    if (
                            this.isStuck
                                    || (this.goalAttractor.getPatch().getPassengers().isEmpty() && (this.isAtQueueFront() || this.isServicedByGoal())) && this.noMovementCounter > noMovementTicksThreshold
                        /*&& this.parent.getTicketType() != TicketBooth.TicketType.STORED_VALUE*/
                    ) {
                        this.isStuck = true;
                        this.stuckCounter++;
                    }/* else {
                        this.isReadyToFree = true;
                    }*/
                }

                // Get the passengers within the current field of view in these patches
                // If there are any other passengers within this field of view, this passenger is at least guaranteed to
                // slow down
                TreeMap<Double, Passenger> passengersWithinFieldOfView = new TreeMap<>();

                // Look around the patches that fall on the passenger's field of view
                for (Patch patch : patchesToExplore) {
                    // Do not apply social forces from obstacles if the passenger is in the queueing action, i.e., when the
                    // passenger is following a floor field
                    // If this patch has an obstacle, take note of it to add a repulsive force from it later
                    if (this.action != Action.QUEUEING) {
                        Amenity.AmenityBlock patchAmenityBlock = patch.getAmenityBlock();

                        // Get the distance between this passenger and the obstacle on this patch
                        if (hasObstacle(patch)) {
                            // Take note of the obstacle density in this area
                            numberOfObstacles++;

                            // If the distance is less than or equal to the specified minimum repulsion distance, compute
                            // for the magnitude of the repulsion force
                            double distanceToObstacle = Coordinates.distance(
                                    this.position,
                                    patchAmenityBlock.getPatch().getPatchCenterCoordinates()
                            );

                            if (
                                    distanceToObstacle <= slowdownStartDistance/*
                                            && !patchAmenityBlock.isAttractor()*/
                            ) {
                                obstaclesEncountered.put(distanceToObstacle, patchAmenityBlock);
                            }
                        }
                    }

                    if (!this.isStuck) {
                        for (Passenger otherPassenger : patch.getPassengers()) {
                            // Make sure that the passenger discovered isn't itself
                            if (!otherPassenger.equals(this.getParent())) {
                                if (!this.hasEncounteredAnyQueueingPassenger && otherPassenger.getPassengerMovement().getState() == State.IN_QUEUE) {
                                    this.hasEncounteredAnyQueueingPassenger = true;
                                }

                                if (
                                        otherPassenger.getPassengerMovement().getState() == State.WALKING
                                                || this.action != Action.HEADING_TO_QUEUEABLE
                                                && otherPassenger.getPassengerMovement().getGoalAmenity() != null && otherPassenger.getPassengerMovement().getGoalAmenity().equals(this.getGoalAmenity())
                                                && (this.passengerFollowedWhenAssembling == null || this.passengerFollowedWhenAssembling.equals(otherPassenger))
                                ) {
                                    // Take note of the passenger density in this area
                                    numberOfPassengers++;

                                    // Check if this passenger is within the field of view and within the slowdown distance
                                    double distanceToPassenger = Coordinates.distance(
                                            this.position,
                                            otherPassenger.getPassengerMovement().getPosition()
                                    );

                                    if (Coordinates.isWithinFieldOfView(
                                            this.position,
                                            otherPassenger.getPassengerMovement().getPosition(),
                                            this.proposedHeading,
                                            this.fieldOfViewAngle)
                                            && distanceToPassenger <= slowdownStartDistance) {
                                        passengersWithinFieldOfView.put(distanceToPassenger, otherPassenger);
                                    }
                                }
                            }
                        }
                    }
                }

                // Compute the perceived density of the passengers
                // Assuming the maximum density a passenger sees within its environment is 3 before it thinks the crowd
                // is very dense, rate the perceived density of the surroundings by dividing the number of people by the
                // maximum tolerated number of passengers
                final double maximumDensityTolerated = 3.0;
                final double passengerDensity
                        = (numberOfPassengers > maximumDensityTolerated ? maximumDensityTolerated : numberOfPassengers)
                        / maximumDensityTolerated;

                // For each passenger found within the slowdown distance, get the nearest one, if there is any
                Map.Entry<Double, Passenger> nearestPassengerEntry = passengersWithinFieldOfView.firstEntry();

                // If there are no passengers within the field of view, good - move normally
                if (nearestPassengerEntry == null/*|| nearestPassengerEntry.getValue().getPassengerMovement().getGoalAmenity() != null && !nearestPassengerEntry.getValue().getPassengerMovement().getGoalAmenity().equals(this.goalAmenity)*/) {
                    this.hasEncounteredPassengerToFollow = this.passengerFollowedWhenAssembling != null;

                    // Get the attractive force of this passenger to the new position
                    this.attractiveForce = this.computeAttractiveForce(
                            new Coordinates(this.position),
                            this.proposedHeading,
                            proposedNewPosition,
                            this.preferredWalkingDistance
                    );

                    vectorsToAdd.add(attractiveForce);
                } else {
                    // Check the distance of that nearest passenger to this passenger
                    double distanceToNearestPassenger = nearestPassengerEntry.getKey();

                    // Modify the maximum stopping distance depending on the density of the environment
                    // That is, the denser the surroundings, the less space this passenger will allow between other
                    // passengers
                    maximumStopDistance -= (maximumStopDistance - minimumStopDistance) * passengerDensity;

                    this.hasEncounteredPassengerToFollow = this.passengerFollowedWhenAssembling != null;

                    // Else, just slow down and move towards the direction of that passenger in front
                    // The slowdown factor linearly depends on the distance between this passenger and the other
                    final double slowdownFactor
                            = (distanceToNearestPassenger - maximumStopDistance)
                            / (slowdownStartDistance - maximumStopDistance);

                    double computedWalkingDistance = slowdownFactor * this.preferredWalkingDistance;

                    Coordinates revisedPosition = this.getFuturePosition(computedWalkingDistance);

                    // Get the attractive force of this passenger to the new position
                    this.attractiveForce = this.computeAttractiveForce(
                            new Coordinates(this.position),
                            this.proposedHeading,
                            revisedPosition,
                            computedWalkingDistance
                    );

                    vectorsToAdd.add(attractiveForce);
                }
            } else {
                // If the passenger hasn't already been moving for a while, consider the passenger stuck, and implement some
                // measures to free this passenger
                if (this.isStuck || this.noNewPatchesSeenCounter > noNewPatchesSeenTicksThreshold) {
                    this.isStuck = true;
                    this.stuckCounter++;
                }

                boolean hasEncounteredQueueingPassengerInLoop = false;

                // Only apply the social forces of a set number of passengers and obstacles
                int passengersProcessed = 0;
                final int passengersProcessedLimit = 5;

                // Look around the patches that fall on the passenger's field of view
                for (Patch patch : patchesToExplore) {
                    // If this patch has an obstacle, take note of it to add a repulsive force from it later
                    Amenity.AmenityBlock patchAmenityBlock = patch.getAmenityBlock();

                    // Get the distance between this passenger and the obstacle on this patch
                    if (hasObstacle(patch)) {
                        // Take note of the obstacle density in this area
                        numberOfObstacles++;

                        // If the distance is less than or equal to the specified minimum repulsion distance, compute
                        // for the magnitude of the repulsion force
                        double distanceToObstacle = Coordinates.distance(
                                this.position,
                                patchAmenityBlock.getPatch().getPatchCenterCoordinates()
                        );

                        if (
                            /*Coordinates.isWithinFieldOfView(
                                    this.position,
                                    patchAmenityBlock.getPatch().getPatchCenterCoordinates(),
                                    this.proposedHeading,
                                    Math.toRadians(fieldOfViewAngleDegrees))
                                    && */distanceToObstacle <= slowdownStartDistance/*
                                && !patchAmenityBlock.isAttractor()*/
                        ) {
                            obstaclesEncountered.put(distanceToObstacle, patchAmenityBlock);
                        }
                    }

                    // Inspect each passenger in each patch in the patches in the field of view
                    for (Passenger otherPassenger : patch.getPassengers()) {
                        if (passengersProcessed == passengersProcessedLimit) {
                            break;
                        }

                        // Make sure that the passenger discovered isn't itself
                        if (!otherPassenger.equals(this.getParent())) {
                            // Take note of the passenger density in this area
                            numberOfPassengers++;

                            // Get the distance between this passenger and the other passenger
                            double distanceToOtherPassenger = Coordinates.distance(
                                    this.position,
                                    otherPassenger.getPassengerMovement().getPosition()
                            );

                            // If the distance is less than or equal to the distance when repulsion is supposed to kick in,
                            // compute for the magnitude of that repulsion force
                            if (distanceToOtherPassenger <= slowdownStartDistance) {
                                // Compute the perceived density of the passengers
                                // Assuming the maximum density a passenger sees within its environment is 3 before it thinks the crowd
                                // is very dense, rate the perceived density of the surroundings by dividing the number of people by the
                                // maximum tolerated number of passengers
                                final int maximumPassengerCountTolerated = 5;

                                // The distance by which the repulsion starts to kick in will depend on the density of the passenger's
                                // surroundings
                                final int minimumObstacleCount = 1;
                                final double maximumDistance = 2.0;
                                final int maximumObstacleCount = 2;
                                final double minimumDistance = 0.7;

                                double computedMaximumDistance = computeMaximumRepulsionDistance(
                                        numberOfObstacles,
                                        maximumPassengerCountTolerated,
                                        minimumObstacleCount,
                                        maximumDistance,
                                        maximumObstacleCount,
                                        minimumDistance
                                );

                                Vector passengerRepulsiveForce = computeSocialForceFromPassenger(
                                        otherPassenger,
                                        distanceToOtherPassenger,
                                        computedMaximumDistance,
                                        minimumPassengerStopDistance,
                                        this.preferredWalkingDistance
                                );

                                // Add the computed vector to the list of vectors
                                this.repulsiveForceFromPassengers.add(passengerRepulsiveForce);

                                // Also, check this passenger's state
                                // If this passenger is queueing, set the relevant variable - it will stay true even if just
                                // one nearby passenger has activated it
                                if (!hasEncounteredQueueingPassengerInLoop) {
                                    // Check if the other passenger is in a queueing or assembling with the same goal as
                                    // this passenger
                                    if (this.passengerFollowedWhenAssembling == null) {
                                        this.hasEncounteredPassengerToFollow = false;
                                    } else {
                                        if (this.passengerFollowedWhenAssembling.equals(otherPassenger)) {
                                            // If the other passenger encountered is already assembling, decide whether this
                                            // passenger will assemble too depending on whether the other passenger was selected
                                            // to be followed by this one
                                            this.hasEncounteredPassengerToFollow
                                                    = (otherPassenger.getPassengerMovement().getAction() == Action.ASSEMBLING
                                                    || otherPassenger.getPassengerMovement().getAction() == Action.QUEUEING)
                                                    && otherPassenger.getPassengerMovement().getGoalAmenity().equals(this.goalAmenity);
                                        } else {
                                            this.hasEncounteredPassengerToFollow = false;
                                        }
                                    }
                                }

                                // If a queueing passenger has been encountered, do not pathfind anymore for for this
                                // goal
                                if (
                                        this.parent.getTicketType() == TicketBooth.TicketType.STORED_VALUE
                                                && this.hasEncounteredPassengerToFollow
                                ) {
                                    this.hasPathfound = true;
                                }

                                hasEncounteredQueueingPassengerInLoop
                                        = this.hasEncounteredPassengerToFollow;

                                passengersProcessed++;
                            }
                        }
                    }
                }

                // Get the attractive force of this passenger to the new position
                this.attractiveForce = this.computeAttractiveForce(
                        new Coordinates(this.position),
                        this.proposedHeading,
                        proposedNewPosition,
                        this.preferredWalkingDistance
                );

                vectorsToAdd.add(attractiveForce);
            }
        } else {
            double newHeading;

            if (this.currentPatch.getAmenityBlock() != null && this.currentPatch.getAmenityBlock().getParent() instanceof Turnstile) {
                // First, get the apex of the floor field with the state of the passenger
                Turnstile turnstile = (Turnstile) this.currentPatch.getAmenityBlock().getParent();

                QueueingFloorField.FloorFieldState floorFieldState;
                Patch apexLocation;

                if (this.disposition == Disposition.BOARDING) {
                    floorFieldState = turnstile.getTurnstileFloorFieldStateBoarding();
                } else {
                    floorFieldState = turnstile.getTurnstileFloorFieldStateAlighting();
                }

                apexLocation = turnstile.getQueueObject().getFloorFields().get(floorFieldState).getApices().get(0);

                // Then compute the heading from the apex to the turnstile attractor
                newHeading = Coordinates.headingTowards(
                        apexLocation.getPatchCenterCoordinates(),
                        turnstile.getAttractors().get(0).getPatch().getPatchCenterCoordinates()
                );
            } else {
                newHeading = this.previousHeading;
            }

            // Compute for the proposed future position
            proposedNewPosition = this.getFuturePosition(
                    this.position,
                    newHeading,
                    this.preferredWalkingDistance
            );

            this.hasEncounteredPassengerToFollow = this.passengerFollowedWhenAssembling != null;

            // Get the attractive force of this passenger to the new position
            this.attractiveForce = this.computeAttractiveForce(
                    new Coordinates(this.position),
                    newHeading,
                    proposedNewPosition,
                    this.preferredWalkingDistance
            );

            vectorsToAdd.add(attractiveForce);

            // Do not automatically (without influence from social forces of surrounding passengers and obstacles step
            // forward again for now
            this.shouldStepForward = false;
        }

        // Here ends the few ticks of grace period for the passenger to leave its starting patch
        if (
                !this.willPathfind
                        && !this.hasPathfound
                        && this.parent.getTicketType() == TicketBooth.TicketType.STORED_VALUE
                        && !hasJustLeftGoal()
        ) {
            this.beginStoredValuePathfinding();
        }

        // Take note of the previous walking distance of this passenger
        double previousWalkingDistance = this.currentWalkingDistance;

        vectorsToAdd.addAll(this.repulsiveForceFromPassengers);

        // Then compute the partial motivation force of the passenger
        Vector partialMotivationForce = Vector.computeResultantVector(
                new Coordinates(this.position),
                vectorsToAdd
        );

        // If the resultant vector is null (i.e., no change in position), simply don't move at all
        if (partialMotivationForce != null) {
            // The distance by which the repulsion starts to kick in will depend on the density of the passenger's
            // surroundings
            final int minimumObstacleCount = 1;
            final double maximumDistance = 2.0;
            final int maximumObstacleCount = 2;
            final double minimumDistance = 0.7;

            final int maximumObstacleCountTolerated = 2;

            double computedMaximumDistance = computeMaximumRepulsionDistance(
                    numberOfObstacles,
                    maximumObstacleCountTolerated,
                    minimumObstacleCount,
                    maximumDistance,
                    maximumObstacleCount,
                    minimumDistance
            );

            // Only apply the social forces on a set number of obstacles
            int obstaclesProcessed = 0;
            final int obstaclesProcessedLimit = 5;

            for (Map.Entry<Double, Amenity.AmenityBlock> obstacleEntry : obstaclesEncountered.entrySet()) {
                if (obstaclesProcessed == obstaclesProcessedLimit) {
                    break;
                }

                this.repulsiveForcesFromObstacles.add(
                        computeSocialForceFromObstacle(
                                obstacleEntry.getValue(),
                                obstacleEntry.getKey(),
                                computedMaximumDistance,
                                minimumObstacleStopDistance,
                                partialMotivationForce.getMagnitude()
                        )
                );

                obstaclesProcessed++;
            }

            vectorsToAdd.clear();

            vectorsToAdd.add(partialMotivationForce);
            vectorsToAdd.addAll(this.repulsiveForcesFromObstacles);

            // Finally, compute the final motivation force
            this.motivationForce = Vector.computeResultantVector(
                    new Coordinates(this.position),
                    vectorsToAdd
            );

            if (this.motivationForce != null) {
                // Cap the magnitude of the motivation force to the passenger's preferred walking distance
                if (this.motivationForce.getMagnitude() > this.preferredWalkingDistance) {
                    this.motivationForce.adjustMagnitude(this.preferredWalkingDistance);
                }

                // Then adjust its heading with minor stochastic deviations
                this.motivationForce.adjustHeading(
                        this.motivationForce.getHeading()
                                + Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * Math.toRadians(5)
                );

                try {
                    // Set the new heading
                    double newHeading = motivationForce.getHeading();

                    Coordinates candidatePosition = this.motivationForce.getFuturePosition();

                    if (hasClearLineOfSight(this.position, candidatePosition, false)) {
                        this.move(candidatePosition);
                    } else {
                        double revisedHeading;
                        Coordinates newFuturePosition;

                        int attempts = 0;
                        final int attemptLimit = 2;

                        boolean freeSpaceFound;

                        do {
//                            System.out.println(this.parent.getIdentifier() + " activated");

                            // Go back with the same magnitude as the original motivation force, but at a different
                            // heading
                            revisedHeading
                                    = (motivationForce.getHeading() + Math.toRadians(180)) % Math.toRadians(360);

                            // Add some stochasticity to this revised heading
                            revisedHeading += Simulator.RANDOM_NUMBER_GENERATOR.nextGaussian() * Math.toRadians(90);
                            revisedHeading %= Math.toRadians(360);

                            // Then calculate the future position from the current position
                            newFuturePosition = this.getFuturePosition(
                                    this.position,
                                    revisedHeading,
                                    this.preferredWalkingDistance * 0.25
                            );

                            freeSpaceFound = hasClearLineOfSight(this.position, newFuturePosition, false);

                            attempts++;
                        } while (attempts < attemptLimit && !freeSpaceFound);

                        // If all the attempts are used and no free space has been found, don't move at all
                        if (attempts != attemptLimit || freeSpaceFound) {
                            this.move(newFuturePosition);
                        }
                    }

                    if (
                            !this.isStuck
                                    || Coordinates.headingDifference(
                                    this.heading,
                                    newHeading
                            ) <= Math.toDegrees(90.0)
                                    || this.currentWalkingDistance > noMovementThreshold
                    ) {
//                         Take note of the new heading
                        this.heading = newHeading;
                    }

                    // Also take note of the new speed
                    this.currentWalkingDistance = motivationForce.getMagnitude();

                    // Finally, take note of the distance travelled by this passenger
                    this.distanceMovedInTick = motivationForce.getMagnitude();

                    // If this passenger's distance covered falls under the threshold, increment the counter denoting the ticks
                    // spent not moving
                    // Otherwise, reset the counter
                    // Do not not count for movements/non-movements when the passenger is in the "in queue" state
                    if (this.state != State.IN_QUEUE) {
                        if (this.recentPatches.size() <= noNewPatchesSeenThreshold) {
                            this.noNewPatchesSeenCounter++;
                            this.newPatchesSeenCounter = 0;
                        } else {
                            this.noNewPatchesSeenCounter = 0;
                            this.newPatchesSeenCounter++;
                        }
                    } else {
                        if (
                                this.distanceMovedInTick < noMovementThreshold
                        ) {
                            this.noMovementCounter++;
                            this.movementCounter = 0;
                        } else {
                            this.noMovementCounter = 0;
                            this.movementCounter++;
                        }
                    }

                    // If the passenger has moved above the no-movement threshold for at least this number of ticks,
                    // remove the passenger from its stuck state
                    if (
                            this.isStuck
                                    && (
                                    (
                                            this.state == State.IN_QUEUE
                                                    && this.movementCounter >= unstuckTicksThreshold
                                                    || this.state != State.IN_QUEUE
                                                    && this.newPatchesSeenCounter >= unstuckTicksThreshold/*
                                            || this.passengerFollowedWhenAssembling != null*/
                                    )
                            )
                    ) {
                        this.isReadyToFree = true;
                    }

/*                    if (this.isStuck && !((this.goalAttractor.getPatch().getPassengers().isEmpty() && (this.isAtQueueFront() || this.isServicedByGoal())) && this.noMovementCounter > noMovementTicksThreshold)) {
                        this.isReadyToFree = true;
                    }*/

                    this.timeSinceLeftPreviousGoal++;

                    // Check if the passenger has slowed down since the last tick
                    // If it did, reset the time spent accelerating counter
                    if (this.currentWalkingDistance < previousWalkingDistance) {
                        this.ticksAcceleratedOrMaintainedSpeed = 0;
                    } else {
                        this.ticksAcceleratedOrMaintainedSpeed++;
                    }

                    return true;
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
        }

        // If it reaches this point, there is no movement to be made
        this.hasEncounteredPassengerToFollow = this.passengerFollowedWhenAssembling != null;

        this.stop();

        // There was no movement by this passenger, so increment the pertinent counter
        this.distanceMovedInTick = 0.0;

        this.noMovementCounter++;
        this.movementCounter = 0;

        this.timeSinceLeftPreviousGoal++;

        this.ticksAcceleratedOrMaintainedSpeed = 0;

        return false;
    }

    private Vector computeAttractiveForce(
            final Coordinates startingPosition,
            final double proposedHeading,
            final Coordinates proposedNewPosition,
            final double preferredWalkingDistance
    ) {
        // Compute for the attractive force
        Vector attractiveForce = new Vector(
                startingPosition,
                proposedHeading,
                proposedNewPosition,
                preferredWalkingDistance
        );

        return attractiveForce;
    }

    private double computeMaximumRepulsionDistance(
            int objectCount,
            final int maximumObjectCountTolerated,
            final int minimumObjectCount,
            final double maximumDistance,
            final int maximumObjectCount,
            final double minimumDistance
    ) {
        if (objectCount > maximumObjectCountTolerated) {
            objectCount = maximumObjectCountTolerated;
        }

        final double a = (maximumDistance - minimumDistance) / (minimumObjectCount - maximumDistance);
        final double b = minimumDistance - a * maximumObjectCount;

        return a * objectCount + b;
    }

    private double computeRepulsionMagnitudeFactor(
            final double distance,
            final double maximumDistance,
            final double minimumRepulsionFactor,
            final double minimumDistance,
            final double maximumRepulsionFactor
    ) {
        // Formula: for the inverse square law equation y = a / x ^ 2 + b,
        // a = (d_max ^ 2 * (r_min * d_max ^ 2 - r_min * d_min ^ 2 + r_max ^ 2 * d_min ^ 2)) / (d_max ^ 2 - d_min ^ 2)
        // and
        // b = -((r_max ^ 2 * d_min ^ 2) / (d_max ^ 2 - d_min ^ 2))
        double differenceOfSquaredDistances = Math.pow(maximumDistance, 2.0) - Math.pow(minimumDistance, 2.0);
        double productOfMaximumRepulsionAndMinimumDistance
                = Math.pow(maximumRepulsionFactor, 2.0) * Math.pow(minimumDistance, 2.0);

        double a
                = (
                Math.pow(maximumDistance, 2.0) * (minimumRepulsionFactor * Math.pow(maximumDistance, 2.0)
                        - minimumRepulsionFactor * Math.pow(minimumDistance, 2.0)
                        + productOfMaximumRepulsionAndMinimumDistance
                )) / differenceOfSquaredDistances;

        double b = -(productOfMaximumRepulsionAndMinimumDistance / differenceOfSquaredDistances);

        double repulsion = a / Math.pow(distance, 2.0) + b;

        // The repulsion value should always be greater or equal to zero
        if (repulsion <= 0.0) {
            repulsion = 0.0;
        }

        return repulsion;
    }

    private Vector computeSocialForceFromPassenger(
            Passenger passenger,
            final double distanceToOtherPassenger,
            final double maximumDistance,
            final double minimumDistance,
            final double maximumMagnitude
    ) {
        final double maximumRepulsionFactor = 1.0;
        final double minimumRepulsionFactor = 0.0;

        Coordinates passengerPosition = passenger.getPassengerMovement().getPosition();

        // If this passenger is closer than the minimum distance specified, apply a force as if the distance is just at
        // that minimum
        double modifiedDistanceToObstacle = Math.max(distanceToOtherPassenger, minimumDistance);

        double repulsionMagnitudeCoefficient;
        double repulsionMagnitude;

        repulsionMagnitudeCoefficient = computeRepulsionMagnitudeFactor(
                modifiedDistanceToObstacle,
                maximumDistance,
                minimumRepulsionFactor,
                minimumDistance,
                maximumRepulsionFactor
        );

        repulsionMagnitude = repulsionMagnitudeCoefficient * maximumMagnitude;

        // If a passenger is stuck, do not exert much force from this passenger
        if (this.isStuck) {
            final double factor = 0.01;

            repulsionMagnitude -= this.stuckCounter * factor;

            if (repulsionMagnitude <= 0.0001 * this.preferredWalkingDistance) {
                repulsionMagnitude = 0.0001 * this.preferredWalkingDistance;
            }
        }

        // Then compute the heading from that other passenger to this passenger
        double headingFromOtherPassenger = Coordinates.headingTowards(
                passengerPosition,
                this.position
        );

        // Then compute for a future position given the other passenger's position, the heading, and the
        // magnitude
        // This will be used as the endpoint of the repulsion vector from this obstacle
        Coordinates passengerRepulsionVectorFuturePosition = this.getFuturePosition(
                passengerPosition,
                headingFromOtherPassenger,
                repulsionMagnitude
        );

        // Finally, given the current position, heading, and future position, create the vector from
        // the other passenger to the current passenger
        return new Vector(
                passengerPosition,
                headingFromOtherPassenger,
                passengerRepulsionVectorFuturePosition,
                repulsionMagnitude
        );
    }

    private Vector computeSocialForceFromObstacle(
            Amenity.AmenityBlock amenityBlock,
            final double distanceToObstacle,
            final double maximumDistance,
            double minimumDistance,
            final double maximumMagnitude
    ) {
        final double maximumRepulsionFactor = 1.0;
        final double minimumRepulsionFactor = 0.0;

        Coordinates repulsionVectorStartingPosition = amenityBlock.getPatch().getPatchCenterCoordinates();

        // If this passenger is closer than the minimum distance specified, apply a force as if the distance is just at
        // that minimum
        double modifiedDistanceToObstacle = Math.max(distanceToObstacle, minimumDistance);

        double repulsionMagnitudeCoefficient;
        double repulsionMagnitude;

        repulsionMagnitudeCoefficient = computeRepulsionMagnitudeFactor(
                modifiedDistanceToObstacle,
                maximumDistance,
                minimumRepulsionFactor,
                minimumDistance,
                maximumRepulsionFactor
        );

        repulsionMagnitude = repulsionMagnitudeCoefficient * maximumMagnitude;

        // If a passenger is stuck, do not exert much force from this obstacle
        if (this.isStuck) {
            final double factor = 0.01;

            repulsionMagnitude -= this.stuckCounter * factor;

            if (repulsionMagnitude <= 0.0001 * this.preferredWalkingDistance) {
                repulsionMagnitude = 0.0001 * this.preferredWalkingDistance;
            }
        }

/*        // Get the potential origins of the two repulsion vectors
        Coordinates xAxisOrigin
                = new Coordinates(
                this.position.getX(),
                amenityBlock.getPatch().getPatchCenterCoordinates().getY()
        );

        Coordinates yAxisOrigin
                = new Coordinates(amenityBlock.getPatch().getPatchCenterCoordinates().getX(), this.position.getY());

        // Get the distances between these origins and this passenger's position
        double xAxisOriginDistance = Math.abs(xAxisOrigin.getY() - this.position.getY());
        double yAxisOriginDistance = Math.abs(yAxisOrigin.getX() - this.position.getX());

        // Get whichever is the larger of these two distances - this will be the starting position of the vector
        Coordinates repulsionVectorStartingPosition;

        if (xAxisOriginDistance >= yAxisOriginDistance) {
            repulsionVectorStartingPosition = xAxisOrigin;
        } else {
            repulsionVectorStartingPosition = yAxisOrigin;
        }*/

        // Compute the heading from that origin point to this passenger
        double headingFromOtherObstacle = Coordinates.headingTowards(
                repulsionVectorStartingPosition,
                this.position
        );

        // Then compute for a future position given the obstacle's position, the heading, and the
        // magnitude
        // This will be used as the endpoint of the repulsion vector from this obstacle
        Coordinates obstacleRepulsionVectorFuturePosition = this.getFuturePosition(
                repulsionVectorStartingPosition,
                headingFromOtherObstacle,
                repulsionMagnitude
        );

        // Finally, given the current position, heading, and future position, create the vector from
        // the obstacle to the current passenger
        return new Vector(
                repulsionVectorStartingPosition,
                headingFromOtherObstacle,
                obstacleRepulsionVectorFuturePosition,
                repulsionMagnitude
        );
    }

    // Make the passenger move given a walking distance
    private void move(double walkingDistance) {
        this.setPosition(this.getFuturePosition(walkingDistance));
    }

    // Make the passenger move given the future position
    private void move(Coordinates futurePosition) {
        this.setPosition(futurePosition);
    }

    // Check if this passenger has reached its goal's queueing floor field
    public boolean hasReachedQueueingFloorField() {
        for (Patch patch : this.goalFloorField.getAssociatedPatches()) {
            if (isOnOrCloseToPatch(patch)) {
                return true;
            }
        }

        return false;
    }

    // Check if this passenger has a path to follow
    public boolean hasPath() {
        return this.currentPath != null;
    }

    // Check if this passenger is on the next patch of its path
    public boolean hasReachedNextPatchInPath() {
        return isOnOrCloseToPatch(this.currentPath.getPath().peek());
    }

    // Register this passenger to its queueable goal's queue
    public void joinQueue() {
        this.goalQueueObject.getPassengersQueueing().addLast(this.parent);
    }

    // Have the passenger stop
    public void stop() {
        this.currentWalkingDistance = 0.0;
    }

    // Unregister this passenger to its queueable goal's queue
    public void leaveQueue() {
        this.goalQueueObject.getPassengersQueueing().remove(this.parent);
    }

    // Check if this passenger has reached an apex of its floor field
    public boolean hasReachedQueueingFloorFieldApex() {
        // If the passenger is in any of this floor field's apices, return true
        for (Patch apex : this.goalFloorField.getApices()) {
            if (isOnOrCloseToPatch(apex)) {
                return true;
            }
        }

        return false;
    }

    // Have this passenger start waiting for an amenity to become vacant
    public void beginWaitingOnAmenity() {
        this.isWaitingOnAmenity = true;
    }

    // Check if the goal of this passenger is currently not servicing anyone
    public boolean isGoalFree() {
        return this.goalQueueObject.getPassengerServiced() == null;
    }

    // Check if this passenger the one currently served by its goal
    public boolean isServicedByGoal() {
        Passenger passengerServiced = this.goalQueueObject.getPassengerServiced();

        return passengerServiced != null && passengerServiced.equals(this.parent);
    }

    // Check if this passenger is at the front of the queue
    public boolean isAtQueueFront() {
        LinkedList<Passenger> passengersQueueing
                = this.goalQueueObject.getPassengersQueueing();

        if (passengersQueueing.isEmpty()) {
            return false;
        }

        return passengersQueueing.getFirst() == this.parent;
    }

    // Have this passenger stop waiting for an amenity to become vacant
    public void endWaitingOnAmenity() {
        this.isWaitingOnAmenity = false;
    }

    // Enable pathfinding for stored value card passengers
    public void beginStoredValuePathfinding() {
        this.willPathfind = true;
    }

    // Disable pathfinding for stored value card passengers
    public void endStoredValuePathfinding() {
        this.currentPath = null;

        this.willPathfind = false;
        this.hasPathfound = true;
    }

    // Check if this passenger has reached its goal
    public boolean hasReachedGoal() {
        // If the passenger is still waiting for an amenity to be vacant, it hasn't reached the goal yet
        if (this.isWaitingOnAmenity) {
            return false;
        }

        return isOnOrCloseToPatch(this.goalAttractor.getPatch());
    }

    // Set the passenger's current amenity and position as it reaches the next goal
    public void reachGoal() {
        // Just in case the passenger isn't actually on its goal, but is adequately close to it, just move the passenger
        // there
        // Make sure to offset the passenger from the center a little so a force will be applied to this passenger
        Coordinates patchCenter = this.goalAttractor.getPatch().getPatchCenterCoordinates();
        Coordinates offsetPatchCenter = this.getFuturePosition(
                patchCenter,
                this.previousHeading,
                Patch.PATCH_SIZE_IN_SQUARE_METERS * 0.1
        );

        this.setPosition(offsetPatchCenter);

        this.currentAmenity = this.goalAmenity;
    }

    // Set the passenger's next patch in its current path as it reaches it
    public void reachPatchInPath() {
        this.currentPath.getPath().pop();
    }

    // Have this passenger's goal service this passenger
    public void beginServicingThisPassenger() {
        // This passenger will now be the one to be served next
        this.goalQueueObject.setPassengerServiced(this.parent);
    }

    // Have this passenger's goal finish serving this passenger
    public void endServicingThisPassenger() {
        // This passenger is done being serviced by this goal
        this.goalQueueObject.setPassengerServiced(null);
    }

    // Check if this passenger has reached its final goal
    public boolean hasReachedFinalGoal() {
        return !this.routePlan.getCurrentRoutePlan().hasNext();
    }

    // Check if this passenger has reached the final patch in its current path
    public boolean hasPassengerReachedFinalPatchInPath() {
        return this.currentPath.getPath().isEmpty();
    }

    // Check if this passenger has reached the specified patch
    private boolean isOnPatch(Patch patch) {
        return ((int) (this.position.getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS)) == patch.getMatrixPosition().getColumn()
                && ((int) (this.position.getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS)) == patch.getMatrixPosition().getRow();
    }

    // Check if this passenger is adequately close enough to a patch
    // In this case, a passenger is close enough to a patch when the distance between this passenger and the patch is
    // less than the distance covered by the passenger per second
    private boolean isOnOrCloseToPatch(Patch patch) {
        return Coordinates.distance(this.position, patch.getPatchCenterCoordinates()) <= this.preferredWalkingDistance;
    }

    // Check if this passenger is allowed by its goal to pass
    public boolean isAllowedPass() {
        return this.getGoalAmenityAsGoal().allowPass();
    }

    // Check if this passenger will enter the train
    public boolean willEnterTrain() {
        TrainDoor closestTrainDoor = getGoalAmenityAsTrainDoor();

        if (closestTrainDoor != null) {
            return isTrainDoorOpen(closestTrainDoor);
        } else {
            return false;
        }
    }

    // Check whether this passenger's goal as a train door is open
    private boolean isTrainDoorOpen(TrainDoor trainDoor) {
        return trainDoor.isOpen();
    }

    // Despawn this passenger
    public void despawnPassenger() {
        // Remove the passenger from its patch
        this.currentPatch.getPassengers().remove(this.parent);

        // Remove this passenger from this floor
        this.currentFloor.getPassengersInFloor().remove(this.parent);

        // Remove this passenger from this station
        this.currentFloor.getStation().getPassengersInStation().remove(this.parent);

        // Remove this passenger from its current floor's patch set, if necessary
        SortedSet<Patch> currentPatchSet = this.currentPatch.getFloor().getPassengerPatchSet();

        if (
                currentPatchSet.contains(this.currentPatch)
                        && this.currentPatch.getPassengers().isEmpty()
        ) {
            currentPatchSet.remove(this.currentPatch);
        }
    }

    // Have the passenger face its current goal, or its queueing area, or the passenger at the end of the queue
    public void faceNextPosition() {
        double newHeading;
        boolean willFaceQueueingPatch;
        Patch proposedGoalPatch;

        // iI the passenger is already heading for a queueable, no need to seek its floor fields again, as
        // it has already done so, and is now just heading to the goal itself
        // If it has floor fields, get the heading towards the nearest floor field value
        // If it doesn't have floor fields, just get the heading towards the goal itself
        if (this.action != Action.HEADING_TO_QUEUEABLE && this.goalAmenity instanceof Queueable) {
            // If a queueing patch has not yet been set for this goal, set it
            if (this.goalNearestQueueingPatch == null) {
                // If the next floor field has not yet been set for this queueing patch, set it
                if (this.goalFloorFieldState == null && this.goalFloorField == null) {
                    Queueable queueable = this.getGoalAmenityAsQueueable();

                    if (queueable instanceof TrainDoor) {
                        TrainDoor trainDoor = (TrainDoor) queueable;

                        this.goalFloorFieldState = new PlatformFloorField.PlatformFloorFieldState(
                                this.disposition,
                                State.IN_QUEUE,
                                this.getGoalAmenityAsQueueable(),
                                this.goalTrainDoorEntranceLocation
                        );

                        this.goalFloorField = queueable.retrieveFloorField(
                                this.goalQueueObject,
                                this.goalFloorFieldState
                        );
                    } else {
                        this.goalFloorFieldState = new QueueingFloorField.FloorFieldState(
                                this.disposition,
                                State.IN_QUEUE,
                                this.getGoalAmenityAsQueueable()
                        );

                        this.goalFloorField = queueable.retrieveFloorField(
                                queueable.getQueueObject(),
                                this.goalFloorFieldState
                        );
                    }
                }

                this.goalNearestQueueingPatch = this.getPatchWithNearestFloorFieldValue();
                proposedGoalPatch = this.goalNearestQueueingPatch;
            }

            // If this passenger is in the "will queue" state, choose between facing the queueing patch, and facing the
            // passenger at the back of the queue
            if (action == Action.WILL_QUEUE || action == Action.ASSEMBLING) {
                LinkedList<Passenger> passengerQueue
                        = this.goalQueueObject.getPassengersQueueing();

                // Check whether there are passengers queueing for the goal
                if (passengerQueue.isEmpty()) {
                    // If there are no passengers queueing yet, simply compute the heading towards the nearest queueing
                    // patch
                    this.passengerFollowedWhenAssembling = null;
                    this.goalNearestQueueingPatch = this.getPatchWithNearestFloorFieldValue();
                    proposedGoalPatch = this.goalNearestQueueingPatch;

                    willFaceQueueingPatch = true;
                } else {
                    Passenger passengerFollowedCandidate;

                    // If there are passengers queueing, join the queue and follow either the last person in the queue
                    // or the person before this
                    if (action == Action.WILL_QUEUE) {
                        passengerFollowedCandidate = passengerQueue.getLast();
                    } else {
                        int passengerFollowedCandidateIndex = passengerQueue.indexOf(this.parent) - 1;

                        if (passengerFollowedCandidateIndex >= 0) {
                            passengerFollowedCandidate
                                    = passengerQueue.get(passengerFollowedCandidateIndex);
                        } else {
                            passengerFollowedCandidate = null;
                        }
                    }

                    // But if the person to be followed is this person itself, or is not assembling, or follows this
                    // person too (forming a cycle), disregard it, and just follow the queueing patch
                    // Otherwise, follow that passenger
                    if (
                            passengerFollowedCandidate == null
                                    || passengerFollowedCandidate.equals(this.parent)
                                    || !passengerFollowedCandidate.equals(this.parent)
                                    && passengerFollowedCandidate.getPassengerMovement()
                                    .getPassengerFollowedWhenAssembling() != null
                                    && passengerFollowedCandidate.getPassengerMovement()
                                    .getPassengerFollowedWhenAssembling().equals(this.parent)
                    ) {
                        this.passengerFollowedWhenAssembling = null;
                        this.goalNearestQueueingPatch = this.getPatchWithNearestFloorFieldValue();
                        proposedGoalPatch = this.goalNearestQueueingPatch;

                        willFaceQueueingPatch = true;
                    } else {
                        // But only follow passengers who are nearer to this passenger than to the chosen queueing
                        // patch and are within this passenger's walking distance and have a clear line of sight to
                        // this passenger
                        if (
                                !hasClearLineOfSight(this.position, passengerFollowedCandidate.getPassengerMovement().getPosition(), true)
                        ) {
                            this.passengerFollowedWhenAssembling = null;
                            this.goalNearestQueueingPatch = this.getPatchWithNearestFloorFieldValue();
                            proposedGoalPatch = this.goalNearestQueueingPatch;

                            willFaceQueueingPatch = true;
                        } else {
                            this.passengerFollowedWhenAssembling = passengerFollowedCandidate;
                            proposedGoalPatch = this.goalNearestQueueingPatch;

                            willFaceQueueingPatch = false;
                        }
                    }
                }
            } else {
                this.passengerFollowedWhenAssembling = null;
                proposedGoalPatch = this.goalNearestQueueingPatch;

                willFaceQueueingPatch = true;
            }

            if (willFaceQueueingPatch) {
                newHeading = Coordinates.headingTowards(
                        this.position,
                        this.goalNearestQueueingPatch.getPatchCenterCoordinates()
                );
            } else {
                // Get the distance from here to both the proposed passenger followed and the nearest queueing
                // patch
                double distanceToPassenger = Coordinates.distance(
                        this.position,
                        this.passengerFollowedWhenAssembling.getPassengerMovement().getPosition()
                );

                double distanceToQueueingPatch = Coordinates.distance(
                        this.position,
                        this.goalNearestQueueingPatch.getPatchCenterCoordinates()
                );

                // Head towards whoever is nearer
                if (distanceToPassenger > distanceToQueueingPatch) {
                    newHeading = Coordinates.headingTowards(
                            this.position,
                            this.goalNearestQueueingPatch.getPatchCenterCoordinates()
                    );
                } else {
                    newHeading = Coordinates.headingTowards(
                            this.position,
                            this.passengerFollowedWhenAssembling.getPassengerMovement().getPosition()
                    );
                }
            }
        } else {
            proposedGoalPatch = this.goalAttractor.getPatch();

            // Compute the heading towards the goal's attractor
            newHeading = Coordinates.headingTowards(
                    this.position,
                    this.goalAttractor.getPatch().getPatchCenterCoordinates()
            );
        }
//        }

        if (this.willPathfind || this.action == Action.REROUTING) {
            // Get the heading towards the goal patch, which was set as the next patch in the path
            newHeading = Coordinates.headingTowards(
                    this.position,
                    this.goalPatch.getPatchCenterCoordinates()
            );

//            this.proposedHeading = newHeading;
        } else {
            this.goalPatch = proposedGoalPatch;
        }

        // Then set the passenger's proposed heading to it
        this.proposedHeading = newHeading;
    }

    // While the passenger is already on a floor field, have the passenger face the one with the highest value
    public void chooseBestQueueingPatch() {
        // Retrieve the patch with the highest floor field value around the passenger's vicinity
        this.goalNearestQueueingPatch = this.getBestQueueingPatch();
        this.goalPatch = this.goalNearestQueueingPatch;
    }

    // If the passenger is following a path, have the passenger face the next one, if any
    public boolean chooseNextPatchInPath() {
        // Generate a path, if one hasn't been generated yet
        if (this.currentPath == null) {
            // Head towards the queue of the goal
            LinkedList<Passenger> passengersQueueing
                    = this.goalQueueObject.getPassengersQueueing();

            // If there are no passengers in that queue at all, simply head for the goal patch
            if (passengersQueueing.isEmpty()) {
                this.currentPath = computePath(
                        this.currentPatch,
                        this.goalPatch,
                        true,
                        true
                );
            } else {
                // If there are passengers in the queue, this passenger should only follow the last passenger in that
                // queue if that passenger is assembling
                // If the last passenger is not assembling, simply head for the goal patch instead
                Passenger lastPassenger = passengersQueueing.getLast();

                if (lastPassenger.getPassengerMovement().getAction() == Action.ASSEMBLING) {
                    this.currentPath = computePath(
                            this.currentPatch,
                            lastPassenger.getPassengerMovement().getCurrentPatch(),
                            true,
                            true
                    );
                } else {
                    this.currentPath = computePath(
                            this.currentPatch,
                            this.goalPatch,
                            true,
                            true
                    );
                }
            }
        }

        // Get the first patch still unvisited in the path
        if (this.currentPath == null || this.currentPath.getPath().isEmpty()) {
            return false;
        }

        this.goalPatch = this.currentPath.getPath().peek();

        return true;
    }

    // Make this passenger free from being stuck
    public void free() {
        this.isStuck = false;

        this.stuckCounter = 0;
        this.noMovementCounter = 0;
        this.noNewPatchesSeenCounter = 0;

        this.currentPath = null;

        this.isReadyToFree = false;
    }

    // From a set of patches associated with a goal's floor field, get the nearest patch below a threshold
    public Patch getPatchWithNearestFloorFieldValue() {
        final double maximumFloorFieldValueThreshold = 0.8;

        // Get the patches associated with the current goal
        List<Patch> associatedPatches = this.goalFloorField.getAssociatedPatches();

        double minimumDistance = Double.MAX_VALUE;
        Patch nearestPatch = null;

        // Look for the nearest patch from the patches associated with the floor field
        double distanceFromPassenger;

        for (Patch patch : associatedPatches) {
            double floorFieldValue
                    = patch.getFloorFieldValues().get(this.getGoalAmenityAsQueueable()).get(this.goalFloorFieldState);

//            if (floorFieldValue <= maximumFloorFieldValueThreshold) {
            // Get the distance of that patch from this passenger
            distanceFromPassenger = Coordinates.distance(this.position, patch.getPatchCenterCoordinates());

            if (distanceFromPassenger < minimumDistance) {
                minimumDistance = distanceFromPassenger;
                nearestPatch = patch;
            }
//            }
        }

        return nearestPatch;
    }

    // Get the next queueing patch in a floor field given the current floor field state
    private Patch getBestQueueingPatch() {
        // Get the patches to explore
        List<Patch> patchesToExplore
                = Floor.get7x7Field(this.currentPatch, this.proposedHeading, true, this.fieldOfViewAngle);

        this.toExplore = patchesToExplore;

        // Collect the patches with the highest floor field values
        List<Patch> highestPatches = new ArrayList<>();

        double maximumFloorFieldValue = 0.0;

        for (Patch patch : patchesToExplore) {
            Map<QueueingFloorField.FloorFieldState, Double> floorFieldStateDoubleMap
                    = patch.getFloorFieldValues().get(this.getGoalAmenityAsQueueable());

            if (
                    !patch.getFloorFieldValues().isEmpty()
                            && floorFieldStateDoubleMap != null
                            && !floorFieldStateDoubleMap.isEmpty()
                            && floorFieldStateDoubleMap.get(
                            this.goalFloorFieldState
                    ) != null
            ) {
                double floorFieldValue = patch.getFloorFieldValues()
                        .get(this.getGoalAmenityAsQueueable())
                        .get(this.goalFloorFieldState);

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

        // If there are more than one highest valued-patches, choose the one where it would take the least heading
        // difference
        Patch chosenPatch = highestPatches.get(0)/* = null*/;

        List<Double> headingChanges = new ArrayList<>();
//        List<Double> distances = new ArrayList<>();

        double headingToHighestPatch;
        double headingChangeRequired;

//        double distance;

        for (Patch patch : highestPatches) {
            headingToHighestPatch = Coordinates.headingTowards(this.position, patch.getPatchCenterCoordinates());
            headingChangeRequired = Coordinates.headingDifference(this.proposedHeading, headingToHighestPatch);

            double headingChangeRequiredDegrees = Math.toDegrees(headingChangeRequired);

            headingChanges.add(headingChangeRequiredDegrees);

/*            distance = Coordinates.distance(this.position, patch.getPatchCenterCoordinates());

            distances.add(distance);*/
        }

        double minimumHeadingChange = Double.MAX_VALUE;

        for (int index = 0; index < highestPatches.size(); index++) {
//            double individualScore = headingChanges.get(index) * 1.0 + (distances.get(index) * 10.0) * 0.0;
            double individualScore = headingChanges.get(index);

            if (individualScore < minimumHeadingChange) {
                minimumHeadingChange = individualScore;
                chosenPatch = highestPatches.get(index);
            }
        }

        return chosenPatch;
    }

    // Check if the given patch has an obstacle
    private boolean hasObstacle(Patch patch) {
        Amenity.AmenityBlock amenityBlock = patch.getAmenityBlock();

        if (amenityBlock == null) {
            return false;
        } else {
            Amenity parent = amenityBlock.getParent();

            if (parent.equals(this.goalAmenity)) {
                return !amenityBlock.isAttractor();
            } else {
                if (parent instanceof Gate) {
                    return !amenityBlock.isAttractor();
                } else {
                    return true;
                }
            }
        }

/*        return amenityBlock != null
                && (
                !amenityBlock.getParent().equals(this.goalAmenity)
                        && (
                        !(amenityBlock.getParent() instanceof Gate)
                                || (amenityBlock.getParent() instanceof Gate) && !amenityBlock.isAttractor()
                )
        );*/

//        return amenityBlock != null && amenityBlock.getParent() instanceof Obstacle;
    }

    // Check if there is a clear line of sight from one point to another
    private boolean hasClearLineOfSight(
            Coordinates sourceCoordinates,
            Coordinates targetCoordinates,
            boolean includeStartingPatch
    ) {
        // First of all, check if the target has an obstacle
        // If it does, then no need to check what is between the two points
        if (hasObstacle(this.currentFloor.getPatch(targetCoordinates))) {
            return false;
        }

        final double resolution = 0.2;

        final double distanceToTargetCoordinates = Coordinates.distance(sourceCoordinates, targetCoordinates);
        final double headingToTargetCoordinates = Coordinates.headingTowards(sourceCoordinates, targetCoordinates);

        Patch startingPatch = this.currentFloor.getPatch(sourceCoordinates);

        Coordinates currentPosition = new Coordinates(sourceCoordinates);
        double distanceCovered = 0.0;

        // Keep looking for blocks while there is still distance to cover
        while (distanceCovered <= distanceToTargetCoordinates) {
            if (includeStartingPatch || !this.currentFloor.getPatch(currentPosition).equals(startingPatch)) {
                // Check if there is an obstacle in the current position
                // If there is, return early
                if (hasObstacle(this.currentFloor.getPatch(currentPosition))) {
                    return false;
                }
            }

            // If there isn't any, move towards the target coordinates with the given increment
            currentPosition = this.getFuturePosition(
                    currentPosition,
                    headingToTargetCoordinates,
                    resolution
            );

            distanceCovered += resolution;
        }

        // The target has been reached without finding an obstacle, so there is a clear line of sight between the two
        // given points
        return true;
    }

    // Update the passenger's recent patches
    private void updateRecentPatches(Patch currentPatch, final int timeElapsedExpiration) {
        List<Patch> patchesToForget = new ArrayList<>();

        // Update the time elapsed in all of the recent patches
        for (Map.Entry<Patch, Integer> recentPatchesAndTimeElapsed : this.recentPatches.entrySet()) {
            this.recentPatches.put(recentPatchesAndTimeElapsed.getKey(), recentPatchesAndTimeElapsed.getValue() + 1);

            // Remove all patches that are equal to the expiration time given
            if (recentPatchesAndTimeElapsed.getValue() == timeElapsedExpiration) {
                patchesToForget.add(recentPatchesAndTimeElapsed.getKey());
            }
        }

        // If there is a new patch to add or update to the recent patch list, do so
        if (currentPatch != null) {
            // The time lapsed value of any patch added or updated will always be zero, as it means this patch has been
            // recently encountered by this passenger
            this.recentPatches.put(currentPatch, 0);
        }

        // Remove all patches set to be forgotten
        for (Patch patchToForget : patchesToForget) {
            this.recentPatches.remove(patchToForget);
        }
    }

    public enum Disposition {
        BOARDING("Going to the train"),
        RIDING_TRAIN("Riding train"),
        ALIGHTING("Going out of the station");

        private final String name;

        Disposition(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum State {
        WALKING,
        IN_QUEUEABLE,
        IN_QUEUE,
        IN_NONQUEUEABLE,
        IN_TRAIN,
    }

    public enum Action {
        /* Walking actions */
        WILL_QUEUE,
        REROUTING,
        /* In queue actions */
        ASSEMBLING,
        QUEUEING,
        HEADING_TO_QUEUEABLE,
        /* In goal actions */
        SECURITY_CHECKING,
        TRANSACTING_TICKET,
        USING_TICKET,
        /* In gate */
        ASCENDING,
        DESCENDING,
        BOARDING_TRAIN,
        /* Train actions */
        RIDING_TRAIN,
        /* Final actions */
        EXITING_STATION
    }

    public enum MovementSignal {
        WALK_FREELY,
        QUEUE_FREELY,
        QUEUE_SLOWLY,
        HALT_PASSENGER,
        HALT_ZERO_FORCE,
    }
}
