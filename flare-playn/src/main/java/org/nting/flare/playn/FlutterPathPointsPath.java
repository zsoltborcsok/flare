package org.nting.flare.playn;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

/// Abstract path that uses Actor PathPoints, slightly higher level
/// that FlutterPath. Most shapes can use this, but if they want to
/// use a different procedural backing call, they should implement
/// FlutterPath and generate the path another way.
public abstract class FlutterPathPointsPath implements FlutterPath {
  ui.Path _path;
  public abstract List<PathPoint> deformedPoints();
  public abstract boolean isClosed();
  private boolean _isValid = false;

  @Override
  public void initializeGraphics() {
    _path = ui.Path();
  }

  @Override
  public ui.Path path() {
    if (_isValid) {
      return _path;
    }
    return _makePath();
  }

  public void invalidatePath() {
    _isValid = false;
  }

  public ui.Path _makePath() {
    _isValid = true;
    _path.reset();
    List<PathPoint> pts = deformedPoints;
    if (pts == null || pts.isEmpty()) {
      return _path;
    }

    List<PathPoint> renderPoints = new ArrayList<>();
    int pl = pts.size();

    final double arcConstant = 0.55;
    final double iarcConstant = 1.0 - arcConstant;
    PathPoint previous = isClosed ? pts[pl - 1] : null;
    for (int i = 0; i < pl; i++) {
      PathPoint point = pts[i];
      switch (point.pointType) {
        case PointType.straight:
          {
            StraightPathPoint straightPoint = point as StraightPathPoint;
            double radius = straightPoint.radius;
            if (radius > 0) {
              if (!isClosed && (i == 0 || i == pl - 1)) {
                renderPoints.add(point);
                previous = point;
              } else {
                PathPoint next = pts[(i + 1) % pl];
                Vec2D prevPoint = previous instanceof CubicPathPoint
                    ? previous.outPoint
                    : previous.translation;
                Vec2D nextPoint =
                    next instanceof CubicPathPoint ? next.inPoint : next.translation;
                Vec2D pos = point.translation;

                Vec2D toPrev = Vec2D.subtract(Vec2D(), prevPoint, pos);
                double toPrevLength = Vec2D.length(toPrev);
                toPrev[0] /= toPrevLength;
                toPrev[1] /= toPrevLength;

                Vec2D toNext = Vec2D.subtract(Vec2D(), nextPoint, pos);
                double toNextLength = Vec2D.length(toNext);
                toNext[0] /= toNextLength;
                toNext[1] /= toNextLength;

                double renderRadius =
                    min(toPrevLength, min(toNextLength, radius));

                Vec2D translation =
                    Vec2D.scaleAndAdd(Vec2D(), pos, toPrev, renderRadius);
                renderPoints.add(CubicPathPoint.fromValues(
                    translation,
                    translation,
                    Vec2D.scaleAndAdd(
                        Vec2D(), pos, toPrev, iarcConstant * renderRadius)));
                translation =
                    Vec2D.scaleAndAdd(Vec2D(), pos, toNext, renderRadius);
                previous = CubicPathPoint.fromValues(
                    translation,
                    Vec2D.scaleAndAdd(
                        Vec2D(), pos, toNext, iarcConstant * renderRadius),
                    translation);
                renderPoints.add(previous);
              }
            } else {
              renderPoints.add(point);
              previous = point;
            }
            break;
          }
        default:
          renderPoints.add(point);
          previous = point;
          break;
      }
    }

    PathPoint firstPoint = renderPoints[0];
    _path.moveTo(firstPoint.translation[0], firstPoint.translation[1]);
    for (int i = 0,
            l = isClosed ? renderPoints.size() : renderPoints.size() - 1,
            pl = renderPoints.size();
        i < l;
        i++) {
      PathPoint point = renderPoints[i];
      PathPoint nextPoint = renderPoints[(i + 1) % pl];
      Vec2D cin = nextPoint instanceof CubicPathPoint ? nextPoint.inPoint : null;
      Vec2D cout = point instanceof CubicPathPoint ? point.outPoint : null;
      if (cin == null && cout == null) {
        _path.lineTo(nextPoint.translation[0], nextPoint.translation[1]);
      } else {
        cout = cout != null ? cout : point.translation;
        cin = cin != null ? cin : nextPoint.translation;

        _path.cubicTo(cout[0], cout[1], cin[0], cin[1],
            nextPoint.translation[0], nextPoint.translation[1]);
      }
    }

    if (isClosed) {
      _path.close();
    }

    return _path;
  }
}
