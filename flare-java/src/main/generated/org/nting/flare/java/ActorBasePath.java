package org.nting.flare.java;

import org.nting.flare.java.maths.AABB;
import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

import java.util.List;

public abstract class ActorBasePath {
  private ActorShape _shape;

  public ActorShape shape() { return _shape; }
  private boolean _isRootPath = false;

  public boolean isRootPath() { return _isRootPath; }

  public abstract List<PathPoint> points();

  public abstract ActorNode parent();

  public abstract void invalidatePath();

  public boolean isPathInWorldSpace() { return false; }

  public abstract Mat2D pathTransform();

  public abstract Mat2D transform();

  public abstract Mat2D worldTransform();

  public abstract List<List<ActorClip>> allClips();

  public List<PathPoint> deformedPoints() { return points; }

  public AABB getPathAABB() {
    double minX = Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE;
    double maxY = -Double.MAX_VALUE;

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
    double minX = Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE;
    double maxY = -Double.MAX_VALUE;

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
    while (possibleShape != null && !(possibleShape instanceof ActorShape)) {
      possibleShape = possibleShape.parent;
    }
    if (possibleShape != null) {
      _shape = (ActorShape) possibleShape;
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
