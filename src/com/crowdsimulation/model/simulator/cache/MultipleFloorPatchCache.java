package com.crowdsimulation.model.simulator.cache;

import com.crowdsimulation.model.core.agent.passenger.movement.pathfinding.MultipleFloorPassengerPath;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class MultipleFloorPatchCache extends Cache {
    private final Map<PathCache.PathCacheKey, MultipleFloorPassengerPath> pairPathMap;

    public MultipleFloorPatchCache(int capacity) {
        super(capacity);

        this.pairPathMap = Collections.synchronizedMap(
                new LinkedHashMap<PathCache.PathCacheKey, MultipleFloorPassengerPath>() {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<PathCache.PathCacheKey, MultipleFloorPassengerPath> eldest) {
                        return size() > capacity;
                    }
                }
        );
    }

    public void put(PathCache.PathCacheKey pathCacheKey, MultipleFloorPassengerPath passengerPath) {
        this.pairPathMap.put(pathCacheKey, passengerPath);
    }

    public MultipleFloorPassengerPath get(PathCache.PathCacheKey pathCacheKey) {
        return this.pairPathMap.get(pathCacheKey);
    }

    public void clear() {
        this.pairPathMap.clear();
    }
}
