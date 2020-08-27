package org.nting.flare.playn;

public class FlutterActorArtboard extends ActorArtboard {
  private boolean _useAntialias;
  FlutterActorArtboard(FlutterActor actor) : super(actor);

  public boolean useAntialias() { return _useAntialias; }
  public void useAntialias(boolean value) {
    if (_useAntialias != value) {
      _useAntialias = value;
      if (drawableNodes != null) {
        for (final ActorDrawable drawable : drawableNodes) {
          (drawable as FlutterActorDrawable).useAntialias = _useAntialias;
        }
      }
    }
  }

  public void draw(ui.Canvas canvas) {
    if (clipContents) {
      canvas.save();
      AABB aabb = artboardAABB();
      canvas.clipRect(Rect.fromLTRB(aabb[0], aabb[1], aabb[2], aabb[3]));
    }
    if (drawableNodes != null) {
      for (final ActorDrawable drawable : drawableNodes) {
        if (drawable instanceof FlutterActorDrawable) {
          (drawable as FlutterActorDrawable).draw(canvas);
        }
      }
    }
    if (clipContents) {
      canvas.restore();
    }
  }

  public void dispose() {}
}
