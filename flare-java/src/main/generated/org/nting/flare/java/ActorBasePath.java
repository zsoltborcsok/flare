package org.nting.flare.java;

import java.util.List;

import org.nting.flare.java.maths.AABB;
import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

import com.google.common.collect.Lists;

public interface ActorBasePath {

    ActorShape shape();

    void shape(ActorShape shape);

    boolean isRootPath();

    void setRootPath(boolean isRootPath);

    List<PathPoint> points();

    ActorNode parent();

    void invalidatePath();

    default boolean isPathInWorldSpace() {
        return false;
    }

    Mat2D pathTransform();

    Mat2D transform();

    Mat2D worldTransform();

    List<List<ActorClip>> allClips();

    default List<PathPoint> deformedPoints() {
        return points();
    }

    default AABB getPathAABB() {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;

        AABB obb = getPathOBB();

        List<Vec2D> pts = Lists.newArrayList(new Vec2D(obb.values()[0], obb.values()[1]),
                new Vec2D(obb.values()[2], obb.values()[1]), new Vec2D(obb.values()[2], obb.values()[3]),
                new Vec2D(obb.values()[0], obb.values()[3]));

        Mat2D localTransform;
        if (isPathInWorldSpace()) {
            // convert the path coordinates into local parent space.
            localTransform = new Mat2D();
            Mat2D.invert(localTransform, parent().worldTransform());
        } else if (!isRootPath()) {
            localTransform = new Mat2D();
            // Path isn't root, so get transform in shape space.
            if (Mat2D.invert(localTransform, shape().worldTransform())) {
                Mat2D.multiply(localTransform, localTransform, worldTransform());
            }
        } else {
            localTransform = transform();
        }

        for (final Vec2D p : pts) {
            Vec2D wp = Vec2D.transformMat2D(p, p, localTransform);
            if (wp.values()[0] < minX) {
                minX = wp.values()[0];
            }
            if (wp.values()[1] < minY) {
                minY = wp.values()[1];
            }

            if (wp.values()[0] > maxX) {
                maxX = wp.values()[0];
            }
            if (wp.values()[1] > maxY) {
                maxY = wp.values()[1];
            }
        }
        return new AABB(minX, minY, maxX, maxY);
    }

    default void invalidateDrawable() {
        invalidatePath();
        if (shape() != null) {
            shape().invalidateShape();
        }
    }

    default AABB getPathOBB() {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;

        List<PathPoint> renderPoints = points();
        for (final PathPoint point : renderPoints) {
            Vec2D t = point.translation();
            float x = t.values()[0];
            float y = t.values()[1];
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
                t = ((CubicPathPoint) point).inPoint();
                x = t.values()[0];
                y = t.values()[1];
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

                t = ((CubicPathPoint) point).outPoint();
                x = t.values()[0];
                y = t.values()[1];
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

        return new AABB(minX, minY, maxX, maxY);
    }

    default void updateShape() {
        if (shape() != null) {
            shape().removePath(this);
        }
        ActorNode possibleShape = parent();
        while (possibleShape != null && !(possibleShape instanceof ActorShape)) {
            possibleShape = possibleShape.parent();
        }
        if (possibleShape != null) {
            shape((ActorShape) possibleShape);
            shape().addPath(this);
        } else {
            shape(null);
        }
        setRootPath(shape() == parent());
    }

    default void completeResolve() {
        updateShape();
    }
}
