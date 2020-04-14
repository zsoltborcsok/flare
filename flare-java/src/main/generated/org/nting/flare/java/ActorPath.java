package org.nting.flare.java;

import java.util.ArrayList;
import java.util.List;

public class ActorPath extends ActorNode with ActorSkinnable, ActorBasePath {
  boolean _isHidden;
  boolean _isClosed;
  List<PathPoint> _points;
  Float32List vertexDeform;

  @Override
  boolean get isPathInWorldSpace => isConnectedToBones;

  @Override
  public void invalidatePath() {
    // Up to the implementation.
  }

  @Override
  Mat2D get pathTransform => isConnectedToBones ? Mat2D() : worldTransform;

  public static final int vertexDeformDirty = 1 << 1;

  @Override
  List<PathPoint> get points => _points;

  @Override
  List<PathPoint> get deformedPoints {
    if (!isConnectedToBones || skin == null) {
      return _points;
    }

    Float32List boneMatrices = skin.boneMatrices;
    List<PathPoint> deformed = <PathPoint>[];
    for (final PathPoint point : _points) {
      deformed.add(point.skin(worldTransform, boneMatrices));
    }
    return deformed;
  }

  boolean get isClosed {
    return _isClosed;
  }

  @Override
  public void onDirty(int dirt) {
    super.onDirty(dirt);
    // We transformed, make sure parent is invalidated.
    if (shape != null) {
      shape.invalidateShape();
    }
  }

  public void makeVertexDeform() {
    if (vertexDeform != null) {
      return;
    }
    int length = points.fold<int>(0, (int previous, PathPoint point) {
      return previous + 2 + (point.pointType == PointType.straight ? 1 : 4);
    });
    Float32List vertices = new Float32List(length);
    int readIdx = 0;
    for (final PathPoint point : points) {
      vertices[readIdx++] = point.translation[0];
      vertices[readIdx++] = point.translation[1];
      if (point.pointType == PointType.straight) {
        // radius
        vertices[readIdx++] = (point as StraightPathPoint).radius;
      } else {
        // in/out
        CubicPathPoint cubicPoint = point as CubicPathPoint;
        vertices[readIdx++] = cubicPoint.inPoint[0];
        vertices[readIdx++] = cubicPoint.inPoint[1];
        vertices[readIdx++] = cubicPoint.outPoint[0];
        vertices[readIdx++] = cubicPoint.outPoint[1];
      }
    }
    vertexDeform = vertices;
  }

  public void markVertexDeformDirty() {
    if (artboard == null) {
      return;
    }
    artboard.addDirt(this, vertexDeformDirty, false);
  }

  @Override
  public void update(int dirt) {
    if (vertexDeform != null &&
        (dirt & vertexDeformDirty) == vertexDeformDirty) {
      int readIdx = 0;
      for (final PathPoint point : _points) {
        point.translation[0] = vertexDeform[readIdx++];
        point.translation[1] = vertexDeform[readIdx++];
        switch (point.pointType) {
          case PointType.straight:
            (point as StraightPathPoint).radius = vertexDeform[readIdx++];
            break;

          default:
            CubicPathPoint cubicPoint = point as CubicPathPoint;
            cubicPoint.inPoint[0] = vertexDeform[readIdx++];
            cubicPoint.inPoint[1] = vertexDeform[readIdx++];
            cubicPoint.outPoint[0] = vertexDeform[readIdx++];
            cubicPoint.outPoint[1] = vertexDeform[readIdx++];
            break;
        }
      }
    }
    invalidateDrawable();

    super.update(dirt);
  }

  static ActorPath read(ActorArtboard artboard, StreamReader reader,
      ActorPath component) {
    component ??= new ActorPath();
    ActorNode.read(artboard, reader, component);
    ActorSkinnable.read(artboard, reader, component);

    component._isHidden = !reader.readBoolean("isVisible");
    component._isClosed = reader.readBoolean("isClosed");

    reader.openArray("points");
    int pointCount = reader.readUint16Length();
    component._points = new ArrayList<PathPoint>(pointCount);
    for (int i = 0; i < pointCount; i++) {
      reader.openObject("point");
      PathPoint point;
      PointType type = PointType.values()[reader.readUint8("pointType")];
      switch (type) {
        case PointType.straight:
          {
            point = new StraightPathPoint();
            break;
          }
        default:
          {
            point = new CubicPathPoint(type);
            break;
          }
      }
      if (point == null) {
        throw new UnsupportedError("Invalid point type " + type.toString());
      } else {
        point.read(reader, component.isConnectedToBones);
      }
      reader.closeObject();

      component._points[i] = point;
    }
    reader.closeArray();
    return component;
  }

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorPath instanceEvent = new ActorPath();
    instanceEvent.copyPath(this, resetArtboard);
    return instanceEvent;
  }

  @Override
  public void resolveComponentIndices(List<ActorComponent> components) {
    super.resolveComponentIndices(components);
    resolveSkinnable(components);
  }

  public void copyPath(ActorPath node, ActorArtboard resetArtboard) {
    copyNode(node, resetArtboard);
    copySkinnable(node, resetArtboard);
    _isHidden = node._isHidden;
    _isClosed = node._isClosed;

    int pointCount = node._points.length;

    _points = new ArrayList<PathPoint>(pointCount);
    for (int i = 0; i < pointCount; i++) {
      _points[i] = node._points[i].makeInstance();
    }

    if (node.vertexDeform != null) {
      vertexDeform = Float32List.fromList(node.vertexDeform);
    }
  }
}
