package org.nting.flare.java.animation;

import java.util.ArrayList;
import java.util.List;

import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.StreamReader;

public class ComponentAnimation {

    private int _componentIndex;
    private List<PropertyAnimation> _properties;

    public int componentIndex() {
        return _componentIndex;
    }

    public List<PropertyAnimation> properties() {
        return _properties;
    }

    public static ComponentAnimation read(StreamReader reader, List<ActorComponent> components) {
        reader.openObject("component");
        ComponentAnimation componentAnimation = new ComponentAnimation();

        componentAnimation._componentIndex = reader.readId("component");
        int numProperties = reader.readUint16Length();
        componentAnimation._properties = new ArrayList<>(numProperties);
        for (int i = 0; i < numProperties; i++) {
            assert (componentAnimation._componentIndex < components.size());
            componentAnimation._properties
                    .add(PropertyAnimation.read(reader, components.get(componentAnimation._componentIndex)));
        }
        reader.closeObject();

        return componentAnimation;
    }

    public void apply(float time, List<ActorComponent> components, float mix) {
        for (PropertyAnimation propertyAnimation : _properties) {
            if (propertyAnimation != null) {
                propertyAnimation.apply(time, components.get(_componentIndex), mix);
            }
        }
    }
}
