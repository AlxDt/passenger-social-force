package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate;

import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.NonObstacle;
import com.crowdsimulation.model.simulator.Simulator;

import java.util.List;

public abstract class Gate extends NonObstacle implements Drawable {
    protected Gate(List<AmenityBlock> amenityBlocks, boolean enabled) {
        super(amenityBlocks, enabled);
    }

    // Spawn a passenger in this position
    public Passenger spawnPassenger() {
        // Get the number of attractors in the gate
        int numAttractors = this.getAttractors().size();

        // Randomly choose between the attractors to determine where the agent will spawn from
        int randomAttractorIndex = Simulator.RANDOM_NUMBER_GENERATOR.nextInt(numAttractors);

        // Get a random attractor
        AmenityBlock attractor = this.getAttractors().get(0);

        // If that random attractor is free from passengers, generate one
        if (attractor.getPatch().getPassengers().isEmpty()) {
            return Passenger.passengerFactory.create(attractor.getPatch());
        } else {
            // Else, do nothing, so return null
            return null;
        }
    }

    // Despawn a passenger in this position
    public void despawnPassenger(Passenger passenger) {
        passenger.getPassengerMovement().despawnPassenger();
    }

    // Gate factory
    public static abstract class GateFactory extends NonObstacleFactory {
    }
}
