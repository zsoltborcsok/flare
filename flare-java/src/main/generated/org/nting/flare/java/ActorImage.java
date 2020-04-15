package org.nting.flare.java;

import org.nting.flare.java.maths.AABB;
import org.nting.flare.java.maths.Mat2D;

import java.util.ArrayList;
import java.util.List;

public class ActorImage extends ActorDrawable with ActorSkinnable {

  public static class SequenceFrame {
    final int _atlasIndex;
    final int _offset;

    SequenceFrame(this._atlasIndex, this._offset);

    @Override
    public String toString() {
      return "(" + _atlasIndex.toString() + ", " + _offset.toString() + ")";
    }

    public int atlasIndex() { return _atlasIndex; }

    public int offset() { return _offset; }
  }

  @Override
  int drawOrder;

  int _textureIndex = -1;
  Float32List _vertices;
  Float32List _dynamicUV;

  public Float32List dynamicUV() { return _dynamicUV; }
  Uint16List _triangles;
  int _vertexCount = 0;
  int _triangleCount = 0;
  Float32List _animationDeformedVertices;

  List<SequenceFrame> _sequenceFrames;
  Float32List _sequenceUVs;
  int _sequenceFrame = 0;

  public int sequenceFrame() { return _sequenceFrame; }

  public Float32List sequenceUVs() { return _sequenceUVs; }

  public List<SequenceFrame> sequenceFrames() { return _sequenceFrames; }

  set sequenceFrame(int value) {
    _sequenceFrame = value;
  }

  public int textureIndex() {
    return _textureIndex;
  }

  public int vertexCount() {
    return _vertexCount;
  }

  public int triangleCount() {
    return _triangleCount;
  }

  public Uint16List triangles() {
    return _triangles;
  }

  public Float32List vertices() {
    return _vertices;
  }

  public int vertexPositionOffset() {
    return 0;
  }

  public int vertexUVOffset() {
    return 2;
  }

  public int vertexBoneIndexOffset() {
    return 4;
  }

  public int vertexBoneWeightOffset() {
    return 8;
  }

  public int vertexStride() {
    return isConnectedToBones ? 12 : 4;
  }

  public boolean doesAnimationVertexDeform() {
    return _animationDeformedVertices != null;
  }

  set doesAnimationVertexDeform(boolean value) {
    if (value) {
      if (_animationDeformedVertices == null ||
          _animationDeformedVertices.size() != _vertexCount * 2) {
        _animationDeformedVertices = new Float32List(vertexCount * 2);
        // Copy the deform verts from the rig verts.
        int writeIdx = 0;
        int readIdx = 0;
        int readStride = vertexStride;
        for (int i = 0; i < _vertexCount; i++) {
          _animationDeformedVertices[writeIdx++] = _vertices[readIdx];
          _animationDeformedVertices[writeIdx++] = _vertices[readIdx + 1];
          readIdx += readStride;
        }
      }
    } else {
      _animationDeformedVertices = null;
    }
  }

  public Float32List animationDeformedVertices() {
    return _animationDeformedVertices;
  }

  ActorImage();

  public void disposeGeometry() {
    // Delete vertices only if we do not vertex deform at runtime.
    if (_animationDeformedVertices == null) {
      _vertices = null;
    }
    _triangles = null;
  }

