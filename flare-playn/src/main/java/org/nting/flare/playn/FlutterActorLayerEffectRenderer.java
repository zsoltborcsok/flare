package org.nting.flare.playn;

import org.nting.flare.java.ActorLayerEffectRenderer;

public class FlutterActorLayerEffectRenderer extends ActorLayerEffectRenderer implements FlutterActorDrawable {

    // @Override
    // public void draw(ui.Canvas canvas) {
    // public var aabb = artboard.artboardAABB();
    // public Rect bounds = Rect.fromLTRB(aabb[0], aabb[1], aabb[2], aabb[3]);
    //
    // public double baseBlurX = 0;
    // public double baseBlurY = 0;
    // public Paint layerPaint = new Paint()..isAntiAlias = useAntialias;
    // public Color layerColor = Colors.white.withOpacity(parent.renderOpacity);
    // layerPaint.color = layerColor;
    // if Optional.ofNullable((blur).ifPresent(v -> v.isActive ?? false) {
    // baseBlurX = blur.blurX;
    // baseBlurY = blur.blurY;
    // layerPaint.imageFilter = _blurFilter(baseBlurX, baseBlurY);
    // }
    //
    // if (dropShadows.isNotEmpty) {
    // for (final dropShadow : dropShadows) {
    // if (!dropShadow.isActive) {
    // continue;
    // }
    // // DropShadow: To draw a shadow we just draw the shape (with
    // // drawPass) with a custom color and image (blur) filter before
    // // drawing the main shape.
    // canvas.save();
    // var color = dropShadow.color;
    // canvas.translate(dropShadow.offsetX, dropShadow.offsetY);
    // var shadowPaint = new Paint()
    // ..isAntiAlias = useAntialias
    // ..color = layerColor
    // ..imageFilter = _blurFilter(
    // dropShadow.blurX + baseBlurX, dropShadow.blurY + baseBlurY)
    // ..colorFilter = ui.ColorFilter.mode(
    // ui.Color.fromRGBO(
    // (color[0] * 255.0).round(),
    // (color[1] * 255.0).round(),
    // (color[2] * 255.0).round(),
    // color[3]),
    // ui.BlendMode.srcIn)
    // ..blendMode = ui.BlendMode.values[dropShadow.blendModeId];
    //
    // drawPass(canvas, bounds, shadowPaint);
    // canvas.restore();
    // canvas.restore();
    // }
    // }
    // drawPass(canvas, bounds, layerPaint);
    // // Draw inner shadows on the main layer.
    // if (innerShadows.isNotEmpty) {
    // for (final innerShadow : innerShadows) {
    // if (!innerShadow.isActive) {
    // continue;
    // }
    // var blendMode = ui.BlendMode.values[innerShadow.blendModeId];
    // boolean extraBlendPass = blendMode != ui.BlendMode.srcOver;
    // if (extraBlendPass) {
    // // if we have a custom blend mode, then we can't just srcATop with
    // // what's already been drawn. We need to draw the contents as a mask
    // // to then draw the shadow on top of with srcIn to only show the
    // // shadow and finally composite with the desired blend mode requested
    // // here.
    // var extraLayerPaint = new Paint()
    // ..blendMode = blendMode
    // ..isAntiAlias = useAntialias;
    // drawPass(canvas, bounds, extraLayerPaint);
    // }
    //
    // // because there's no way to compose image filters (use two filters in
    // // one) we have to use an extra layer to invert the alpha for the inner
    // // shadow before blurring.
    //
    // var color = innerShadow.color;
    // var shadowPaint = new Paint()
    // ..isAntiAlias = useAntialias
    // ..color = layerColor
    // ..blendMode =
    // extraBlendPass ? ui.BlendMode.srcIn : ui.BlendMode.srcATop
    // ..imageFilter = _blurFilter(
    // innerShadow.blurX + baseBlurX, innerShadow.blurY + baseBlurY)
    // ..colorFilter = ui.ColorFilter.mode(
    // ui.Color.fromRGBO(
    // (color[0] * 255.0).round(),
    // (color[1] * 255.0).round(),
    // (color[2] * 255.0).round(),
    // color[3]),
    // ui.BlendMode.srcIn);
    //
    // canvas.saveLayer(bounds, shadowPaint);
    // canvas.translate(innerShadow.offsetX, innerShadow.offsetY);
    //
    // // Invert the alpha to compute inner part.
    // var invertPaint = new Paint()
    // ..isAntiAlias = useAntialias
    // ..colorFilter = final ui.ColorFilter.matrix([
    // 1,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 1,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 1,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // -1,
    // 255,
    // ]);
    // drawPass(canvas, bounds, invertPaint);
    // // restore draw pass (inverted aint)
    // canvas.restore();
    // // restore save layer used to that blurs and colors the shadow
    // canvas.restore();
    //
    // if (extraBlendPass) {
    // // Restore extra layer used to draw the contents to clip against (we
    // // clip by drawing with srcIn)
    // canvas.restore();
    // }
    // }
    // }
    // canvas.restore();
    // }
    //
    // public void drawPass(ui.Canvas canvas, Rect bounds, Paint layerPaint) {
    // canvas.saveLayer(bounds, layerPaint);
    // for (final drawable : drawables) {
    // if (drawable instanceof FlutterActorDrawable) {
    // (drawable as FlutterActorDrawable).draw(canvas);
    // }
    // }
    //
    // for (final renderMask : renderMasks) {
    // var mask = renderMask.mask;
    // if (!mask.isActive) {
    // continue;
    // }
    //
    // var maskPaint = new Paint();
    // switch (mask.maskType) {
    // case MaskType.invertedAlpha:
    // maskPaint.colorFilter = final ui.ColorFilter.matrix(
    // [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 255]);
    // break;
    // case MaskType.luminance:
    // maskPaint.colorFilter = final ui.ColorFilter.matrix([
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0.33,
    // 0.59,
    // 0.11,
    // 0,
    // 0
    // ]);
    // break;
    // case MaskType.invertedLuminance:
    // maskPaint.colorFilter = final ui.ColorFilter.matrix([
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // 0,
    // -0.33,
    // -0.59,
    // -0.11,
    // 0,
    // 255
    // ]);
    // break;
    // case MaskType.alpha:
    // default:
    // maskPaint.colorFilter = final ui.ColorFilter.matrix(
    // [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0]);
    // break;
    // }
    //
    // maskPaint.blendMode = BlendMode.dstIn;
    // maskPaint.isAntiAlias = useAntialias;
    // canvas.saveLayer(bounds, maskPaint);
    // for (final drawable : renderMask.drawables) {
    // boolean wasHidden = drawable.isHidden;
    // if (wasHidden) {
    // drawable.isHidden = false;
    // }
    // (drawable as FlutterActorDrawable).draw(canvas);
    // if (wasHidden) {
    // drawable.isHidden = true;
    // }
    // }
    // canvas.restore();
    // }
    // }
    //
    // @Override
    // public void onBlendModeChanged(ui.BlendMode blendMode) {
    // // We don't currently support custom blend modes on the layer effect
    // // renderer.
    // }
    //
    // @Override
    // public void onAntialiasChanged(boolean useAA) {
    // for (final drawable : drawables) {
    // if (drawable instanceof FlutterActorDrawable) {
    // (drawable as FlutterActorDrawable).useAntialias = useAA;
    // }
    // }
    // }
    //
    // public ui.ImageFilter _blurFilter(double x, double y) {
    // double bx = x.abs() < 0.1 ? 0 : x;
    // double by = y.abs() < 0.1 ? 0 : y;
    // return bx == 0 && by == 0
    // ? null
    // : ui.ImageFilter.blur(sigmaX: bx, sigmaY: by);
    // }
}
