package sample;

import java.util.Objects;

public class FloorField {
    private double value;
    private Patch goal;
    private Patch apex;

    public FloorField() {
        this.value = 0.0;
        this.goal = null;
        this.apex = null;
    }

    public FloorField(double value, Patch goal) {
        this.value = value;
        this.goal = goal;
        this.apex = null;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Patch getGoal() {
        return goal;
    }

    public void setGoal(Patch goal) {
        this.goal = goal;
    }

    public Patch getApex() {
        return apex;
    }

    public void setApex(Patch apex) {
        this.apex = apex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloorField that = (FloorField) o;
        return Double.compare(that.value, value) == 0 &&
                Objects.equals(goal, that.goal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, goal);
    }
}
