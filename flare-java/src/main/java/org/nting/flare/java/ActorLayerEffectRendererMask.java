package org.nting.flare.java;

import java.util.ArrayList;
import java.util.List;

public class ActorLayerEffectRendererMask {

    public final ActorMask mask;
    public final List<ActorDrawable> drawables = new ArrayList<>();

    public ActorLayerEffectRendererMask(ActorMask mask) {
        this.mask = mask;
    }
}
