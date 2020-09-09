package org.nting.flare.playn.component;

import static java.lang.Float.max;
import static java.lang.Float.min;
import static org.nting.toolkit.ui.style.material.MaterialColorPalette.amber_100;
import static org.nting.toolkit.ui.style.material.MaterialColorPalette.brown_500;
import static playn.core.PlayN.assets;

import java.util.Objects;
import java.util.function.Consumer;

import org.nting.data.Property;
import org.nting.data.util.Pair;
import org.nting.flare.java.animation.ActorAnimation;
import org.nting.flare.playn.JavaActor;
import org.nting.flare.playn.JavaActorArtboard;
import org.nting.toolkit.Component;
import org.nting.toolkit.component.AbstractComponent;
import org.nting.toolkit.component.Panel;
import org.nting.toolkit.ui.ComponentUI;

import playn.core.Canvas;
import playn.core.PlayN;
import playn.core.util.Callback;
import pythagoras.f.Dimension;

public class FlareActorRenderObject extends AbstractComponent {

    public static Component lazyFlareActorRenderObject(String filePath, String artboardName, String animationName,
            Consumer<FlareActorRenderObject> loadedCallback) {
        Panel panel = new Panel();

        assets().getBytes(filePath, new Callback<byte[]>() {

            @Override
            public void onSuccess(byte[] result) {
                JavaActor javaActor = JavaActor.loadFromByteData(result);
                FlareActorRenderObject flareActorRenderObject = new FlareActorRenderObject(javaActor, artboardName,
                        animationName);

                Component parent = panel.getParent();
                Object constraints = parent.getLayoutConstraints(panel);
                parent.removeComponent(panel);
                parent.addComponent(flareActorRenderObject, constraints);
                loadedCallback.accept(flareActorRenderObject);
            }

            @Override
            public void onFailure(Throwable cause) {
                PlayN.log().error(cause.getMessage(), cause);
            }
        });

        return panel;
    }

    public final Property<BoxFit> fit = createProperty("fit", BoxFit.CONTAIN);
    public final Property<Boolean> paused = createProperty("paused", false);

    private final JavaActor javaActor;
    private JavaActorArtboard artboard;
    private ActorAnimation animation;
    private float time = 0.0f;

    public FlareActorRenderObject(JavaActor javaActor, String artboardName, String animationName) {
        this.javaActor = javaActor;
        artboard = (JavaActorArtboard) javaActor.getArtboard(artboardName);
        artboard.initializeGraphics();
        animation = artboard.getAnimation(animationName);
        if (animation != null) {
            animation.apply(time, artboard, 1.0f);
        }
        artboard.advance(0.0f);
    }

    public void setArtboardName(String artboardName) {
        if (!Objects.equals(artboardName, artboard.name())) {
            return;
        }

        artboard = (JavaActorArtboard) javaActor.getArtboard(artboardName);
        artboard.initializeGraphics();
        setAnimationName(animation.name());
        artboard.advance(0.0f);
    }

    public void setAnimationName(String animationName) {
        ActorAnimation animation = artboard.getAnimation(animationName);
        if (animation != null) {
            animation.apply(time = 0.0f, artboard, 1.0f);
        }
    }

    @Override
    public void setComponentUI(ComponentUI componentUI) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(artboard.width(), artboard.height());
    }

    @Override
    public void doPaintComponent(Canvas canvas) {
        float elapsedSeconds = time;
        if (animation != null && !paused.getValue()) {
            if (animation.isLooping()) {
                time %= animation.duration();
            }
            animation.apply(time, artboard, 1.0f);
        }
        artboard.advance(elapsedSeconds);

        Pair<Float, Float> scaling = calculateScaling();
        float dx = (width.getValue() - scaling.first * artboard.width()) / 2;
        float dy = (height.getValue() - scaling.second * artboard.height()) / 2;
        canvas.transform(scaling.first, 0, 0, scaling.second, dx, dy);

        // paintDebug(canvas);
        artboard.draw(canvas);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (animation != null && !paused.getValue()) {
            time += (delta / 1000);
            repaint();
        }
    }

    public void rewind() {
        if (animation != null) {
            time = 0;
        }
    }

    public void fastForward() {
        if (animation != null) {
            time = animation.duration();
        }
    }

    private void paintDebug(Canvas canvas) {
        canvas.setFillColor(amber_100);
        canvas.fillRect(0, 0, artboard.width(), artboard.height());
        canvas.setStrokeColor(brown_500);
        canvas.setStrokeWidth(1);
        canvas.strokeRect(0, 0, artboard.width() - 1, artboard.height() - 1);
    }

    private Pair<Float, Float> calculateScaling() {
        float scaleX = 1.0f, scaleY = 1.0f;
        float contentWidth = artboard.width();
        float contentHeight = artboard.height();
        Dimension size = getSize();

        switch (fit.getValue()) {
        case FILL:
            scaleX = size.width / contentWidth;
            scaleY = size.height / contentHeight;
            break;
        case CONTAIN:
            float minScale = min(size.width / contentWidth, size.height / contentHeight);
            scaleX = scaleY = minScale;
            break;
        case COVER:
            float maxScale = max(size.width / contentWidth, size.height / contentHeight);
            scaleX = scaleY = maxScale;
            break;
        case FIT_HEIGHT:
            minScale = size.height / contentHeight;
            scaleX = scaleY = minScale;
            break;
        case FIT_WIDTH:
            minScale = size.width / contentWidth;
            scaleX = scaleY = minScale;
            break;
        case NONE:
            scaleX = scaleY = 1.0f;
            break;
        case SCALE_DOWN:
            minScale = min(size.width / contentWidth, size.height / contentHeight);
            scaleX = scaleY = Math.min(minScale, 1.0f);
            break;
        }

        return Pair.of(scaleX, scaleY);
    }
}
