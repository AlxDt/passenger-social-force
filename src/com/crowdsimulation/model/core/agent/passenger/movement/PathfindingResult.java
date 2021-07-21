package com.crowdsimulation.model.core.agent.passenger.movement;

import com.crowdsimulation.model.core.environment.station.patch.Patch;

import java.util.List;

public class PathfindingResult {
    private final List<Patch> path;
    private final double distance;

    public PathfindingResult(List<Patch> path, double distance) {
        this.path = path;
        this.distance = distance;
    }

    public List<Patch> getPath() {
        return path;
    }

    public double getDistance() {
        return distance;
    }
}
