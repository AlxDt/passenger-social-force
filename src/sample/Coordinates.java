package sample;

import java.util.Objects;

// Represents a pair of 2D Cartesian coordinates
public class Coordinates {
    private double x;
    private double y;

    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Get the central coordinates of the given patch
    public static Coordinates patchCenterCoordinates(Patch patch) {
        // Retrieve the row and column positions of the patch
        double column = patch.getMatrixPosition().getColumn();
        double row = patch.getMatrixPosition().getRow();

        // Set the centered x and y coordinates
        double centeredX = column + 0.5;
        double centeredY = row + 0.5;

        return new Coordinates(centeredX, centeredY);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
