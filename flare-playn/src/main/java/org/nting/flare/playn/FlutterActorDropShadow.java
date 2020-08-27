package org.nting.flare.playn;

public class FlutterActorDropShadow extends ActorDropShadow {
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
