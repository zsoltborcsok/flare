package org.nting.flare.playn;

import java.util.List;
import java.util.stream.Collectors;

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

public class FlutterActor extends Actor {

    public final List<Image> images = Lists.newLinkedList();

    @Override
    public ActorArtboard makeArtboard() {
        return new FlutterActorArtboard(this);
    }

    @Override
    public ActorShape makeShapeNode(ActorShape source) {
        if (source != null && source.transformAffectsStroke()) {
            return new FlutterActorShapeWithTransformedStroke();
        } else {
            return new FlutterActorShape();
        }
    }

    @Override
    public ActorPath makePathNode() {
        return new FlutterActorPath();
    }

    @Override
    public ActorImage makeImageNode() {
        return new FlutterActorImage();
    }

    @Override
    public ActorRectangle makeRectangle() {
        return new FlutterActorRectangle();
    }

    @Override
    public ActorTriangle makeTriangle() {
        return new FlutterActorTriangle();
    }

    @Override
    public ActorStar makeStar() {
        return new FlutterActorStar();
    }

    @Override
    public ActorPolygon makePolygon() {
        return new FlutterActorPolygon();
    }

    @Override
    public ActorEllipse makeEllipse() {
        return new FlutterActorEllipse();
    }

    @Override
    public ColorFill makeColorFill() {
        return new FlutterColorFill();
    }

    @Override
    public ColorStroke makeColorStroke() {
        return new FlutterColorStroke();
    }

    @Override
    public GradientFill makeGradientFill() {
        return new FlutterGradientFill();
    }

    @Override
    public GradientStroke makeGradientStroke() {
        return new FlutterGradientStroke();
    }

    @Override
    public RadialGradientFill makeRadialFill() {
        return new FlutterRadialFill();
    }

    @Override
    public RadialGradientStroke makeRadialStroke() {
        return new FlutterRadialStroke();
    }

    @Override
    public ActorDropShadow makeDropShadow() {
        return new FlutterActorDropShadow();
    }

    @Override
    public ActorLayerEffectRenderer makeLayerEffectRenderer() {
        return new FlutterActorLayerEffectRenderer();
    }

    @Override
    public ActorInnerShadow makeInnerShadow() {
        return new FlutterActorInnerShadow();
    }

    @Override
    protected Object jsonDecode(String json) {
        return new JsonMap(PlayN.json().parse(json));
    }

    @Override
    public byte[] readOutOfBandAsset(String filename, Object context) {
        return new byte[0];
    }

    public static FlutterActor loadFromByteData(byte[] data) {
        // ByteData data = await context.bundle.load(context.filename);
        FlutterActor actor = new FlutterActor();
        actor.load(data, null);
        return actor;
    }

    public void copyFlutterActor(FlutterActor actor) {
        copyActor(actor);
        // _images = actor._images;
    }

    public void dispose() {
    }

    private List<byte[]> _rawAtlasData;

    @Override
    public boolean loadAtlases(List<byte[]> rawAtlases) {
        _rawAtlasData = rawAtlases;
        return true;
    }

    public void loadImages() {
        images.clear();
        if (_rawAtlasData == null) {
            return;
        }

        images.addAll(_rawAtlasData.stream().map(String::new).map(s -> PlayN.assets().getRemoteImage(s))
                .collect(Collectors.toList()));
    }

}
