package org.nting.flare.java;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class BlockTypes {

    public static final int unknown = 0;
    public static final int components = 1;
    public static final int actorNode = 2;
    public static final int actorBone = 3;
    public static final int actorRootBone = 4;
    public static final int actorImage = 5;
    public static final int view = 6;
    public static final int animation = 7;
    public static final int animations = 8;
    public static final int atlases = 9;
    public static final int atlas = 10;
    public static final int actorIKTarget = 11;
    public static final int actorEvent = 12;
    public static final int customIntProperty = 13;
    public static final int customFloatProperty = 14;
    public static final int customStringProperty = 15;
    public static final int customBooleanProperty = 16;
    public static final int actorColliderRectangle = 17;
    public static final int actorColliderTriangle = 18;
    public static final int actorColliderCircle = 19;
    public static final int actorColliderPolygon = 20;
    public static final int actorColliderLine = 21;
    public static final int actorImageSequence = 22;
    public static final int actorNodeSolo = 23;
    public static final int jellyComponent = 28;
    public static final int actorJellyBone = 29;
    public static final int actorIKConstraint = 30;
    public static final int actorDistanceConstraint = 31;
    public static final int actorTranslationConstraint = 32;
    public static final int actorRotationConstraint = 33;
    public static final int actorScaleConstraint = 34;
    public static final int actorTransformConstraint = 35;
    public static final int actorShape = 100;
    public static final int actorPath = 101;
    public static final int colorFill = 102;
    public static final int colorStroke = 103;
    public static final int gradientFill = 104;
    public static final int gradientStroke = 105;
    public static final int radialGradientFill = 106;
    public static final int radialGradientStroke = 107;
    public static final int actorEllipse = 108;
    public static final int actorRectangle = 109;
    public static final int actorTriangle = 110;
    public static final int actorStar = 111;
    public static final int actorPolygon = 112;
    public static final int actorSkin = 113;
    public static final int actorArtboard = 114;
    public static final int artboards = 115;
    public static final int actorLayerEffectRenderer = 116;
    public static final int actorMask = 117;
    public static final int actorBlur = 118;
    public static final int actorDropShadow = 119;
    public static final int actorInnerShadow = 120;

    public static final Map<String, Integer> blockTypesMap = ImmutableMap.<String, Integer> builder()
            .put("unknown", BlockTypes.unknown) //
            .put("nodes", BlockTypes.components) //
            .put("node", BlockTypes.actorNode) //
            .put("bone", BlockTypes.actorBone) //
            .put("rootBone", BlockTypes.actorRootBone) //
            .put("image", BlockTypes.actorImage) //
            .put("view", BlockTypes.view) //
            .put("animation", BlockTypes.animation) //
            .put("animations", BlockTypes.animations) //
            .put("atlases", BlockTypes.atlases) //
            .put("atlas", BlockTypes.atlas) //
            .put("event", BlockTypes.actorEvent) //
            .put("customInt", BlockTypes.customIntProperty) //
            .put("customFloat", BlockTypes.customFloatProperty) //
            .put("customString", BlockTypes.customStringProperty) //
            .put("customBoolean", BlockTypes.customBooleanProperty) //
            .put("rectangleCollider", BlockTypes.actorColliderRectangle) //
            .put("triangleCollider", BlockTypes.actorColliderTriangle) //
            .put("circleCollider", BlockTypes.actorColliderCircle) //
            .put("polygonCollider", BlockTypes.actorColliderPolygon) //
            .put("lineCollider", BlockTypes.actorColliderLine) //
            .put("imageSequence", BlockTypes.actorImageSequence) //
            .put("solo", BlockTypes.actorNodeSolo) //
            .put("jelly", BlockTypes.jellyComponent) //
            .put("jellyBone", BlockTypes.actorJellyBone) //
            .put("ikConstraint", BlockTypes.actorIKConstraint) //
            .put("distanceConstraint", BlockTypes.actorDistanceConstraint) //
            .put("translationConstraint", BlockTypes.actorTranslationConstraint) //
            .put("rotationConstraint", BlockTypes.actorRotationConstraint) //
            .put("scaleConstraint", BlockTypes.actorScaleConstraint) //
            .put("transformConstraint", BlockTypes.actorTransformConstraint) //
            .put("shape", BlockTypes.actorShape) //
            .put("path", BlockTypes.actorPath) //
            .put("colorFill", BlockTypes.colorFill) //
            .put("colorStroke", BlockTypes.colorStroke) //
            .put("gradientFill", BlockTypes.gradientFill) //
            .put("gradientStroke", BlockTypes.gradientStroke) //
            .put("radialGradientFill", BlockTypes.radialGradientFill) //
            .put("radialGradientStroke", BlockTypes.radialGradientStroke) //
            .put("ellipse", BlockTypes.actorEllipse) //
            .put("rectangle", BlockTypes.actorRectangle) //
            .put("triangle", BlockTypes.actorTriangle) //
            .put("star", BlockTypes.actorStar) //
            .put("polygon", BlockTypes.actorPolygon) //
            .put("artboards", BlockTypes.artboards) //
            .put("artboard", BlockTypes.actorArtboard) //
            .put("effectRenderer", BlockTypes.actorLayerEffectRenderer) //
            .put("mask", BlockTypes.actorMask) //
            .put("blur", BlockTypes.actorBlur) //
            .put("dropShadow", BlockTypes.actorDropShadow) //
            .put("innerShadow", BlockTypes.actorInnerShadow).build();
}
