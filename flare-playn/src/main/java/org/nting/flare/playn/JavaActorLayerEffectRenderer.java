package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorDrawable;
import org.nting.flare.java.ActorLayerEffectRenderer;
import org.nting.flare.java.maths.AABB;

import playn.core.Canvas;
import pythagoras.f.Rectangle;

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
        AABB aabb = artboard.artboardAABB();
        Rectangle bounds = new Rectangle(aabb.values()[0], aabb.values()[1], aabb.values()[2] - aabb.values()[0],
                aabb.values()[3] - aabb.values()[1]);

        // TODO Paint blur

        // TODO Paint drop shadows

        drawPass(canvas, bounds);

        // TODO Paint inner shadows
    }

    void drawPass(Canvas canvas, Rectangle bounds) {
        for (ActorDrawable drawable : drawables()) {
            if (drawable instanceof JavaActorDrawable) {
                ((JavaActorDrawable) drawable).draw(canvas);
            }
        }

        // TODO paint masks
    }
}
