package org.nting.flare.java;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.nting.flare.java.BlockTypes.blockTypesMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.nting.flare.java.animation.ActorAnimation;
import org.nting.flare.java.maths.AABB;
import org.nting.flare.java.maths.Vec2D;

public class ActorArtboard {

    private int _flags = ActorFlags.isDrawOrderDirty;
    private int _drawableNodeCount = 0;
    private int _nodeCount = 0;
    private int _dirtDepth = 0;
    private ActorNode _root;
    private List<ActorComponent> _components;
    private List<ActorNode> _nodes;
    private final List<ActorDrawable> _drawableNodes = new ArrayList<>();
    private final List<ActorLayerEffectRenderer> _effectRenderers = new ArrayList<>();
    private List<ActorAnimation> _animations;
    private List<ActorComponent> _dependencyOrder;
    private Actor _actor;
    private String _name;
    private final Vec2D _translation = new Vec2D();
    private float _width = 0.0f;
    private float _height = 0.0f;
    private final Vec2D _origin = new Vec2D();
    private boolean _clipContents = true;
    private final float[] _color = new float[4];
    private float _modulateOpacity = 1.0f;
    private float[] _overrideColor;

    public String name() {
        return _name;
    }

    public float width() {
        return _width;
    }

    public float height() {
        return _height;
    }

    public Vec2D origin() {
        return _origin;
    }

    public Vec2D translation() {
        return _translation;
    }

    public boolean clipContents() {
        return _clipContents;
    }

    public float[] color() {
        return _color;
    }

    public float modulateOpacity() {
        return _modulateOpacity;
    }

    public float[] overrideColor() {
        return _overrideColor;
    }

    public void overrideColor(float[] value) {
        _overrideColor = value;
        for (final ActorDrawable drawable : _drawableNodes) {
            addDirt(drawable, DirtyFlags.paintDirty, true);
        }
    }

    public void modulateOpacity(float value) {
        _modulateOpacity = value;
        for (final ActorDrawable drawable : _drawableNodes) {
            addDirt(drawable, DirtyFlags.paintDirty, true);
        }
    }

    public ActorArtboard(Actor actor) {
        _actor = actor;
        _root = new ActorNode(this);
    }

    public Actor actor() {
        return _actor;
    }

    public List<ActorComponent> components() {
        return _components;
    }

    public List<ActorNode> nodes() {
        return _nodes;
    }

    public List<ActorAnimation> animations() {
        return _animations;
    }

    public List<ActorDrawable> drawableNodes() {
        return _drawableNodes;
    }

    public int componentCount() {
        return _components.size();
    }

    public int nodeCount() {
        return _nodeCount;
    }

    public int drawNodeCount() {
        return _drawableNodeCount;
    }

    public ActorNode root() {
        return _root;
    }

    public boolean addDependency(ActorComponent a, ActorComponent b) {
        List<ActorComponent> dependents = b.dependents;
        if (dependents == null) {
            b.dependents = dependents = new ArrayList<ActorComponent>();
        }
        if (dependents.contains(a)) {
            return false;
        }
        dependents.add(a);
        return true;
    }

    public void sortDependencies() {
        DependencySorter sorter = new DependencySorter();
        _dependencyOrder = sorter.sort(_root);
        int graphOrder = 0;
        for (final ActorComponent component : _dependencyOrder) {
            component.graphOrder = graphOrder++;
            component.dirtMask = 255;
        }
        _flags |= ActorFlags.isDirty;
    }

