package org.nting.flare.java;

import static java.lang.Math.max;
import static java.lang.Math.min;
import org.nting.flare.java.maths.AABB;
import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

import java.util.List;

public class ActorShape extends ActorDrawable {
  private final List<ActorBasePath> _paths = <ActorBasePath>[];
  private final List<ActorStroke> _strokes = <ActorStroke>[];
  private final List<ActorFill> _fills = <ActorFill>[];
  private boolean _transformAffectsStroke = false;

  public boolean transformAffectsStroke() { return _transformAffectsStroke; }

  public ActorFill fill() { return !_fills.isEmpty() ? _fills.get(0) : null; }

  public ActorStroke stroke() { return !_strokes.isEmpty() ? _strokes.get(0) : null; }

  public List<ActorFill> fills() { return _fills; }

  public List<ActorStroke> strokes() { return _strokes; }

  public List<ActorBasePath> paths() { return _paths; }

  @Override
  public void update(int dirt) {
    super.update(dirt);
    invalidateShape();
  }

  static ActorShape read(ActorArtboard artboard, StreamReader reader,
      ActorShape component) {
    ActorDrawable.read(artboard, reader, component);
    if (artboard.actor.version >= 22) {
      component._transformAffectsStroke =
          reader.readBoolean("transformAffectsStroke");
    }

    return component;
  }

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorShape instanceShape = resetArtboard.actor.makeShapeNode(this);
    instanceShape.copyShape(this, resetArtboard);
    return instanceShape;
  }

  public void copyShape(ActorShape node, ActorArtboard resetArtboard) {
    copyDrawable(node, resetArtboard);
    _transformAffectsStroke = node._transformAffectsStroke;
  }

  @Override
  public AABB computeAABB() {
    AABB aabb;
    for (final List<ClipShape> clips : clipShapes) {
      for (final ClipShape clipShape : clips) {
        AABB bounds = clipShape.shape.computeAABB();
        if (bounds == null) {
          continue;
        }
        if (aabb == null) {
          aabb = bounds;
        } else {
          if (bounds[0] < aabb[0]) {
            aabb[0] = bounds[0];
          }
          if (bounds[1] < aabb[1]) {
            aabb[1] = bounds[1];
          }
          if (bounds[2] > aabb[2]) {
            aabb[2] = bounds[2];
          }
          if (bounds[3] > aabb[3]) {
            aabb[3] = bounds[3];
          }
        }
      }
    }
    if (aabb != null) {
      return aabb;
    }

    for (final ActorComponent component : children) {
      if (!(component instanceof ActorBasePath)) {
        continue;
      }
      ActorBasePath path = (ActorBasePath) component;
      // This is the axis aligned bounding box in the space of the
      // parent (this case our shape).
      AABB pathAABB = path.getPathAABB();

      if (aabb == null) {
        aabb = pathAABB;
      } else {
        // Combine.
        aabb[0] = min(aabb[0], pathAABB[0]);
        aabb[1] = min(aabb[1], pathAABB[1]);

        aabb[2] = max(aabb[2], pathAABB[2]);
        aabb[3] = max(aabb[3], pathAABB[3]);
      }
    }

    double minX = double.maxFinite;
    double minY = double.maxFinite;
    double maxX = -double.maxFinite;
    double maxY = -double.maxFinite;

    if (aabb == null) {
      return AABB.fromValues(minX, minY, maxX, maxY);
    }
    Mat2D world = worldTransform;

    if (_strokes != null) {
      double maxStroke = 0.0;
      for (final ActorStroke stroke : _strokes) {
        if (stroke.width > maxStroke) {
          maxStroke = stroke.width;
        }
      }
      double padStroke = maxStroke / 2.0;
      aabb[0] -= padStroke;
      aabb[2] += padStroke;
      aabb[1] -= padStroke;
      aabb[3] += padStroke;
    }

    List<Vec2D> points = [
      Vec2D.fromValues(aabb[0], aabb[1]),
      Vec2D.fromValues(aabb[2], aabb[1]),
      Vec2D.fromValues(aabb[2], aabb[3]),
      Vec2D.fromValues(aabb[0], aabb[3])
    ];
    for (var i = 0; i < points.size(); i++) {
      Vec2D pt = points[i];
      Vec2D wp = Vec2D.transformMat2D(pt, pt, world);
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

  public void addStroke(ActorStroke stroke) {
    _strokes.add(stroke);
  }

  public void addFill(ActorFill fill) {
    _fills.add(fill);
  }

  @Override
  public void initializeGraphics() {
    for (final ActorStroke stroke : _strokes) {
      stroke.initializeGraphics();
    }
    for (final ActorFill fill : _fills) {
      fill.initializeGraphics();
    }
  }

  @Override
  public int blendModeId() {
    return 0;
  }

  @Override
  set blendModeId(int value) {}

  public boolean addPath(ActorBasePath path) {
    if (_paths.contains(path)) {
      return false;
    }
    _paths.add(path);
    return true;
  }

  public boolean removePath(ActorBasePath path) {
    return _paths.remove(path);
  }
}
