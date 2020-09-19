package org.nting.flare.playn;

import static java.lang.Math.round;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorBlur;
import org.nting.flare.java.ActorDrawable;
import org.nting.flare.java.ActorDropShadow;
import org.nting.flare.java.ActorLayerEffectRenderer;

import playn.core.BlurImageFilter;
import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.PlayN;

public class JavaActorLayerEffectRenderer extends ActorLayerEffectRenderer implements JavaActorDrawable {

    private static float BLUR_COEFFICIENT = 2.0f; // It helps to mimic the flare's blur painting.

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
        float blurX = 0;
        float blurY = 0;
        ActorBlur blur = blur();
        if (blur != null && blur.isActive()) {
            blurX = blur.blurX * BLUR_COEFFICIENT;
            blurY = blur.blurY * BLUR_COEFFICIENT;
        }

        drawDropShadows(canvas, blurX, blurY);

        if (blur != null && blur.isActive()) {
            drawBlur(canvas, blurX, blurY);
        } else {
            drawPass(canvas);
        }

        // TODO Paint inner shadows
    }

    private void drawBlur(Canvas canvas, float blurX, float blurY) {
        CanvasImage canvasImage = PlayN.graphics().createImage(artboard.width(), artboard.height());
        Canvas layerCanvas = canvasImage.canvas();
        drawPass(layerCanvas);

        BlurImageFilter blurImageFilter = new BlurImageFilter((int) blurX, (int) blurY);
        canvas.drawImage(blurImageFilter.apply(canvasImage), 0, 0);
    }

    private void drawDropShadows(Canvas canvas, float baseBlurX, float baseBlurY) {
        if (dropShadows() != null) {
            for (ActorDropShadow dropShadow : dropShadows()) {
                if (!dropShadow.isActive()) {
                    continue;
                }

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

                BlurImageFilter blurImageFilter = new BlurImageFilter(
                        (int) (dropShadow.blurX * BLUR_COEFFICIENT + baseBlurX),
                        (int) (dropShadow.blurY * BLUR_COEFFICIENT + baseBlurY));
                canvas.drawImage(blurImageFilter.apply(canvasImage), 0, 0);
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
