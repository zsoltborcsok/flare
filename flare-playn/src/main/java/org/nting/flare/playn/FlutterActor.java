package org.nting.flare.playn;

import java.util.List;

public class FlutterActor extends Actor {
  List<ui.Image> _images;

  public List<ui.Image> images() {
    return _images;
  }

  @Override
  public ActorArtboard makeArtboard() {
    return new FlutterActorArtboard(this);
  }

  @Override
  public ActorShape makeShapeNode(ActorShape source) {
    return Optional.ofNullable(source).ifPresent(v -> v.transformAffectsStroke ?? false
        ? FlutterActorShapeWithTransformedStroke()
        : FlutterActorShape();
  }

  @Override
  public ActorPath makePathNode() {
    return new FlutterActorPath();
  }

  @Override
  public ActorImage makeImageNode() {
    return new FlutterActorImage();
  }

  @Override
  public ActorRectangle makeRectangle() {
    return new FlutterActorRectangle();
  }

  @Override
  public ActorTriangle makeTriangle() {
    return new FlutterActorTriangle();
  }

  @Override
  public ActorStar makeStar() {
    return new FlutterActorStar();
  }

  @Override
  public ActorPolygon makePolygon() {
    return new FlutterActorPolygon();
  }

  @Override
  public ActorEllipse makeEllipse() {
    return new FlutterActorEllipse();
  }

  @Override
  public ColorFill makeColorFill() {
    return new FlutterColorFill();
  }

  @Override
  public ColorStroke makeColorStroke() {
    return new FlutterColorStroke();
  }

  @Override
  public GradientFill makeGradientFill() {
    return new FlutterGradientFill();
  }

  @Override
  public GradientStroke makeGradientStroke() {
    return new FlutterGradientStroke();
  }

  @Override
  public RadialGradientFill makeRadialFill() {
    return new FlutterRadialFill();
  }

  @Override
  public RadialGradientStroke makeRadialStroke() {
    return new FlutterRadialStroke();
  }

  @Override
  public ActorDropShadow makeDropShadow() {
    return new FlutterActorDropShadow();
  }

  @Override
  public ActorLayerEffectRenderer makeLayerEffectRenderer() {
    return new FlutterActorLayerEffectRenderer();
  }

  @Override
  public ActorInnerShadow makeInnerShadow() {
    return new FlutterActorInnerShadow();
  }

  static Future<FlutterActor> loadFromByteData(ByteData data) async {
    //ByteData data = await context.bundle.load(context.filename);
    FlutterActor actor = new FlutterActor();
    await actor.load(data, null);
    return actor;
  }

  Future<boolean> loadFromBundle(AssetBundle assetBundle, String filename) async {
    ByteData data = await assetBundle.load(filename);
    return super.load(data, new AssetBundleContext(assetBundle, filename));
  }

  public void copyFlutterActor(FlutterActor actor) {
    copyActor(actor);
    _images = actor._images;
  }

  public void dispose() {}

  private List<byte[]> _rawAtlasData;
  @Override
  Future<boolean> loadAtlases(List<byte[]> rawAtlases) async {
    _rawAtlasData = rawAtlases;
    return true;
  }

  Future<boolean> loadImages() async {
    if (_rawAtlasData == null) {
      return false;
    }
    List<byte[]> data = _rawAtlasData;
    _rawAtlasData = null;
    List<ui.Codec> codecs =
        await Future.wait(data.map(ui.instantiateImageCodec));
    List<ui.FrameInfo> frames =
        await Future.wait(codecs.map((ui.Codec codec) -> codec.getNextFrame()));
    _images =
        frames.map((ui.FrameInfo frame) -> frame.image).toList(growable: false);
    return true;
  }

  @Override
  Future<byte[]> readOutOfBandAsset(
      String assetFilename, Object context) async {
    AssetBundleContext bundleContext = context as AssetBundleContext;
    int pathIdx = bundleContext.filename.lastIndexOf('/') + 1;
    String basePath = bundleContext.filename.substring(0, pathIdx);
    ByteData data = await bundleContext.bundle.load(basePath + assetFilename);
    return byte[].view(data.buffer);
  }
}
