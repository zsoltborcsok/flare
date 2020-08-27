package org.nting.flare.playn;

import org.nting.flare.java.ActorShape;

public class FlutterActorShape extends ActorShape implements FlutterActorDrawable {

    // ui.Path _path;
    // private boolean _isValid = false;
    //
    // @Override
    // public void initializeGraphics() {
    // super.initializeGraphics();
    // _path = ui.Path();
    // for (final ActorBasePath path : paths) {
    // (path as FlutterPath).initializeGraphics();
    // }
    // }
    //
    // @Override
    // public void invalidateShape() {
    // _isValid = false;
    // Optional.ofNullable(stroke).ifPresent(v -> v.markPathEffectsDirty());
    // }
    //
    // public void _markPaintDirty() {
    // if (fills != null) {
    // for (final ActorFill actorFill : fills) {
    // (actorFill as ActorPaint).markPaintDirty();
    // }
    // }
    // if (strokes != null) {
    // for (final ActorStroke actorStroke : strokes) {
    // (actorStroke as ActorPaint).markPaintDirty();
    // }
    // }
    // }
    //
    // @Override
    // public void onBlendModeChanged(ui.BlendMode mode) {
    // _markPaintDirty();
    // }
    //
    // @Override
    // public void onAntialiasChanged(boolean useAA) {
    // _markPaintDirty();
    // }
    //
    // public ui.Path path() {
    // if (_isValid) {
    // return _path;
    // }
    // _isValid = true;
    // _path.reset();
    //
    // if (fill != null && fill.fillRule == FillRule.evenOdd) {
    // _path.fillType = PathFillType.evenOdd;
    // } else {
    // _path.fillType = PathFillType.nonZero;
    // }
    //
    // for (final ActorBasePath path : paths) {
    // Mat2D transform = path.pathTransform;
    // _path.addPath((path as FlutterPath).path, ui.Offset.zero,
    // matrix4: Optional.ofNullable(transform).ifPresent(v -> v.mat4));
    // }
    // return _path;
    // }
    //
    // public ui.Path getRenderPath(ui.Canvas canvas) {
    // return path;
    // }
    //
    // @Override
    // public void draw(ui.Canvas canvas) {
    // if (!doesDraw) {
    // return;
    // }
    //
    // canvas.save();
    //
    // clip(canvas);
    //
    // ui.Path renderPath = getRenderPath(canvas);
    //
    // if (fills != null) {
    // for (final ActorFill actorFill : fills) {
    // FlutterFill fill = actorFill as FlutterFill;
    // fill.paint(actorFill, canvas, renderPath);
    // }
    // }
    // if (strokes != null) {
    // for (final ActorStroke actorStroke : strokes) {
    // FlutterStroke stroke = actorStroke as FlutterStroke;
    // stroke.paint(actorStroke, canvas, renderPath);
    // }
    // }
    //
    // canvas.restore();
    // }
}
