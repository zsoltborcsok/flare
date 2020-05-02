package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;

public abstract class ActorProceduralPath extends ActorNode with ActorBasePath {
  private double _width;
  private double _height;

  public double width() { return _width; }

  public double height() { return _height; }

  @Override
  public Mat2D pathTransform() { return worldTransform; }

  public void width(double w) {
    if (w != _width) {
      _width = w;
      invalidateDrawable();
    }
  }

  public void height(double w) {
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