    public boolean addDirt(ActorComponent component, int value, boolean recurse) {
        if ((component.dirtMask & value) == value) {
            // Already marked.
            return false;
        }

        // Make sure dirt is set before calling anything that can set more dirt.
        int dirt = component.dirtMask | value;
        component.dirtMask = dirt;

        _flags |= ActorFlags.isDirty;

        component.onDirty(dirt);

        /// If the order of this component is less than the current dirt depth,
        /// update the dirt depth so that the update loop can break out early
        /// and re-run (something up the tree is dirty).
        if (component.graphOrder < _dirtDepth) {
            _dirtDepth = component.graphOrder;
        }
        if (!recurse) {
            return true;
        }
        List<ActorComponent> dependents = component.dependents;
        if (dependents != null) {
            for (final ActorComponent d : dependents) {
                addDirt(d, value, recurse);
            }
        }

        return true;
    }

    public ActorAnimation getAnimation(String name) {
        for (final ActorAnimation a : _animations) {
            if (Objects.equals(a.name(), name)) {
                return a;
            }
        }
        return null;
    }

    public ActorNode getNode(String name) {
        for (final ActorNode node : _nodes) {
            if (node != null && Objects.equals(node.name(), name)) {
                return node;
            }
        }
        return null;
    }

    public void markDrawOrderDirty() {
        _flags |= ActorFlags.isDrawOrderDirty;
    }

    public ActorArtboard makeInstance() {
        ActorArtboard artboardInstance = _actor.makeArtboard();
        artboardInstance.copyArtboard(this);
        return artboardInstance;
    }

    public ActorArtboard makeInstanceWithActor(Actor actor) {
        ActorArtboard artboardInstance = actor.makeArtboard();
        artboardInstance.copyArtboard(this);
        return artboardInstance;
    }

    public void copyArtboard(ActorArtboard artboard) {
        _name = artboard._name;
        Vec2D.copy(_translation, artboard._translation);
        _width = artboard._width;
        _height = artboard._height;
        Vec2D.copy(_origin, artboard._origin);
        _clipContents = artboard._clipContents;

        _color[0] = artboard._color[0];
        _color[1] = artboard._color[1];
        _color[2] = artboard._color[2];
        _color[3] = artboard._color[3];

        // _actor = artboard._actor;
        _animations = artboard._animations;
        _drawableNodeCount = artboard._drawableNodeCount;
        _nodeCount = artboard._nodeCount;

        if (artboard.componentCount() != 0) {
            _components = new ArrayList<>(artboard.componentCount());
        }
        if (_nodeCount != 0) // This will always be at least 1.
        {
            _nodes = Arrays.asList(new ActorNode[_nodeCount]);
        }

        if (artboard.componentCount() != 0) {
            for (final ActorComponent component : artboard.components()) {
                if (component == null) {
                    _components.add(null);
                    continue;
                }
                ActorComponent instanceComponent = component.makeInstance(this);
                _components.add(instanceComponent);
            }
        }
        // Copy dependency order.
        _dependencyOrder = Arrays.asList(new ActorComponent[artboard._dependencyOrder.size()]);
        for (final ActorComponent component : artboard._dependencyOrder) {
            final ActorComponent localComponent = _components.get(component.idx);
            _dependencyOrder.set(component.graphOrder, localComponent);
            localComponent.dirtMask = 255;
        }

        _flags |= ActorFlags.isDirty;
        _root = (ActorNode) _components.get(0);
        resolveHierarchy();
        completeResolveHierarchy();
    }

    public void resolveHierarchy() {
        // Resolve nodes.
        int anIdx = 0;

        _drawableNodes.clear();
        int componentCount = this.componentCount();
        for (int i = 1; i < componentCount; i++) {
            ActorComponent c = _components.get(i);

            /// Nodes can be null if we read from a file version that contained
            /// nodes that we don't interpret in this runtime.
            if (c != null) {
                c.resolveComponentIndices(_components);
            }

            if (c instanceof ActorNode) {
                ActorNode an = (ActorNode) c;
                _nodes.add(anIdx++, an);
            }
        }
    }