  static ActorImage read(ActorArtboard artboard, StreamReader reader,
      ActorImage node) {
    node ??= new ActorImage();

    ActorDrawable.read(artboard, reader, node);
    ActorSkinnable.read(artboard, reader, node);

    if (!node.isHidden) {
      node._textureIndex = reader.readUint8("atlas");

      int numVertices = reader.readUint32("numVertices");

      node._vertexCount = numVertices;
      node._vertices =
          reader.readFloat32Array(numVertices * node.vertexStride, "vertices");

      // In version 24 we started packing the original UV coordinates if the
      // image was marked for dynamic runtime swapping.
      if (artboard.actor.version >= 24) {
        boolean isDynamic = reader.readBoolean("isDynamic");
        if (isDynamic) {
          node._dynamicUV = reader.readFloat32Array(numVertices * 2, "uv");
        }
      }

      int numTris = reader.readUint32("numTriangles");
      node._triangles = new Uint16List(numTris * 3);
      node._triangleCount = numTris;
      node._triangles = reader.readUint16Array(numTris * 3, "triangles");
    }
    return node;
  }

// TODO: fix sequences for flare.
//   static ActorImage readSequence(
//       ActorArtboard artboard, StreamReader reader, ActorImage node) {
//     ActorImage.read(artboard, reader, node);

//     if (node._textureIndex != -1) {
//       reader.openArray("frames");
//       int frameAssetCount = reader.readUint16Length();
//       // node._sequenceFrames = new ArrayList<>();
//       Float32List uvs = new Float32List(node._vertexCount * 2 * frameAssetCount);
//       int uvStride = node._vertexCount * 2;
//       node._sequenceUVs = uvs;
//       SequenceFrame firstFrame = new SequenceFrame(node._textureIndex, 0);
//       node._sequenceFrames = new ArrayList<SequenceFrame>();
//       node._sequenceFrames.add(firstFrame);
//       int readIdx = 2;
//       int writeIdx = 0;
//       int vertexStride = 4;
//       if (node._boneConnections != null && node._boneConnections.size() > 0) {
//         vertexStride = 12;
//       }
//       for (int i = 0; i < node._vertexCount; i++) {
//         uvs[writeIdx++] = node._vertices[readIdx];
//         uvs[writeIdx++] = node._vertices[readIdx + 1];
//         readIdx += vertexStride;
//       }

//       int offset = uvStride;
//       for (int i = 1; i < frameAssetCount; i++) {
//         reader.openObject("frame");

//         SequenceFrame frame =
//             SequenceFrame(reader.readUint8("atlas"), offset * 4);
//         node._sequenceFrames.add(frame);
//         reader.readFloat32ArrayOffset(uvs, uvStride, offset, "uv");
//         offset += uvStride;

//         reader.closeObject();
//       }

//       reader.closeArray();
//     }

//     return node;
//   }

  @Override
  public void resolveComponentIndices(List<ActorComponent> components) {
    super.resolveComponentIndices(components);
    resolveSkinnable(components);
  }

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorImage instanceNode = resetArtboard.actor.makeImageNode();
    instanceNode.copyImage(this, resetArtboard);
    return instanceNode;
  }

  public void copyImage(ActorImage node, ActorArtboard resetArtboard) {
    copyDrawable(node, resetArtboard);
    copySkinnable(node, resetArtboard);

    _textureIndex = node._textureIndex;
    _vertexCount = node._vertexCount;
    _triangleCount = node._triangleCount;
    _vertices = node._vertices;
    _triangles = node._triangles;
    _dynamicUV = node._dynamicUV;
    if (node._animationDeformedVertices != null) {
      _animationDeformedVertices =
          Float32List.fromList(node._animationDeformedVertices);
    }
  }

