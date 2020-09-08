package org.nting.flare.java.animation;

import static org.nting.flare.java.animation.PropertyTypes.propertyTypesMap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.StreamReader;
import org.nting.flare.java.animation.KeyFrames.KeyFrameActiveChild;
import org.nting.flare.java.animation.KeyFrames.KeyFrameBlurX;
import org.nting.flare.java.animation.KeyFrames.KeyFrameBlurY;
import org.nting.flare.java.animation.KeyFrames.KeyFrameBooleanProperty;
import org.nting.flare.java.animation.KeyFrames.KeyFrameCollisionEnabledProperty;
import org.nting.flare.java.animation.KeyFrames.KeyFrameConstraintStrength;
import org.nting.flare.java.animation.KeyFrames.KeyFrameCornerRadius;
import org.nting.flare.java.animation.KeyFrames.KeyFrameDrawOrder;
import org.nting.flare.java.animation.KeyFrames.KeyFrameFillColor;
import org.nting.flare.java.animation.KeyFrames.KeyFrameFloatProperty;
import org.nting.flare.java.animation.KeyFrames.KeyFrameGradient;
import org.nting.flare.java.animation.KeyFrames.KeyFrameImageVertices;
import org.nting.flare.java.animation.KeyFrames.KeyFrameInnerRadius;
import org.nting.flare.java.animation.KeyFrames.KeyFrameIntProperty;
import org.nting.flare.java.animation.KeyFrames.KeyFrameLength;
import org.nting.flare.java.animation.KeyFrames.KeyFrameOpacity;
import org.nting.flare.java.animation.KeyFrames.KeyFramePaintOpacity;
import org.nting.flare.java.animation.KeyFrames.KeyFramePathVertices;
import org.nting.flare.java.animation.KeyFrames.KeyFramePosX;
import org.nting.flare.java.animation.KeyFrames.KeyFramePosY;
import org.nting.flare.java.animation.KeyFrames.KeyFrameRadial;
import org.nting.flare.java.animation.KeyFrames.KeyFrameRotation;
import org.nting.flare.java.animation.KeyFrames.KeyFrameScaleX;
import org.nting.flare.java.animation.KeyFrames.KeyFrameScaleY;
import org.nting.flare.java.animation.KeyFrames.KeyFrameSequence;
import org.nting.flare.java.animation.KeyFrames.KeyFrameShadowColor;
import org.nting.flare.java.animation.KeyFrames.KeyFrameShadowOffsetX;
import org.nting.flare.java.animation.KeyFrames.KeyFrameShadowOffsetY;
import org.nting.flare.java.animation.KeyFrames.KeyFrameShapeHeight;
import org.nting.flare.java.animation.KeyFrames.KeyFrameShapeWidth;
import org.nting.flare.java.animation.KeyFrames.KeyFrameStringProperty;
import org.nting.flare.java.animation.KeyFrames.KeyFrameStrokeColor;
import org.nting.flare.java.animation.KeyFrames.KeyFrameStrokeEnd;
import org.nting.flare.java.animation.KeyFrames.KeyFrameStrokeOffset;
import org.nting.flare.java.animation.KeyFrames.KeyFrameStrokeStart;
import org.nting.flare.java.animation.KeyFrames.KeyFrameStrokeWidth;
import org.nting.flare.java.animation.KeyFrames.KeyFrameTrigger;

public class PropertyAnimation {

    @FunctionalInterface
    public interface KeyFrameReader extends BiFunction<StreamReader, ActorComponent, KeyFrame> {
    }

    private int _type;
    private List<KeyFrame> _keyFrames;

    public int propertyType() {
        return _type;
    }

    public List<KeyFrame> keyFrames() {
        return _keyFrames;
    }

