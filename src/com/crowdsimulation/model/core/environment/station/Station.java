package com.crowdsimulation.model.core.environment.station;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.agent.passenger.movement.RoutePlan;
import com.crowdsimulation.model.core.agent.passenger.movement.pathfinding.DirectoryResult;
import com.crowdsimulation.model.core.agent.passenger.movement.pathfinding.MultipleFloorPassengerPath;
import com.crowdsimulation.model.core.environment.Environment;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.impenetrable.Track;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.NonObstacle;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.PortalShaft;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorShaft;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorShaft;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs.StairPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs.StairShaft;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Turnstile;
import com.crowdsimulation.model.simulator.cache.DistanceCache;
import com.crowdsimulation.model.simulator.cache.MultipleFloorPatchCache;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Station extends BaseStationObject implements Environment {
    // Default station name
    private static final String DEFAULT_STATION_NAME = "New station";

    // Contains binding values for the number of floors this station has
    // The name of the station
    private String name;

    // Denotes the dimensions of this station
    private final int rows;
    private final int columns;

    // Each station contains a list of floors
    private final List<Floor> floors;

    // And a list of portal shafts associated with this station
    // These lists belong here as portal shafts transcend single floors
    private final List<StairShaft> stairShafts;
    private final List<EscalatorShaft> escalatorShafts;
    private final List<ElevatorShaft> elevatorShafts;

    // Take note of which floor each amenities are located
    private final HashMap<Class<? extends Amenity>, HashSet<Floor>> amenityFloorIndex;

    // The list of passengers in this station
    private final CopyOnWriteArrayList<Passenger> passengersInStation;

    // Caches for optimized performance
    private final MultipleFloorPatchCache multipleFloorPatchCache;
    private final DistanceCache distanceCache;

    public Station(int rows, int columns) {
        this.name = DEFAULT_STATION_NAME;
        this.floors = Collections.synchronizedList(new ArrayList<>());

        this.rows = rows;
        this.columns = columns;

        this.stairShafts = Collections.synchronizedList(new ArrayList<>());
        this.escalatorShafts = Collections.synchronizedList(new ArrayList<>());
        this.elevatorShafts = Collections.synchronizedList(new ArrayList<>());

        this.amenityFloorIndex = new HashMap<>();

        this.passengersInStation = new CopyOnWriteArrayList<>();

        int multiFloorPathCacheCapacity = 50;
        this.multipleFloorPatchCache = new MultipleFloorPatchCache(multiFloorPathCacheCapacity);

        int distanceCacheCapacity = 50;
        this.distanceCache = new DistanceCache(distanceCacheCapacity);

        // Initially, the station has one floor
        Floor.addFloor(this, this.floors, 0, rows, columns);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public HashMap<Class<? extends Amenity>, HashSet<Floor>> getAmenityFloorIndex() {
        return amenityFloorIndex;
    }

    public List<StairShaft> getStairShafts() {
        return stairShafts;
    }

    public List<EscalatorShaft> getEscalatorShafts() {
        return escalatorShafts;
    }

    public List<ElevatorShaft> getElevatorShafts() {
        return elevatorShafts;
    }

    public CopyOnWriteArrayList<Passenger> getPassengersInStation() {
        return passengersInStation;
    }

    public MultipleFloorPatchCache getMultiFloorPathCache() {
        return multipleFloorPatchCache;
    }

    public DistanceCache getDistanceCache() {
        return distanceCache;
    }

    // Assemble this station's amenity-floor index
    public static void assembleAmenityFloorIndex(Station station) {
        station.amenityFloorIndex.clear();

        station.amenityFloorIndex.put(StationGate.class, new HashSet<>());
        station.amenityFloorIndex.put(Security.class, new HashSet<>());
        station.amenityFloorIndex.put(TicketBooth.class, new HashSet<>());
        station.amenityFloorIndex.put(Turnstile.class, new HashSet<>());
        station.amenityFloorIndex.put(TrainDoor.class, new HashSet<>());

        for (Floor floor : station.floors) {
            if (!floor.getStationGates().isEmpty()) {
                for (StationGate stationGate : floor.getStationGates()) {
                    station.amenityFloorIndex.get(StationGate.class).add(
                            stationGate.getAmenityBlocks().get(0).getPatch().getFloor()
                    );
                }
            }

            if (!floor.getSecurities().isEmpty()) {
                for (Security security : floor.getSecurities()) {
                    station.amenityFloorIndex.get(Security.class).add(
                            security.getAmenityBlocks().get(0).getPatch().getFloor()
                    );
                }
            }

            if (!floor.getTicketBooths().isEmpty()) {
                for (TicketBooth ticketBooth : floor.getTicketBooths()) {
                    station.amenityFloorIndex.get(TicketBooth.class).add(
                            ticketBooth.getAmenityBlocks().get(0).getPatch().getFloor()
                    );
                }
            }

            if (!floor.getTurnstiles().isEmpty()) {
                for (Turnstile turnstile : floor.getTurnstiles()) {
                    station.amenityFloorIndex.get(Turnstile.class).add(
                            turnstile.getAmenityBlocks().get(0).getPatch().getFloor()
                    );
                }
            }

            if (!floor.getTrainDoors().isEmpty()) {
                for (TrainDoor trainDoor : floor.getTrainDoors()) {
                    station.amenityFloorIndex.get(TrainDoor.class).add(
                            trainDoor.getAmenityBlocks().get(0).getPatch().getFloor()
                    );
                }
            }
        }
    }

    // Validate both the station layout and its floor fields as one
    public static boolean validateStation(Station station) {
        return validateStationShallowly(station) && validateFloorFieldsInStation(station);
    }

    // Validate the floor fields of each amenity in the station
    public static boolean validateFloorFieldsInStation(Station station) {
        // For each floor in the station, validate the amenities in there which have floor fields
        List<Security> securities;
        List<TicketBooth> ticketBooths;
        List<Turnstile> turnstiles;
        List<TrainDoor> trainDoors;

        for (Floor floor : station.getFloors()) {
            securities = floor.getSecurities();
            ticketBooths = floor.getTicketBooths();
            turnstiles = floor.getTurnstiles();
            trainDoors = floor.getTrainDoors();

            for (Security security : securities) {
                if (!security.isFloorFieldsComplete()) {
                    return false;
                }
            }

            for (TicketBooth ticketBooth : ticketBooths) {
                if (!ticketBooth.isFloorFieldsComplete()) {
                    return false;
                }
            }

            for (Turnstile turnstile : turnstiles) {
                if (!turnstile.isFloorFieldsComplete()) {
                    return false;
                }
            }

            for (TrainDoor trainDoor : trainDoors) {
                if (!trainDoor.isFloorFieldsComplete()) {
                    return false;
                }
            }
        }

        // Then validate the elevator portals in this station
        for (ElevatorShaft elevatorShaft : station.getElevatorShafts()) {
            if (!((ElevatorPortal) elevatorShaft.getUpperPortal()).isFloorFieldsComplete()) {
                return false;
            }
        }

        // This part will only be reached once we pass every test
        return true;
    }

    // Clear all caches of this station
    public void clearCaches() {
        this.getMultiFloorPathCache().clear();
        this.getDistanceCache().clear();

        for (Floor floor : this.getFloors()) {
            floor.getPathCache().clear();
        }
    }

    // Thoroughly validate the layout of the station
    public static StationValidationResult validateStationLayoutDeeply(Station station) {
        // Clear caches
        station.clearCaches();

        // For each floor in the station, collect station gates
        List<StationGate> stationGates = new ArrayList<>();

        for (Floor floor : station.getFloors()) {
            stationGates.addAll(floor.getStationGates());
        }

        // If there are no station gates at all, we now know that this station is instantly invalid
        if (stationGates.isEmpty()) {
            return new StationValidationResult(
                    StationValidationResult.StationValidationResultType.NO_STATION_GATES,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        // For each floor in the station, collect train doors
        List<TrainDoor> trainDoors = new ArrayList<>();

        for (Floor floor : station.getFloors()) {
            trainDoors.addAll(floor.getTrainDoors());
        }

        // If there are no train doors at all, we now know that this station is instantly invalid
        if (trainDoors.isEmpty()) {
            return new StationValidationResult(
                    StationValidationResult.StationValidationResultType.NO_TRAIN_DOORS,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        // For each floor in the station, collect train tracks
        List<Track> trainTracks = new ArrayList<>();

        for (Floor floor : station.getFloors()) {
            trainTracks.addAll(floor.getTracks());
        }

        // If there are no train tracks at all, we now know that this station is instantly invalid
        if (trainTracks.isEmpty()) {
            return new StationValidationResult(
                    StationValidationResult.StationValidationResultType.NO_TRAIN_TRACKS,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        } else {
            // Collect all the directions of the train tracks and the train doors
            HashSet<PassengerMovement.TravelDirection> trainTrackDirections = new HashSet<>();
            HashSet<PassengerMovement.TravelDirection> trainDoorDirections = new HashSet<>();

            for (Track track : trainTracks) {
                trainTrackDirections.add(track.getTrackDirection());
            }

            for (TrainDoor trainDoor : trainDoors) {
                trainDoorDirections.add(trainDoor.getPlatformDirection());
            }

            // See if all directions of the train doors and train tracks match
            if (!trainTrackDirections.equals(trainDoorDirections)) {
                // All train door directions that are not in the train track directions
                HashSet<PassengerMovement.TravelDirection> trainTrackDirectionsCopy;
                HashSet<PassengerMovement.TravelDirection> trainDoorDirectionsCopy;

                trainTrackDirectionsCopy = new HashSet<>(trainTrackDirections);
                trainDoorDirectionsCopy = new HashSet<>(trainDoorDirections);

                trainTrackDirectionsCopy.removeAll(trainDoorDirectionsCopy);

                if (trainTrackDirectionsCopy.size() < trainDoorDirectionsCopy.size()) {
                    return new StationValidationResult(
                            StationValidationResult.StationValidationResultType.TRAIN_TRACKS_MISMATCH,
                            (PassengerMovement.TravelDirection) trainDoorDirectionsCopy.toArray()[0],
                            null,
                            null,
                            null,
                            Track.class
                    );
                } else {
                    return new StationValidationResult(
                            StationValidationResult.StationValidationResultType.TRAIN_TRACKS_MISMATCH,
                            (PassengerMovement.TravelDirection) trainTrackDirectionsCopy.toArray()[0],
                            null,
                            null,
                            null,
                            TrainDoor.class
                    );
                }
            }
        }

        // Assemble this station's amenity-floor index
        Station.assembleAmenityFloorIndex(station);

        // For each station gate, for each direction that the station gate may spawn, check if a single journey and
        // stored value card holder passenger may be able to navigate from this station gate to its corresponding
        // train door
        boolean isStoredValueCardHolder;

        RoutePlan boardingRoutePlanSingleJourney;
        RoutePlan boardingRoutePlanStoredValue;
        RoutePlan alightingRoutePlan;

        // Check the validity for each station gate in the station, for each boarding passenger
        for (StationGate stationGate : stationGates) {
            // Get the directions of the passengers that may be spawned by this station gate
            List<PassengerMovement.TravelDirection> travelDirectionsSpawnable
                    = stationGate.getStationGatePassengerTravelDirections();

            // Check the validity for each travel direction spawnable by this station gate
            for (PassengerMovement.TravelDirection travelDirectionSpawnable : travelDirectionsSpawnable) {
                // Initialize a potential passenger's route plans
                isStoredValueCardHolder = false;
                boardingRoutePlanSingleJourney = new RoutePlan(isStoredValueCardHolder, true);

                isStoredValueCardHolder = true;
                boardingRoutePlanStoredValue = new RoutePlan(isStoredValueCardHolder, true);

                Amenity currentAmenity;

                // Check the validity for boarding single journey ticket holders
                currentAmenity = stationGate;
                DirectoryResult directoryResultBoardingSingleJourney;

                while (true) {
                    // Check if a path exists from the current goal to any amenity that comes after it
                    directoryResultBoardingSingleJourney = getPortalsToGoal(
                            station,
                            boardingRoutePlanSingleJourney,
                            PassengerMovement.Disposition.BOARDING,
                            travelDirectionSpawnable,
                            currentAmenity
                    );

                    // If there are no paths found to any next amenity, this station is instantly valid
                    if (directoryResultBoardingSingleJourney.getPortals() == null) {
                        return new StationValidationResult(
                                StationValidationResult.StationValidationResultType.UNREACHABLE,
                                travelDirectionSpawnable,
                                TicketBooth.TicketType.SINGLE_JOURNEY,
                                PassengerMovement.Disposition.BOARDING,
                                currentAmenity,
                                boardingRoutePlanSingleJourney.getCurrentAmenityClass()
                        );
                    }

                    // TODO: For each portal in the goal portals list, attach the goal amenity to its directory,
                    //  signifying that to get to that amenity, this goal portal is the way

                    // Go one level deeper
                    currentAmenity = directoryResultBoardingSingleJourney.getGoalAmenity();

                    // If the next amenity is already a train door, this means a complete path was found from start to
                    // end, so this path is valid
                    if (currentAmenity instanceof TrainDoor) {
                        break;
                    } else {
                        boardingRoutePlanSingleJourney.setNextAmenityClass();
                    }
                }

                // Check the validity for boarding stored value ticket holders
                currentAmenity = stationGate;
                DirectoryResult directoryResultBoardingStoredValue;

                while (true) {
                    // Check if a path exists from the current goal to any amenity that comes after it
                    directoryResultBoardingStoredValue = getPortalsToGoal(
                            station,
                            boardingRoutePlanStoredValue,
                            PassengerMovement.Disposition.BOARDING,
                            travelDirectionSpawnable,
                            currentAmenity
                    );

                    // If there are no paths found to any next amenity, this station is instantly valid
                    if (directoryResultBoardingStoredValue.getPortals() == null) {
                        return new StationValidationResult(
                                StationValidationResult.StationValidationResultType.UNREACHABLE,
                                travelDirectionSpawnable,
                                TicketBooth.TicketType.STORED_VALUE,
                                PassengerMovement.Disposition.BOARDING,
                                currentAmenity,
                                boardingRoutePlanStoredValue.getCurrentAmenityClass()
                        );
                    }

                    // TODO: For each portal in the goal portals list, attach the goal amenity to its directory,
                    //  signifying that to get to that amenity, this goal portal is the way

                    // Go one level deeper
                    currentAmenity = directoryResultBoardingStoredValue.getGoalAmenity();

                    // If the next amenity is already a train door, this means a complete path was found from start to
                    // end, so this path is valid
                    if (currentAmenity instanceof TrainDoor) {
                        break;
                    } else {
                        boardingRoutePlanStoredValue.setNextAmenityClass();
                    }
                }
            }
        }

        // Check the validity for each train door in the station, for each alighting passenger
        for (TrainDoor trainDoor : trainDoors) {
            // Initialize a potential passenger's route plans
            alightingRoutePlan = new RoutePlan(false, false);

            // Get the directions of the passengers that are spawned by this train door
            PassengerMovement.TravelDirection travelDirectionSpawnable = trainDoor.getPlatformDirection();

            Amenity currentAmenity;

            // Check the validity for alighting passengers
            currentAmenity = trainDoor;
            DirectoryResult directoryResultAlighting;

            while (true) {
                // Check if a path exists from the current goal to any amenity that comes after it
                directoryResultAlighting = getPortalsToGoal(
                        station,
                        alightingRoutePlan,
                        PassengerMovement.Disposition.ALIGHTING,
                        travelDirectionSpawnable,
                        currentAmenity
                );

                // If there are no paths found to any next amenity, this station is instantly valid
                if (directoryResultAlighting.getPortals() == null) {
                    return new StationValidationResult(
                            StationValidationResult.StationValidationResultType.UNREACHABLE,
                            travelDirectionSpawnable,
                            null,
                            PassengerMovement.Disposition.ALIGHTING,
                            currentAmenity,
                            alightingRoutePlan.getCurrentAmenityClass()
                    );
                }

                // TODO: For each portal in the goal portals list, attach the goal amenity to its directory,
                //  signifying that to get to that amenity, this goal portal is the way

                // Go one level deeper
                currentAmenity = directoryResultAlighting.getGoalAmenity();

                // If the next amenity is already a station gate, this means a complete path was found from start to
                // end, so this path is valid
                if (currentAmenity instanceof StationGate) {
                    break;
                } else {
                    alightingRoutePlan.setNextAmenityClass();
                }
            }
        }

        return new StationValidationResult(
                StationValidationResult.StationValidationResultType.NO_ERROR,
                null,
                null,
                null,
                null,
                null
        );
    }

    private static DirectoryResult getPortalsToGoal(
            Station station,
            RoutePlan routePlan,
            PassengerMovement.Disposition disposition,
            PassengerMovement.TravelDirection travelDirectionSpawnable,
            Amenity currentAmenity
    ) {
        // Set the next amenity class
        Class<? extends Amenity> nextAmenityClass = routePlan.getCurrentAmenityClass();

        // Based on the passenger's current direction and route plan, get the next amenity class to be sought
        // Given the next amenity class, collect the floors which have this amenity class
        Set<Floor> floors = station.getAmenityFloorIndex().get(nextAmenityClass);

        assert !floors.isEmpty();

        // Compile all amenities in each floor
        List<Amenity> amenityListInFloors = new ArrayList<>();

        for (Floor floorToExplore : floors) {
            amenityListInFloors.addAll(floorToExplore.getAmenityList(nextAmenityClass));
        }

        // Compute the distance from the current position to the possible goal, taking into account possible
        // paths passing through other floors
        MultipleFloorPassengerPath bestPath = null;
        Amenity closestGoal = null;
        double closestDistance = Double.MAX_VALUE;

        for (Amenity candidateGoal : amenityListInFloors) {
            // Only consider amenities that are enabled
            NonObstacle nonObstacle = ((NonObstacle) candidateGoal);

            if (!nonObstacle.isEnabled()) {
                continue;
            }

            // Filter the amenity search space only to what is compatible with this passenger
            if (candidateGoal instanceof StationGate) {
                // If the goal of the passenger is a station gate, this means the passenger is leaving
                // So only consider station gates which allow exits and accepts the passenger's direction
                StationGate stationGateExit = ((StationGate) candidateGoal);

                if (stationGateExit.getStationGateMode() == StationGate.StationGateMode.ENTRANCE) {
                    continue;
                } else {
                    if (!stationGateExit.getStationGatePassengerTravelDirections().contains(travelDirectionSpawnable)) {
                        continue;
                    }
                }
            } else if (candidateGoal instanceof Turnstile) {
                // Only consider turnstiles which match this passenger's disposition and travel direction
                Turnstile turnstile = ((Turnstile) candidateGoal);

                if (!turnstile.getTurnstileTravelDirections().contains(travelDirectionSpawnable)) {
                    continue;
                }

                if (turnstile.getTurnstileMode() != Turnstile.TurnstileMode.BIDIRECTIONAL) {
                    if (
                            turnstile.getTurnstileMode() == Turnstile.TurnstileMode.BOARDING
                                    && disposition.equals(PassengerMovement.Disposition.ALIGHTING)
                                    || turnstile.getTurnstileMode() == Turnstile.TurnstileMode.ALIGHTING
                                    && disposition.equals(PassengerMovement.Disposition.BOARDING)
                    ) {
                        continue;
                    }
                }
            } else if (candidateGoal instanceof TrainDoor) {
                // Only consider train doors which match this passenger's travel direction
                TrainDoor trainDoor = ((TrainDoor) candidateGoal);

                if (trainDoor.getPlatformDirection() != travelDirectionSpawnable) {
                    continue;
                }
            }

            Patch currentAmenityPatch = currentAmenity.getAmenityBlocks().get(0).getPatch();

            MultipleFloorPassengerPath multipleFloorPassengerPath
                    = PassengerMovement.computePathAcrossFloors(
                    currentAmenityPatch.getFloor(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    currentAmenityPatch,
                    candidateGoal.getAmenityBlocks().get(0)
            );

            if (multipleFloorPassengerPath != null) {
                if (multipleFloorPassengerPath.getDistance() < closestDistance) {
                    bestPath = multipleFloorPassengerPath;
                    closestDistance = multipleFloorPassengerPath.getDistance();
                    closestGoal = candidateGoal;
                }
            }
        }

        // Then set the passenger's goal portals, given the path found to the goal and the portals required to
        // get to it
        return new DirectoryResult(
                (bestPath != null) ? new ArrayList<>(bestPath.getPortals()) : null,
                closestGoal
        );
    }

    // Validate the layout of the station
    public static boolean validateStationShallowly(Station station) {
        boolean boardingValid = true;
        boolean alightingValid = true;

        // For each floor in the station, collect a single station gate
        Map<Floor, StationGate> floorStationGatesMap = new HashMap<>();

        for (Floor floor : station.getFloors()) {
            // Just sample the first station gate in the list, if any
            if (!floor.getStationGates().isEmpty()) {
                floorStationGatesMap.put(floor, floor.getStationGates().get(0));
            }
        }

        // If there are no station gates at all, this layout is instantly invalid
        if (floorStationGatesMap.isEmpty()) {
            return false;
        }

        // For each station gate, check if there exists a complete path from start to end (for both boarding and
        // alighting passengers)
        List<Class<? extends Amenity>> boardingPlan
                = RoutePlan.DIRECTION_ROUTE_MAP.get(PassengerMovement.Disposition.BOARDING);

        List<Class<? extends Amenity>> alightingPlan
                = RoutePlan.DIRECTION_ROUTE_MAP.get(PassengerMovement.Disposition.ALIGHTING);

        Class<? extends Amenity> currentAmenity;
        Class<? extends Amenity> nextAmenity;

        List<AmenityClassFloorPair> boardingNotFoundList;
        List<AmenityClassFloorPair> boardingFoundList;

        List<AmenityClassFloorPair> alightingNotFoundList;
        List<AmenityClassFloorPair> alightingFoundList;

        for (Floor floor : floorStationGatesMap.keySet()) {
            boardingNotFoundList = new ArrayList<>();
            boardingFoundList = new ArrayList<>();

            currentAmenity = boardingPlan.get(0);
            nextAmenity = boardingPlan.get(1);

            boardingValid = boardingValid && validatePath(
                    boardingPlan,
                    1,
                    floor,
                    boardingNotFoundList,
                    boardingFoundList,
                    currentAmenity,
                    nextAmenity
            );

            if (!boardingValid) {
                return false;
            }

            alightingNotFoundList = new ArrayList<>();
            alightingFoundList = new ArrayList<>();

            currentAmenity = alightingPlan.get(0);
            nextAmenity = alightingPlan.get(1);

            alightingValid = alightingValid && validatePath(
                    alightingPlan,
                    1,
                    floor,
                    alightingNotFoundList,
                    alightingFoundList,
                    currentAmenity,
                    nextAmenity
            );

            if (!alightingValid) {
                return false;
            }
        }

        return true;
    }

    private static boolean validatePath(
            List<Class<? extends Amenity>> routePlan,
            int index,
            Floor currentFloor,
            List<AmenityClassFloorPair> notFoundList,
            List<AmenityClassFloorPair> foundList,
            Class<? extends Amenity> currentAmenity,
            Class<? extends Amenity> nextAmenity
    ) {
        AmenityClassFloorPair amenityClassFloorPair = new AmenityClassFloorPair(
                nextAmenity,
                currentFloor
        );

        // If this amenity has already been proven to not be in this floor, no need to look for that amenity again in
        // this floor - return false
        if (notFoundList.contains(amenityClassFloorPair)) {
            return false;
        }

        // Get the necessary amenity list
        List<Amenity> amenityList = new ArrayList<>();
        if (nextAmenity == StationGate.class) {
            amenityList.addAll(currentFloor.getStationGates());
        } else if (nextAmenity == Security.class) {
            amenityList.addAll(currentFloor.getSecurities());
        } else if (nextAmenity == TicketBooth.class) {
            amenityList.addAll(currentFloor.getTicketBooths());
        } else if (nextAmenity == Turnstile.class) {
            amenityList.addAll(currentFloor.getTurnstiles());
        } else if (nextAmenity == TrainDoor.class) {
            amenityList.addAll(currentFloor.getTrainDoors());
        } else {
            amenityList = null;
        }

        assert amenityList != null;

        // Using the acquired amenity list, check if this floor contains such amenity
        if (foundList.contains(amenityClassFloorPair) || !amenityList.isEmpty()) {
            // Add this amenity-floor pair to the found list
            foundList.add(amenityClassFloorPair);

            // Get the next amenity
            Class<? extends Amenity> newCurrentAmenity;
            Class<? extends Amenity> newNextAmenity;

            // If there is no more next amenity, return true
            if (index + 1 == routePlan.size()) {
                return true;
            } else {
                // Else, keep recursing
                int newIndex = index + 1;

                newCurrentAmenity = nextAmenity;
                newNextAmenity = routePlan.get(newIndex);

                return validatePath(
                        routePlan,
                        newIndex,
                        currentFloor,
                        notFoundList,
                        foundList,
                        newCurrentAmenity,
                        newNextAmenity
                );
            }
        } else {
            // Add this amenity-floor pair to the not found list
            notFoundList.add(amenityClassFloorPair);

            boolean portalsValid = false;

            // Gather all portals in this floor
            List<PortalShaft> portalsInFloor = new ArrayList<>();

            // TODO: Consider directions
            for (StairShaft stairShaft : currentFloor.getStation().getStairShafts()) {
                if (stairShaft.getLowerPortal().getFloorServed() == currentFloor
                        || stairShaft.getUpperPortal().getFloorServed() == currentFloor) {
                    if (!portalsInFloor.contains(stairShaft)) {
                        portalsInFloor.add(stairShaft);
                    }
                }
            }

            for (EscalatorShaft escalatorShaft : currentFloor.getStation().getEscalatorShafts()) {
                if (escalatorShaft.getLowerPortal().getFloorServed() == currentFloor
                        || escalatorShaft.getUpperPortal().getFloorServed() == currentFloor) {
                    if (!portalsInFloor.contains(escalatorShaft)) {
                        portalsInFloor.add(escalatorShaft);
                    }
                }
            }

            for (ElevatorShaft elevatorShaft : currentFloor.getStation().getElevatorShafts()) {
                if (elevatorShaft.getLowerPortal().getFloorServed() == currentFloor
                        || elevatorShaft.getUpperPortal().getFloorServed() == currentFloor) {
                    if (!portalsInFloor.contains(elevatorShaft)) {
                        portalsInFloor.add(elevatorShaft);
                    }
                }
            }

            // For each portal, go to the floor it connects to
            for (PortalShaft portalShaft : portalsInFloor) {
                Floor connectedFloor;

                if (portalShaft.getLowerPortal().getFloorServed() == currentFloor) {
                    connectedFloor = portalShaft.getUpperPortal().getFloorServed();
                } else {
                    connectedFloor = portalShaft.getLowerPortal().getFloorServed();
                }

                // Go to that floor and validate it
                // At least one portal should lead to the next amenity
                // Else, return false
                portalsValid = portalsValid || validatePath(
                        routePlan,
                        index,
                        connectedFloor,
                        notFoundList,
                        foundList,
                        currentAmenity,
                        nextAmenity
                );
            }

            // No other portals, so just return false
            return portalsValid;
        }
    }

    public static class StationValidationResult {
        private final StationValidationResultType stationValidationResultType;
        private final PassengerMovement.TravelDirection travelDirection;
        private final TicketBooth.TicketType ticketType;
        private final PassengerMovement.Disposition disposition;
        private final Amenity lastValidAmenity;
        private final Class<? extends Amenity> nextAmenityClass;

        public enum StationValidationResultType {
            NO_ERROR,
            NO_STATION_GATES,
            NO_TRAIN_DOORS,
            NO_TRAIN_TRACKS,
            TRAIN_TRACKS_MISMATCH,
            UNREACHABLE
        }

        public StationValidationResult(
                StationValidationResultType stationValidationResultType,
                PassengerMovement.TravelDirection travelDirection,
                TicketBooth.TicketType ticketType,
                PassengerMovement.Disposition disposition,
                Amenity lastValidAmenity,
                Class<? extends Amenity> nextAmenityClass
        ) {
            this.stationValidationResultType = stationValidationResultType;
            this.travelDirection = travelDirection;
            this.ticketType = ticketType;
            this.disposition = disposition;
            this.lastValidAmenity = lastValidAmenity;
            this.nextAmenityClass = nextAmenityClass;
        }

        public StationValidationResultType getStationValidationResultType() {
            return stationValidationResultType;
        }

        @Override
        public String toString() {
            String amenityClassName;
            String errorMessageTemplate;

            switch (this.stationValidationResultType) {
                case NO_STATION_GATES:
                    return "This station does not have any station gates.";
                case NO_TRAIN_DOORS:
                    return "This station does not have any train boarding areas.";
                case NO_TRAIN_TRACKS:
                    return "This station does not have any train tracks.";
                case TRAIN_TRACKS_MISMATCH:
                    amenityClassName = getAmenityName(this.nextAmenityClass);

                    errorMessageTemplate = "There are no {0}s for the {1}";

                    if (this.nextAmenityClass == Track.class) {
                        errorMessageTemplate += " train boarding areas.";
                    } else {
                        errorMessageTemplate += " train tracks.";
                    }

                    errorMessageTemplate = errorMessageTemplate.replace("{0}", amenityClassName);
                    errorMessageTemplate
                            = errorMessageTemplate.replace("{1}", this.travelDirection.toString().toLowerCase());

                    return errorMessageTemplate;
                case UNREACHABLE:
                    String lastValidAmenityClassName = getAmenityName(this.lastValidAmenity.getClass());

                    amenityClassName = getAmenityName(this.nextAmenityClass);

                    errorMessageTemplate = "No {0}s are reachable from the {1} at patch {2} for {3} passengers" +
                            " who are {4}.";

                    errorMessageTemplate = errorMessageTemplate.replace("{0}", amenityClassName);
                    errorMessageTemplate = errorMessageTemplate.replace("{1}", lastValidAmenityClassName);
                    errorMessageTemplate = errorMessageTemplate.replace(
                            "{2}",
                            String.valueOf(this.lastValidAmenity.getAttractors().get(0).getPatch())
                    );
                    errorMessageTemplate = errorMessageTemplate.replace(
                            "{3}",
                            this.travelDirection.toString().toLowerCase()
                    );
                    errorMessageTemplate = errorMessageTemplate.replace(
                            "{4}",
                            this.disposition.toString().toLowerCase()
                    );

                    return errorMessageTemplate;
                default:
                    return "The station layout is valid.";
            }
        }

        private String getAmenityName(Class<? extends Amenity> amenityClass) {
            String amenityClassName = "";

            if (amenityClass == StationGate.class) {
                amenityClassName = "station entrance/exit";
            } else if (amenityClass == StairPortal.class) {
                amenityClassName = "staircase";
            } else if (amenityClass == EscalatorPortal.class) {
                amenityClassName = "escalator";
            } else if (amenityClass == ElevatorPortal.class) {
                amenityClassName = "elevator";
            } else if (amenityClass == Security.class) {
                amenityClassName = "security gate";
            } else if (amenityClass == TicketBooth.class) {
                amenityClassName = "ticket booth";
            } else if (amenityClass == Turnstile.class) {
                amenityClassName = "turnstile";
            } else if (amenityClass == TrainDoor.class) {
                amenityClassName = "train boarding area";
            } else if (amenityClass == Track.class) {
                amenityClassName = "train track";
            }

            return amenityClassName;
        }
    }

    private static class AmenityClassFloorPair {
        private final Class<? extends Amenity> amenityClass;
        private final Floor floor;

        public AmenityClassFloorPair(Class<? extends Amenity> amenityClass, Floor floor) {
            this.amenityClass = amenityClass;
            this.floor = floor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AmenityClassFloorPair that = (AmenityClassFloorPair) o;
            return amenityClass.equals(that.amenityClass) && floor.equals(that.floor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(amenityClass, floor);
        }
    }

    public static class AmenityFloorPair {
        private final Amenity amenity;
        private final Floor floor;

        public AmenityFloorPair(Amenity amenity, Floor floor) {
            this.amenity = amenity;
            this.floor = floor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AmenityFloorPair that = (AmenityFloorPair) o;
            return amenity.equals(that.amenity) && floor.equals(that.floor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(amenity, floor);
        }
    }
}
