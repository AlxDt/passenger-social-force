package com.crowdsimulation.model.core.agent.passenger;

import com.crowdsimulation.controller.graphics.amenity.graphic.passenger.PassengerGraphic;
import com.crowdsimulation.model.core.agent.Agent;
import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.agent.passenger.movement.PassengerTripInformation;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.PatchObject;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Gate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBooth;
import com.crowdsimulation.model.simulator.Simulator;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

public class Passenger extends PatchObject implements Agent {
    // Keep track of the number of passengers generated
    private static int passengerCount = 0;

    // Contains the important information of this passenger
    private final PassengerInformation passengerInformation;

    // Contains the mechanisms for this passenger's movement
    private final PassengerMovement passengerMovement;

    // Keeps track of the passenger's time in the simulation
    private final PassengerTime passengerTime;

    // Handles how this passenger is displayed
    private final PassengerGraphic passengerGraphic;

    // Factory for passenger creation
    public static final PassengerFactory passengerFactory;

    static {
        passengerFactory = new PassengerFactory();
    }

    // TODO: Passengers don't actually despawn until at the destination station, so the only disposition of the
    //  passenger will be boarding
    private Passenger(
            Patch spawnPatch,
            PassengerTripInformation passengerTripInformation
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

            PassengerInformation.Gender gender
                    = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean()
                    ? PassengerInformation.Gender.FEMALE : PassengerInformation.Gender.MALE;

            Demographic demographic = Demographic.generateDemographic();

            // Given the demographic, get this passenger's walking distance
            double baseWalkingDistance = Demographic.walkingSpeedsByAgeRange.get(demographic.getAgeRange());

            // Initialize card-related variables
            String cardNumber = null;

            final double singleJourneyPercentage = 0.5;

            TicketBooth.TicketType ticketType =
                    Simulator.RANDOM_NUMBER_GENERATOR.nextDouble() < singleJourneyPercentage
                            ? TicketBooth.TicketType.SINGLE_JOURNEY : TicketBooth.TicketType.STORED_VALUE;

            // The identifier of this passenger is its serial number (based on the number of passengers generated)
            int serialNumber = passengerCount;

            // Initialize this passenger's information
            this.passengerInformation = new PassengerInformation(
                    serialNumber,
                    cardNumber,
                    ticketType,
                    gender,
                    demographic
            );

            // Initialize this passenger's timekeeping
            this.passengerTime = new PassengerTime(null);

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

            PassengerInformation.Gender gender = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? PassengerInformation.Gender.FEMALE : PassengerInformation.Gender.MALE;
            Demographic demographic = Demographic.generateDemographic();

            // Given the demographic, get this passenger's walking distance
            double baseWalkingDistance = Demographic.walkingSpeedsByAgeRange.get(demographic.getAgeRange());

            // Initialize card-related variables
            String cardNumber = passengerTripInformation.getCardNumber();

            TicketBooth.TicketType ticketType
                    = passengerTripInformation.isStoredValueHolder()
                    ? TicketBooth.TicketType.STORED_VALUE : TicketBooth.TicketType.SINGLE_JOURNEY;

            // The identifier of this passenger is its serial number (based on the number of passengers generated)
            int serialNumber = passengerCount;

            // Initialize this passenger's information
            this.passengerInformation = new PassengerInformation(
                    serialNumber,
                    cardNumber,
                    ticketType,
                    gender,
                    demographic
            );

            // Initialize this passenger's timekeeping
            this.passengerTime = new PassengerTime(passengerTripInformation.getTurnstileTapInTime());

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

        // Set the graphic object of this passenger
        this.passengerGraphic = new PassengerGraphic(this);
    }

    public PassengerInformation.Gender getGender() {
        return this.passengerInformation.gender;
    }

    public Demographic getDemographic() {
        return this.passengerInformation.getDemographic();
    }

    public String getCardNumber() {
        return this.passengerInformation.getCardNumber();
    }

    public TicketBooth.TicketType getTicketType() {
        return this.passengerInformation.ticketType;
    }

    public int getSerialNumber() {
        return this.passengerInformation.serialNumber;
    }

