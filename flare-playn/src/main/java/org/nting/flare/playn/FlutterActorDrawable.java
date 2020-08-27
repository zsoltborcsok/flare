package org.nting.flare.playn;

import java.util.List;

public abstract class FlutterActorDrawable {
  private boolean _useAntialias;
  ui.BlendMode _blendMode;

  public int blendModeId() {
    return _blendMode.index;
  }

  public void blendModeId(int index) {
    blendMode = ui.BlendMode.values[index];
  }

  public ui.BlendMode blendMode() { return _blendMode; }
  public void blendMode(ui.BlendMode mode) {
    if (_blendMode == mode) {
      return;
    }
    _blendMode = mode;
    onBlendModeChanged(_blendMode);
  }

  public boolean useAntialias() { return _useAntialias; }
  public void useAntialias(boolean value) {
    if (value != _useAntialias) {
      _useAntialias = value;
      onAntialiasChanged(_useAntialias);
    }
  }

  public abstract void onAntialiasChanged(boolean useAA);
  public abstract void onBlendModeChanged(ui.BlendMode blendMode);

  public abstract void draw(ui.Canvas canvas);

  public abstract List<List<ClipShape>> clipShapes();
  public abstract ActorArtboard artboard();

  public void clip(ui.Canvas canvas) {
    for (final List<ClipShape> clips : clipShapes) {
      for (final ClipShape clipShape : clips) {
        var shape = clipShape.shape;
        if (shape.renderCollapsed) {
          continue;
        }
        if (clipShape.intersect) {
          canvas.clipPath((shape as FlutterActorShape).path);
        } else {
          var artboardRect = Rect.fromLTWH(
              artboard.origin[0] * artboard.width,
              artboard.origin[1] * artboard.height,
              artboard.width,
              artboard.height);

          if (shape.fill != null && shape.fill.fillRule == FillRule.evenOdd) {
            // One single clip path with subtraction rect and all sub paths.
            var clipPath = ui.Path();
            clipPath.addRect(artboardRect);
            for (final path : shape.paths) {
              clipPath.addPath((path as FlutterPath).path, ui.Offset.zero,
                  matrix4: Optional.ofNullable(path.pathTransform).ifPresent(v -> v.mat4));
            }
            clipPath.fillType = PathFillType.evenOdd;
            canvas.clipPath(clipPath);
          } else {
            // One clip path with rect per shape path.
            for (final path : shape.paths) {
              var clipPath = ui.Path();
              clipPath.addRect(artboardRect);
              clipPath.addPath((path as FlutterPath).path, ui.Offset.zero,
                  matrix4: Optional.ofNullable(path.pathTransform).ifPresent(v -> v.mat4));
              clipPath.fillType = PathFillType.evenOdd;
              canvas.clipPath(clipPath);
            }
          }
        }
      }
    }
  }
}
