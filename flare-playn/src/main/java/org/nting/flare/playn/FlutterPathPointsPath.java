package org.nting.flare.playn;

import static java.lang.Math.min;

import java.util.List;

import org.nting.flare.java.CubicPathPoint;
import org.nting.flare.java.PathPoint;
import org.nting.flare.java.StraightPathPoint;
import org.nting.flare.java.maths.Vec2D;

import com.google.common.collect.Lists;

import pythagoras.f.Path;

/// Abstract path that uses Actor PathPoints, slightly higher level
/// that FlutterPath. Most shapes can use this, but if they want to
/// use a different procedural backing call, they should implement
/// FlutterPath and generate the path another way.
public interface FlutterPathPointsPath extends FlutterPath {

    List<PathPoint> deformedPoints();

    boolean isClosed();

    default void buildPath(Path path) {
        path.reset();

        List<PathPoint> pts = deformedPoints();
        if (pts == null || pts.isEmpty()) {
            return;
        }

        List<PathPoint> renderPoints = Lists.newLinkedList();
        int pl = pts.size();

        final float arcConstant = 0.55f;
        final float iarcConstant = 1.0f - arcConstant;
        PathPoint previous = isClosed() ? pts.get(pl - 1) : null;
        for (int i = 0; i < pl; i++) {
            PathPoint point = pts.get(i);
            switch (point.pointType()) {
            case straight: {
                StraightPathPoint straightPoint = (StraightPathPoint) point;
                float radius = straightPoint.radius;
                if (radius > 0) {
                    if (!isClosed() && (i == 0 || i == pl - 1)) {
                        renderPoints.add(point);
                        previous = point;
                    } else {
                        PathPoint next = pts.get((i + 1) % pl);
                        Vec2D prevPoint = previous instanceof CubicPathPoint ? ((CubicPathPoint) previous).outPoint()
                                : previous.translation();
                        Vec2D nextPoint = next instanceof CubicPathPoint ? ((CubicPathPoint) next).inPoint()
                                : next.translation();
                        Vec2D pos = point.translation();

                        Vec2D toPrev = Vec2D.subtract(new Vec2D(), prevPoint, pos);
                        float toPrevLength = Vec2D.length(toPrev);
                        toPrev.values()[0] /= toPrevLength;
                        toPrev.values()[1] /= toPrevLength;

                        Vec2D toNext = Vec2D.subtract(new Vec2D(), nextPoint, pos);
                        float toNextLength = Vec2D.length(toNext);
                        toNext.values()[0] /= toNextLength;
                        toNext.values()[1] /= toNextLength;

                        float renderRadius = min(toPrevLength, min(toNextLength, radius));

                        Vec2D translation = Vec2D.scaleAndAdd(new Vec2D(), pos, toPrev, renderRadius);
                        renderPoints.add(new CubicPathPoint(translation, translation,
                                Vec2D.scaleAndAdd(new Vec2D(), pos, toPrev, iarcConstant * renderRadius)));
                        translation = Vec2D.scaleAndAdd(new Vec2D(), pos, toNext, renderRadius);
                        previous = new CubicPathPoint(translation,
                                Vec2D.scaleAndAdd(new Vec2D(), pos, toNext, iarcConstant * renderRadius), translation);
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

        PathPoint firstPoint = renderPoints.get(0);
        path.moveTo(firstPoint.translation().values()[0], firstPoint.translation().values()[1]);
        for (int i = 0, l = isClosed() ? renderPoints.size() : renderPoints.size() - 1, psl = renderPoints
                .size(); i < l; i++) {
            PathPoint point = renderPoints.get(i);
            PathPoint nextPoint = renderPoints.get((i + 1) % psl);
            Vec2D cin = nextPoint instanceof CubicPathPoint ? ((CubicPathPoint) nextPoint).inPoint() : null;
            Vec2D cout = point instanceof CubicPathPoint ? ((CubicPathPoint) point).outPoint() : null;
            if (cin == null && cout == null) {
                path.lineTo(nextPoint.translation().values()[0], nextPoint.translation().values()[1]);
            } else {
                cout = cout != null ? cout : point.translation();
                cin = cin != null ? cin : nextPoint.translation();

                path.curveTo(cout.values()[0], cout.values()[1], cin.values()[0], cin.values()[1],
                        nextPoint.translation().values()[0], nextPoint.translation().values()[1]);
            }
        }

        if (isClosed()) {
            path.closePath();
        }
    }
}
