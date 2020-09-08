package org.nting.flare.java.animation;

import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.StreamReader;

public abstract class KeyFrame {

    protected float _time;

    public float time() {
        return _time;
    }

    public abstract void apply(ActorComponent component, float mix);

    public abstract void applyInterpolation(ActorComponent component, float time, KeyFrame toFrame, float mix);

    public abstract void setNext(KeyFrame frame);

    public static boolean read(StreamReader reader, KeyFrame frame) {
        frame._time = (float) reader.readFloat64("time");

        return true;
    }
}
