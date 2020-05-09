package org.nting.flare.java;

public class ActorClip {

    public int clipIdx;
    public boolean intersect = true;
    public ActorNode node;

    public ActorClip(int clipIdx) {
        this.clipIdx = clipIdx;
    }

    public ActorClip(ActorClip from) {
        clipIdx = from.clipIdx;
        intersect = from.intersect;
    }
}
