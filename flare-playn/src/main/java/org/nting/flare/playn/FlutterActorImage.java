package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorImage;
import playn.core.Canvas;

public class FlutterActorImage extends ActorImage implements FlutterActorDrawable {

    @Override
    public ActorArtboard artboard() {
        return null; // TODO
    }

    @Override
    public void blendModeId(int blendModeId) {
        super.blendModeId(blendModeId);
        // onPaintUpdated();
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO not yet supported
    }
}
