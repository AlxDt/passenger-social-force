package com.crowdsimulation.model.core.environment.station.patch.position;

import java.util.List;

public class Vector {
    private Coordinates startingPosition;
    private double heading;
    private Coordinates futurePosition;

    private double xDisplacement;
    private double yDisplacement;

    public Vector() {

    }

    public Vector(
            Coordinates startingPosition,
            double heading,
            Coordinates futurePosition
    ) {
        setVector(
                startingPosition,
                heading,
                futurePosition
        );
    }

    public Vector(
            Coordinates startingPosition,
            double heading,
            Coordinates futurePosition,
            double xDisplacement,
            double yDisplacement
    ) {
        this.startingPosition = startingPosition;
        this.heading = heading;
        this.futurePosition = futurePosition;
        this.xDisplacement = xDisplacement;
        this.yDisplacement = yDisplacement;
    }

    public Vector(Vector vector) {
        this.startingPosition = vector.getStartingPosition();
        this.heading = vector.getHeading();
        this.futurePosition = vector.getFuturePosition();

        this.xDisplacement = vector.getXDisplacement();
        this.yDisplacement = vector.yDisplacement;
    }

    // Given the magnitude and direction, compute for the x and y displacements of the vector
    public void setVector(
            Coordinates currentPosition,
            double currentHeading,
            Coordinates futurePosition
    ) {
        // Set the known values first
        this.startingPosition = currentPosition;
        this.heading = currentHeading;
        this.futurePosition = futurePosition;

        // Then from these known values, compute the displacement values
        // They will be needed for adding and subtracting vectors
        this.xDisplacement = this.futurePosition.getX() - this.startingPosition.getX();
        this.yDisplacement = this.futurePosition.getY() - this.startingPosition.getY();
    }

    public Coordinates getStartingPosition() {
        return startingPosition;
    }

    public double getHeading() {
        return heading;
    }

    public Coordinates getFuturePosition() {
        return futurePosition;
    }

    public double getXDisplacement() {
        return xDisplacement;
    }

    public double getYDisplacement() {
        return yDisplacement;
    }

    // Given a list of vectors, compute for the resultant vector - the sum of all such vectors
    public static Vector computeResultantVector(Coordinates startingPosition, List<Vector> vectors) {
        double sumX = 0.0;
        double sumY = 0.0;

        // Get the sum of the vector displacements
        for (Vector vector : vectors) {
            // Ignore null vectors, if any is encountered in the list
            if (vector != null) {
                sumX += vector.getXDisplacement();
                sumY += vector.getYDisplacement();
            }
        }

        // Compute for the ending position, given the computed displacements
        double endX = startingPosition.getX() + sumX;
        double endY = startingPosition.getY() + sumY;
        Coordinates endingPosition = new Coordinates(endX, endY);

        // Finally, compute for the heading from the starting to the ending position
        double newHeading = Coordinates.headingTowards(startingPosition, endingPosition);

        // If the heading is NaN, this means the result of the vector superposition ended up in the starting point
        // That is, there is no vector produced
        // In this case, return null
        if (!Double.isNaN(newHeading)) {
            // Then return the resultant vector given the computed values
            return new Vector(
                    startingPosition,
                    newHeading,
                    endingPosition,
                    sumX,
                    sumY
            );
        } else {
            return null;
        }
    }
}
