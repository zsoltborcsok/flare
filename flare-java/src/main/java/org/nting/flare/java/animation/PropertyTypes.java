package org.nting.flare.java.animation;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class PropertyTypes {

    public static final int unknown = 0;
    public static final int posX = 1;
    public static final int posY = 2;
    public static final int scaleX = 3;
    public static final int scaleY = 4;
    public static final int rotation = 5;
    public static final int opacity = 6;
    public static final int drawOrder = 7;
    public static final int length = 8;
    public static final int imageVertices = 9;
    public static final int constraintStrength = 10;
    public static final int trigger = 11;
    public static final int intProperty = 12;
    public static final int floatProperty = 13;
    public static final int stringProperty = 14;
    public static final int booleanProperty = 15;
    public static final int collisionEnabled = 16;
    public static final int sequence = 17;
    public static final int activeChildIndex = 18;
    public static final int pathVertices = 19;
    public static final int fillColor = 20;
    public static final int fillGradient = 21;
    public static final int fillRadial = 22;
    public static final int strokeColor = 23;
    public static final int strokeGradient = 24;
    public static final int strokeRadial = 25;
    public static final int strokeWidth = 26;
    public static final int strokeOpacity = 27;
    public static final int fillOpacity = 28;
    public static final int shapeWidth = 29;
    public static final int shapeHeight = 30;
    public static final int cornerRadius = 31;
    public static final int innerRadius = 32;
    public static final int strokeStart = 33;
    public static final int strokeEnd = 34;
    public static final int strokeOffset = 35;
    public static final int color = 36;
    public static final int offsetX = 37;
    public static final int offsetY = 38;
    public static final int blurX = 39;
    public static final int blurY = 40;

    public static final Map<String, Integer> propertyTypesMap = ImmutableMap.<String, Integer> builder()
            .put("unknown", PropertyTypes.unknown) //
            .put("posX", PropertyTypes.posX) //
            .put("posY", PropertyTypes.posY) //
            .put("scaleX", PropertyTypes.scaleX) //
            .put("scaleY", PropertyTypes.scaleY) //
            .put("rotation", PropertyTypes.rotation) //
            .put("opacity", PropertyTypes.opacity) //
            .put("drawOrder", PropertyTypes.drawOrder) //
            .put("length", PropertyTypes.length) //
            .put("vertices", PropertyTypes.imageVertices) //
            .put("strength", PropertyTypes.constraintStrength) //
            .put("trigger", PropertyTypes.trigger) //
            .put("intValue", PropertyTypes.intProperty) //
            .put("floatValue", PropertyTypes.floatProperty) //
            .put("stringValue", PropertyTypes.stringProperty) //
            .put("boolValue", PropertyTypes.booleanProperty) //
            .put("isCollisionEnabled", PropertyTypes.collisionEnabled) //
            .put("sequence", PropertyTypes.sequence) //
            .put("activeChild", PropertyTypes.activeChildIndex) //
            .put("pathVertices", PropertyTypes.pathVertices) //
            .put("fillColor", PropertyTypes.fillColor) //
            .put("fillGradient", PropertyTypes.fillGradient) //
            .put("fillRadial", PropertyTypes.fillRadial) //
            .put("strokeColor", PropertyTypes.strokeColor) //
            .put("strokeGradient", PropertyTypes.strokeGradient) //
            .put("strokeRadial", PropertyTypes.strokeRadial) //
            .put("strokeWidth", PropertyTypes.strokeWidth) //
            .put("strokeOpacity", PropertyTypes.strokeOpacity) //
            .put("fillOpacity", PropertyTypes.fillOpacity) //
            .put("width", PropertyTypes.shapeWidth) //
            .put("height", PropertyTypes.shapeHeight) //
            .put("cornerRadius", PropertyTypes.cornerRadius) //
            .put("innerRadius", PropertyTypes.innerRadius) //
            .put("strokeStart", PropertyTypes.strokeStart) //
            .put("strokeEnd", PropertyTypes.strokeEnd) //
            .put("strokeOffset", PropertyTypes.strokeOffset) //
            .put("color", PropertyTypes.color) //
            .put("offsetX", PropertyTypes.offsetX) //
            .put("offsetY", PropertyTypes.offsetY) //
            .put("blurX", PropertyTypes.blurX) //
            .put("blurY", PropertyTypes.blurY).build();
}
