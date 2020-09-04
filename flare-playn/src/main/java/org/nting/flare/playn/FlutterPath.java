package org.nting.flare.playn;

import org.nting.flare.java.maths.Mat2D;
import pythagoras.f.AffineTransform;
import pythagoras.f.Path;

/// Abstract base path that can be invalidated and somehow
/// regenerates, no concrete logic
public interface FlutterPath {

    Path path();

    default Path pathWithTransform(Mat2D pathTransform) {
        Path path = path();
        if (pathTransform == null) {
            return path;
        } else {
            float[] m32 = pathTransform.values(); // 3x2 matrix
            path.transform(new AffineTransform(m32[0], m32[1], m32[2], m32[3], m32[4], m32[5]));
            return path;
        }
    }
}
