package org.nting.flare.playn;

import static java.lang.Math.abs;
import static org.nting.flare.playn.util.TrimPathUtil.trimPath;
import static pythagoras.f.MathUtil.clamp;

import org.nting.flare.java.ActorStroke;
import org.nting.flare.java.StrokeCap;
import org.nting.flare.java.StrokeJoin;
import org.nting.flare.java.TrimPath;
import org.nting.flare.playn.util.Capture;

import playn.core.Canvas;
import playn.core.Canvas.LineCap;
import playn.core.Canvas.LineJoin;
import playn.core.Path;

public interface FlutterStroke {

    Capture<Path> effectPath();

    static LineCap getLineCap(StrokeCap cap) {
        switch (cap) {
        case butt:
            return LineCap.BUTT;
        case round:
            return LineCap.ROUND;
        case square:
            return LineCap.SQUARE;
        }
        return LineCap.BUTT;
    }

    static LineJoin getLineJoin(StrokeJoin join) {
        switch (join) {
        case miter:
            return LineJoin.MITER;
        case round:
            return LineJoin.ROUND;
        case bevel:
            return LineJoin.BEVEL;
        }
        return LineJoin.MITER;
    }

    default void paint(ActorStroke stroke, Canvas canvas, Path path) {
        if (stroke.width() == 0) {
            return;
        }

        if (stroke.isTrimmed()) {
            if (effectPath().get() == null) {
                boolean isSequential = stroke.trim() == TrimPath.sequential;
                float start = clamp(stroke.trimStart(), 0, 1);
                float end = clamp(stroke.trimEnd(), 0, 1);
                float offset = stroke.trimOffset();
                boolean inverted = start > end;
                if (abs(start - end) != 1.0f) {
                    start = (start + offset) % 1.0f;
                    end = (end + offset) % 1.0f;

                    if (start < 0) {
                        start += 1.0f;
                    }
                    if (end < 0) {
                        end += 1.0f;
                    }
                    if (inverted) {
                        final float swap = end;
                        end = start;
                        start = swap;
                    }
                    Path effectPath = canvas.createPath();
                    if (end >= start) {
                        effectPath.append(trimPath(path, start, end, false, isSequential), false);
                    } else {
                        effectPath.append(trimPath(path, end, start, true, isSequential), false);
                    }
                    effectPath().set(effectPath);
                } else {
                    effectPath().set(path);
                }
            }
            path = effectPath().get();
        }

        canvas.setStrokeWidth(stroke.width());
        canvas.setLineCap(getLineCap(stroke.cap()));
        canvas.setLineJoin(getLineJoin(stroke.join()));
        canvas.strokePath(path);
    }

    default void markPathEffectsDirty() {
        effectPath().set(null);
    }
}
