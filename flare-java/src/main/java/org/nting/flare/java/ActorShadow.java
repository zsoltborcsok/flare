package org.nting.flare.java;

public abstract class ActorShadow extends ActorBlur {

    public float offsetX;
    public float offsetY;
    private float[] _color = new float[4];

    public abstract int blendModeId();

    public abstract void blendModeId(int value);

    public float[] color() {
        return _color;
    }

    public static ActorShadow read(ActorArtboard artboard, StreamReader reader, ActorShadow component) {
        ActorBlur.read(artboard, reader, component);
        component.offsetX = reader.readFloat32("offsetX");
        component.offsetY = reader.readFloat32("offsetY");
        component._color = reader.readFloat32Array(4, "color");
        component.blendModeId(reader.readUint8("blendMode"));
        return component;
    }

    public void copyShadow(ActorShadow from, ActorArtboard resetArtboard) {
        copyBlur(from, resetArtboard);
        offsetX = from.offsetX;
        offsetY = from.offsetY;
        _color[0] = from._color[0];
        _color[1] = from._color[1];
        _color[2] = from._color[2];
        _color[3] = from._color[3];
        blendModeId(from.blendModeId());
    }
}
