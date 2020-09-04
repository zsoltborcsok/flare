package org.nting.flare.playn;

import org.nting.flare.java.ActorBasePath;
import org.nting.flare.java.maths.Mat2D;
import playn.core.Canvas;
import pythagoras.f.Path;

public class FlutterActorShapeWithTransformedStroke extends FlutterActorShape {

    private final Path _localPath = new Path();
    private boolean _isLocalValid = false;

    @Override
    public void invalidateShape() {
        _isLocalValid = false;
        super.invalidateShape();
    }

    private Path localPath() {
        if (_isLocalValid) {
            return _localPath;
        }
        _isLocalValid = true;
        _localPath.reset();

        Mat2D inverseWorld = new Mat2D();
        if (!Mat2D.invert(inverseWorld, worldTransform())) {
            Mat2D.identity(inverseWorld);
        }

        for (final ActorBasePath path : paths()) {
            Mat2D transform = path.pathTransform();

            Mat2D localTransform = null;
            if (transform != null) {
                localTransform = new Mat2D();
                Mat2D.multiply(localTransform, inverseWorld, transform);
            }
            _localPath.append(((FlutterPath) path).pathWithTransform(localTransform), false);
        }
        return _localPath;
    }

    @Override
    protected playn.core.Path getRenderPath(Canvas canvas) {
        float[] m32 = worldTransform().values(); // 3x2 matrix
        canvas.transform(m32[0], m32[1], m32[2], m32[3], m32[4], m32[5]);

        playn.core.Path localPath = canvas.createPath();
        localPath.append(localPath(), false);
        return localPath;
    }
}
