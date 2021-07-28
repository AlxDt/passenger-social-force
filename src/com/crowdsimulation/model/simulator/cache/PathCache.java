package com.crowdsimulation.model.simulator.cache;

import com.crowdsimulation.model.core.agent.passenger.movement.PassengerPath;
import com.crowdsimulation.model.core.environment.station.patch.Patch;

import java.util.LinkedHashMap;
import java.util.Map;

public class PathCache extends Cache {
    private static final int CAPACITY = 100;

    private final LinkedHashMap<Patch.PatchPair, PassengerPath> pairPathMap;

    public PathCache() {
        this.pairPathMap = new LinkedHashMap<Patch.PatchPair, PassengerPath>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Patch.PatchPair, PassengerPath> eldest) {
                return size() > CAPACITY;
            }
        };
    }

    public void put(Patch.PatchPair patchPair, PassengerPath passengerPath) {
        this.pairPathMap.put(patchPair, passengerPath);
    }

    public PassengerPath get(Patch.PatchPair patchPair) {
        return this.pairPathMap.get(patchPair);
    }

    public void clear() {
        this.pairPathMap.clear();
    }
}
