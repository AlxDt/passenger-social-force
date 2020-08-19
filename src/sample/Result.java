package sample;
//package sample;
//
//import java.util.List;
//import java.util.Objects;
//
//public class Result {
//    final private Cell coordinate;
//    final private Result parent;
//    final private List<Cell> directions;
//    private double score;
//
//    public Result(Cell cell, double score, Result parent, List<Cell> directions) {
//        this.coordinate = cell;
//        this.score = score;
//        this.parent = parent;
//        this.directions = directions;
//    }
//
//    public Cell getCoordinate() {
//        return coordinate;
//    }
//
//    public double getScore() {
//        return score;
//    }
//
//    public void setScore(double score) {
//        this.score = score;
//    }
//
//    public Result getParent() {
//        return parent;
//    }
//
//    public List<Cell> getDirections() {
//        return directions;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Result result = (Result) o;
//        return coordinate.equals(result.coordinate);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(coordinate);
//    }
//}

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Result {
    private final Patch patch;
    private final Result parent;
    private final Main.Direction direction;
    private double f;
    private double g;
    private double h;

    public Result(Patch patch, Result parent, Main.Direction direction, double g, double h) {
        this.patch = patch;
        this.parent = parent;
        this.direction = direction;

        this.g = g;
        this.h = h;
        this.f = g + h;
    }

    public Patch getPatch() {
        return patch;
    }

    public Result getParent() {
        return parent;
    }

    public Main.Direction getDirection() {
        return direction;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public List<Result> generateNeighbors(int layer) {
        // TODO: Obstacle
        List<Result> neighbors = new ArrayList<>();

        Result newResult;
        int newRow;
        int newCol;

        // Left
        if (patch.getMatrixPosition().getCol() > 0) {
            newRow = patch.getMatrixPosition().getRow();
            newCol = patch.getMatrixPosition().getCol() - 1;

            Patch neighborPatch = Main.region.getPatch(newRow, newCol);

            newResult = new Result(neighborPatch,
                    this,
                    Main.Direction.L,
                    this.g + 1,
                    Main.region.getAttraction(layer, newRow, newCol)
            );

            neighbors.add(newResult);
        }

        // Right
        if (patch.getMatrixPosition().getCol() < Main.region.getCols() - 1) {
            newRow = patch.getMatrixPosition().getRow();
            newCol = patch.getMatrixPosition().getCol() + 1;

            Patch neighborPatch = Main.region.getPatch(newRow, newCol);

            newResult = new Result(neighborPatch,
                    this,
                    Main.Direction.R,
                    this.g + 1,
                    Main.region.getAttraction(layer, newRow, newCol)
            );

            neighbors.add(newResult);
        }

        // Up
        if (patch.getMatrixPosition().getRow() > 0) {
            newRow = patch.getMatrixPosition().getRow() - 1;
            newCol = patch.getMatrixPosition().getCol();

            Patch neighborPatch = Main.region.getPatch(newRow, newCol);

            newResult = new Result(neighborPatch,
                    this,
                    Main.Direction.U,
                    this.g + 1,
                    Main.region.getAttraction(layer, newRow, newCol)
            );

            neighbors.add(newResult);
        }

        // Down
        if (patch.getMatrixPosition().getRow() < Main.region.getRows() - 1) {
            newRow = patch.getMatrixPosition().getRow() + 1;
            newCol = patch.getMatrixPosition().getCol();

            Patch neighborPatch = Main.region.getPatch(newRow, newCol);

            newResult = new Result(neighborPatch,
                    this,
                    Main.Direction.D,
                    this.g + 1,
                    Main.region.getAttraction(layer, newRow, newCol)
            );

            neighbors.add(newResult);
        }

        // Upper left
        if (patch.getMatrixPosition().getRow() > 0 && patch.getMatrixPosition().getCol() > 0) {
            newRow = patch.getMatrixPosition().getRow() - 1;
            newCol = patch.getMatrixPosition().getCol() - 1;

            Patch neighborPatch = Main.region.getPatch(newRow, newCol);

            newResult = new Result(neighborPatch,
                    this,
                    Main.Direction.UL,
                    this.g + 1,
                    Main.region.getAttraction(layer, newRow, newCol)
            );

            neighbors.add(newResult);
        }

        // Lower left
        if (patch.getMatrixPosition().getRow() < Main.region.getRows() - 1 && patch.getMatrixPosition().getCol() > 0) {
            newRow = patch.getMatrixPosition().getRow() + 1;
            newCol = patch.getMatrixPosition().getCol() - 1;

            Patch neighborPatch = Main.region.getPatch(newRow, newCol);

            newResult = new Result(neighborPatch,
                    this,
                    Main.Direction.DL,
                    this.g + 1,
                    Main.region.getAttraction(layer, newRow, newCol)
            );

            neighbors.add(newResult);
        }

        // Upper right
        if (patch.getMatrixPosition().getRow() > 0 && patch.getMatrixPosition().getCol() < Main.region.getCols() - 1) {
            newRow = patch.getMatrixPosition().getRow() - 1;
            newCol = patch.getMatrixPosition().getCol() + 1;

            Patch neighborPatch = Main.region.getPatch(newRow, newCol);

            newResult = new Result(neighborPatch,
                    this,
                    Main.Direction.UR,
                    this.g + 1,
                    Main.region.getAttraction(layer, newRow, newCol)
            );

            neighbors.add(newResult);
        }

        // Lower right
        if (patch.getMatrixPosition().getRow() < Main.region.getRows() - 1 && patch.getMatrixPosition().getCol() < Main.region.getCols() - 1) {
            newRow = patch.getMatrixPosition().getRow() + 1;
            newCol = patch.getMatrixPosition().getCol() + 1;

            Patch neighborPatch = Main.region.getPatch(newRow, newCol);

            newResult = new Result(neighborPatch,
                    this,
                    Main.Direction.DR,
                    this.g + 1,
                    Main.region.getAttraction(layer, newRow, newCol)
            );

            neighbors.add(newResult);
        }

        return neighbors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        return patch.equals(result.patch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patch);
    }
}
