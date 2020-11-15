package sample;

import javafx.scene.paint.Color;

import java.util.Objects;
import java.util.Random;

public class Passenger {
    // Keep track of the number of passengers generated
    private static int passengerCount = 0;
    private final int identifier;
    private final Color color;
    private final PassengerMovement passengerMovement;

    public Passenger(double x, double y, int numGoals) {
        Random randomColor = new Random();

        this.color = Color.color(randomColor.nextDouble(), randomColor.nextDouble(), randomColor.nextDouble());

        // The identifier of this passenger is its serial number (based on the number of passengers generated)
        this.identifier = passengerCount;

        // Increment the number of passengers made by one
        Passenger.passengerCount++;

        // Instantiate all movement-related fields
        this.passengerMovement = new PassengerMovement(this, x, y, numGoals);
    }

    public Color getColor() {
        return color;
    }

    public PassengerMovement getPassengerMovement() {
        return this.passengerMovement;
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
