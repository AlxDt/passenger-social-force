package com.crowdsimulation.model.simulator.cache;

import com.crowdsimulation.model.core.agent.passenger.movement.PassengerPath;
import com.crowdsimulation.model.core.environment.Environment;
import com.crowdsimulation.model.core.environment.station.patch.Patch;

import java.util.LinkedHashMap;
import java.util.Map;

public class PathCache extends Cache {
    private static final int CAPACITY = 100;

    private final LinkedHashMap<PathCacheKey, PassengerPath> pairPathMap;

    public PathCache() {
        this.pairPathMap = new LinkedHashMap<PathCacheKey, PassengerPath>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<PathCacheKey, PassengerPath> eldest) {
                return size() > CAPACITY;
            }
        };
    }

    public void put(PathCacheKey pathCacheKey, PassengerPath passengerPath) {
        this.pairPathMap.put(pathCacheKey, passengerPath);
    }

    public PassengerPath get(PathCacheKey pathCacheKey) {
        return this.pairPathMap.get(pathCacheKey);
    }

    public void clear() {
        this.pairPathMap.clear();
    }

    public static class PathCacheKey implements Environment {
        private final Patch.PatchPair patchPair;
        private final boolean includeStartingPatch;
        private final boolean includeGoalPatch;

        public PathCacheKey(Patch.PatchPair patchPair, boolean includeStartingPatch, boolean includeGoalPatch) {
            this.patchPair = patchPair;
            this.includeStartingPatch = includeStartingPatch;
            this.includeGoalPatch = includeGoalPatch;
        }

        public Patch.PatchPair getPatchPair() {
            return patchPair;
        }

        public boolean isIncludeStartingPatch() {
            return includeStartingPatch;
        }

        public boolean isIncludeGoalPatch() {
            return includeGoalPatch;
        }
    }
}
