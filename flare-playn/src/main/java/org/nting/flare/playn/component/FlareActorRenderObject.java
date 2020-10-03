package org.nting.flare.playn.component;

import static java.lang.Float.max;
import static java.lang.Float.min;
import static playn.core.PlayN.assets;

import java.util.Objects;
import java.util.function.Consumer;

import org.nting.data.Property;
import org.nting.data.util.Pair;
import org.nting.flare.java.animation.ActorAnimation;
import org.nting.flare.java.maths.AABB;
import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;
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
    public final Property<Boolean> paused = createProperty("paused", true);

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

        setClip(true);
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

        AABB bounds = artboard.artboardAABB();
        if (bounds != null) {
            canvas.save();

            Pair<Float, Float> scaling = calculateScaling();
            Mat2D transform = calculateTransform(bounds, 0, 0, scaling.first, scaling.second);
            canvas.transform(transform.values()[0], transform.values()[1], transform.values()[2], transform.values()[3],
                    transform.values()[4], transform.values()[5]);

            artboard.draw(canvas);

            canvas.restore();
        }
    }

    private Mat2D calculateTransform(AABB bounds, float alignmentX, float alignmentY, float scaleX, float scaleY) {
        Dimension size = getSize();
        float contentWidth = bounds.values()[2] - bounds.values()[0];
        float contentHeight = bounds.values()[3] - bounds.values()[1];
        float x = -1 * bounds.values()[0] - contentWidth / 2.0f - (alignmentX * contentWidth / 2.0f);
        float y = -1 * bounds.values()[1] - contentHeight / 2.0f - (alignmentY * contentHeight / 2.0f);

        Mat2D transform = new Mat2D();
        transform.values()[4] = size.width / 2.0f + (alignmentX * size.width / 2.0f);
        transform.values()[5] = size.height / 2.0f + (alignmentY * size.height / 2.0f);
        Mat2D.scale(transform, transform, new Vec2D(scaleX, scaleY));
        Mat2D center = new Mat2D();
        center.values()[4] = x;
        center.values()[5] = y;
        Mat2D.multiply(transform, transform, center);

        return transform;
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