    public void completeResolveHierarchy() {
        int componentCount = this.componentCount();

        // Complete resolve.
        for (int i = 1; i < componentCount; i++) {
            ActorComponent c = components().get(i);
            if (c != null) {
                c.completeResolve();
            }
        }

        // Build lists. Important to do this after all components have resolved as
        // layers won't be known before this.
        for (int i = 1; i < componentCount; i++) {
            ActorComponent c = components().get(i);
            if (c instanceof ActorDrawable && ((ActorDrawable) c).layerEffectRenderParent == null) {
                _drawableNodes.add((ActorDrawable) c);
            }
            if (c instanceof ActorLayerEffectRenderer
                    && ((ActorLayerEffectRenderer) c).layerEffectRenderParent == null) {
                _effectRenderers.add((ActorLayerEffectRenderer) c);
            }
        }

        sortDrawOrder();
    }

    public void sortDrawOrder() {
        _drawableNodes.sort(Comparator.comparingInt(ActorDrawable::drawOrder));
        for (int i = 0; i < _drawableNodes.size(); i++) {
            _drawableNodes.get(i).drawIndex = i;
        }
        for (final ActorLayerEffectRenderer layer : _effectRenderers) {
            layer.sortDrawables();
        }
    }

    public void advance(float seconds) {
        if ((_flags & ActorFlags.isDirty) != 0) {
            final int maxSteps = 100;
            int step = 0;
            int count = _dependencyOrder.size();
            while ((_flags & ActorFlags.isDirty) != 0 && step < maxSteps) {
                _flags &= ~ActorFlags.isDirty;
                // Track dirt depth here so that if something else marks
                // dirty, we restart.
                for (int i = 0; i < count; i++) {
                    ActorComponent component = _dependencyOrder.get(i);
                    _dirtDepth = i;
                    int d = component.dirtMask;
                    if (d == 0) {
                        continue;
                    }
                    component.dirtMask = 0;
                    component.update(d);
                    if (_dirtDepth < i) {
                        break;
                    }
                }
                step++;
            }
        }

        if ((_flags & ActorFlags.isDrawOrderDirty) != 0) {
            _flags &= ~ActorFlags.isDrawOrderDirty;
            sortDrawOrder();
        }
    }

    public void read(StreamReader reader) {
        _name = reader.readString("name");
        Vec2D.copyFromList(_translation, reader.readFloat32Array(2, "translation"));
        _width = reader.readFloat32("width");
        _height = reader.readFloat32("height");
        Vec2D.copyFromList(_origin, reader.readFloat32Array(2, "origin"));
        _clipContents = reader.readBoolean("clipContents");

        float[] color = reader.readFloat32Array(4, "color");
        _color[0] = color[0];
        _color[1] = color[1];
        _color[2] = color[2];
        _color[3] = color[3];

        StreamReader block;
        while ((block = reader.readNextBlock(blockTypesMap)) != null) {
            switch (block.blockType()) {
            case BlockTypes.components:
                readComponentsBlock(block);
                break;
            case BlockTypes.animations:
                readAnimationsBlock(block);
                break;
            }
        }
    }

