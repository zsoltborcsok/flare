package org.nting.flare.java.animation;

import static java.lang.Math.floor;

import java.util.ArrayList;
import java.util.List;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorBlur;
import org.nting.flare.java.ActorBoneBase;
import org.nting.flare.java.ActorColor;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.ActorConstraint;
import org.nting.flare.java.ActorDrawable;
import org.nting.flare.java.ActorImage;
import org.nting.flare.java.ActorNode;
import org.nting.flare.java.ActorNodeSolo;
import org.nting.flare.java.ActorPaint;
import org.nting.flare.java.ActorPath;
import org.nting.flare.java.ActorProceduralPath;
import org.nting.flare.java.ActorRectangle;
import org.nting.flare.java.ActorShadow;
import org.nting.flare.java.ActorStar;
import org.nting.flare.java.ActorStroke;
import org.nting.flare.java.ColorStroke;
import org.nting.flare.java.GradientColor;
import org.nting.flare.java.GradientStroke;
import org.nting.flare.java.PathPoint;
import org.nting.flare.java.PointType;
import org.nting.flare.java.RadialGradientColor;
import org.nting.flare.java.RadialGradientStroke;
import org.nting.flare.java.StreamReader;
import org.nting.flare.java.animation.interpolation.CubicInterpolator;
import org.nting.flare.java.animation.interpolation.HoldInterpolator;
import org.nting.flare.java.animation.interpolation.Interpolator;
import org.nting.flare.java.animation.interpolation.LinearInterpolator;

public class KeyFrames {

    public static ActorStroke actorStrokeFromActorComponent(ActorComponent actorComponent) {
        if (actorComponent instanceof ColorStroke) {
            return ((ColorStroke) actorComponent).actorStroke;
        } else if (actorComponent instanceof GradientStroke) {
            return ((GradientStroke) actorComponent).actorStroke;
        } else if (actorComponent instanceof RadialGradientStroke) {
            return ((RadialGradientStroke) actorComponent).actorStroke;
        }

        return null;
    }

    public static class DrawOrderIndex {
        public int componentIndex;
        public int order;
    }

    public static class KeyFrameActiveChild extends KeyFrame {
        private int _value;

        @Override
        public void apply(ActorComponent component, float mix) {
            ActorNodeSolo soloNode = (ActorNodeSolo) component;
            soloNode.activeChildIndex(_value);
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
            apply(component, mix);
        }

        @Override
        public void setNext(KeyFrame frame) {
            // No Interpolation
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameActiveChild frame = new KeyFrameActiveChild();
            if (!KeyFrame.read(reader, frame)) {
                return null;
            }
            frame._value = (int) reader.readFloat32("value");
            return frame;
        }
    }

    public static class KeyFrameBooleanProperty extends KeyFrame {
        boolean _value;

        @Override
        public void apply(ActorComponent component, float mix) {
            // CustomBooleanProperty prop = component as CustomBooleanProperty;
            // prop.value = _value;
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
            apply(component, mix);
        }

        @Override
        public void setNext(KeyFrame frame) {
            // Do nothing.
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameBooleanProperty frame = new KeyFrameBooleanProperty();
            if (!KeyFrame.read(reader, frame)) {
                return null;
            }
            frame._value = reader.readBoolean("value");
            return frame;
        }
    }

    public static class KeyFrameCollisionEnabledProperty extends KeyFrame {
        boolean _value;

        @Override
        public void apply(ActorComponent component, float mix) {
            // ActorCollider collider = component as ActorCollider;
            // collider.isCollisionEnabled = _value;
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
            apply(component, mix);
        }

        @Override
        public void setNext(KeyFrame frame) {
            // Do nothing.
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameCollisionEnabledProperty frame = new KeyFrameCollisionEnabledProperty();
            if (!KeyFrame.read(reader, frame)) {
                return null;
            }
            frame._value = reader.readBoolean("value");
            return frame;
        }
    }

