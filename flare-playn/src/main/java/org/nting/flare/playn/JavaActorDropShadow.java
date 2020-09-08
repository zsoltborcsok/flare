package org.nting.flare.playn;

import org.nting.flare.java.ActorDropShadow;

public class JavaActorDropShadow extends ActorDropShadow {

    private BlendMode blendMode;

    @Override
    public int blendModeId() {
        return blendMode.ordinal();
    }

    @Override
    public void blendModeId(int index) {
        blendMode = BlendMode.values()[index];
    }
}