    public void readComponentsBlock(StreamReader block) {
        int componentCount = block.readUint16Length();
        _components = new ArrayList<ActorComponent>(componentCount + 1);
        _components.add(_root);

        // Guaranteed from the exporter to be in index order.
        _nodeCount = 1;
        for (int componentIndex = 1, end = componentCount + 1; componentIndex < end; componentIndex++) {
            StreamReader nodeBlock = block.readNextBlock(blockTypesMap);
            if (nodeBlock == null) {
                break;
            }
            ActorComponent component = null;
            switch (nodeBlock.blockType()) {
            case BlockTypes.actorNode:
                component = ActorNode.read(this, nodeBlock, null);
                break;

            case BlockTypes.actorBone:
                component = ActorBone.read(this, nodeBlock, null);
                break;

            case BlockTypes.actorRootBone:
                component = ActorRootBone.read(this, nodeBlock, null);
                break;

            // TODO: fix sequences for flare.
            // case BlockTypes.ActorImageSequence:
            // component =
            // ActorImage.readSequence(this, nodeBlock, actor().makeImageNode());
            // ActorImage ai = component as ActorImage;
            // actor().maxTextureIndex = ai
            // .sequenceFrames.last.atlasIndex; // Last atlasIndex is the biggest
            // break;

            case BlockTypes.actorImage:
                component = ActorImage.read(this, nodeBlock, actor().makeImageNode());
                if (((ActorImage) component).textureIndex() > actor().maxTextureIndex) {
                    actor().maxTextureIndex = ((ActorImage) component).textureIndex();
                }
                break;

            case BlockTypes.actorIKTarget:
                // component = ActorIKTarget.Read(this, nodeBlock);
                break;

            case BlockTypes.actorEvent:
                component = ActorEvent.read(this, nodeBlock, null);
                break;

            case BlockTypes.customIntProperty:
                // component = CustomIntProperty.Read(this, nodeBlock);
                break;

            case BlockTypes.customFloatProperty:
                // component = CustomFloatProperty.Read(this, nodeBlock);
                break;

            case BlockTypes.customStringProperty:
                // component = CustomStringProperty.Read(this, nodeBlock);
                break;

            case BlockTypes.customBooleanProperty:
                // component = CustomBooleanProperty.Read(this, nodeBlock);
                break;

            case BlockTypes.actorColliderRectangle:
                // component = ActorColliderRectangle.Read(this, nodeBlock);
                break;

            case BlockTypes.actorColliderTriangle:
                // component = ActorColliderTriangle.Read(this, nodeBlock);
                break;

            case BlockTypes.actorColliderCircle:
                // component = ActorColliderCircle.Read(this, nodeBlock);
                break;

            case BlockTypes.actorColliderPolygon:
                // component = ActorColliderPolygon.Read(this, nodeBlock);
                break;

            case BlockTypes.actorColliderLine:
                // component = ActorColliderLine.Read(this, nodeBlock);
                break;

            case BlockTypes.actorNodeSolo:
                component = ActorNodeSolo.read(this, nodeBlock, null);
                break;

            case BlockTypes.actorJellyBone:
                component = ActorJellyBone.read(this, nodeBlock, null);
                break;

            case BlockTypes.jellyComponent:
                component = JellyComponent.read(this, nodeBlock, null);
                break;

            case BlockTypes.actorIKConstraint:
                component = ActorIKConstraint.read(this, nodeBlock, null);
                break;

            case BlockTypes.actorDistanceConstraint:
                component = ActorDistanceConstraint.read(this, nodeBlock, null);
                break;

            case BlockTypes.actorTranslationConstraint:
                component = ActorTranslationConstraint.read(this, nodeBlock, null);
                break;

            case BlockTypes.actorScaleConstraint:
                component = ActorScaleConstraint.read(this, nodeBlock, null);
                break;

            case BlockTypes.actorRotationConstraint:
                component = ActorRotationConstraint.read(this, nodeBlock, null);
                break;

            case BlockTypes.actorTransformConstraint:
                component = ActorTransformConstraint.read(this, nodeBlock, null);
                break;

            case BlockTypes.actorShape:
                component = ActorShape.read(this, nodeBlock, actor().makeShapeNode(null));
                break;

            case BlockTypes.actorPath:
                component = ActorPath.read(this, nodeBlock, actor().makePathNode());
                break;

            case BlockTypes.colorFill:
                component = ColorFill.read(this, nodeBlock, actor().makeColorFill());
                break;

            case BlockTypes.colorStroke:
                component = ColorStroke.read(this, nodeBlock, actor().makeColorStroke());
                break;

            case BlockTypes.gradientFill:
                component = GradientFill.read(this, nodeBlock, actor().makeGradientFill());
                break;

            case BlockTypes.gradientStroke:
                component = GradientStroke.read(this, nodeBlock, actor().makeGradientStroke());
                break;

            case BlockTypes.radialGradientFill:
                component = RadialGradientFill.read(this, nodeBlock, actor().makeRadialFill());
                break;

            case BlockTypes.radialGradientStroke:
                component = RadialGradientStroke.read(this, nodeBlock, actor().makeRadialStroke());
                break;

            case BlockTypes.actorEllipse:
                component = ActorEllipse.read(this, nodeBlock, actor().makeEllipse());
                break;

            case BlockTypes.actorRectangle:
                component = ActorRectangle.read(this, nodeBlock, actor().makeRectangle());
                break;

            case BlockTypes.actorTriangle:
                component = ActorTriangle.read(this, nodeBlock, actor().makeTriangle());
                break;

            case BlockTypes.actorStar:
                component = ActorStar.read(this, nodeBlock, actor().makeStar());
                break;

            case BlockTypes.actorPolygon:
                component = ActorPolygon.read(this, nodeBlock, actor().makePolygon());
                break;

            case BlockTypes.actorSkin:
                component = ActorComponent.read(this, nodeBlock, new ActorSkin());
                break;

            case BlockTypes.actorLayerEffectRenderer:
                component = ActorDrawable.read(this, nodeBlock, actor().makeLayerEffectRenderer());
                break;

            case BlockTypes.actorMask:
                component = ActorMask.read(this, nodeBlock, new ActorMask());
                break;

            case BlockTypes.actorBlur:
                component = ActorBlur.read(this, nodeBlock, null);
                break;

            case BlockTypes.actorDropShadow:
                component = ActorShadow.read(this, nodeBlock, actor().makeDropShadow());
                break;

            case BlockTypes.actorInnerShadow:
                component = ActorShadow.read(this, nodeBlock, actor().makeInnerShadow());
                break;
            }
            if (component instanceof ActorDrawable) {
                _drawableNodeCount++;
            }

            if (component instanceof ActorNode) {
                _nodeCount++;
            }
            _components.add(component);
            if (component != null) {
                component.idx = componentIndex;
            }
        }

        _nodes = new ArrayList<ActorNode>(_nodeCount);
        _nodes.add(_root);
    }

