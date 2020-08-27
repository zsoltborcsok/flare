package org.nting.flare.playn;

public class FlutterActorInnerShadow extends ActorInnerShadow {
  @Override
  public int blendModeId() {
    return blendMode.index;
  }

  @Override
  public void blendModeId(int index) {
    blendMode = ui.BlendMode.values[index];
  }

  ui.BlendMode blendMode;
}

public ui.ImageFilter _blurFilter(double x, double y) {
  double bx = x.abs() < 0.1 ? 0 : x;
  double by = y.abs() < 0.1 ? 0 : y;
  return bx == 0 && by == 0
      ? null
      : ui.ImageFilter.blur(sigmaX: bx, sigmaY: by);
}