    public static class KeyFrameConstraintStrength extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorConstraint constraint = (ActorConstraint) component;
            constraint.strength(constraint.strength() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameConstraintStrength frame = new KeyFrameConstraintStrength();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameCornerRadius extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorRectangle node = (ActorRectangle) component;
            node.radius(node.radius() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameCornerRadius frame = new KeyFrameCornerRadius();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameDrawOrder extends KeyFrame {
        private List<DrawOrderIndex> _orderedNodes;

        @Override
        public void apply(ActorComponent component, float mix) {
            ActorArtboard artboard = component.artboard;

            for (final DrawOrderIndex doi : _orderedNodes) {
                ActorComponent actorComponent = artboard.components().get(doi.componentIndex);
                if (actorComponent instanceof ActorDrawable) {
                    ((ActorDrawable) actorComponent).drawOrder(doi.order);
                }
            }
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
            apply(component, mix);
        }

        @Override
        public void setNext(KeyFrame frame) {
            // Do nothing.
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameDrawOrder frame = new KeyFrameDrawOrder();
            if (!KeyFrame.read(reader, frame)) {
                return null;
            }
            reader.openArray("drawOrder");
            int numOrderedNodes = reader.readUint16Length();
            frame._orderedNodes = new ArrayList<>(numOrderedNodes);
            for (int i = 0; i < numOrderedNodes; i++) {
                reader.openObject("order");
                DrawOrderIndex drawOrder = new DrawOrderIndex();
                drawOrder.componentIndex = reader.readId("component");
                drawOrder.order = reader.readUint16("order");
                reader.closeObject();
                frame._orderedNodes.add(drawOrder);
            }
            reader.closeArray();
            return frame;
        }
    }

    public static class KeyFrameFillColor extends KeyFrameWithInterpolation {
        private float[] _value;

        public float[] value() {
            return _value;
        }

        @Override
        public void apply(ActorComponent component, float mix) {
            ActorColor ac = (ActorColor) component;
            int l = _value.length;
            float[] wr = ac.color();
            if (mix == 1.0) {
                for (int i = 0; i < l; i++) {
                    wr[i] = _value[i];
                }
            } else {
                float mixi = 1.0f - mix;
                for (int i = 0; i < l; i++) {
                    wr[i] = wr[i] * mixi + _value[i] * mix;
                }
            }
            ac.markPaintDirty();
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
            ActorColor ac = (ActorColor) component;
            float[] wr = ac.color();
            float[] to = ((KeyFrameFillColor) toFrame)._value;
            int l = _value.length;

            float f = (time - _time) / (toFrame._time - _time);
            if (_interpolator != null) {
                f = _interpolator.getEasedMix(f);
            }
            float fi = 1.0f - f;
            if (mix == 1.0) {
                for (int i = 0; i < l; i++) {
                    wr[i] = _value[i] * fi + to[i] * f;
                }
            } else {
                float mixi = 1.0f - mix;
                for (int i = 0; i < l; i++) {
                    float v = _value[i] * fi + to[i] * f;

                    wr[i] = wr[i] * mixi + v * mix;
                }
            }
            ac.markPaintDirty();
        }

        @Override
        public void setNext(KeyFrame frame) {
            // Do nothing.
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameFillColor frame = new KeyFrameFillColor();
            if (!KeyFrameWithInterpolation.read(reader, frame)) {
                return null;
            }

            frame._value = reader.readFloat32Array(4, "value");
            return frame;
        }
    }

    public static class KeyFrameFloatProperty extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            // TODO
            // CustomFloatProperty node = component as CustomFloatProperty;
            // node.value = node.value * (1.0 - mix) + value * mix;
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameFloatProperty frame = new KeyFrameFloatProperty();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameGradient extends KeyFrameWithInterpolation {
        private float[] _value;

        public float[] value() {
            return _value;
        }

        @Override
        public void apply(ActorComponent component, float mix) {
            GradientColor gradient = (GradientColor) component;

            int ridx = 0;
            int wi = 0;

            if (mix == 1.0) {
                gradient.start().values()[0] = _value[ridx++];
                gradient.start().values()[1] = _value[ridx++];
                gradient.end().values()[0] = _value[ridx++];
                gradient.end().values()[1] = _value[ridx++];

                while (ridx < _value.length && wi < gradient.colorStops().length) {
                    gradient.colorStops()[wi++] = _value[ridx++];
                }
            } else {
                float imix = 1.0f - mix;
                gradient.start().values()[0] = gradient.start().values()[0] * imix + _value[ridx++] * mix;
                gradient.start().values()[1] = gradient.start().values()[1] * imix + _value[ridx++] * mix;
                gradient.end().values()[0] = gradient.end().values()[0] * imix + _value[ridx++] * mix;
                gradient.end().values()[1] = gradient.end().values()[1] * imix + _value[ridx++] * mix;

                while (ridx < _value.length && wi < gradient.colorStops().length) {
                    gradient.colorStops()[wi] = gradient.colorStops()[wi] * imix + _value[ridx++];
                    wi++;
                }
            }
            gradient.markPaintDirty();
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
            GradientColor gradient = (GradientColor) component;
            float[] v = ((KeyFrameGradient) toFrame)._value;

            float f = (time - _time) / (toFrame._time - _time);
            if (_interpolator != null) {
                f = _interpolator.getEasedMix(f);
            }
            float fi = 1.0f - f;

            int ridx = 0;
            int wi = 0;

            if (mix == 1.0) {
                gradient.start().values()[0] = _value[ridx] * fi + v[ridx++] * f;
                gradient.start().values()[1] = _value[ridx] * fi + v[ridx++] * f;
                gradient.end().values()[0] = _value[ridx] * fi + v[ridx++] * f;
                gradient.end().values()[1] = _value[ridx] * fi + v[ridx++] * f;

                while (ridx < v.length && wi < gradient.colorStops().length) {
                    gradient.colorStops()[wi++] = _value[ridx] * fi + v[ridx++] * f;
                }
            } else {
                float imix = 1.0f - mix;

                // Mix : first interpolate the KeyFrames,
                // and then mix on top of the current value.
                float val = _value[ridx] * fi + v[ridx] * f;
                gradient.start().values()[0] = gradient.start().values()[0] * imix + val * mix;
                ridx++;

                val = _value[ridx] * fi + v[ridx] * f;
                gradient.start().values()[1] = gradient.start().values()[1] * imix + val * mix;
                ridx++;

                val = _value[ridx] * fi + v[ridx] * f;
                gradient.end().values()[0] = gradient.end().values()[0] * imix + val * mix;
                ridx++;

                val = _value[ridx] * fi + v[ridx] * f;
                gradient.end().values()[1] = gradient.end().values()[1] * imix + val * mix;
                ridx++;

                while (ridx < v.length && wi < gradient.colorStops().length) {
                    val = _value[ridx] * fi + v[ridx] * f;
                    gradient.colorStops()[wi] = gradient.colorStops()[wi] * imix + val * mix;

                    ridx++;
                    wi++;
                }
            }
            gradient.markPaintDirty();
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameGradient frame = new KeyFrameGradient();
            if (!KeyFrameWithInterpolation.read(reader, frame)) {
                return null;
            }
            int len = reader.readUint16("length");
            frame._value = reader.readFloat32Array(len, "value");
            return frame;
        }
    }

    public static class KeyFrameImageVertices extends KeyFrameWithInterpolation {
        private float[] _vertices;

        public float[] vertices() {
            return _vertices;
        }

        @Override
        public void apply(ActorComponent component, float mix) {
            ActorImage imageNode = (ActorImage) component;
            int l = _vertices.length;
            float[] wr = imageNode.animationDeformedVertices();
            if (mix == 1.0) {
                for (int i = 0; i < l; i++) {
                    wr[i] = _vertices[i];
                }
            } else {
                float mixi = 1.0f - mix;
                for (int i = 0; i < l; i++) {
                    wr[i] = wr[i] * mixi + _vertices[i] * mix;
                }
            }

            imageNode.invalidateDrawable();
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
            ActorImage imageNode = (ActorImage) component;
            float[] wr = imageNode.animationDeformedVertices();
            float[] to = ((KeyFrameImageVertices) toFrame)._vertices;
            int l = _vertices.length;

            float f = (time - _time) / (toFrame._time - _time);
            if (_interpolator != null) {
                f = _interpolator.getEasedMix(f);
            }

            float fi = 1.0f - f;
            if (mix == 1.0) {
                for (int i = 0; i < l; i++) {
                    wr[i] = _vertices[i] * fi + to[i] * f;
                }
            } else {
                float mixi = 1.0f - mix;
                for (int i = 0; i < l; i++) {
                    float v = _vertices[i] * fi + to[i] * f;

                    wr[i] = wr[i] * mixi + v * mix;
                }
            }

            imageNode.invalidateDrawable();
        }

        @Override
        public void setNext(KeyFrame frame) {
            // Do nothing.
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameImageVertices frame = new KeyFrameImageVertices();
            if (!KeyFrameWithInterpolation.read(reader, frame)) {
                return null;
            }

            ActorImage imageNode = (ActorImage) component;
            frame._vertices = reader.readFloat32Array(imageNode.vertexCount() * 2, "value");

            imageNode.doesAnimationVertexDeform(true);

            return frame;
        }
    }

    public static class KeyFrameInnerRadius extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            if (component == null)
                return;

            ActorStar star = (ActorStar) component;
            star.innerRadius(star.innerRadius() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameInnerRadius frame = new KeyFrameInnerRadius();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public abstract static class KeyFrameInt extends KeyFrameWithInterpolation {
        private float _value;

        public float value() {
            return _value;
        }

        @Override
        public void apply(ActorComponent component, float mix) {
            setValue(component, _value, mix);
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
            KeyFrameNumeric to = (KeyFrameNumeric) toFrame;
            float f = (time - _time) / (to._time - _time);
            if (_interpolator != null) {
                f = _interpolator.getEasedMix(f);
            }
            setValue(component, _value * (1.0f - f) + to._value * f, mix);
        }

        public abstract void setValue(ActorComponent component, float value, float mix);

        public static boolean read(StreamReader reader, KeyFrameInt frame) {
            if (!KeyFrameWithInterpolation.read(reader, frame)) {
                return false;
            }
            frame._value = reader.readInt32("value");
            return true;
        }
    }

    public static class KeyFrameIntProperty extends KeyFrameInt {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            // TODO
            // CustomIntProperty node = component as CustomIntProperty;
            // node.value = (node.value * (1.0 - mix) + value * mix).round();
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameIntProperty frame = new KeyFrameIntProperty();
            if (KeyFrameInt.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameLength extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorBoneBase bone = (ActorBoneBase) component;
            if (bone == null) {
                return;
            }
            bone.length(bone.length() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameLength frame = new KeyFrameLength();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static abstract class KeyFrameNumeric extends KeyFrameWithInterpolation {
        private float _value;

        public float value() {
            return _value;
        }

        @Override
        public void apply(ActorComponent component, float mix) {
            setValue(component, _value, mix);
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
            KeyFrameNumeric to = (KeyFrameNumeric) toFrame;
            float f = (time - _time) / (to._time - _time);
            if (_interpolator != null) {
                f = _interpolator.getEasedMix(f);
            }
            setValue(component, _value * (1.0f - f) + to._value * f, mix);
        }

        public abstract void setValue(ActorComponent component, float value, float mix);

        public static boolean read(StreamReader reader, KeyFrameNumeric frame) {
            if (!KeyFrameWithInterpolation.read(reader, frame)) {
                return false;
            }
            frame._value = reader.readFloat32("value");
            if (Float.isNaN(frame._value)) {
                // Do we want to warn the user the animation contains invalid values?
                frame._value = 1.0f;
            }
            return true;
        }
    }

    public static class KeyFrameOpacity extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorNode node = (ActorNode) component;
            node.opacity(node.opacity() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameOpacity frame = new KeyFrameOpacity();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFramePaintOpacity extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorPaint node = (ActorPaint) component;
            node.opacity(node.opacity() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFramePaintOpacity frame = new KeyFramePaintOpacity();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFramePathVertices extends KeyFrameWithInterpolation {
        private float[] _vertices;

        public float[] vertices() {
            return _vertices;
        }

        @Override
        public void apply(ActorComponent component, float mix) {
            ActorPath path = (ActorPath) component;
            int l = _vertices.length;
            float[] wr = path.vertexDeform;
            if (mix == 1.0) {
                for (int i = 0; i < l; i++) {
                    wr[i] = _vertices[i];
                }
            } else {
                float mixi = 1.0f - mix;
                for (int i = 0; i < l; i++) {
                    wr[i] = wr[i] * mixi + _vertices[i] * mix;
                }
            }

            path.markVertexDeformDirty();
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
            ActorPath path = (ActorPath) component;
            float[] wr = path.vertexDeform;
            float[] to = ((KeyFramePathVertices) toFrame)._vertices;
            int l = _vertices.length;

            float f = (time - _time) / (toFrame._time - _time);
            if (_interpolator != null) {
                f = _interpolator.getEasedMix(f);
            }
            float fi = 1.0f - f;
            if (mix == 1.0) {
                for (int i = 0; i < l; i++) {
                    wr[i] = _vertices[i] * fi + to[i] * f;
                }
            } else {
                float mixi = 1.0f - mix;
                for (int i = 0; i < l; i++) {
                    float v = _vertices[i] * fi + to[i] * f;

                    wr[i] = wr[i] * mixi + v * mix;
                }
            }

            path.markVertexDeformDirty();
        }

        @Override
        public void setNext(KeyFrame frame) {
            // Do nothing.
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFramePathVertices frame = new KeyFramePathVertices();
            if (!KeyFrameWithInterpolation.read(reader, frame) || !(component instanceof ActorPath)) {
                return null;
            }

            ActorPath pathNode = (ActorPath) component;

            int length = pathNode.points().stream().map(point -> 2 + (point.pointType() == PointType.straight ? 1 : 4))
                    .reduce(0, Integer::sum);
            frame._vertices = new float[length];
            int readIdx = 0;
            reader.openArray("value");
            for (final PathPoint point : pathNode.points()) {
                frame._vertices[readIdx++] = reader.readFloat32("translationX");
                frame._vertices[readIdx++] = reader.readFloat32("translationY");
                if (point.pointType() == PointType.straight) {
                    // radius
                    frame._vertices[readIdx++] = reader.readFloat32("radius");
                } else {
                    // in/out
                    frame._vertices[readIdx++] = reader.readFloat32("inValueX");
                    frame._vertices[readIdx++] = reader.readFloat32("inValueY");
                    frame._vertices[readIdx++] = reader.readFloat32("outValueX");
                    frame._vertices[readIdx++] = reader.readFloat32("outValueY");
                }
            }
            reader.closeArray();

            pathNode.makeVertexDeform();
            return frame;
        }
    }

    public static class KeyFramePosX extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorNode node = (ActorNode) component;
            node.x(node.x() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFramePosX frame = new KeyFramePosX();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFramePosY extends KeyFrameNumeric {
        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorNode node = (ActorNode) component;
            node.y(node.y() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFramePosY frame = new KeyFramePosY();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameRadial extends KeyFrameWithInterpolation {
        private float[] _value;

        public float[] value() {
            return _value;
        }

        @Override
        public void apply(ActorComponent component, float mix) {
            RadialGradientColor radial = (RadialGradientColor) component;

            int ridx = 0;
            int wi = 0;

            if (mix == 1.0) {
                radial.secondaryRadiusScale = _value[ridx++];
                radial.start().values()[0] = _value[ridx++];
                radial.start().values()[1] = _value[ridx++];
                radial.end().values()[0] = _value[ridx++];
                radial.end().values()[1] = _value[ridx++];

                while (ridx < _value.length && wi < radial.colorStops().length) {
                    radial.colorStops()[wi++] = _value[ridx++];
                }
            } else {
                float imix = 1.0f - mix;
                radial.secondaryRadiusScale = radial.secondaryRadiusScale * imix + _value[ridx++] * mix;
                radial.start().values()[0] = radial.start().values()[0] * imix + _value[ridx++] * mix;
                radial.start().values()[1] = radial.start().values()[1] * imix + _value[ridx++] * mix;
                radial.end().values()[0] = radial.end().values()[0] * imix + _value[ridx++] * mix;
                radial.end().values()[1] = radial.end().values()[1] * imix + _value[ridx++] * mix;

                while (ridx < _value.length && wi < radial.colorStops().length) {
                    radial.colorStops()[wi] = radial.colorStops()[wi] * imix + _value[ridx++];
                    wi++;
                }
            }
            radial.markPaintDirty();
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
            RadialGradientColor radial = (RadialGradientColor) component;
            float[] v = ((KeyFrameRadial) toFrame)._value;

            float f = (time - _time) / (toFrame._time - _time);
            if (_interpolator != null) {
                f = _interpolator.getEasedMix(f);
            }
            float fi = 1.0f - f;

            int ridx = 0;
            int wi = 0;

            if (mix == 1.0) {
                radial.secondaryRadiusScale = _value[ridx] * fi + v[ridx++] * f;
                radial.start().values()[0] = _value[ridx] * fi + v[ridx++] * f;
                radial.start().values()[1] = _value[ridx] * fi + v[ridx++] * f;
                radial.end().values()[0] = _value[ridx] * fi + v[ridx++] * f;
                radial.end().values()[1] = _value[ridx] * fi + v[ridx++] * f;

                while (ridx < v.length && wi < radial.colorStops().length) {
                    radial.colorStops()[wi++] = _value[ridx] * fi + v[ridx++] * f;
                }
            } else {
                float imix = 1.0f - mix;

                // Mix : first interpolate the KeyFrames,
                // and then mix on top of the current value.
                float val = _value[ridx] * fi + v[ridx] * f;
                radial.secondaryRadiusScale = _value[ridx] * fi + v[ridx++] * f;
                val = _value[ridx] * fi + v[ridx] * f;
                radial.start().values()[0] = _value[ridx++] * imix + val * mix;
                val = _value[ridx] * fi + v[ridx] * f;
                radial.start().values()[1] = _value[ridx++] * imix + val * mix;
                val = _value[ridx] * fi + v[ridx] * f;
                radial.end().values()[0] = _value[ridx++] * imix + val * mix;
                val = _value[ridx] * fi + v[ridx] * f;
                radial.end().values()[1] = _value[ridx++] * imix + val * mix;

                while (ridx < v.length && wi < radial.colorStops().length) {
                    val = _value[ridx] * fi + v[ridx] * f;
                    radial.colorStops()[wi] = radial.colorStops()[wi] * imix + val * mix;

                    ridx++;
                    wi++;
                }
            }
            radial.markPaintDirty();
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameRadial frame = new KeyFrameRadial();
            if (!KeyFrameWithInterpolation.read(reader, frame)) {
                return null;
            }
            int len = reader.readUint16("length");
            frame._value = reader.readFloat32Array(len, "value");
            return frame;
        }
    }

    public static class KeyFrameRotation extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorNode node = (ActorNode) component;
            node.rotation(node.rotation() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameRotation frame = new KeyFrameRotation();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameScaleX extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorNode node = (ActorNode) component;
            node.scaleX(node.scaleX() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameScaleX frame = new KeyFrameScaleX();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameScaleY extends KeyFrameNumeric {
        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorNode node = (ActorNode) component;
            node.scaleY(node.scaleY() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameScaleY frame = new KeyFrameScaleY();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameSequence extends KeyFrameNumeric {
        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorImage node = (ActorImage) component;
            int frameIndex = ((int) floor(value)) % node.sequenceFrames().size();
            if (frameIndex < 0) {
                frameIndex += node.sequenceFrames().size();
            }
            node.sequenceFrame(frameIndex);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameSequence frame = new KeyFrameSequence();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameShadowColor extends KeyFrameWithInterpolation {
        float[] _value;

        float[] value() {
            return _value;
        }

        @Override
        public void apply(ActorComponent component, float mix) {
            ActorShadow shadow = (ActorShadow) component;
            int l = _value.length;
            float[] wr = shadow.color();
            if (mix == 1.0) {
                for (int i = 0; i < l; i++) {
                    wr[i] = _value[i];
                }
            } else {
                float mixi = 1.0f - mix;
                for (int i = 0; i < l; i++) {
                    wr[i] = wr[i] * mixi + _value[i] * mix;
                }
            }
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
            ActorShadow shadow = (ActorShadow) component;
            float[] wr = shadow.color();
            float[] to = ((KeyFrameShadowColor) toFrame)._value;
            int l = _value.length;

            float f = (time - _time) / (toFrame._time - _time);
            if (_interpolator != null) {
                f = _interpolator.getEasedMix(f);
            }
            float fi = 1.0f - f;
            if (mix == 1.0) {
                for (int i = 0; i < l; i++) {
                    wr[i] = _value[i] * fi + to[i] * f;
                }
            } else {
                float mixi = 1.0f - mix;
                for (int i = 0; i < l; i++) {
                    float v = _value[i] * fi + to[i] * f;

                    wr[i] = wr[i] * mixi + v * mix;
                }
            }
        }

        @Override
        public void setNext(KeyFrame frame) {
            // Do nothing.
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameShadowColor frame = new KeyFrameShadowColor();
            if (!KeyFrameWithInterpolation.read(reader, frame)) {
                return null;
            }

            frame._value = reader.readFloat32Array(4, "value");
            return frame;
        }
    }

    public static class KeyFrameShapeHeight extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            if (component == null)
                return;

            if (component instanceof ActorProceduralPath) {
                ActorProceduralPath actorProceduralPath = (ActorProceduralPath) component;
                actorProceduralPath.height(actorProceduralPath.height() * (1.0f - mix) + value * mix);
            }
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameShapeHeight frame = new KeyFrameShapeHeight();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameShapeWidth extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            if (component == null)
                return;

            if (component instanceof ActorProceduralPath) {
                ActorProceduralPath actorProceduralPath = (ActorProceduralPath) component;
                actorProceduralPath.width(actorProceduralPath.width() * (1.0f - mix) + value * mix);
            }
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameShapeWidth frame = new KeyFrameShapeWidth();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameStringProperty extends KeyFrame {
        private String _value;

        @Override
        public void apply(ActorComponent component, float mix) {
            // CustomStringProperty prop = component as CustomStringProperty;
            // prop.value = _value;
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
            apply(component, mix);
        }

        @Override
        public void setNext(KeyFrame frame) {
            // Do nothing.
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameStringProperty frame = new KeyFrameStringProperty();
            if (!KeyFrame.read(reader, frame)) {
                return null;
            }
            frame._value = reader.readString("value");
            return frame;
        }
    }

    public static class KeyFrameStrokeColor extends KeyFrameWithInterpolation {
        private float[] _value;

        public float[] value() {
            return _value;
        }

        @Override
        public void apply(ActorComponent component, float mix) {
            ColorStroke node = (ColorStroke) component;
            float[] wr = node.color();
            int len = wr.length;
            if (mix == 1.0) {
                for (int i = 0; i < len; i++) {
                    wr[i] = _value[i];
                }
            } else {
                float mixi = 1.0f - mix;
                for (int i = 0; i < len; i++) {
                    wr[i] = wr[i] * mixi + _value[i] * mix;
                }
            }
            node.markPaintDirty();
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
            ColorStroke cs = (ColorStroke) component;
            float[] wr = cs.color();
            float[] to = ((KeyFrameStrokeColor) toFrame)._value;
            int len = _value.length;

            float f = (time - _time) / (toFrame._time - _time);
            if (_interpolator != null) {
                f = _interpolator.getEasedMix(f);
            }
            float fi = 1.0f - f;
            if (mix == 1.0) {
                for (int i = 0; i < len; i++) {
                    wr[i] = _value[i] * fi + to[i] * f;
                }
            } else {
                float mixi = 1.0f - mix;
                for (int i = 0; i < len; i++) {
                    float v = _value[i] * fi + to[i] * f;

                    wr[i] = wr[i] * mixi + v * mix;
                }
            }
            cs.markPaintDirty();
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameStrokeColor frame = new KeyFrameStrokeColor();
            if (!KeyFrameWithInterpolation.read(reader, frame)) {
                return null;
            }
            frame._value = reader.readFloat32Array(4, "value");
            return frame;
        }
    }

    public static class KeyFrameStrokeEnd extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorStroke actorStroke = actorStrokeFromActorComponent(component);
            if (actorStroke == null) {
                return;
            }

            actorStroke.trimEnd(actorStroke.trimEnd() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameStrokeEnd frame = new KeyFrameStrokeEnd();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameStrokeOffset extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorStroke actorStroke = actorStrokeFromActorComponent(component);
            if (actorStroke == null) {
                return;
            }

            actorStroke.trimOffset(actorStroke.trimOffset() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameStrokeOffset frame = new KeyFrameStrokeOffset();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameStrokeStart extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorStroke actorStroke = actorStrokeFromActorComponent(component);
            if (actorStroke == null) {
                return;
            }

            actorStroke.trimStart(actorStroke.trimStart() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameStrokeStart frame = new KeyFrameStrokeStart();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameStrokeWidth extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorStroke actorStroke = actorStrokeFromActorComponent(component);
            if (actorStroke == null) {
                return;
            }

            actorStroke.width(actorStroke.width() * (1.0f - mix) + value * mix);
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameStrokeWidth frame = new KeyFrameStrokeWidth();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameTrigger extends KeyFrame {

        @Override
        public void apply(ActorComponent component, float mix) {
        }

        @Override
        public void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix) {
        }

        @Override
        public void setNext(KeyFrame frame) {
            // Do nothing.
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameTrigger frame = new KeyFrameTrigger();
            if (!KeyFrame.read(reader, frame)) {
                return null;
            }
            return frame;
        }
    }

    public abstract static class KeyFrameWithInterpolation extends KeyFrame {

        protected Interpolator _interpolator;

        public Interpolator interpolator() {
            return _interpolator;
        }

        @Override
        public void setNext(KeyFrame frame) {
            // Null out the interpolator if the next frame doesn't validate.
            // if(_interpolator != null && !_interpolator.setNextFrame(this, frame))
            // {
            // _interpolator = null;
            // }
        }

        public static boolean read(StreamReader reader, KeyFrameWithInterpolation frame) {
            if (!KeyFrame.read(reader, frame)) {
                return false;
            }
            int type = reader.readUint8("interpolatorType");

            InterpolationTypes actualType = (0 <= type && type < InterpolationTypes.values().length)
                    ? InterpolationTypes.values()[type]
                    : InterpolationTypes.linear;

            switch (actualType) {
            case hold:
                frame._interpolator = HoldInterpolator.INSTANCE;
                break;
            case linear:
                frame._interpolator = LinearInterpolator.INSTANCE;
                break;
            case cubic:
                CubicInterpolator interpolator = new CubicInterpolator();
                if (interpolator.read(reader)) {
                    frame._interpolator = interpolator;
                }
                break;
            default:
                frame._interpolator = null;
            }
            return true;
        }
    }

    public static class KeyFrameShadowOffsetX extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorShadow node = (ActorShadow) component;
            node.offsetX = node.offsetX * (1.0f - mix) + value * mix;
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameShadowOffsetX frame = new KeyFrameShadowOffsetX();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameShadowOffsetY extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorShadow node = (ActorShadow) component;
            node.offsetY = node.offsetY * (1.0f - mix) + value * mix;
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameShadowOffsetY frame = new KeyFrameShadowOffsetY();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameBlurX extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorBlur node = (ActorBlur) component;
            node.blurX = node.blurX * (1.0f - mix) + value * mix;
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameBlurX frame = new KeyFrameBlurX();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }

    public static class KeyFrameBlurY extends KeyFrameNumeric {

        @Override
        public void setValue(ActorComponent component, float value, float mix) {
            ActorBlur node = (ActorBlur) component;
            node.blurY = node.blurY * (1.0f - mix) + value * mix;
        }

        public static KeyFrame read(StreamReader reader, ActorComponent component) {
            KeyFrameBlurY frame = new KeyFrameBlurY();
            if (KeyFrameNumeric.read(reader, frame)) {
                return frame;
            }
            return null;
        }
    }
}
