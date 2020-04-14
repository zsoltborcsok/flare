package org.nting.flare.java;

import org.nting.flare.java.maths.AABB;
import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

import java.util.List;

public abstract class ActorBasePath {
  ActorShape _shape;

  public ActorShape shape() { return _shape; }
  boolean _isRootPath = false;

  public boolean isRootPath() { return _isRootPath; }

  List<PathPoint> get points;

  ActorNode get parent;

  public abstract void invalidatePath();

  public boolean isPathInWorldSpace() { return false; }

  Mat2D get pathTransform;

  Mat2D get transform;

  Mat2D get worldTransform;

  List<List<ActorClip>> get allClips;

  public List<PathPoint> deformedPoints() { return points; }

  public AABB getPathAABB() {
    double minX = double.maxFinite;
    double minY = double.maxFinite;
    double maxX = -double.maxFinite;
    double maxY = -double.maxFinite;

    AABB obb = getPathOBB();

    List<Vec2D> pts = [
      Vec2D.fromValues(obb[0], obb[1]),
      Vec2D.fromValues(obb[2], obb[1]),
      Vec2D.fromValues(obb[2], obb[3]),
      Vec2D.fromValues(obb[0], obb[3])
    ];

    Mat2D localTransform;
    if (isPathInWorldSpace) {
      //  convert the path coordinates into local parent space.
      localTransform = new Mat2D();
      Mat2D.invert(localTransform, parent.worldTransform);
    } else if (!_isRootPath) {
      localTransform = new Mat2D();
      // Path isn't root, so get transform in shape space.
      if (Mat2D.invert(localTransform, shape.worldTransform)) {
        Mat2D.multiply(localTransform, localTransform, worldTransform);
      }
    } else {
      localTransform = transform;
    }

    for (final Vec2D p : pts) {
      Vec2D wp = Vec2D.transformMat2D(p, p, localTransform);
      if (wp[0] < minX) {
        minX = wp[0];
      }
      if (wp[1] < minY) {
        minY = wp[1];
      }

      if (wp[0] > maxX) {
        maxX = wp[0];
      }
      if (wp[1] > maxY) {
        maxY = wp[1];
      }
    }
    return AABB.fromValues(minX, minY, maxX, maxY);
  }

  public void invalidateDrawable() {
    invalidatePath();
    if (shape != null) {
      shape.invalidateShape();
    }
  }

  public AABB getPathOBB() {
    double minX = double.maxFinite;
    double minY = double.maxFinite;
    double maxX = -double.maxFinite;
    double maxY = -double.maxFinite;

    List<PathPoint> renderPoints = points;
    for (final PathPoint point : renderPoints) {
      Vec2D t = point.translation;
      double x = t[0];
      double y = t[1];
      if (x < minX) {
        minX = x;
      }
      if (y < minY) {
        minY = y;
      }
      if (x > maxX) {
        maxX = x;
      }
      if (y > maxY) {
        maxY = y;
      }

      if (point instanceof CubicPathPoint) {
        Vec2D t = point.inPoint;
        x = t[0];
        y = t[1];
        if (x < minX) {
          minX = x;
        }
        if (y < minY) {
          minY = y;
        }
        if (x > maxX) {
          maxX = x;
        }
        if (y > maxY) {
          maxY = y;
        }

        t = point.outPoint;
        x = t[0];
        y = t[1];
        if (x < minX) {
          minX = x;
        }
        if (y < minY) {
          minY = y;
        }
        if (x > maxX) {
          maxX = x;
        }
        if (y > maxY) {
          maxY = y;
        }
      }
    }

    return AABB.fromValues(minX, minY, maxX, maxY);
  }

  public void updateShape() {
    if (_shape != null) {
      _shape.removePath(this);
    }
    ActorNode possibleShape = parent;
    while (possibleShape != null && possibleShape is! ActorShape) {
      possibleShape = possibleShape.parent;
    }
    if (possibleShape != null) {
      _shape = possibleShape as ActorShape;
      _shape.addPath(this);
    } else {
      _shape = null;
    }
    _isRootPath = _shape == parent;
  }

  public void completeResolve() {
    updateShape();
  }
}
