package org.nting.flare.playn;

import org.nting.flare.java.ActorFill;

import playn.core.Canvas;
import playn.core.Path;

public interface JavaFill {

    default void paint(ActorFill fill, Canvas canvas, Path path) {
        // path.setFillType(fill.fillRule()) is not supported by PlayN (evenOdd vs nonZero)
        canvas.fillPath(path);
    }
}
