package org.nting.flare.playn;

import playn.core.Canvas.Composite;
import playn.core.PlayN;

public enum BlendMode {
    CLEAR(null), //
    SRC(Composite.SRC), //
    DST(null), //
    SRC_OVER(Composite.SRC_OVER), //
    DST_OVER(Composite.DST_OVER), //
    SRC_IN(Composite.SRC_IN), //
    DST_IN(Composite.DST_IN), //
    SRC_OUT(Composite.SRC_OUT), //
    DST_OUT(Composite.DST_OUT), //
    SRC_ATOP(Composite.SRC_ATOP), //
    DST_ATOP(Composite.DST_ATOP), //
    XOR(Composite.XOR), //
    PLUS(null), //
    MODULATE(null), //
    SCREEN(null), //
    OVERLAY(null), //
    DARKEN(null), //
    LIGHTEN(null), //
    COLOR_DODGE(null), //
    COLOR_BURN(null), //
    HARD_LIGHT(null), //
    SOFT_LIGHT(null), //
    DIFFERENCE(null), //
    EXCLUSION(null), //
    MULTIPLY(Composite.MULTIPLY), //
    HUE(null), //
    SATURATION(null), //
    COLOR(null), //
    LUMINOSITY(null);

    private final Composite composite;

    BlendMode(Composite composite) {
        this.composite = composite;
    }

    public Composite getComposite() {
        if (composite != null) {
            return composite;
        } else {
            PlayN.log(getClass()).error("Not supported BlendMode: {}", name());
            return Composite.SRC_OVER;
        }
    }
}