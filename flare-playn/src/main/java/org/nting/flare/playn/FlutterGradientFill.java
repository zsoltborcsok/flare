package org.nting.flare.playn;

import java.util.ArrayList;
import java.util.List;

public class FlutterGradientFill extends GradientFill with FlutterFill {
  @Override
  public void update(int dirt) {
    super.update(dirt);
    List<ui.Color> colors = new ArrayList<ui.Color>();
    List<double> stops = new ArrayList<double>();
    int numStops = (colorStops.size() / 5).round();

    int idx = 0;
    for (int i = 0; i < numStops; i++) {
      double o = colorStops[idx + 3].clamp(0.0, 1.0).toDouble();
      ui.Color color = ui.Color.fromRGBO(
          (colorStops[idx] * 255.0).round(),
          (colorStops[idx + 1] * 255.0).round(),
          (colorStops[idx + 2] * 255.0).round(),
          o);
      colors.add(color);
      stops.add(colorStops[idx + 4]);
      idx += 5;
    }

    Color paintColor;
    if (artboard.overrideColor == null) {
      paintColor = Colors.white.withOpacity(
          (artboard.modulateOpacity * opacity * shape.renderOpacity)
              .clamp(0.0, 1.0)
              .toDouble());
    } else {
      float[] overrideColor = artboard.overrideColor;
      double o = (overrideColor[3] *
              artboard.modulateOpacity *
              opacity *
              shape.renderOpacity)
          .clamp(0.0, 1.0)
          .toDouble();
      paintColor = ui.Color.fromRGBO(
          (overrideColor[0] * 255.0).round(),
          (overrideColor[1] * 255.0).round(),
          (overrideColor[2] * 255.0).round(),
          o);
    }

    var parentShape = parent as FlutterActorShape;
    _paint
      ..color = paintColor
      ..isAntiAlias = parentShape.useAntialias
      ..blendMode = parentShape.blendMode
      ..shader = ui.Gradient.linear(ui.Offset(renderStart[0], renderStart[1]),
          ui.Offset(renderEnd[0], renderEnd[1]), colors, stops);
    onPaintUpdated(_paint);
  }

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    FlutterGradientFill instanceNode = new FlutterGradientFill();
    instanceNode.copyGradientFill(this, resetArtboard);
    return instanceNode;
  }
}
