package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.ColorStroke;

public class FlutterColorStroke extends ColorStroke implements FlutterStroke {

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        FlutterColorStroke instanceNode = new FlutterColorStroke();
        instanceNode.copyColorStroke(this, resetArtboard);
        return instanceNode;
    }

    // public Color uiColor() {
    // float[] c = displayColor;
    // double o = (artboard.modulateOpacity * opacity * shape.renderOpacity)
    // .clamp(0.0, 1.0)
    // .toDouble();
    // return Color.fromRGBO((c[0] * 255.0).round(), (c[1] * 255.0).round(),
    // (c[2] * 255.0).round(), c[3] * o);
    // }
    //
    // public void uiColor(Color c) {
    // color = Float32List.fromList(
    // [c.red / 255, c.green / 255, c.blue / 255, c.opacity]);
    // }

    @Override
    public void update(int dirt) {
        super.update(dirt);

        // var parentShape = parent as FlutterActorShape;
        // _paint
        // ..color = uiColor
        // ..strokeWidth = width
        // ..isAntiAlias = parentShape.useAntialias
        // ..blendMode = parentShape.blendMode;
        // onPaintUpdated(_paint);
    }

    @Override
    public void initializeGraphics() {
        // See FlutterStroke
    }
}
