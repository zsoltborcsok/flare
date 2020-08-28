package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.RadialGradientStroke;

public class FlutterRadialStroke extends RadialGradientStroke implements FlutterStroke {

    @Override
    public void update(int dirt) {
        super.update(dirt);
        // double radius = Vec2D.distance(renderStart, renderEnd);
        // List<ui.Color> colors = new ArrayList<ui.Color>();
        // List<double> stops = new ArrayList<double>();
        // int numStops = (colorStops.size() / 5).round();
        //
        // int idx = 0;
        // for (int i = 0; i < numStops; i++) {
        // double o = colorStops[idx + 3].clamp(0.0, 1.0).toDouble();
        // ui.Color color = ui.Color.fromRGBO(
        // (colorStops[idx] * 255.0).round(),
        // (colorStops[idx + 1] * 255.0).round(),
        // (colorStops[idx + 2] * 255.0).round(),
        // o);
        // colors.add(color);
        // stops.add(colorStops[idx + 4]);
        // idx += 5;
        // }
        //
        // Color paintColor;
        // if (artboard.overrideColor == null) {
        // paintColor = Colors.white.withOpacity(
        // (artboard.modulateOpacity * opacity * shape.renderOpacity)
        // .clamp(0.0, 1.0)
        // .toDouble());
        // } else {
        // float[] overrideColor = artboard.overrideColor;
        // double o = (overrideColor[3] *
        // artboard.modulateOpacity *
        // opacity *
        // shape.renderOpacity)
        // .clamp(0.0, 1.0)
        // .toDouble();
        // paintColor = ui.Color.fromRGBO(
        // (overrideColor[0] * 255.0).round(),
        // (overrideColor[1] * 255.0).round(),
        // (overrideColor[2] * 255.0).round(),
        // o);
        // }
        //
        // var parentShape = parent as FlutterActorShape;
        // _paint
        // ..color = paintColor
        // ..strokeWidth = width
        // ..isAntiAlias = parentShape.useAntialias
        // ..blendMode = parentShape.blendMode
        // ..shader = ui.Gradient.radial(Offset(renderStart[0], renderStart[1]),
        // radius, colors, stops, ui.TileMode.clamp);
        // onPaintUpdated(_paint);
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        FlutterRadialStroke instanceNode = new FlutterRadialStroke();
        // instanceNode.copyRadialStroke(this, resetArtboard);
        return instanceNode;
    }
}
