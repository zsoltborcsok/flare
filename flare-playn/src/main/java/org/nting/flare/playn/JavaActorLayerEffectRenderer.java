package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorLayerEffectRenderer;

import playn.core.Canvas;

public class JavaActorLayerEffectRenderer extends ActorLayerEffectRenderer implements JavaActorDrawable {

    @Override
    public ActorArtboard artboard() {
        return artboard;
    }

    @Override
    public void blendModeId(int blendModeId) {
        // We don't currently support custom blend modes on the layer effect renderer
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO not yet supported
    }
}
