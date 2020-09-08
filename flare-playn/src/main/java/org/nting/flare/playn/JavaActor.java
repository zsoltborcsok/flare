package org.nting.flare.playn;

import java.util.List;

import org.nting.flare.java.Actor;
import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorDropShadow;
import org.nting.flare.java.ActorEllipse;
import org.nting.flare.java.ActorImage;
import org.nting.flare.java.ActorInnerShadow;
import org.nting.flare.java.ActorLayerEffectRenderer;
import org.nting.flare.java.ActorPath;
import org.nting.flare.java.ActorPolygon;
import org.nting.flare.java.ActorRectangle;
import org.nting.flare.java.ActorShape;
import org.nting.flare.java.ActorStar;
import org.nting.flare.java.ActorTriangle;
import org.nting.flare.java.ColorFill;
import org.nting.flare.java.ColorStroke;
import org.nting.flare.java.GradientFill;
import org.nting.flare.java.GradientStroke;
import org.nting.flare.java.RadialGradientFill;
import org.nting.flare.java.RadialGradientStroke;
import org.nting.flare.playn.util.JsonMap;

import com.google.common.collect.Lists;

import playn.core.Image;
import playn.core.PlayN;

public class JavaActor extends Actor {

    public final List<Image> images = Lists.newLinkedList();

    @Override
    public ActorArtboard makeArtboard() {
        return new JavaActorArtboard(this);
    }

    @Override
    public ActorShape makeShapeNode(ActorShape source) {
        if (source != null && source.transformAffectsStroke()) {
            return new JavaActorShapeWithTransformedStroke();
        } else {
            return new JavaActorShape();
        }
    }

    @Override
    public ActorPath makePathNode() {
        return new JavaActorPath();
    }

    @Override
    public ActorImage makeImageNode() {
        return new JavaActorImage();
    }

    @Override
    public ActorRectangle makeRectangle() {
        return new JavaActorRectangle();
    }

    @Override
    public ActorTriangle makeTriangle() {
        return new JavaActorTriangle();
    }

    @Override
    public ActorStar makeStar() {
        return new JavaActorStar();
    }

    @Override
    public ActorPolygon makePolygon() {
        return new JavaActorPolygon();
    }

    @Override
    public ActorEllipse makeEllipse() {
        return new JavaActorEllipse();
    }

    @Override
    public ColorFill makeColorFill() {
        return new JavaColorFill();
    }

    @Override
    public ColorStroke makeColorStroke() {
        return new JavaColorStroke();
    }

    @Override
    public GradientFill makeGradientFill() {
        return new JavaGradientFill();
    }

    @Override
    public GradientStroke makeGradientStroke() {
        return new JavaGradientStroke();
    }

    @Override
    public RadialGradientFill makeRadialFill() {
        return new JavaRadialFill();
    }

    @Override
    public RadialGradientStroke makeRadialStroke() {
        return new JavaRadialStroke();
    }

    @Override
    public ActorDropShadow makeDropShadow() {
        return new JavaActorDropShadow();
    }

    @Override
    public ActorLayerEffectRenderer makeLayerEffectRenderer() {
        return new JavaActorLayerEffectRenderer();
    }

    @Override
    public ActorInnerShadow makeInnerShadow() {
        return new JavaActorInnerShadow();
    }

    @Override
    protected Object jsonDecode(String json) {
        return new JsonMap(PlayN.json().parse(json));
    }

    @Override
    public byte[] readOutOfBandAsset(String filename, Object context) {
        return new byte[0];
    }

    public static JavaActor loadFromByteData(byte[] data) {
        // ByteData data = await context.bundle.load(context.filename);
        JavaActor actor = new JavaActor();
        actor.load(data, null);
        return actor;
    }

    public void copyJavaActor(JavaActor actor) {
        copyActor(actor);
        images.clear();
        images.addAll(actor.images);
    }

    public void dispose() {
        images.clear();
    }

    @Override
    public boolean loadAtlases(List<byte[]> rawAtlases) {
        images.clear();
        if (rawAtlases == null) {
            return false;
        }

        for (byte[] rawAtlas : rawAtlases) {
            if (4 < rawAtlas.length && (rawAtlas[0] == 'd' || rawAtlas[0] == 'D')
                    && (rawAtlas[1] == 'a' || rawAtlas[1] == 'A') && (rawAtlas[2] == 't' || rawAtlas[2] == 'T')
                    && (rawAtlas[3] == 'a' || rawAtlas[3] == 'A')) {
                images.add(PlayN.assets().getRemoteImage(new String(rawAtlas)));
            } else {
                images.add(PlayN.assets().getImage(rawAtlas));
            }
        }
        return true;
    }
}
