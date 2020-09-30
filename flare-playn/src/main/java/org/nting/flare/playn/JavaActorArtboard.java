package org.nting.flare.playn;

import static java.lang.Math.round;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorDrawable;
import org.nting.flare.java.maths.AABB;

import playn.core.Canvas;
import playn.core.Color;

// Note Canvas.setUseAntialias() is not supported
public class JavaActorArtboard extends ActorArtboard {

    public JavaActorArtboard(JavaActor actor) {
        super(actor);
    }

    public void draw(Canvas canvas) {
        AABB aabb = artboardAABB();
        if (clipContents()) {
            canvas.save();
            canvas.clipRect(aabb.values()[0], aabb.values()[1], aabb.values()[2] - aabb.values()[0],
                    aabb.values()[3] - aabb.values()[1]);
        }

        canvas.setFillColor(background());
        canvas.fillRect(aabb.values()[0], aabb.values()[1], aabb.values()[2] - aabb.values()[0],
                aabb.values()[3] - aabb.values()[1]);

        if (drawableNodes() != null) {
            for (ActorDrawable drawable : drawableNodes()) {
                if (drawable instanceof JavaActorDrawable) {
                    ((JavaActorDrawable) drawable).draw(canvas);
                }
            }
        }

        if (clipContents()) {
            canvas.restore();
        }
    }

    private int background() {
        float[] c = color();
        return Color.argb(round(c[3] * 255.0f), round(c[0] * 255.0f), round(c[1] * 255.0f), round(c[2] * 255.0f));
    }
}
