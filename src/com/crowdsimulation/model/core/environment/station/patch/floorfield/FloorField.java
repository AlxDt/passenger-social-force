package com.crowdsimulation.model.core.environment.station.patch.floorfield;

import com.crowdsimulation.model.core.agent.passenger.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.BaseStationObject;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;

import java.util.ArrayList;
import java.util.List;

// A floor field is a group of patches with values from 0.0 to 1.0 corresponding to a state
public abstract class FloorField extends BaseStationObject {
    // Denotes the patches contained in this floor field
    private final List<Patch> associatedPatches;

    /*    private double value;*/

    public FloorField() {
        super();

        /*//        this.value = 0.0;*/
        this.associatedPatches = new ArrayList<>();
/*
        this.goal = null;
        this.apex = null;*/
    }

/*    public double getValue() {
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
    }*/

/*    public FloorField(QueueObject goal, Patch apex) {
        super();

        this.associatedPatches = new ArrayList<>();

        this.goal = goal;
        this.apex = apex;
    }*/

    public List<Patch> getAssociatedPatches() {
        return associatedPatches;
    }

/*    public Patch getApex() {
        return apex;
    }

    public QueueObject getGoal() {
        return goal;
    }

    public void setApex(Patch apex) {
        this.apex = apex;
    }

    public void setGoal(QueueObject goal) {
        this.goal = goal;
    }*/

/*    @Override
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
    }*/
}
