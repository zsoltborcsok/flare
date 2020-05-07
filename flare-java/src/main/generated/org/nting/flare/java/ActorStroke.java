package org.nting.flare.java;

import java.util.Optional;

public abstract class ActorStroke {
  private double _width = 1.0;

  public double width() { return _width; }

  public void width(double value) {
    if (value == _width) {
      return;
    }
    _width = value;
    markPaintDirty();
  }

  private StrokeCap _cap = StrokeCap.butt;
  private StrokeJoin _join = StrokeJoin.miter;

  public StrokeCap cap() { return _cap; }

  public StrokeJoin join() { return _join; }

  private TrimPath _trim = TrimPath.off;

  public TrimPath trim() { return _trim; }

  public boolean isTrimmed() { return _trim != TrimPath.off; }

  private double _trimStart = 0.0;

  public double trimStart() { return _trimStart; }

  public void trimStart(double value) {
    if (_trimStart == value) {
      return;
    }
    _trimStart = value;
    markPathEffectsDirty();
  }

  private double _trimEnd = 0.0;

  public double trimEnd() { return _trimEnd; }

  public void trimEnd(double value) {
    if (_trimEnd == value) {
      return;
    }
    _trimEnd = value;
    markPathEffectsDirty();
  }

  private double _trimOffset = 0.0;

  public double trimOffset() { return _trimOffset; }

  public void trimOffset(double value) {
    if (_trimOffset == value) {
      return;
    }
    _trimOffset = value;
    markPathEffectsDirty();
  }

  public abstract void markPaintDirty();

  public abstract void markPathEffectsDirty();

  static void read(ActorArtboard artboard, StreamReader reader,
      ActorStroke component) {
    component.width = reader.readFloat32("width");
    if (artboard.actor.version >= 19) {
      component._cap = StrokeCap.values()[reader.readUint8("cap")];
      component._join = StrokeJoin.values()[reader.readUint8("join")];
      if (artboard.actor.version >= 20) {
        component._trim =
                Optional.ofNullable(TrimPath.values()[reader.readUint8("trim")]).orElse(TrimPath.off);
        if (component.isTrimmed) {
          component._trimStart = reader.readFloat32("start");
          component._trimEnd = reader.readFloat32("end");
          component._trimOffset = reader.readFloat32("offset");
        }
      }
    }
  }

  public void copyStroke(ActorStroke node, ActorArtboard resetArtboard) {
    width = node.width;
    _cap = node._cap;
    _join = node._join;
    _trim = node._trim;
    _trimStart = node._trimStart;
    _trimEnd = node._trimEnd;
    _trimOffset = node._trimOffset;
  }

  public abstract void initializeGraphics();
}
