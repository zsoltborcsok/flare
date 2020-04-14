package org.nting.flare.java;

public abstract class ActorProceduralPath extends ActorNode with ActorBasePath {
  double _width;
  double _height;

  double get width => _width;

  double get height => _height;

  @Override
  Mat2D get pathTransform => worldTransform;

  set width(double w) {
    if (w != _width) {
      _width = w;
      invalidateDrawable();
    }
  }

  set height(double w) {
    if (w != _height) {
      _height = w;
      invalidateDrawable();
    }
  }

  public void copyPath(ActorProceduralPath node, ActorArtboard resetArtboard) {
    copyNode(node, resetArtboard);
    _width = node.width;
    _height = node.height;
  }

  @Override
  public void onDirty(int dirt) {
    super.onDirty(dirt);
    // We transformed, make sure parent is invalidated.
    if (shape != null) {
      shape.invalidateShape();
    }
  }
}