//   void transformBind(Mat2D xform) {
//     if (_boneConnections != null) {
//       for (BoneConnection bc : _boneConnections) {
//         Mat2D.multiply(bc.bind, xform, bc.bind);
//         Mat2D.invert(bc.inverseBind, bc.bind);
//       }
//     }
//   }

  public Float32List makeVertexPositionBuffer() {
    return new Float32List(_vertexCount * 2);
  }

  public Float32List makeVertexUVBuffer() {
    return new Float32List(_vertexCount * 2);
  }

  public void transformDeformVertices(Mat2D wt) {
    if (_animationDeformedVertices == null) {
      return;
    }

    Float32List fv = _animationDeformedVertices;

    int vidx = 0;
    for (int j = 0; j < _vertexCount; j++) {
      double x = fv[vidx];
      double y = fv[vidx + 1];

      fv[vidx] = wt[0] * x + wt[2] * y + wt[4];
      fv[vidx + 1] = wt[1] * x + wt[3] * y + wt[5];

      vidx += 2;
    }
  }

  public void updateVertexUVBuffer(Float32List buffer) {
    int readIdx = vertexUVOffset;
    int writeIdx = 0;
    int stride = vertexStride;

    Float32List v = _vertices;
    for (int i = 0; i < _vertexCount; i++) {
      buffer[writeIdx++] = v[readIdx];
      buffer[writeIdx++] = v[readIdx + 1];
      readIdx += stride;
    }
  }

  void updateVertexPositionBuffer(Float32List buffer,
      boolean isSkinnedDeformInWorld) {
    Mat2D worldTransform = this.worldTransform;
    int readIdx = 0;
    int writeIdx = 0;

    Float32List v = _animationDeformedVertices != null
        ? _animationDeformedVertices
        : _vertices;
    int stride = _animationDeformedVertices != null ? 2 : vertexStride;

    if (skin != null) {
      Float32List boneTransforms = skin.boneMatrices;

      //Mat2D inverseWorldTransform = Mat2D.Invert(new Mat2D(), worldTransform);
      Float32List influenceMatrix =
      Float32List.fromList([0.0, 0.0, 0.0, 0.0, 0.0, 0.0]);

      // if(this.name == "evolution_1_0001s_0003_evolution_1_weapo")
      // {
      // //	print("TEST!");
      // 	int boneIndexOffset = vertexBoneIndexOffset;
      // 	int weightOffset = vertexBoneWeightOffset;
      // 	for(int i = 0; i < _vertexCount; i++)
      // 	{
      // 		for(int wi = 0; wi < 4; wi++)
      // 		{
      // 			int boneIndex = _vertices[boneIndexOffset+wi].toInt();
      // 			double weight = _vertices[weightOffset+wi];
      // 			if(boneIndex == 1)
      // 			{
      // 				_vertices[weightOffset+wi] = 1.0;
      // 			}
      // 			else if(boneIndex == 2)
      // 			{
      // 				_vertices[weightOffset+wi] = 0.0;
      // 			}
      // 			//print("BI $boneIndex $weight");
      // 		}
      // 		boneIndexOffset += vertexStride;
      // 		weightOffset += vertexStride;
      // 	}
      // }
      int boneIndexOffset = vertexBoneIndexOffset;
      int weightOffset = vertexBoneWeightOffset;
      for (int i = 0; i < _vertexCount; i++) {
        double x = v[readIdx];
        double y = v[readIdx + 1];

        double px, py;

        if (_animationDeformedVertices != null && isSkinnedDeformInWorld) {
          px = x;
          py = y;
        } else {
          px =
              worldTransform[0] * x + worldTransform[2] * y + worldTransform[4];
          py =
              worldTransform[1] * x + worldTransform[3] * y + worldTransform[5];
        }

        influenceMatrix[0] = influenceMatrix[1] = influenceMatrix[2] =
        influenceMatrix[3] = influenceMatrix[4] = influenceMatrix[5] = 0.0;

        for (int wi = 0; wi < 4; wi++) {
          int boneIndex = _vertices[boneIndexOffset + wi].toInt();
          double weight = _vertices[weightOffset + wi];

          int boneTransformIndex = boneIndex * 6;
          if (boneIndex <= connectedBones.size()) {
            for (int j = 0; j < 6; j++) {
              influenceMatrix[j] +=
                  boneTransforms[boneTransformIndex + j] * weight;
            }
          }
        }

        x = influenceMatrix[0] * px +
            influenceMatrix[2] * py +
            influenceMatrix[4];
        y = influenceMatrix[1] * px +
            influenceMatrix[3] * py +
            influenceMatrix[5];

        readIdx += stride;
        boneIndexOffset += vertexStride;
        weightOffset += vertexStride;

        buffer[writeIdx++] = x;
        buffer[writeIdx++] = y;
      }
    } else {
      for (int i = 0; i < _vertexCount; i++) {
        buffer[writeIdx++] = v[readIdx];
        buffer[writeIdx++] = v[readIdx + 1];
        readIdx += stride;
      }
    }
  }

  @Override
  public AABB computeAABB() {
    // Todo: implement for image.
    Mat2D worldTransform = this.worldTransform;
    return AABB.fromValues(worldTransform[4], worldTransform[5],
        worldTransform[4], worldTransform[5]);
  }

  public Mat2D imageTransform() { return isConnectedToBones ? null : worldTransform; }

  @Override
  public void initializeGraphics() {}

  @Override
  public void invalidateDrawable() {}

  @Override
  public int blendModeId() {
    return 0;
  }

  @Override
  set blendModeId(int value) {}
}
