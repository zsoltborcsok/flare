package org.nting.flare.java;

import org.nting.flare.java.maths.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;

public class ActorLayerEffectRenderer extends ActorDrawable {

    private final List<ActorDrawable> _drawables = new ArrayList<>();

    public List<ActorDrawable> drawables() {
        return _drawables;
    }

    private final List<ActorLayerEffectRendererMask> _renderMasks = new ArrayList<>();

    public List<ActorLayerEffectRendererMask> renderMasks() {
        return _renderMasks;
    }

    private ActorBlur _blur;
    private List<ActorDropShadow> _dropShadows;
    private List<ActorInnerShadow> _innerShadows;

    public ActorBlur blur() {
        return _blur;
    }

    public List<ActorDropShadow> dropShadows() {
        return _dropShadows;
    }

    public List<ActorInnerShadow> innerShadows() {
        return _innerShadows;
    }

    public void sortDrawables() {
        _drawables.sort(comparingInt(ActorDrawable::drawOrder));
    }

    @Override
    public void onParentChanged(ActorNode from, ActorNode to) {
        super.onParentChanged(from, to);
        Optional.ofNullable(from).ifPresent(ActorNode::findLayerEffect);
        Optional.ofNullable(to).ifPresent(ActorNode::findLayerEffect);
        findEffects();
    }

    @Override
    public AABB computeAABB() {
        return artboard.artboardAABB();
    }

    @Override
    public ActorLayerEffectRenderer makeInstance(ActorArtboard resetArtboard) {
        ActorLayerEffectRenderer instanceNode = resetArtboard.actor().makeLayerEffectRenderer();
        instanceNode.copyDrawable(this, resetArtboard);
        return instanceNode;
    }

    public void findEffects() {
        List<ActorComponent> blurs = parent().children().stream()
                .filter(child -> child instanceof ActorBlur && !(child instanceof ActorShadow))
                .collect(Collectors.toList());
        _blur = !blurs.isEmpty() ? ((ActorBlur) blurs.get(0)) : null;
        _dropShadows = parent().children().stream().filter(child -> child instanceof ActorDropShadow)
                .map(child -> (ActorDropShadow) child).collect(Collectors.toList());
        _innerShadows = parent().children().stream().filter(child -> child instanceof ActorInnerShadow)
                .map(child -> (ActorInnerShadow) child).collect(Collectors.toList());
    }

    @Override
    public void resolveComponentIndices(List<ActorComponent> components) {
        super.resolveComponentIndices(components);
        parent().findLayerEffect();
    }

    @Override
    public void completeResolve() {
        super.completeResolve();

        _drawables.clear();

        Optional.ofNullable(parent()).ifPresent(v -> v.all(node -> {
            if (node == this) {
                // don't recurse into this renderer
                return false;
            } else if (node instanceof ActorNode && ((ActorNode) node).layerEffect() != null
                    && ((ActorNode) node).layerEffect() != this) {
                _drawables.add(((ActorNode) node).layerEffect());
                // don't recurse further into nodes that are drawing to layers
                return false;
            }
            if (node instanceof ActorDrawable) {
                _drawables.add((ActorDrawable) node);
            }
            return true;
        }));

        _drawables.forEach(this::_computeLayerNode);

        sortDrawables();
        computeMasks();
        findEffects();
    }

    public void computeMasks() {
        _renderMasks.clear();
        List<ActorMask> masks = parent().children().stream().filter(child -> child instanceof ActorMask)
                .map(child -> (ActorMask) child).collect(Collectors.toList());

        for (ActorMask mask : masks) {
            ActorLayerEffectRendererMask renderMask = new ActorLayerEffectRendererMask(mask);
            Optional.ofNullable(mask.source()).ifPresent(v -> v.all(child -> {
                if (child == parent()) {
                    // recursive mask was selected
                    return false;
                }
                if (child instanceof ActorDrawable) {
                    if (child == this) {
                        return false;
                    } else if (((ActorDrawable) child).layerEffect() != null) {
                        // Layer effect is direct discendant of this layer, so we want to
                        // draw it with the other drawables in this layer.
                        renderMask.drawables.add(((ActorDrawable) child).layerEffect());
                        // Don't iterate if child has further layer effect
                        return false;
                    } else {
                        renderMask.drawables.add((ActorDrawable) child);
                    }
                }
                return true;
            }));

            if (!renderMask.drawables.isEmpty()) {
                _renderMasks.add(renderMask);
            }
        }
    }

    private void _computeLayerNode(ActorDrawable drawable) {
        ActorNode parent = drawable;
        while (parent != null) {
            if (parent.layerEffect() != null) {
                drawable.layerEffectRenderParent = parent;
                return;
            }
            parent = parent.parent();
        }
        drawable.layerEffectRenderParent = null;
    }
}
