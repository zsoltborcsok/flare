package org.nting.flare.playn;

import org.nting.flare.java.ActorInnerShadow;

public class JavaActorInnerShadow extends ActorInnerShadow {

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
