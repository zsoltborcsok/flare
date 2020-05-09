package org.nting.flare.java;

import java.util.Optional;

public class ActorStroke {

    private final Runnable markPaintDirty;
    private final Runnable markPathEffectsDirty;
    private final Runnable initializeGraphics;

    private float _width = 1.0f;
    private StrokeCap _cap = StrokeCap.butt;
    private StrokeJoin _join = StrokeJoin.miter;
    private TrimPath _trim = TrimPath.off;
    private float _trimStart = 0.0f;
    private float _trimEnd = 0.0f;
    private float _trimOffset = 0.0f;

    public ActorStroke(Runnable markPaintDirty, Runnable markPathEffectsDirty, Runnable initializeGraphics) {
        this.markPaintDirty = markPaintDirty;
        this.markPathEffectsDirty = markPathEffectsDirty;
        this.initializeGraphics = initializeGraphics;
    }

    public float width() {
        return _width;
    }

    public void width(float value) {
        if (value == _width) {
            return;
        }
        _width = value;
        markPaintDirty.run();
    }

    public StrokeCap cap() {
        return _cap;
    }

    public StrokeJoin join() {
        return _join;
    }

    public TrimPath trim() {
        return _trim;
    }

    public boolean isTrimmed() {
        return _trim != TrimPath.off;
    }

    public float trimStart() {
        return _trimStart;
    }

    public void trimStart(float value) {
        if (_trimStart == value) {
            return;
        }
        _trimStart = value;
        markPathEffectsDirty.run();
    }

    public float trimEnd() {
        return _trimEnd;
    }

    public void trimEnd(float value) {
        if (_trimEnd == value) {
            return;
        }
        _trimEnd = value;
        markPathEffectsDirty.run();
    }

    public float trimOffset() {
        return _trimOffset;
    }

    public void trimOffset(float value) {
        if (_trimOffset == value) {
            return;
        }
        _trimOffset = value;
        markPathEffectsDirty.run();
    }

    public static void read(ActorArtboard artboard, StreamReader reader, ActorStroke component) {
        component.width(reader.readFloat32("width"));
        if (artboard.actor().version() >= 19) {
            component._cap = StrokeCap.values()[reader.readUint8("cap")];
            component._join = StrokeJoin.values()[reader.readUint8("join")];
            if (artboard.actor().version() >= 20) {
                component._trim = Optional.ofNullable(TrimPath.values()[reader.readUint8("trim")]).orElse(TrimPath.off);
                if (component.isTrimmed()) {
                    component._trimStart = reader.readFloat32("start");
                    component._trimEnd = reader.readFloat32("end");
                    component._trimOffset = reader.readFloat32("offset");
                }
            }
        }
    }

    public void copyStroke(ActorStroke node, ActorArtboard resetArtboard) {
        _width = node._width;
        _cap = node._cap;
        _join = node._join;
        _trim = node._trim;
        _trimStart = node._trimStart;
        _trimEnd = node._trimEnd;
        _trimOffset = node._trimOffset;
    }

    public void initializeGraphics() {
        initializeGraphics.run();
    }
}
