package org.nting.flare.java.animation;

import org.nting.flare.java.ActorComponent;

public class AnimationEventArgs {

    private String _name;
    private ActorComponent _component;
    private int _propertyType;
    private float _keyFrameTime;
    private float _elapsedTime;

    public AnimationEventArgs(String name, ActorComponent component, int type, float keyframeTime, float elapsedTime) {
        _name = name;
        _component = component;
        _propertyType = type;
        _keyFrameTime = keyframeTime;
        _elapsedTime = elapsedTime;
    }

    public String name() {
        return _name;
    }

    public ActorComponent component() {
        return _component;
    }

    public int propertyType() {
        return _propertyType;
    }

    public float keyFrameTime() {
        return _keyFrameTime;
    }

    public float elapsedTime() {
        return _elapsedTime;
    }
}