    public static PropertyAnimation read(StreamReader reader, ActorComponent component) {
        StreamReader propertyBlock = reader.readNextBlock(propertyTypesMap);
        if (propertyBlock == null) {
            return null;
        }
        PropertyAnimation propertyAnimation = new PropertyAnimation();
        int type = propertyBlock.blockType();
        propertyAnimation._type = type;

        KeyFrameReader keyFrameReader = null;
        switch (propertyAnimation._type) {
        case PropertyTypes.posX:
            keyFrameReader = KeyFramePosX::read;
            break;
        case PropertyTypes.posY:
            keyFrameReader = KeyFramePosY::read;
            break;
        case PropertyTypes.scaleX:
            keyFrameReader = KeyFrameScaleX::read;
            break;
        case PropertyTypes.scaleY:
            keyFrameReader = KeyFrameScaleY::read;
            break;
        case PropertyTypes.rotation:
            keyFrameReader = KeyFrameRotation::read;
            break;
        case PropertyTypes.opacity:
            keyFrameReader = KeyFrameOpacity::read;
            break;
        case PropertyTypes.drawOrder:
            keyFrameReader = KeyFrameDrawOrder::read;
            break;
        case PropertyTypes.length:
            keyFrameReader = KeyFrameLength::read;
            break;
        case PropertyTypes.imageVertices:
            keyFrameReader = KeyFrameImageVertices::read;
            break;
        case PropertyTypes.constraintStrength:
            keyFrameReader = KeyFrameConstraintStrength::read;
            break;
        case PropertyTypes.trigger:
            keyFrameReader = KeyFrameTrigger::read;
            break;
        case PropertyTypes.intProperty:
            keyFrameReader = KeyFrameIntProperty::read;
            break;
        case PropertyTypes.floatProperty:
            keyFrameReader = KeyFrameFloatProperty::read;
            break;
        case PropertyTypes.stringProperty:
            keyFrameReader = KeyFrameStringProperty::read;
            break;
        case PropertyTypes.booleanProperty:
            keyFrameReader = KeyFrameBooleanProperty::read;
            break;
        case PropertyTypes.collisionEnabled:
            keyFrameReader = KeyFrameCollisionEnabledProperty::read;
            break;
        case PropertyTypes.activeChildIndex:
            keyFrameReader = KeyFrameActiveChild::read;
            break;
        case PropertyTypes.sequence:
            keyFrameReader = KeyFrameSequence::read;
            break;
        case PropertyTypes.pathVertices:
            keyFrameReader = KeyFramePathVertices::read;
            break;
        case PropertyTypes.fillColor:
            keyFrameReader = KeyFrameFillColor::read;
            break;
        case PropertyTypes.color:
            keyFrameReader = KeyFrameShadowColor::read;
            break;
        case PropertyTypes.offsetX:
            keyFrameReader = KeyFrameShadowOffsetX::read;
            break;
        case PropertyTypes.offsetY:
            keyFrameReader = KeyFrameShadowOffsetY::read;
            break;
        case PropertyTypes.blurX:
            keyFrameReader = KeyFrameBlurX::read;
            break;
        case PropertyTypes.blurY:
            keyFrameReader = KeyFrameBlurY::read;
            break;
        case PropertyTypes.fillGradient:
            keyFrameReader = KeyFrameGradient::read;
            break;
        case PropertyTypes.strokeGradient:
            keyFrameReader = KeyFrameGradient::read;
            break;
        case PropertyTypes.fillRadial:
            keyFrameReader = KeyFrameRadial::read;
            break;
        case PropertyTypes.strokeRadial:
            keyFrameReader = KeyFrameRadial::read;
            break;
        case PropertyTypes.strokeColor:
            keyFrameReader = KeyFrameStrokeColor::read;
            break;
        case PropertyTypes.strokeWidth:
            keyFrameReader = KeyFrameStrokeWidth::read;
            break;
        case PropertyTypes.strokeOpacity:
        case PropertyTypes.fillOpacity:
            keyFrameReader = KeyFramePaintOpacity::read;
            break;
        case PropertyTypes.shapeWidth:
            keyFrameReader = KeyFrameShapeWidth::read;
            break;
        case PropertyTypes.shapeHeight:
            keyFrameReader = KeyFrameShapeHeight::read;
            break;
        case PropertyTypes.cornerRadius:
            keyFrameReader = KeyFrameCornerRadius::read;
            break;
        case PropertyTypes.innerRadius:
            keyFrameReader = KeyFrameInnerRadius::read;
            break;
        case PropertyTypes.strokeStart:
            keyFrameReader = KeyFrameStrokeStart::read;
            break;
        case PropertyTypes.strokeEnd:
            keyFrameReader = KeyFrameStrokeEnd::read;
            break;
        case PropertyTypes.strokeOffset:
            keyFrameReader = KeyFrameStrokeOffset::read;
            break;
        }

        if (keyFrameReader == null) {
            return null;
        }

        propertyBlock.openArray("frames");
        int keyFrameCount = propertyBlock.readUint16Length();
        propertyAnimation._keyFrames = new ArrayList<>(keyFrameCount);
        KeyFrame lastKeyFrame = null;
        for (int i = 0; i < keyFrameCount; i++) {
            propertyBlock.openObject("frame");
            KeyFrame frame = keyFrameReader.apply(propertyBlock, component);
            propertyAnimation._keyFrames.add(frame);
            if (lastKeyFrame != null) {
                lastKeyFrame.setNext(frame);
            }
            lastKeyFrame = frame;
            propertyBlock.closeObject();
        }
        propertyBlock.closeArray();
        // }

        return propertyAnimation;
    }

    void apply(float time, ActorComponent component, float mix) {
        if (_keyFrames.isEmpty()) {
            return;
        }

        int idx = 0;
        // Binary find the keyframe index.
        {
            int mid = 0;
            float element = 0.0f;
            int start = 0;
            int end = _keyFrames.size() - 1;

            while (start <= end) {
                mid = (start + end) >> 1;
                element = _keyFrames.get(mid).time();
                if (element < time) {
                    start = mid + 1;
                } else if (element > time) {
                    end = mid - 1;
                } else {
                    start = mid;
                    break;
                }
            }

            idx = start;
        }

        if (idx == 0) {
            _keyFrames.get(0).apply(component, mix);
        } else {
            if (idx < _keyFrames.size()) {
                KeyFrame fromFrame = _keyFrames.get(idx - 1);
                KeyFrame toFrame = _keyFrames.get(idx);
                if (time == toFrame.time()) {
                    toFrame.apply(component, mix);
                } else {
                    fromFrame.applyInterpolation(component, time, toFrame, mix);
                }
            } else {
                _keyFrames.get(idx - 1).apply(component, mix);
            }
        }
    }
}
