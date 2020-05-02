package org.nting.flare.java;

public abstract class ActorAxisConstraint extends ActorTargetedConstraint {
  private boolean _copyX = false;
  private boolean _copyY = false;
  private boolean _enableMinX = false;
  private boolean _enableMaxX = false;
  private boolean _enableMinY = false;
  private boolean _enableMaxY = false;
  private boolean _offset = false;

  private double _scaleX = 1.0;
  private double _scaleY = 1.0;
  private double _minX = 0.0;
  private double _maxX = 0.0;
  private double _minY = 0.0;
  private double _maxY = 0.0;

  private int _sourceSpace = TransformSpace.world;
  private int _destSpace = TransformSpace.world;
  private int _minMaxSpace = TransformSpace.world;

  ActorAxisConstraint() : super();

  static ActorAxisConstraint read(ActorArtboard artboard, StreamReader reader,
      ActorAxisConstraint component) {
    ActorTargetedConstraint.read(artboard, reader, component);
    component._copyX = reader.readBoolean("copyX");
    if (component._copyX) {
      component._scaleX = reader.readFloat32("scaleX");
    }

    component._enableMinX = reader.readBoolean("enableMinX");
    if (component._enableMinX) {
      component._minX = reader.readFloat32("minX");
    }

    component._enableMaxX = reader.readBoolean("enableMaxX");
    if (component._enableMaxX) {
      component._maxX = reader.readFloat32("maxX");
    }

    component._copyY = reader.readBoolean("copyY");
    if (component._copyY) {
      component._scaleY = reader.readFloat32("scaleY");
    }

    component._enableMinY = reader.readBoolean("enableMinY");
    if (component._enableMinY) {
      component._minY = reader.readFloat32("minY");
    }

    component._enableMaxY = reader.readBoolean("enableMaxY");
    if (component._enableMaxY) {
      component._maxY = reader.readFloat32("maxY");
    }

    component._offset = reader.readBoolean("offset");
    component._sourceSpace = reader.readUint8("sourceSpaceId");
    component._destSpace = reader.readUint8("destSpaceId");
    component._minMaxSpace = reader.readUint8("minMaxSpaceId");

    return component;
  }

  void copyAxisConstraint(ActorAxisConstraint node,
      ActorArtboard resetArtboard) {
    copyTargetedConstraint(node, resetArtboard);

    _copyX = node._copyX;
    _copyY = node._copyY;
    _enableMinX = node._enableMinX;
    _enableMaxX = node._enableMaxX;
    _enableMinY = node._enableMinY;
    _enableMaxY = node._enableMaxY;
    _offset = node._offset;

    _scaleX = node._scaleX;
    _scaleY = node._scaleY;
    _minX = node._minX;
    _maxX = node._maxX;
    _minY = node._minY;
    _maxY = node._maxY;

    _sourceSpace = node._sourceSpace;
    _destSpace = node._destSpace;
    _minMaxSpace = node._minMaxSpace;
  }

  @Override
  public void onDirty(int dirt) {
    markDirty();
  }

  public boolean copyX() { return _copyX; }

  public boolean copyY() { return _copyY; }

  public int destSpace() { return _destSpace; }

  public boolean enableMaxX() { return _enableMaxX; }

  public boolean enableMaxY() { return _enableMaxY; }

  public boolean enableMinX() { return _enableMinX; }

  public boolean enableMinY() { return _enableMinY; }

  public double maxX() { return _maxX; }

  public double maxY() { return _maxY; }

  public int minMaxSpace() { return _minMaxSpace; }

  public double minX() { return _minX; }

  public double minY() { return _minY; }

  public boolean offset() { return _offset; }

  public double scaleX() { return _scaleX; }

  public double scaleY() { return _scaleY; }

  public int sourceSpace() { return _sourceSpace; }
}
