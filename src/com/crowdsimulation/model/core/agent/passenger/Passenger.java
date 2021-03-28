package com.crowdsimulation.model.core.agent.passenger;

import com.crowdsimulation.model.core.agent.Agent;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.PatchObject;
import javafx.scene.paint.Color;

import java.util.Objects;
import java.util.Random;

public class Passenger extends PatchObject implements Agent {
    // Keep track of the number of passengers generated
    private static int passengerCount = 0;

    // Denotes the serial number of this passenger
    private final int identifier;

    // TODO: Maybe move somewhere?
    // Denotes the color of this passenger (as displayed on the interface)
    private final Color color;

    // Contains the mechanisms for this passenger's movement
    private final PassengerMovement passengerMovement;

    public Passenger(double x, double y, int numGoals) {
        // TODO: Set color options from interface
        Random randomColor = new Random();

        this.color = Color.color(randomColor.nextDouble(), randomColor.nextDouble(), randomColor.nextDouble());

        // The identifier of this passenger is its serial number (based on the number of passengers generated)
        this.identifier = passengerCount;

        // Increment the number of passengers made by one
        Passenger.passengerCount++;

        // Instantiate all movement-related fields
        this.passengerMovement = new PassengerMovement(/*this, x, y, numGoals*/);
    }

    public Color getColor() {
        return color;
    }

    public PassengerMovement getPassengerMovement() {
        return this.passengerMovement;
    }

    public int getIdentifier() {
        return this.identifier;
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