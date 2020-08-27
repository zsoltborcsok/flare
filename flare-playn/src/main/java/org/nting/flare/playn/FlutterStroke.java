package org.nting.flare.playn;

import static java.lang.Math.min;
import org.nting.flare.java.maths.AABB;
import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

import java.util.ArrayList;
import java.util.List;

public abstract class FlutterStroke {
  ui.Paint _paint;
  public void onPaintUpdated(ui.Paint paint) {}
  ui.Path effectPath;

  public void initializeGraphics() {
    // yikes, no nice way to inherit with a mixin.
    ActorStroke stroke = this as ActorStroke;

    _paint = ui.Paint()
      ..style = ui.PaintingStyle.stroke
      ..strokeWidth = stroke.width
      ..strokeCap = FlutterStroke.getStrokeCap(stroke.cap)
      ..strokeJoin = FlutterStroke.getStrokeJoin(stroke.join);
    onPaintUpdated(_paint);
  }

  public static ui.StrokeCap getStrokeCap(StrokeCap cap) {
    switch (cap) {
      case StrokeCap.butt:
        return ui.StrokeCap.butt;
      case StrokeCap.round:
        return ui.StrokeCap.round;
      case StrokeCap.square:
        return ui.StrokeCap.square;
    }
    return ui.StrokeCap.butt;
  }

  public static ui.StrokeJoin getStrokeJoin(StrokeJoin join) {
    switch (join) {
      case StrokeJoin.miter:
        return ui.StrokeJoin.miter;
      case StrokeJoin.round:
        return ui.StrokeJoin.round;
      case StrokeJoin.bevel:
        return ui.StrokeJoin.bevel;
    }
    return ui.StrokeJoin.miter;
  }

  public void paint(ActorStroke stroke, ui.Canvas canvas, ui.Path path) {
    if (stroke.width == 0) {
      return;
    }

    if (stroke.isTrimmed) {
      if (effectPath == null) {
        boolean isSequential = stroke.trim == TrimPath.sequential;
        double start = stroke.trimStart.clamp(0, 1).toDouble();
        double end = stroke.trimEnd.clamp(0, 1).toDouble();
        double offset = stroke.trimOffset;
        boolean inverted = start > end;
        if ((start - end).abs() != 1.0) {
          start = (start + offset) % 1.0;
          end = (end + offset) % 1.0;

          if (start < 0) {
            start += 1.0;
          }
          if (end < 0) {
            end += 1.0;
          }
          if (inverted) {
            final double swap = end;
            end = start;
            start = swap;
          }
          if (end >= start) {
            effectPath = trimPath(path, start, end, false, isSequential);
          } else {
            effectPath = trimPath(path, end, start, true, isSequential);
          }
        } else {
          effectPath = path;
        }
      }
      path = effectPath;
    }
    canvas.drawPath(path, _paint);
  }

  public void markPathEffectsDirty() {
    effectPath = null;
  }
}

