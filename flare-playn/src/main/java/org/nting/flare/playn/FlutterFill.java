package org.nting.flare.playn;

import org.nting.flare.java.ActorFill;

import playn.core.Canvas;
import playn.core.Path;

public interface FlutterFill {

    default void paint(ActorFill fill, Canvas canvas, Path path) {
        // Note FillRule is not supported by PlayN
        canvas.fillPath(path);
    }
}
