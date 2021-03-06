package org.nting.flare.playn;

import static java.lang.Math.round;
import static org.nting.flare.java.maths.Floats.clamp;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.ActorFill;
import org.nting.flare.java.GradientFill;
import org.nting.flare.java.maths.Vec2D;

import playn.core.Canvas;
import playn.core.Color;
import playn.core.Path;
import playn.core.PlayN;

public class JavaGradientFill extends GradientFill implements JavaFill {

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        JavaGradientFill instanceNode = new JavaGradientFill();
        instanceNode.copyGradientFill(this, resetArtboard);
        return instanceNode;
    }

    @Override
    public void paint(ActorFill fill, Canvas canvas, Path path) {
        float[] colorStops = colorStops();
        int numStops = round(colorStops.length / 5.0f);
        int[] colors = new int[numStops];
        float[] stops = new float[numStops];

        int idx = 0;
        for (int i = 0; i < numStops; i++) {
            float o = clamp(colorStops[idx + 3], 0.0f, 1.0f);
            int color = Color.argb(round(o * 255.0f), round(colorStops[idx] * 255.0f),
                    round(colorStops[idx + 1] * 255.0f), round(colorStops[idx + 2] * 255.0f));
            colors[i] = color;
            stops[i] = colorStops[idx + 4];
            idx += 5;
        }

        int paintColor;
        if (artboard.overrideColor() == null) {
            float o = clamp(artboard.modulateOpacity() * opacity() * shape().renderOpacity(), 0.0f, 1.0f);
            paintColor = Color.withAlpha(0xFFFFFFFF, round(o * 255.0f));
        } else {
            float[] overrideColor = artboard.overrideColor();
            float o = clamp(overrideColor[3] * artboard.modulateOpacity() * opacity() * shape().renderOpacity(), 0.0f,
                    1.0f);
            paintColor = Color.argb(round(o * 255.0f), round(overrideColor[0] * 255.0f),
                    round(overrideColor[1] * 255.0f), round(overrideColor[2] * 255.0f));
        }

        Vec2D renderStart = renderStart();
        Vec2D renderEnd = renderEnd();

        canvas.save();
        try {
            JavaActorShape parentShape = (JavaActorShape) parent();
            canvas.setFillColor(paintColor);
            // canvas.setUseAntialias() is not supported
            canvas.setCompositeOperation(parentShape.blendMode().getComposite());
            if (0 < numStops) {
                canvas.setFillGradient(PlayN.graphics().createLinearGradient(renderStart.values()[0],
                        renderStart.values()[1], renderEnd.values()[0], renderEnd.values()[1], colors, stops));
            }
            JavaFill.super.paint(fill, canvas, path);
        } finally {
            canvas.restore();
        }
    }
}
