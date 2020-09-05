package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorDrawable;
import org.nting.flare.java.maths.AABB;

import playn.core.Canvas;

// Note Canvas.setUseAntialias() is not supported
public class FlutterActorArtboard extends ActorArtboard {

    public FlutterActorArtboard(FlutterActor actor) {
        super(actor);
    }

    public void draw(Canvas canvas) {
        if (clipContents()) {
            canvas.save();
            AABB aabb = artboardAABB();
            canvas.clipRect(aabb.values()[0], aabb.values()[1], aabb.values()[2] - aabb.values()[0],
                    aabb.values()[3] - aabb.values()[1]);
        }
        if (drawableNodes() != null) {
            for (ActorDrawable drawable : drawableNodes()) {
                if (drawable instanceof FlutterActorDrawable) {
                    ((FlutterActorDrawable) drawable).draw(canvas);
                }
            }
        }
        if (clipContents()) {
            canvas.restore();
        }
    }
}
