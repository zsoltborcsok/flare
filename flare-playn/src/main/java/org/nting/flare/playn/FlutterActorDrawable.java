package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorBasePath;
import org.nting.flare.java.ActorDrawable.ClipShape;
import org.nting.flare.java.ActorShape;
import org.nting.flare.java.FillRule;
import playn.core.Canvas;
import playn.core.Path;
import pythagoras.f.Rectangle;

import java.util.List;

public interface FlutterActorDrawable {

    default BlendMode blendMode() {
        return BlendMode.values()[blendModeId()];
    }

    int blendModeId();

    void draw(Canvas canvas);

    List<List<ClipShape>> clipShapes();

    ActorArtboard artboard();

    default void clip(Canvas canvas) {
        for (final List<ClipShape> clips : clipShapes()) {
            for (final ClipShape clipShape : clips) {
                ActorShape shape = clipShape.shape;
                if (shape.renderCollapsed()) {
                    continue;
                }
                if (clipShape.intersect) {
                    Path clipPath = canvas.createPath();
                    clipPath.append(((FlutterActorShape) shape).path(), false);
                    canvas.clip(clipPath);
                } else {
                    Rectangle artboardRect = new Rectangle(artboard().origin().values()[0] * artboard().width(),
                            artboard().origin().values()[1] * artboard().height(), artboard().width(),
                            artboard().height());

                    if (shape.fill() != null && shape.fill().fillRule() == FillRule.evenOdd) {
                        // One single clip path with subtraction rect and all sub paths.
                        Path clipPath = canvas.createPath();
                        clipPath.append(artboardRect, false);
                        for (ActorBasePath path : shape.paths()) {
                            clipPath.append(((FlutterPath) path).pathWithTransform(path.pathTransform()), false);
                        }
                        // clipPath.fillType = PathFillType.evenOdd;
                        canvas.clip(clipPath);
                    } else {
                        // One clip path with rect per shape path.
                        for (ActorBasePath path : shape.paths()) {
                            Path clipPath = canvas.createPath();
                            clipPath.append(artboardRect, false);
                            clipPath.append(((FlutterPath) path).pathWithTransform(path.pathTransform()), false);
                            // clipPath.fillType = PathFillType.evenOdd;
                            canvas.clip(clipPath);
                        }
                    }
                }
            }
        }
    }
}
