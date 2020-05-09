package org.nting.flare.java;

import java.util.Optional;

public abstract class ActorColor extends ActorPaint {

    private float[] _color = new float[4];

    public float[] color() {
        return _color;
    }

    public float[] displayColor() {
        return Optional.ofNullable(artboard).map(ActorArtboard::overrideColor).orElse(_color);
    }

    public void color(float[] value) {
        if (value.length != 4) {
            return;
        }
        _color[0] = value[0];
        _color[1] = value[1];
        _color[2] = value[2];
        _color[3] = value[3];
        markPaintDirty();
    }

    public void copyColor(ActorColor node, ActorArtboard resetArtboard) {
        copyPaint(node, resetArtboard);
        _color[0] = node._color[0];
        _color[1] = node._color[1];
        _color[2] = node._color[2];
        _color[3] = node._color[3];
    }

    public static ActorColor read(ActorArtboard artboard, StreamReader reader, ActorColor component) {
        ActorPaint.read(artboard, reader, component);

        component._color = reader.readFloat32Array(4, "color");

        return component;
    }

    @Override
    public void onDirty(int dirt) {
    }

    @Override
    public void update(int dirt) {
    }
}
