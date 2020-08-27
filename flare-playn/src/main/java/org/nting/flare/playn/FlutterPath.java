package org.nting.flare.playn;

/// Abstract base path that can be invalidated and somehow
/// regenerates, no concrete logic
public abstract class FlutterPath {
  public abstract ui.Path path();
  public abstract void initializeGraphics();
}
