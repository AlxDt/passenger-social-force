package com.crowdsimulation.model.core.environment.station;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.Environment;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.impenetrable.Track;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.impenetrable.Wall;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorShaft;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorShaft;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs.StairShaft;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Turnstile;
import com.crowdsimulation.model.core.environment.station.patch.position.Coordinates;
import com.crowdsimulation.model.core.environment.station.patch.position.MatrixPosition;
import com.crowdsimulation.model.simulator.cache.PathCache;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Floor extends BaseStationObject implements Environment {
    // Denotes the station which contains this floor
    private final Station station;

    // Denotes the number of rows this floor has
    private final int rows;

    // Denotes the number of columns this floor has
    private final int columns;

    // The row x column containing the patches which constitute this floor
    private final Patch[][] patches;

    // Patch sets (for drawing purposes)
    // List all patches occupied by passengers and amenities
    private final SortedSet<Patch> amenityPatchSet;
    private final SortedSet<Patch> passengerPatchSet;

    // Amenity lists
    private final List<StationGate> stationGates;
    private final List<Security> securities;

    private final List<TicketBooth> ticketBooths;
    private final List<Turnstile> turnstiles;

    private final List<TrainDoor> trainDoors;
    private final List<Track> tracks;

    private final List<Wall> walls;

    // Passengers in this floor
    private final CopyOnWriteArrayList<Passenger> passengersInFloor;

    // Caches for optimized performance
    private final PathCache pathCache;

    // Factory for floor creation
    private static final Floor.FloorFactory floorFactory;

    static {
        // Initialize factories
        floorFactory = new FloorFactory();
    }

    protected Floor(Station station, int rows, int columns) {
        this.station = station;

        this.rows = rows;
        this.columns = columns;

        // Initialize the patches
        this.patches = new Patch[rows][columns];
        this.initializePatches();

        // Initialize the patch set
        this.amenityPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());
        this.passengerPatchSet = Collections.synchronizedSortedSet(new TreeSet<>());

        // Initialize the amenity lists
        this.stationGates = Collections.synchronizedList(new ArrayList<>());
        this.securities = Collections.synchronizedList(new ArrayList<>());

        this.ticketBooths = Collections.synchronizedList(new ArrayList<>());
        this.turnstiles = Collections.synchronizedList(new ArrayList<>());

        this.trainDoors = Collections.synchronizedList(new ArrayList<>());
        this.tracks = Collections.synchronizedList(new ArrayList<>());

        this.walls = Collections.synchronizedList(new ArrayList<>());

        // Initialize the passenger list
        this.passengersInFloor = new CopyOnWriteArrayList<>();

        // Initialize caches
        int capacity = 100;

        this.pathCache = new PathCache(capacity);
    }

    public Station getStation() {
        return station;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public SortedSet<Patch> getAmenityPatchSet() {
        return amenityPatchSet;
    }

    public SortedSet<Patch> getPassengerPatchSet() {
        return passengerPatchSet;
    }

    public List<StationGate> getStationGates() {
        return stationGates;
    }

    public List<Security> getSecurities() {
        return securities;
    }

    public List<TicketBooth> getTicketBooths() {
        return ticketBooths;
    }

    public List<Turnstile> getTurnstiles() {
        return turnstiles;
    }

    public List<TrainDoor> getTrainDoors() {
        return trainDoors;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    private void initializePatches() {
        MatrixPosition matrixPosition;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                matrixPosition = new MatrixPosition(row, column);

                patches[row][column] = new Patch(this, matrixPosition);
            }
        }
    }

    public CopyOnWriteArrayList<Passenger> getPassengersInFloor() {
        return passengersInFloor;
    }

    public PathCache getPathCache() {
        return pathCache;
    }

    public Patch getPatch(Coordinates coordinates) {
        return getPatch(
                (int) (coordinates.getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS),
                (int) (coordinates.getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS)
        );
    }

    public Patch getPatch(MatrixPosition matrixPosition) {
        return getPatch(matrixPosition.getRow(), matrixPosition.getColumn());
    }

    public Patch getPatch(int row, int column) {
        return patches[row][column];
    }

    public Patch[][] getPatches() {
        return this.patches;
    }

    public static List<Patch> get7x7Field(
            Floor floor,
            Patch centerPatch,
            double heading,
            boolean includeCenterPatch,
            double fieldOfViewAngle
    ) {
        int truncatedX = (int) (centerPatch.getPatchCenterCoordinates().getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS);
        int truncatedY = (int) (centerPatch.getPatchCenterCoordinates().getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS);

        Patch chosenPatch;
        List<Patch> patchesToExplore = new ArrayList<>();

        for (int rowOffset = -3; rowOffset <= 3; rowOffset++) {
            for (int columnOffset = -3; columnOffset <= 3; columnOffset++) {
                boolean xCondition;
                boolean yCondition;

                // Exclude the center patch, unless explicitly specified
                boolean isCenterPatch = rowOffset == 0 && columnOffset == 0;

                if (!includeCenterPatch) {
                    if (isCenterPatch) {
                        continue;
                    }
                }

                // Separate upper and lower rows
                if (rowOffset < 0) {
                    yCondition = truncatedY + rowOffset > 0;
                } else if (rowOffset > 0) {
                    yCondition = truncatedY + rowOffset < floor.getRows();
                } else {
                    yCondition = true;
                }

                // Separate left and right columns
                if (columnOffset < 0) {
                    xCondition = truncatedX + columnOffset > 0;
                } else if (columnOffset > 0) {
                    xCondition = truncatedX + columnOffset < floor.getColumns();
                } else {
                    xCondition = true;
                }

                // Insert the patch to the list of patches to be explored if the patches are within the bounds of the
                // floor
                if (xCondition && yCondition) {
                    chosenPatch = floor.getPatch(
                            truncatedY + rowOffset,
                            truncatedX + columnOffset
                    );

                    // Make sure that the patch to be added is within the field of view of the passenger which invoked
                    // this method
                    if ((includeCenterPatch && isCenterPatch) || Coordinates.isWithinFieldOfView(
                            centerPatch.getPatchCenterCoordinates(),
                            chosenPatch.getPatchCenterCoordinates(),
                            heading,
                            fieldOfViewAngle)) {
                        patchesToExplore.add(chosenPatch);
                    }
                }
            }
        }

        return patchesToExplore;
    }

    // Add a floor above or below the given current floor in a list of floors
    public static Floor addFloorAboveOrBelow(
            Station station,
            List<Floor> floors,
            Floor currentFloor,
            boolean aboveCurrentFloor,
            int newFloorRows,
            int newFloorColumns) {
        // Get the index of the current floor within the given floor list
        int currentFloorIndex = floors.indexOf(currentFloor);

        if (!aboveCurrentFloor) {
            return addFloorBelowCurrentFloor(station, floors, currentFloorIndex, newFloorRows, newFloorColumns);
        } else {
            return addFloorAboveCurrentFloor(station, floors, currentFloorIndex, newFloorRows, newFloorColumns);
        }
    }

    // Add a floor below the given current floor in a list of floors
    private static Floor addFloorBelowCurrentFloor(
            Station station,
            List<Floor> floors,
            int currentFloorIndex,
            int newFloorRows,
            int newFloorColumns) {
        // If adding below, the index of the new floor is the index of the current floor
        // (because the current floor would be chronologically bumped up in the list)
        int newFloorIndex = currentFloorIndex;

        // Add the floor given the new index
        return addFloor(station, floors, newFloorIndex, newFloorRows, newFloorColumns);
    }

    // Add a floor above the given current floor in a list of floors
    private static Floor addFloorAboveCurrentFloor(
            Station station,
            List<Floor> floors,
            int currentFloorIndex,
            int newFloorRows,
            int newFloorsColumns) {
        // If adding below, the index of the new floor is 1 + the index of the current floor
        // (because the current floor would stay put in the list)
        int newFloorIndex = 1 + currentFloorIndex;

        // Add the floor given the new index
        return addFloor(station, floors, newFloorIndex, newFloorRows, newFloorsColumns);
    }

    // Add a floor to the list of floors given an index
    public static Floor addFloor(
            Station station,
            List<Floor> floors,
            int newFloorIndex,
            int newFloorsRows,
            int newFloorColumns) {
        Floor newFloor = Floor.floorFactory.create(
                station,
                newFloorsRows,
                newFloorColumns
        );

        floors.add(
                newFloorIndex,
                newFloor
        );

        return newFloor;
    }

    // Remove the given floor from a list of floors
    public static void deleteFloor(List<Floor> floors, Floor floorToBeRemoved) {
        // Delete all amenities in the floor to be removed
        Main.mainScreenController.clearAmenities();

        // Remove the floor specified
        floors.remove(floorToBeRemoved);
    }

    // Depending on the given amenity class, grab the appropriate amenity list
    public List<? extends Amenity> getAmenityList(Class<? extends Amenity> amenityClass) {
        if (amenityClass == StationGate.class) {
            return this.getStationGates();
        } else if (amenityClass == Security.class) {
            return this.getSecurities();
        } else if (amenityClass == StairShaft.class) {
            List<StairShaft> stairShaftsInFloor = new ArrayList<>();

            for (StairShaft stairShaft : this.getStation().getStairShafts()) {
                Portal lowerPortal = stairShaft.getLowerPortal();
                Portal upperPortal = stairShaft.getUpperPortal();

                // Only add stairs that are in this floor
                if (lowerPortal.getFloorServed() == this || upperPortal.getFloorServed() == this) {
                    stairShaftsInFloor.add(stairShaft);
                }
            }

            return stairShaftsInFloor;
        } else if (amenityClass == EscalatorShaft.class) {
            List<EscalatorShaft> escalatorShaftsInFloor = new ArrayList<>();

            for (EscalatorShaft escalatorShaft : this.getStation().getEscalatorShafts()) {
                Portal lowerPortal = escalatorShaft.getLowerPortal();
                Portal upperPortal = escalatorShaft.getUpperPortal();

                // Only add escalators that are in this floor
                if (lowerPortal.getFloorServed() == this || upperPortal.getFloorServed() == this) {
                    escalatorShaftsInFloor.add(escalatorShaft);
                }
            }

            return escalatorShaftsInFloor;
        } else if (amenityClass == ElevatorShaft.class) {
            List<ElevatorShaft> elevatorShaftsInFloor = new ArrayList<>();

            for (ElevatorShaft elevatorShaft : this.getStation().getElevatorShafts()) {
                Portal lowerPortal = elevatorShaft.getLowerPortal();
                Portal upperPortal = elevatorShaft.getUpperPortal();

                // Only add escalators that are in this floor
                if (lowerPortal.getFloorServed() == this || upperPortal.getFloorServed() == this) {
                    elevatorShaftsInFloor.add(elevatorShaft);
                }
            }

            return elevatorShaftsInFloor;
        } else if (amenityClass == TicketBooth.class) {
            return this.getTicketBooths();
        } else if (amenityClass == Turnstile.class) {
            return this.getTurnstiles();
        } else if (amenityClass == TrainDoor.class) {
            return this.getTrainDoors();
        } else if (amenityClass == Track.class) {
            return this.getTracks();
        } else if (amenityClass == Wall.class) {
            return this.getWalls();
        } else {
            return null;
        }
    }

    // Create a floor
    public static class FloorFactory extends BaseStationObject.StationObjectFactory {
        public Floor create(Station station, int rows, int columns) {
            return new Floor(
                    station,
                    rows,
                    columns
            );
        }
    }
}
