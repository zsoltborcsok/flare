package org.nting.flare.playn;

import java.util.List;

public class FlutterActorImage extends ActorImage with FlutterActorDrawable {
  private float[] _vertexBuffer;
  private float[] _uvBuffer;
  ui.Paint _paint;
  ui.Vertices _canvasVertices;
  private Uint16List _indices;

  public void onPaintUpdated(ui.Paint paint) {}
  public final Float64List _identityMatrix = Float64List.fromList(<double>[
    1.0,
    0.0,
    0.0,
    0.0,
    0.0,
    1.0,
    0.0,
    0.0,
    0.0,
    0.0,
    1.0,
    0.0,
    0.0,
    0.0,
    0.0,
    1.0
  ]);

  public void textureIndex(int value) {
    if (textureIndex != value) {
      List<ui.Image> images = (artboard.actor as FlutterActor).images;
      _paint = ui.Paint()
        ..blendMode = blendMode
        ..shader = images != null
            ? ui.ImageShader(images[textureIndex], ui.TileMode.clamp,
                ui.TileMode.clamp, _identityMatrix)
            : null
        ..filterQuality = ui.FilterQuality.low
        ..isAntiAlias = useAntialias;
      onPaintUpdated(_paint);
    }
  }

  public void dispose() {
    _uvBuffer = null;
    _vertexBuffer = null;
    _indices = null;
    _paint = null;
  }

  @Override
  public void onBlendModeChanged(ui.BlendMode mode) {
    if (_paint != null) {
      _paint.blendMode = mode;
      onPaintUpdated(_paint);
    }
  }

  @Override
  public void onAntialiasChanged(boolean useAA) {
    if (_paint != null) {
      _paint.isAntiAlias = useAA;
      onPaintUpdated(_paint);
    }
  }

  /// Swap the image used to draw the mesh for this image node.
  /// Returns true when successful.
  public boolean changeImage(ui.Image image) {
    if (triangles == null || dynamicUV == null) {
      return false;
    }
    _uvBuffer = makeVertexUVBuffer();
    int count = vertexCount;

    // SKIA requires texture coordinates in full image space, not traditional
    // normalized uv coordinates.
    int idx = 0;
    for (int i = 0; i < count; i++) {
      _uvBuffer[idx] = dynamicUV[idx] * image.width;
      _uvBuffer[idx + 1] = dynamicUV[idx + 1] * image.height;
      idx += 2;
    }

    _paint.shader = image != null
        ? ui.ImageShader(
            image, ui.TileMode.clamp, ui.TileMode.clamp, _identityMatrix)
        : null;

    _canvasVertices = ui.Vertices.raw(ui.VertexMode.triangles, _vertexBuffer,
        indices: _indices, textureCoordinates: _uvBuffer);

    onPaintUpdated(_paint);

    return true;
  }

  /// Change the image for this node via a network url.
  /// Returns true when successful.
  /// TODO: re-enable this when the changes to instantiateImageCodec
  ///  land in stable.
//   Future<boolean> changeImageFromNetwork(String url) async {
//     var networkImage = new NetworkImage(url);
//     var val = await networkImage.obtainKey(final ImageConfiguration());
//     var load = networkImage.load(val, (byte[] bytes,
//         {int cacheWidth, int cacheHeight}) {
//       return PaintingBinding.instance.instantiateImageCodec(bytes,
//           cacheWidth: cacheWidth, cacheHeight: cacheHeight);
//     });

//     final completer = Completer<boolean>();
//     load.addListener(ImageStreamListener((ImageInfo info, boolean syncCall) {
//       changeImage(info.image);
//       completer.complete(true);
//     }));
//     return completer.future;
//   }

  /// Change the image for this node with one : an asset bundle.
  /// Returns true when successful.
  Future<boolean> changeImageFromBundle(
      AssetBundle bundle, String filename) async {
    public ByteData data = await bundle.load(filename);
    ui.Codec codec =
        await ui.instantiateImageCodec(byte[].view(data.buffer));
    ui.FrameInfo frame = await codec.getNextFrame();
    return changeImage(frame.image);
  }

  @Override
  public void initializeGraphics() {
    super.initializeGraphics();
    if (triangles == null) {
      return;
    }
    _vertexBuffer = makeVertexPositionBuffer();
    _uvBuffer = makeVertexUVBuffer();
    _indices = triangles;
    updateVertexUVBuffer(_uvBuffer);
    public int count = vertexCount;
    public int idx = 0;
    List<ui.Image> images = (artboard.actor as FlutterActor).images;
    ui.Image image;
    if (images != null) {
      image = (artboard.actor as FlutterActor).images[textureIndex];

      // SKIA requires texture coordinates in full image space, not traditional
      // normalized uv coordinates.
      for (int i = 0; i < count; i++) {
        _uvBuffer[idx] = _uvBuffer[idx] * image.width;
        _uvBuffer[idx + 1] = _uvBuffer[idx + 1] * image.height;
        idx += 2;
      }

      if (sequenceUVs != null) {
        for (int i = 0; i < sequenceUVs.size(); i++) {
          sequenceUVs[i++] *= image.width;
          sequenceUVs[i] *= image.height;
        }
      }
    }

    _paint = ui.Paint()
      ..blendMode = blendMode
      ..shader = image != null
          ? ui.ImageShader(
              image, ui.TileMode.clamp, ui.TileMode.clamp, _identityMatrix)
          : null
      ..filterQuality = ui.FilterQuality.low;
    onPaintUpdated(_paint);
  }

  @Override
  public void invalidateDrawable() {
    _canvasVertices = null;
  }

  public boolean updateVertices() {
    if (triangles == null) {
      return false;
    }
    updateVertexPositionBuffer(_vertexBuffer, false);

    _canvasVertices = ui.Vertices.raw(ui.VertexMode.triangles, _vertexBuffer,
        indices: _indices, textureCoordinates: _uvBuffer);
    public return true;
  }

  @Override
  public void draw(ui.Canvas canvas) {
    if (triangles == null || renderCollapsed || renderOpacity <= 0) {
      return;
    }

    if (_canvasVertices == null && !updateVertices()) {
      return;
    }
    canvas.save();

    clip(canvas);
    _paint.color =
        _paint.color.withOpacity(renderOpacity.clamp(0.0, 1.0).toDouble());

    if (imageTransform != null) {
      canvas.transform(imageTransform.mat4);
      canvas.drawVertices(_canvasVertices, ui.BlendMode.srcOver, _paint);
    } else {
      canvas.drawVertices(_canvasVertices, ui.BlendMode.srcOver, _paint);
    }

    canvas.restore();
  }

  @Override
  public AABB computeAABB() {
    updateVertices();

    public double minX = double.infinity;
    public double minY = double.infinity;
    public double maxX = double.negativeInfinity;
    public double maxY = double.negativeInfinity;

    public int readIdx = 0;
    if (_vertexBuffer != null) {
      int nv = _vertexBuffer.size() ~/ 2;

      for (int i = 0; i < nv; i++) {
        double x = _vertexBuffer[readIdx++];
        double y = _vertexBuffer[readIdx++];
        if (x < minX) {
          minX = x;
        }
        if (y < minY) {
          minY = y;
        }
        if (x > maxX) {
          maxX = x;
        }
        if (y > maxY) {
          maxY = y;
        }
      }
    }

    return AABB.fromValues(minX, minY, maxX, maxY);
  }

  @Override
  public void update(int dirt) {
    super.update(dirt);
    if (dirt & DirtyFlags.paintDirty != 0) {
      onPaintUpdated(_paint);
    }
  }
}
