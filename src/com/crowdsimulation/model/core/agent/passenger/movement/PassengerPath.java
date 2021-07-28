package com.crowdsimulation.model.core.agent.passenger.movement;

import com.crowdsimulation.model.core.environment.Environment;
import com.crowdsimulation.model.core.environment.station.patch.Patch;

import java.util.Stack;

public class PassengerPath implements Environment {
    private final Stack<Patch> path;
    private final double length;

    public PassengerPath(Stack<Patch> path, double length) {
        this.path = path;
        this.length = length;
    }

    public Stack<Patch> getPath() {
        return path;
    }

    public double getLength() {
        return length;
    }
}
