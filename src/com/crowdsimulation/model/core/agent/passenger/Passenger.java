package com.crowdsimulation.model.core.agent.passenger;

import com.crowdsimulation.controller.graphics.amenity.graphic.passenger.PassengerGraphic;
import com.crowdsimulation.model.core.agent.Agent;
import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.PatchObject;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Gate;
import com.crowdsimulation.model.simulator.Simulator;

import java.util.Objects;

public class Passenger extends PatchObject implements Agent {
    // Keep track of the number of passengers generated
    private static int passengerCount = 0;

    // Denotes the serial number of this passenger
    private final int identifier;

    // Denotes the gender of this passenger
    // TODO: Move to passenger information object?
    private final Gender gender;

    // Handles how this passenger is displayed
    private final PassengerGraphic passengerGraphic;

    // Contains the mechanisms for this passenger's movement
    private final PassengerMovement passengerMovement;

    // Factory for passenger creation
    public static final PassengerFactory passengerFactory;

    static {
        passengerFactory = new PassengerFactory();
    }

    private Passenger(Gate gate) {
        this.gender = Simulator.RANDOM_NUMBER_GENERATOR.nextBoolean() ? Gender.FEMALE : Gender.MALE;

        // Set the graphic object of this passenger
        this.passengerGraphic = new PassengerGraphic(this);

        // The identifier of this passenger is its serial number (based on the number of passengers generated)
        this.identifier = passengerCount;

        // Increment the number of passengers made by one
        Passenger.passengerCount++;

        // Get the number of attractors in the gate
        int numAttractors = gate.getAttractors().size();

        // Randomly choose between the attractors to determine where the agent will spawn from
        int randomAttractor = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(numAttractors);

        // Instantiate all movement-related fields
        this.passengerMovement = new PassengerMovement(
                gate,
                this,
                gate.getAttractors().get(randomAttractor).getPatch().getPatchCenterCoordinates()
        );
    }

    public Gender getGender() {
        return gender;
    }

    public PassengerGraphic getPassengerGraphic() {
        return passengerGraphic;
    }

    public PassengerMovement getPassengerMovement() {
        return this.passengerMovement;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public static class PassengerFactory extends StationObjectFactory {
        public Passenger create(Gate gate) {
            return new Passenger(gate);
        }
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

    // Denotes the gender of this passenger
    public enum Gender {
        FEMALE,
        MALE
    }
}
