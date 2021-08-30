package com.crowdsimulation.model.core.agent.passenger;

import com.crowdsimulation.controller.graphics.amenity.graphic.passenger.PassengerGraphic;
import com.crowdsimulation.model.core.agent.Agent;
import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.agent.passenger.movement.RoutePlan;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.PatchObject;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Gate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBooth;
import com.crowdsimulation.model.simulator.Simulator;

import java.util.Objects;

public class Passenger extends PatchObject implements Agent {
    // Keep track of the number of passengers generated
    private static int passengerCount = 0;

    // Denotes the serial number of this passenger
    private final int serialNumber;

    // Denotes the card number of this passenger
    private String cardNumber;

    // Denotes the gender of this passenger
    // TODO: Move to passenger information object?
    private final Gender gender;

    // Denotes the demographic of this passenger
    private final Demographic demographic;

    // Denotes the ticket type of this passenger
    private final TicketBooth.TicketType ticketType;

    // Handles how this passenger is displayed
    private final PassengerGraphic passengerGraphic;

    // Contains the mechanisms for this passenger's movement
    private final PassengerMovement passengerMovement;

    // Factory for passenger creation
    public static final PassengerFactory passengerFactory;

    static {
        passengerFactory = new PassengerFactory();
    }

    // TODO: Passengers don't actually despawn until at the destination station, so the only disposition of the
    //  passenger will be boarding
    private Passenger(
            Patch spawnPatch,
            RoutePlan.PassengerTripInformation passengerTripInformation
    ) {
        if (passengerTripInformation == null) {
            Gate gate = ((Gate) spawnPatch.getAmenityBlock().getParent());

            PassengerMovement.TravelDirection travelDirectionChosen = null;
            boolean isBoarding = true;

            if (gate instanceof StationGate) {
                StationGate stationGate = ((StationGate) gate);

                // Get the pool of possible travel directions of the passengers to be spawned, depending on the settings of this
                // passenger gate
                // From this pool of travel directions, pick a random one
                int randomIndex
                        = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(
                        stationGate.getStationGatePassengerTravelDirections().size()
                );

                travelDirectionChosen = stationGate.getStationGatePassengerTravelDirections().get(randomIndex);

                isBoarding = true;
            } else if (gate instanceof TrainDoor) {
                TrainDoor trainDoor = ((TrainDoor) gate);

                travelDirectionChosen = trainDoor.getPlatformDirection();

                isBoarding = false;
            }

            this.gender = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? Gender.FEMALE : Gender.MALE;
            this.demographic = Demographic.generateDemographic();

            // Given the demographic, get this passenger's walking distance
            double baseWalkingDistance = Demographic.walkingSpeedsByAgeRange.get(this.demographic.getAgeRange());

            // Initialize card-related variables
            this.cardNumber = null;

            final double singleJourneyPercentage = 0.5;

            this.ticketType
                    = Simulator.RANDOM_NUMBER_GENERATOR.nextDouble() < singleJourneyPercentage
                    ? TicketBooth.TicketType.SINGLE_JOURNEY : TicketBooth.TicketType.STORED_VALUE;

            // Set the graphic object of this passenger
            this.passengerGraphic = new PassengerGraphic(this);

            // The identifier of this passenger is its serial number (based on the number of passengers generated)
            this.serialNumber = passengerCount;

            // Increment the number of passengers made by one
            Passenger.passengerCount++;

            // Initialize all movement-related fields
            this.passengerMovement = new PassengerMovement(
                    gate,
                    this,
                    baseWalkingDistance,
                    spawnPatch.getPatchCenterCoordinates(),
                    travelDirectionChosen,
                    isBoarding
            );
        } else {
            Gate gate = ((Gate) spawnPatch.getAmenityBlock().getParent());

            this.gender = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? Gender.FEMALE : Gender.MALE;
            this.demographic = Demographic.generateDemographic();

            // Given the demographic, get this passenger's walking distance
            double baseWalkingDistance = Demographic.walkingSpeedsByAgeRange.get(this.demographic.getAgeRange());

            // Initialize card-related variables
            this.cardNumber = passengerTripInformation.getCardNumber();

            this.ticketType
                    = passengerTripInformation.isStoredValueHolder()
                    ? TicketBooth.TicketType.STORED_VALUE : TicketBooth.TicketType.SINGLE_JOURNEY;

            // Set the graphic object of this passenger
            this.passengerGraphic = new PassengerGraphic(this);

            // The identifier of this passenger is its serial number (based on the number of passengers generated)
            this.serialNumber = passengerCount;

            // Increment the number of passengers made by one
            Passenger.passengerCount++;

            // Initialize all movement-related fields
            this.passengerMovement = new PassengerMovement(
                    gate,
                    this,
                    baseWalkingDistance,
                    spawnPatch.getPatchCenterCoordinates(),
                    passengerTripInformation
            );
        }
    }

    public Gender getGender() {
        return gender;
    }

    public Demographic getDemographic() {
        return demographic;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public TicketBooth.TicketType getTicketType() {
        return ticketType;
    }

    public PassengerGraphic getPassengerGraphic() {
        return passengerGraphic;
    }

    public PassengerMovement getPassengerMovement() {
        return this.passengerMovement;
    }

    public int getSerialNumber() {
        return this.serialNumber;
    }

    public static class PassengerFactory extends StationObjectFactory {
        public Passenger create(
                Patch spawnPatch,
                RoutePlan.PassengerTripInformation passengerTripInformation
        ) {
            return new Passenger(spawnPatch, passengerTripInformation);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return serialNumber == passenger.serialNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber);
    }

    @Override
    public String toString() {
        return String.valueOf(this.serialNumber);
    }

    // Denotes the gender of this passenger
    public enum Gender {
        FEMALE,
        MALE
    }
}
