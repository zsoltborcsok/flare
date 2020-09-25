package org.nting.flare.playn;

import static java.lang.Math.round;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorBlur;
import org.nting.flare.java.ActorDrawable;
import org.nting.flare.java.ActorDropShadow;
import org.nting.flare.java.ActorInnerShadow;
import org.nting.flare.java.ActorLayerEffectRenderer;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Image;
import playn.core.PlayN;
import playn.core.transform.BlurImageDataTransform;
import playn.core.transform.ColorImageDataTransform;
import playn.core.transform.ImageTransforms;

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

        drawInnerShadows(canvas, blurX, blurY);
    }

    private void drawBlur(Canvas canvas, float blurX, float blurY) {
        CanvasImage canvasImage = PlayN.graphics().createImage(artboard.width(), artboard.height());
        Canvas layerCanvas = canvasImage.canvas();
        float dx = artboard.origin().values()[0] * artboard.width();
        float dy = artboard.origin().values()[1] * artboard.height();
        layerCanvas.translate(dx, dy);
        drawPass(layerCanvas);

        Image blurredImage = ImageTransforms.blur(canvasImage, (int) blurX, (int) blurY);
        canvas.drawImage(blurredImage, -dx, -dy);
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

                float dx = artboard.origin().values()[0] * artboard.width();
                float dy = artboard.origin().values()[1] * artboard.height();

                CanvasImage canvasImage = PlayN.graphics().createImage(artboard.width(), artboard.height());
                Canvas layerCanvas = canvasImage.canvas();
                layerCanvas.translate(dropShadow.offsetX + dx, dropShadow.offsetY + dy);
                drawPass(layerCanvas);
                layerCanvas.translate(-dropShadow.offsetX - dx, -dropShadow.offsetY - dy);
                layerCanvas.setCompositeOperation(Canvas.Composite.SRC_IN);
                layerCanvas.setFillColor(color).fillRect(0, 0, artboard.width(), artboard.height());

                Image blurredImage = ImageTransforms.blur(canvasImage,
                        (int) (dropShadow.blurX * BLUR_COEFFICIENT + baseBlurX),
                        (int) (dropShadow.blurY * BLUR_COEFFICIENT + baseBlurY));
                canvas.drawImage(blurredImage, -dx, -dy);
            }
        }
    }

    private void drawInnerShadows(Canvas canvas, float baseBlurX, float baseBlurY) {
        if (innerShadows() != null) {
            for (ActorInnerShadow innerShadow : innerShadows()) {
                if (!innerShadow.isActive()) {
                    continue;
                }

                float[] c = innerShadow.color();
                int color = Color.argb(round(c[3] * 255.0f), round(c[0] * 255.0f), round(c[1] * 255.0f),
                        round(c[2] * 255.0f));

                float dx = artboard.origin().values()[0] * artboard.width();
                float dy = artboard.origin().values()[1] * artboard.height();

                CanvasImage canvasImage = PlayN.graphics().createImage(artboard.width(), artboard.height());
                Canvas layerCanvas = canvasImage.canvas();
                layerCanvas.translate(innerShadow.offsetX + dx, innerShadow.offsetY + dy);
                drawPass(layerCanvas);
                layerCanvas.translate(-innerShadow.offsetX - dx, -innerShadow.offsetY - dy);
                layerCanvas.setCompositeOperation(Canvas.Composite.SRC_IN);
                layerCanvas.setFillColor(color).fillRect(0, 0, artboard.width(), artboard.height());
                Image blurredImage = ImageTransforms.transformToNew(canvasImage,
                        new BlurImageDataTransform((int) (innerShadow.blurX * BLUR_COEFFICIENT + baseBlurX),
                                (int) (innerShadow.blurY * BLUR_COEFFICIENT + baseBlurY))
                                        .andThen(ColorImageDataTransform.INVERT_ALPHA));
                layerCanvas.drawImage(blurredImage, innerShadow.offsetX, innerShadow.offsetY);

                canvas.drawImage(canvasImage, -innerShadow.offsetX - dx, -innerShadow.offsetY - dy);
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
