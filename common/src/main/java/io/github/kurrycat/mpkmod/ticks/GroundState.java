package io.github.kurrycat.mpkmod.ticks;

public enum GroundState {
    GROUNDED(false, true),
    AIRBORNE(false, false),
    JUMPING(true, false);

    public final boolean jump, ground;

    GroundState(boolean jump, boolean ground) {
        this.jump = jump;
        this.ground = ground;
    }

    public static GroundState fromBooleans(boolean jump, boolean ground) {
        if (jump && ground)
            throw new IllegalArgumentException("Cannot be both jumping and on ground");

        if (jump) return JUMPING;
        if (ground) return GROUNDED;
        return AIRBORNE;
    }
}
