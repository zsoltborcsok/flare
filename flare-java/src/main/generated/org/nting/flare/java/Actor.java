package org.nting.flare.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.nting.flare.java.BlockTypes.blockTypesMap;

public abstract class Actor {

    public int maxTextureIndex = 0;
    private int _version = 0;
    private List<ActorArtboard> _artboards;

    public Actor() {
    }

    public ActorArtboard artboard() {
        return !_artboards.isEmpty() ? _artboards.get(0) : null;
    }

    public ActorArtboard getArtboard(String name) {
        return name == null ? artboard()
                : _artboards.stream().filter(Objects::nonNull).filter(artboard -> Objects.equals(artboard.name(), name))
                        .findFirst().orElse(null);
    }

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
            _artboards = new ArrayList<>(artboardCount);
            for (final ActorArtboard artboard : actor._artboards) {
                if (artboard == null) {
                    _artboards.add(null);
                    continue;
                }
                ActorArtboard instanceArtboard = artboard.makeInstanceWithActor(this);
                _artboards.add(instanceArtboard);
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

    public abstract boolean loadAtlases(List<byte[]> rawAtlases);

    public boolean load(byte[] data, Object context) {
        if (data.length < 5) {
            throw new IllegalStateException("Not a valid Flare file.");
        }

        boolean success = true;

        int F = data[0] & 0xff;
        int L = data[1] & 0xff;
        int A = data[2] & 0xff;
        int R = data[3] & 0xff;
        int E = data[4] & 0xff;

        Object inputData = data;

        if (F != 70 || L != 76 || A != 65 || R != 82 || E != 69) {
            Object jsonActor = jsonDecode(new String(data));
            inputData = Collections.singletonMap("container", jsonActor);
        }

        StreamReader reader = StreamReader.createStreamReader(inputData);
        _version = reader.readVersion();

        StreamReader block;
        while ((block = reader.readNextBlock(blockTypesMap)) != null) {
            switch (block.blockType()) {
            case BlockTypes.artboards:
                readArtboardsBlock(block);
                break;

            case BlockTypes.atlases:
                List<byte[]> rawAtlases = readAtlasesBlock(block, context);
                success = loadAtlases(rawAtlases);
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

    // Should return either a List or a Map
    protected abstract Object jsonDecode(String json);

    public void readArtboardsBlock(StreamReader block) {
        int artboardCount = block.readUint16Length();
        _artboards = new ArrayList<>(artboardCount);

        for (int artboardIndex = 0; artboardIndex < artboardCount; artboardIndex++) {
            StreamReader artboardBlock = block.readNextBlock(blockTypesMap);
            if (artboardBlock == null) {
                break;
            }
            if (artboardBlock.blockType() == BlockTypes.actorArtboard) {
                ActorArtboard artboard = makeArtboard();
                artboard.read(artboardBlock);
                _artboards.add(artboard);
            }
        }
    }

    public abstract byte[] readOutOfBandAsset(String filename, Object context);

    public List<byte[]> readAtlasesBlock(StreamReader block, Object context) {
        // Determine whether or not the atlas is in or out of band.
        boolean isOOB = block.readBoolean("isOOB");
        block.openArray("data");
        int numAtlases = block.readUint16Length();
        List<byte[]> result;
        if (isOOB) {
            List<byte[]> outOfBandAssets = new ArrayList<>(numAtlases);
            for (int i = 0; i < numAtlases; i++) {
                outOfBandAssets.add(readOutOfBandAsset(block.readString("data"), context));
            }
            result = outOfBandAssets;
        } else {
            // This is sync.
            List<byte[]> inBandAssets = new ArrayList<>(numAtlases);
            for (int i = 0; i < numAtlases; i++) {
                inBandAssets.add(block.readAsset());
            }
            result = inBandAssets;
        }
        block.closeArray();
        return result;
    }
}
