package org.nting.flare.java;

public abstract class RadialGradientColor extends GradientColor {
  public double secondaryRadiusScale = 1.0;

  void copyRadialGradient(RadialGradientColor node,
      ActorArtboard resetArtboard) {
    copyGradient(node, resetArtboard);
    secondaryRadiusScale = node.secondaryRadiusScale;
  }

  static RadialGradientColor read(ActorArtboard artboard, StreamReader reader,
      RadialGradientColor component) {
    GradientColor.read(artboard, reader, component);

    component.secondaryRadiusScale = reader.readFloat32("secondaryRadiusScale");

    return component;
  }
}
