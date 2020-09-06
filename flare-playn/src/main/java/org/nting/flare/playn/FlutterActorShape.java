package org.nting.flare.playn;

import java.util.Optional;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorBasePath;
import org.nting.flare.java.ActorFill;
import org.nting.flare.java.ActorShape;
import org.nting.flare.java.ActorStroke;
import org.nting.flare.java.maths.Mat2D;

import playn.core.Canvas;
import pythagoras.f.Path;

public class FlutterActorShape extends ActorShape implements FlutterActorDrawable {

    private final Path _path = new Path();
    private boolean _isValid = false;

    @Override
    public ActorArtboard artboard() {
        return artboard;
    }

    @Override
    public void blendModeId(int blendModeId) {
        super.blendModeId(blendModeId);
        markPaintDirty();
    }

    @Override
    public void invalidateShape() {
        _isValid = false;
        Optional.ofNullable(stroke()).ifPresent(v -> v.markPathEffectsDirty.run());
    }

    private void markPaintDirty() {
        for (ActorFill actorFill : fills()) {
            actorFill.actorPaint.markPaintDirty();
        }
        for (ActorStroke actorStroke : strokes()) {
            actorStroke.actorPaint.markPaintDirty();
        }
    }

    public Path path() {
        if (_isValid) {
            return _path;
        }
        _isValid = true;
        _path.reset();

        // if (fill != null && fill.fillRule == FillRule.evenOdd) {
        // _path.fillType = PathFillType.evenOdd;
        // } else {
        // _path.fillType = PathFillType.nonZero;
        // }

        for (final ActorBasePath path : paths()) {
            Mat2D transform = path.pathTransform();
            _path.append(((FlutterPath) path).pathWithTransform(transform), false);
        }
        return _path;
    }

    protected playn.core.Path getRenderPath(Canvas canvas) {
        playn.core.Path path = canvas.createPath();
        path.append(path(), false);
        return path;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!doesDraw()) {
            return;
        }

        canvas.save();

        clip(canvas);

        playn.core.Path renderPath = getRenderPath(canvas);

        for (ActorFill actorFill : fills()) {
            FlutterFill fill = (FlutterFill) actorFill.actorPaint;
            fill.paint(actorFill, canvas, renderPath);
        }
        for (ActorStroke actorStroke : strokes()) {
            FlutterStroke stroke = (FlutterStroke) actorStroke.actorPaint;
            stroke.paint(actorStroke, canvas, renderPath);
        }

        canvas.restore();
    }
}
