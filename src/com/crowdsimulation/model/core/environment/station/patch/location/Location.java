package com.crowdsimulation.model.core.environment.station.patch.location;

import com.crowdsimulation.model.core.environment.Environment;
import com.crowdsimulation.model.core.environment.station.BaseStationObject;

public abstract class Location extends BaseStationObject implements Environment {
    // Convert the given continuous screen coordinates to a discrete row and column
    public static MatrixPosition screenCoordinatesToMatrixPosition(double x, double y, double tileSize) {
        // Get the raw position of the mouse click
        double rawX = x;
        double rawY = y;

        // Get the scaled position from the raw position to match the specified tile size
        double scaledX = rawX / tileSize;
        double scaledY = rawY / tileSize;

        // Get the truncated position from the scaled position
        int truncatedX = (int) Math.floor(scaledX);
        int truncatedY = (int) Math.floor(scaledY);

        return new MatrixPosition(truncatedY, truncatedX);
    }
}
