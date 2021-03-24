package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal;

import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.BlockableAmenity;

public class Turnstile extends BlockableAmenity {
    // Denotes the current mode of this turnstile
    private TurnstileMode turnstileMode;

    public Turnstile(
            Patch patch,
            boolean enabled,
            int waitingTime,
            boolean blockEntry,
            TurnstileMode turnstileMode
    ) {
        super(patch, enabled, waitingTime, blockEntry);

        this.turnstileMode = turnstileMode;
    }

    public TurnstileMode getTurnstileMode() {
        return turnstileMode;
    }

    public void setTurnstileMode(TurnstileMode turnstileMode) {
        this.turnstileMode = turnstileMode;
    }

    @Override
    public String toString() {
        return "Turnstile";
    }

    // Turnstile factory
    public static class TurnstileFactory extends AmenityFactory {
        @Override
        public Amenity create(Patch patch, Object... objects) {
            return new Turnstile(
                    patch,
                    (boolean) objects[0],
                    (int) objects[1],
                    (boolean) objects[2],
                    (TurnstileMode) objects[3]
            );
        }
    }

    // Lists the possible modes of this turnstile
    public enum TurnstileMode {
        BOARDING,
        ALIGHTING,
        BIDIRECTIONAL
    }
}
