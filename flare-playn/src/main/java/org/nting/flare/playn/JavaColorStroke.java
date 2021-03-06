package org.nting.flare.playn;

import static java.lang.Math.round;
import static org.nting.flare.java.maths.Floats.clamp;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.ActorStroke;
import org.nting.flare.java.ColorStroke;
import org.nting.flare.playn.util.Capture;

import playn.core.Canvas;
import playn.core.Color;
import playn.core.Path;

public class JavaColorStroke extends ColorStroke implements JavaStroke {

    private final Capture<Path> effectPath = new Capture<>();

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        JavaColorStroke instanceNode = new JavaColorStroke();
        instanceNode.copyColorStroke(this, resetArtboard);
        return instanceNode;
    }

    public int uiColor() {
        float[] c = displayColor();
        float o = clamp(artboard.modulateOpacity() * opacity() * shape().renderOpacity(), 0.0f, 1.0f);
        return Color.argb(round(c[3] * o * 255.0f), round(c[0] * 255.0f), round(c[1] * 255.0f), round(c[2] * 255.0f));
    }

    public void uiColor(int c) {
        color(new float[] { Color.red(c) / 255.0f, Color.green(c) / 255.0f, Color.blue(c) / 255.0f,
                Color.alpha(c) / 255.0f });
    }

    @Override
    public Capture<Path> effectPath() {
        return effectPath;
    }

    @Override
    public void paint(ActorStroke stroke, Canvas canvas, Path path) {
        canvas.save();
        try {
            JavaActorShape parentShape = (JavaActorShape) parent();
            canvas.setStrokeColor(uiColor());
            canvas.setStrokeWidth(stroke.width());
            // canvas.setUseAntialias() is not supported
            canvas.setCompositeOperation(parentShape.blendMode().getComposite());
            JavaStroke.super.paint(stroke, canvas, path);
        } finally {
            canvas.restore();
        }
    }

    @Override
    public void markPathEffectsDirty() {
        JavaStroke.super.markPathEffectsDirty();
    }
}
