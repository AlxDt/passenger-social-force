package sample;

import java.util.Objects;

public class FloorField {
    private double value;
    private Patch association;

    public FloorField() {
        this.value = 0.0;
        this.association = null;
    }

    public FloorField(double value, Patch association) {
        this.value = value;
        this.association = association;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Patch getAssociation() {
        return association;
    }

    public void setAssociation(Patch association) {
        this.association = association;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloorField that = (FloorField) o;
        return Double.compare(that.value, value) == 0 &&
                Objects.equals(association, that.association);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, association);
    }
}