    public PassengerGraphic getPassengerGraphic() {
        return this.passengerGraphic;
    }

    public PassengerMovement getPassengerMovement() {
        return this.passengerMovement;
    }

    public static class PassengerFactory extends StationObjectFactory {
        public Passenger create(
                Patch spawnPatch,
                PassengerTripInformation passengerTripInformation
        ) {
            return new Passenger(spawnPatch, passengerTripInformation);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return this.getSerialNumber() == passenger.getSerialNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getSerialNumber());
    }

    @Override
    public String toString() {
        return String.valueOf(this.getSerialNumber() + "(" + this.passengerInformation.cardNumber + ")");
    }

    public static class PassengerInformation {
        // Denotes the serial number of this passenger
        private final int serialNumber;

        // Denotes the card number of this passenger
        private final String cardNumber;

        // Denotes the ticket type of this passenger
        private final TicketBooth.TicketType ticketType;

        // Denotes the gender of this passenge
        private final PassengerInformation.Gender gender;

        // Denotes the demographic of this passenger
        private final Demographic demographic;

        public PassengerInformation(
                int serialNumber,
                String cardNumber,
                TicketBooth.TicketType ticketType,
                Gender gender,
                Demographic demographic
        ) {
            this.serialNumber = serialNumber;
            this.cardNumber = cardNumber;
            this.ticketType = ticketType;
            this.gender = gender;
            this.demographic = demographic;
        }

        public int getSerialNumber() {
            return serialNumber;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public TicketBooth.TicketType getTicketType() {
            return ticketType;
        }

        public Gender getGender() {
            return gender;
        }

        public Demographic getDemographic() {
            return demographic;
        }

        // Denotes the gender of this passenger
        public enum Gender {
            FEMALE,
            MALE
        }
    }

    // Contains the timekeeping aspects of the passenger
    public static class PassengerTime {
        // Denotes the times (since spawning) when this passenger has achieved the following milestones:
        //     (a) Entered station gate,
        //     (b) Passed security,
        //     (c) Tapped in to a turnstile,
        //     (d) Entered train,
        //     (e) Exited train,
        //     (F) Tapped out of a turnstile,
        //     (g) Exited the station (and despawned)
        private final LocalTime timeSpawned;

        private Duration enteredStation;
        private Duration passedSecurity;
        private Duration tappedInTurnstile;
        private Duration enteredTrain;
        private Duration exitedTrain;
        private Duration tappedOutTurnstile;
        private Duration exitedStation;

        public PassengerTime(LocalTime timeSpawned) {
            this.timeSpawned = timeSpawned;
        }

        public LocalTime getTimeSpawned() {
            return timeSpawned;
        }

        public Duration getEnteredStation() {
            return enteredStation;
        }

        public void setEnteredStation(Duration enteredStation) {
            this.enteredStation = enteredStation;
        }

        public Duration getPassedSecurity() {
            return passedSecurity;
        }

        public void setPassedSecurity(Duration passedSecurity) {
            this.passedSecurity = passedSecurity;
        }

        public Duration getTappedInTurnstile() {
            return tappedInTurnstile;
        }

        public void setTappedInTurnstile(Duration tappedInTurnstile) {
            this.tappedInTurnstile = tappedInTurnstile;
        }

        public Duration getEnteredTrain() {
            return enteredTrain;
        }

        public void setEnteredTrain(Duration enteredTrain) {
            this.enteredTrain = enteredTrain;
        }

        public Duration getExitedTrain() {
            return exitedTrain;
        }

        public void setExitedTrain(Duration exitedTrain) {
            this.exitedTrain = exitedTrain;
        }

        public Duration getTappedOutTurnstile() {
            return tappedOutTurnstile;
        }

        public void setTappedOutTurnstile(Duration tappedOutTurnstile) {
            this.tappedOutTurnstile = tappedOutTurnstile;
        }

        public Duration getExitedStation() {
            return exitedStation;
        }

        public void setExitedStation(Duration exitedStation) {
            this.exitedStation = exitedStation;
        }
    }
}
