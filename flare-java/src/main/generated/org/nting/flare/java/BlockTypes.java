package org.nting.flare.java;

final Map<String, int> blockTypesMap = {
  "unknown": BlockTypes.unknown,
  "nodes": BlockTypes.components,
  "node": BlockTypes.actorNode,
  "bone": BlockTypes.actorBone,
  "rootBone": BlockTypes.actorRootBone,
  "image": BlockTypes.actorImage,
  "view": BlockTypes.view,
  "animation": BlockTypes.animation,
  "animations": BlockTypes.animations,
  "atlases": BlockTypes.atlases,
  "atlas": BlockTypes.atlas,
  "event": BlockTypes.actorEvent,
  "customInt": BlockTypes.customIntProperty,
  "customFloat": BlockTypes.customFloatProperty,
  "customString": BlockTypes.customStringProperty,
  "customBoolean": BlockTypes.customBooleanProperty,
  "rectangleCollider": BlockTypes.actorColliderRectangle,
  "triangleCollider": BlockTypes.actorColliderTriangle,
  "circleCollider": BlockTypes.actorColliderCircle,
  "polygonCollider": BlockTypes.actorColliderPolygon,
  "lineCollider": BlockTypes.actorColliderLine,
  "imageSequence": BlockTypes.actorImageSequence,
  "solo": BlockTypes.actorNodeSolo,
  "jelly": BlockTypes.jellyComponent,
  "jellyBone": BlockTypes.actorJellyBone,
  "ikConstraint": BlockTypes.actorIKConstraint,
  "distanceConstraint": BlockTypes.actorDistanceConstraint,
  "translationConstraint": BlockTypes.actorTranslationConstraint,
  "rotationConstraint": BlockTypes.actorRotationConstraint,
  "scaleConstraint": BlockTypes.actorScaleConstraint,
  "transformConstraint": BlockTypes.actorTransformConstraint,
  "shape": BlockTypes.actorShape,
  "path": BlockTypes.actorPath,
  "colorFill": BlockTypes.colorFill,
  "colorStroke": BlockTypes.colorStroke,
  "gradientFill": BlockTypes.gradientFill,
  "gradientStroke": BlockTypes.gradientStroke,
  "radialGradientFill": BlockTypes.radialGradientFill,
  "radialGradientStroke": BlockTypes.radialGradientStroke,
  "ellipse": BlockTypes.actorEllipse,
  "rectangle": BlockTypes.actorRectangle,
  "triangle": BlockTypes.actorTriangle,
  "star": BlockTypes.actorStar,
  "polygon": BlockTypes.actorPolygon,
  "artboards": BlockTypes.artboards,
  "artboard": BlockTypes.actorArtboard,
  "effectRenderer": BlockTypes.actorLayerEffectRenderer,
  "mask": BlockTypes.actorMask,
  "blur": BlockTypes.actorBlur,
  "dropShadow": BlockTypes.actorDropShadow,
  "innerShadow": BlockTypes.actorInnerShadow
};

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
}
