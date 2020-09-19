package org.nting.flare.playn;

import static java.lang.Math.round;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorDrawable;
import org.nting.flare.java.ActorDropShadow;
import org.nting.flare.java.ActorLayerEffectRenderer;

import playn.core.BlurImageFilter;
import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.PlayN;

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
        // TODO Paint blur

        drawDropShadows(canvas);

        drawPass(canvas);

        // TODO Paint inner shadows
    }

    private void drawDropShadows(Canvas canvas) {
        if (!dropShadows().isEmpty()) {
            for (ActorDropShadow dropShadow : dropShadows()) {
                if (!dropShadow.isActive()) {
                    continue;
                }

                canvas.save();
                float[] c = dropShadow.color();
                int color = Color.argb(round(c[3] * 255.0f), round(c[0] * 255.0f), round(c[1] * 255.0f),
                        round(c[2] * 255.0f));

                CanvasImage canvasImage = PlayN.graphics().createImage(artboard.width(), artboard.height());
                Canvas layerCanvas = canvasImage.canvas();
                layerCanvas.translate(dropShadow.offsetX, dropShadow.offsetY);
                drawPass(layerCanvas);
                layerCanvas.translate(-dropShadow.offsetX, -dropShadow.offsetY);
                layerCanvas.setFillColor(color);
                layerCanvas.setCompositeOperation(Canvas.Composite.SRC_IN);
                layerCanvas.fillRect(0, 0, artboard.width(), artboard.height());

                BlurImageFilter blurImageFilter = new BlurImageFilter((int) dropShadow.blurX, (int) dropShadow.blurY);
                canvas.drawImage(blurImageFilter.apply(canvasImage), 0, 0);

                canvas.restore();
            }
        }
    }

    private void drawPass(Canvas canvas) {
        for (ActorDrawable drawable : drawables()) {
            if (drawable instanceof JavaActorDrawable) {
                ((JavaActorDrawable) drawable).draw(canvas);
            }
        }

        // TODO paint masks
    }
}
