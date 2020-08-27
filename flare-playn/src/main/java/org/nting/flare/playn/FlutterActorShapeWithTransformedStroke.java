package org.nting.flare.playn;

public class FlutterActorShapeWithTransformedStroke extends FlutterActorShape {
  ui.Path _localPath;
  private boolean _isLocalValid = false;

  @Override
  public void initializeGraphics() {
    super.initializeGraphics();
    _localPath = ui.Path();
  }

  @Override
  public void invalidateShape() {
    _isLocalValid = false;
    super.invalidateShape();
  }

  public ui.Path localPath() {
    if (_isLocalValid) {
      return _localPath;
    }
    _isLocalValid = true;
    _localPath.reset();

    Mat2D inverseWorld = new Mat2D();
    if (!Mat2D.invert(inverseWorld, worldTransform)) {
      Mat2D.identity(inverseWorld);
    }

    for (final ActorBasePath path : paths) {
      Mat2D transform = path.pathTransform;

      Mat2D localTransform;
      if (transform != null) {
        localTransform = new Mat2D();
        Mat2D.multiply(localTransform, inverseWorld, transform);
      }
      _localPath.addPath((path as FlutterPath).path, ui.Offset.zero,
          matrix4: Optional.ofNullable(localTransform).ifPresent(v -> v.mat4));
    }
    return _localPath;
  }

  @Override
  public ui.Path getRenderPath(ui.Canvas canvas) {
    canvas.transform(worldTransform.mat4);
    return localPath;
  }
}
