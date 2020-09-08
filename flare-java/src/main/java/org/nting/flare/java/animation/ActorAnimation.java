package org.nting.flare.java.animation;

import java.util.ArrayList;
import java.util.List;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.ActorEvent;
import org.nting.flare.java.StreamReader;

public class ActorAnimation {

    private String _name;
    private int _fps;
    private float _duration;
    private boolean _isLooping;
    private List<ComponentAnimation> _components;
    private List<ComponentAnimation> _triggerComponents;

    public String name() {
        return _name;
    }

    public int fps() {
        return _fps;
    }

    public boolean isLooping() {
        return _isLooping;
    }

    public float duration() {
        return _duration;
    }

    public List<ComponentAnimation> animatedComponents() {
        return _components;
    }

    public void triggerEvents(List<ActorComponent> components, float fromTime, float toTime,
            List<AnimationEventArgs> triggerEvents) {
        for (ComponentAnimation keyedComponent : _triggerComponents) {
            for (PropertyAnimation property : keyedComponent.properties()) {
                switch (property.propertyType()) {
                case PropertyTypes.trigger:
                    List<KeyFrame> keyFrames = property.keyFrames();

                    int kfl = keyFrames.size();
                    if (kfl == 0) {
                        continue;
                    }

                    int idx = 0;
                // Binary find the keyframe index.
                {
                    int mid = 0;
                    float element = 0.0f;
                    int start = 0;
                    int end = kfl - 1;

                    while (start <= end) {
                        mid = (start + end) >> 1;
                        element = keyFrames.get(mid).time();
                        if (element < toTime) {
                            start = mid + 1;
                        } else if (element > toTime) {
                            end = mid - 1;
                        } else {
                            start = mid;
                            break;
                        }
                    }

                    idx = start;
                }

                    if (idx == 0) {
                        if (kfl > 0 && keyFrames.get(0).time() == toTime) {
                            ActorComponent component = components.get(keyedComponent.componentIndex());
                            triggerEvents.add(new AnimationEventArgs(component.name(), component,
                                    property.propertyType(), toTime, 0.0f));
                        }
                    } else {
                        for (int k = idx - 1; k >= 0; k--) {
                            KeyFrame frame = keyFrames.get(k);

                            if (frame.time() > fromTime) {
                                ActorComponent component = components.get(keyedComponent.componentIndex());
                                triggerEvents.add(new AnimationEventArgs(component.name(), component,
                                        property.propertyType(), frame.time(), toTime - frame.time()));
                                /*
                                 * triggered.push({ name:component._Name, component:component,
                                 * propertyType:property._Type, keyFrameTime:frame._Time, elapsed:toTime-frame._Time });
                                 */
                            } else {
                                break;
                            }
                        }
                    }
                    break;
                default:
                    break;
                }
            }
        }
    }

    /// Apply the specified time to all the components of this animation.
    /// This operation will result in the application of the keyframe values
    /// at the given time, and perform interpolation if needed.
    ///
    /// @time is the current time for this animation
    /// @artboard is the artboard that contains it
    /// @mix is a value [0,1]
    /// This is a blending parameter to allow smoothing between concurrent
    /// animations.
    /// By setting mix to 1, the current animation will fully replace the
    /// existing values. By ramping up mix with values between 0 and 1, the
    /// transition from one animation to the next will be more gradual as it
    /// gets mixed in, preventing poppying effects.
    public void apply(float time, ActorArtboard artboard, float mix) {
        for (ComponentAnimation componentAnimation : _components) {
            componentAnimation.apply(time, artboard.components(), mix);
        }
    }

    public static ActorAnimation read(StreamReader reader, List<ActorComponent> components) {
        ActorAnimation animation = new ActorAnimation();
        animation._name = reader.readString("name");
        animation._fps = reader.readUint8("fps");
        animation._duration = reader.readFloat32("duration");
        animation._isLooping = reader.readBoolean("isLooping");

        reader.openArray("keyed");
        int numKeyedComponents = reader.readUint16Length();

        // We distinguish between animated and triggered components as ActorEvents
        // are currently only used to trigger events and don't need the full
        // animation cycle. This lets them optimize them out of the regular
        // animation cycle.
        int animatedComponentCount = 0;
        int triggerComponentCount = 0;

        List<ComponentAnimation> animatedComponents = new ArrayList<>(numKeyedComponents);
        for (int i = 0; i < numKeyedComponents; i++) {
            ComponentAnimation componentAnimation = ComponentAnimation.read(reader, components);
            animatedComponents.add(componentAnimation);
            if (componentAnimation != null && componentAnimation.componentIndex() < components.size()) {
                ActorComponent actorComponent = components.get(componentAnimation.componentIndex());
                if (actorComponent != null) {
                    if (actorComponent instanceof ActorEvent) {
                        triggerComponentCount++;
                    } else {
                        animatedComponentCount++;
                    }
                }
            }
        }
        reader.closeArray();

        animation._components = new ArrayList<>(animatedComponentCount);
        animation._triggerComponents = new ArrayList<>(triggerComponentCount);

        for (int i = 0; i < numKeyedComponents; i++) {
            ComponentAnimation componentAnimation = animatedComponents.get(i);
            if (componentAnimation != null) {
                ActorComponent actorComponent = components.get(componentAnimation.componentIndex());
                if (actorComponent != null) {
                    if (actorComponent instanceof ActorEvent) {
                        animation._triggerComponents.add(componentAnimation);
                    } else {
                        animation._components.add(componentAnimation);
                    }
                }
            }
        }

        return animation;
    }
}
