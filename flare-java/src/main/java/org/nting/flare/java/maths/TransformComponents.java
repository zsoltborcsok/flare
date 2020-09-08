package org.nting.flare.java.maths;

import java.util.Arrays;

public class TransformComponents {

    private float[] _buffer;

    public TransformComponents() {
        _buffer = new float[] { 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f };
    }

    public TransformComponents(TransformComponents copy) {
        _buffer = Arrays.copyOf(copy._buffer, copy._buffer.length);
    }

    public float[] values() {
        return _buffer;
    }

    public float x() {
        return _buffer[0];
    }

    public void x(float value) {
        _buffer[0] = value;
    }

    public float y() {
        return _buffer[1];
    }

    public void y(float value) {
        _buffer[1] = value;
    }

    public float scaleX() {
        return _buffer[2];
    }

    public void scaleX(float value) {
        _buffer[2] = value;
    }

    public float scaleY() {
        return _buffer[3];
    }

    public void scaleY(float value) {
        _buffer[3] = value;
    }

    public float rotation() {
        return _buffer[4];
    }

    public void rotation(float value) {
        _buffer[4] = value;
    }

    public float skew() {
        return _buffer[5];
    }

    public void skew(float value) {
        _buffer[5] = value;
    }

    public Vec2D translation() {
        return new Vec2D(_buffer[0], _buffer[1]);
    }

    public Vec2D scale() {
        return new Vec2D(_buffer[2], _buffer[3]);
    }
}
