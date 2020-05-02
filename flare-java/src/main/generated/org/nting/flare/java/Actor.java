package org.nting.flare.java;

import java.util.ArrayList;
import java.util.List;

public abstract class Actor {
  public int maxTextureIndex = 0;
  private int _version = 0;
  private List<ActorArtboard> _artboards;

  Actor();

  public ActorArtboard artboard() { return !_artboards.isEmpty() ? _artboards.get(0) : null; }

  ActorArtboard getArtboard(String name) =>
      name == null
          ? artboard
          : _artboards.firstWhere((artboard) => artboard?.name == name,
          orElse: () => null);

  public int version() {
    return _version;
  }

  public int texturesUsed() {
    return maxTextureIndex + 1;
  }

  public void copyActor(Actor actor) {
    maxTextureIndex = actor.maxTextureIndex;
    int artboardCount = actor._artboards.size();
    if (artboardCount > 0) {
      int idx = 0;
      _artboards = new ArrayList<ActorArtboard>(artboardCount);
      for (final ActorArtboard artboard : actor._artboards) {
        if (artboard == null) {
          _artboards[idx++] = null;
          continue;
        }
        ActorArtboard instanceArtboard = artboard.makeInstanceWithActor(this);
        _artboards[idx++] = instanceArtboard;
      }
    }
  }

  public ActorArtboard makeArtboard() {
    return new ActorArtboard(this);
  }

  public ActorImage makeImageNode() {
    return new ActorImage();
  }

  public ActorPath makePathNode() {
    return new ActorPath();
  }

  public ActorShape makeShapeNode(ActorShape source) {
    return new ActorShape();
  }

  public ActorRectangle makeRectangle() {
    return new ActorRectangle();
  }

  public ActorTriangle makeTriangle() {
    return new ActorTriangle();
  }

  public ActorStar makeStar() {
    return new ActorStar();
  }

  public ActorPolygon makePolygon() {
    return new ActorPolygon();
  }

  public ActorEllipse makeEllipse() {
    return new ActorEllipse();
  }

  public abstract ColorFill makeColorFill();

  public abstract ColorStroke makeColorStroke();

  public abstract GradientFill makeGradientFill();

  public abstract GradientStroke makeGradientStroke();

  public abstract RadialGradientFill makeRadialFill();

  public abstract RadialGradientStroke makeRadialStroke();

  public abstract ActorDropShadow makeDropShadow();

  public abstract ActorInnerShadow makeInnerShadow();

  public abstract ActorLayerEffectRenderer makeLayerEffectRenderer();

  Future<boolean> loadAtlases(List<byte[]> rawAtlases);

  Future<boolean> load(ByteData data, Object context) async {
    if (data.lengthInBytes < 5) {
      throw new UnsupportedError("Not a valid Flare file.");
    }

    boolean success = true;

    int F = data.getUint8(0);
    int L = data.getUint8(1);
    int A = data.getUint8(2);
    int R = data.getUint8(3);
    int E = data.getUint8(4);

    Object inputData = data;

    if (F != 70 || L != 76 || A != 65 || R != 82 || E != 69) {
      byte[] charCodes = data.buffer.asbyte[]();
      String stringData = String.fromCharCodes(charCodes);
      Object jsonActor = jsonDecode(stringData);
      Map jsonObject = <Object, Object>{};
      jsonObject["container"] = jsonActor;
      inputData = jsonObject;
    }

    StreamReader reader = new StreamReader(inputData);
    _version = reader.readVersion();

    StreamReader block;
    while ((block = reader.readNextBlock(blockTypesMap)) != null) {
      switch (block.blockType) {
        case BlockTypes.artboards:
          readArtboardsBlock(block);
          break;

        case BlockTypes.atlases:
          List<byte[]> rawAtlases = await readAtlasesBlock(block, context);
          success = await loadAtlases(rawAtlases);
          break;
      }
    }

    // Resolve now.
    for (final ActorArtboard artboard : _artboards) {
      artboard.resolveHierarchy();
    }
    for (final ActorArtboard artboard : _artboards) {
      artboard.completeResolveHierarchy();
    }

    for (final ActorArtboard artboard : _artboards) {
      artboard.sortDependencies();
    }

    return success;
  }

  public void readArtboardsBlock(StreamReader block) {
    int artboardCount = block.readUint16Length();
    _artboards = new ArrayList<ActorArtboard>(artboardCount);

    for (int artboardIndex = 0, end = _artboards.size();
    artboardIndex < end;
    artboardIndex++) {
      StreamReader artboardBlock = block.readNextBlock(blockTypesMap);
      if (artboardBlock == null) {
        break;
      }
      switch (artboardBlock.blockType) {
        case BlockTypes.actorArtboard:
          {
            ActorArtboard artboard = makeArtboard();
            artboard.read(artboardBlock);
            _artboards[artboardIndex] = artboard;
            break;
          }
      }
    }
  }

  Future<byte[]> readOutOfBandAsset(String filename, Object context);

  Future<List<byte[]>> readAtlasesBlock(StreamReader block,
      Object context) {
    // Determine whether or not the atlas is in or out of band.
    boolean isOOB = block.readBoolean("isOOB");
    block.openArray("data");
    int numAtlases = block.readUint16Length();
    Future<List<byte[]>> result;
    if (isOOB) {
      List<Future<byte[]>> waitingFor = new ArrayList<Future<byte[]>>(numAtlases);
      for (int i = 0; i < numAtlases; i++) {
        waitingFor[i] = readOutOfBandAsset(block.readString("data"), context);
      }
      result = Future.wait(waitingFor);
    } else {
      // This is sync.
      List<byte[]> inBandAssets = new ArrayList<byte[]>(numAtlases);
      for (int i = 0; i < numAtlases; i++) {
        inBandAssets[i] = block.readAsset();
      }
      Completer<List<byte[]>> completer = Completer<List<byte[]>>();
      completer.complete(inBandAssets);
      result = completer.future;
    }
    block.closeArray();
    return result;
  }
}