    public void initializeGraphics() {
        // Iterate components as some drawables may end up in other layers.
        for (final ActorComponent component : _components) {
            if (component instanceof ActorDrawable) {
                ((ActorDrawable) component).initializeGraphics();
            }
        }
    }

    public void readAnimationsBlock(StreamReader block) {
        // Read animations.
        int animationCount = block.readUint16Length();
        _animations = new ArrayList<ActorAnimation>(animationCount);
        StreamReader animationBlock;

        while ((animationBlock = block.readNextBlock(blockTypesMap)) != null) {
            if (animationBlock.blockType() == BlockTypes.animation) {
                ActorAnimation anim = ActorAnimation.read(animationBlock, _components);
                _animations.add(anim);
            }
        }
    }

    public AABB artboardAABB() {
        float minX = -1 * _origin.values()[0] * width();
        float minY = -1 * _origin.values()[1] * height();
        return new AABB(minX, minY, minX + _width, minY + height());
    }

    public AABB computeAABB() {
        if (_drawableNodes.isEmpty()) {
            return new AABB();
        }

        AABB aabb = null;
        for (final ActorDrawable drawable : _drawableNodes) {
            // This is the axis aligned bounding box in the space
            // of the parent (this case our shape).
            AABB pathAABB = drawable.computeAABB();
            if (pathAABB == null) {
                continue;
            }
            if (aabb == null) {
                aabb = pathAABB;
            } else {
                // Combine.
                aabb.values()[0] = min(aabb.values()[0], pathAABB.values()[0]);
                aabb.values()[1] = min(aabb.values()[1], pathAABB.values()[1]);

                aabb.values()[2] = max(aabb.values()[2], pathAABB.values()[2]);
                aabb.values()[3] = max(aabb.values()[3], pathAABB.values()[3]);
            }
        }

        return aabb;
    }
}
