package org.nting.flare.java;

public abstract class ActorStroke {
  double _width = 1.0;

  double get width => _width;

  set width(double value) {
    if (value == _width) {
      return;
    }
    _width = value;
    markPaintDirty();
  }

  StrokeCap _cap = StrokeCap.butt;
  StrokeJoin _join = StrokeJoin.miter;

  StrokeCap get cap => _cap;

  StrokeJoin get join => _join;

  TrimPath _trim = TrimPath.off;

  TrimPath get trim => _trim;

  boolean get isTrimmed => _trim != TrimPath.off;

  double _trimStart = 0.0;

  double get trimStart => _trimStart;

  set trimStart(double value) {
    if (_trimStart == value) {
      return;
    }
    _trimStart = value;
    markPathEffectsDirty();
  }

  double _trimEnd = 0.0;

  double get trimEnd => _trimEnd;

  set trimEnd(double value) {
    if (_trimEnd == value) {
      return;
    }
    _trimEnd = value;
    markPathEffectsDirty();
  }

  double _trimOffset = 0.0;

  double get trimOffset => _trimOffset;

  set trimOffset(double value) {
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
            TrimPath.values()[reader.readUint8("trim")] ?? TrimPath.off;
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
