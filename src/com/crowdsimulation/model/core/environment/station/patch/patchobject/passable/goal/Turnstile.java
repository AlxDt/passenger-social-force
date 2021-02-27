package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal;

import com.crowdsimulation.model.core.environment.station.patch.Patch;

public class Turnstile extends Goal {
    // Denotes the current mode of this turnstile
    private TurnstileMode turnstileMode;

    public Turnstile() {
        super(null,true, 0);

        this.turnstileMode = null;
    }

    public Turnstile(
            Patch patch,
            boolean enabled,
            int waitingTime,
            TurnstileMode turnstileMode
    ) {
        super(patch, enabled, waitingTime);

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

    // Lists the possible modes of this turnstile
    public enum TurnstileMode {
        BOARDING,
        ALIGHTING,
        BIDIRECTIONAL
    }
}
