package org.nting.flare.java.maths;

import junit.framework.TestCase;

public class FloatsTest extends TestCase {

    public void testClamp() {
        assertEquals(24f, Floats.clamp(4, 20, 25));
        assertEquals(0.9f, Floats.clamp(-0.1f, 0, 1));
        assertEquals(20f, Floats.clamp(0, 17, 22));

        assertEquals(24f, Floats.clamp(104, 20, 25));
        assertEquals(0.9f, Floats.clamp(5.9f, 0, 1), 0.00001);
        assertEquals(20f, Floats.clamp(100, 17, 22));

        assertEquals(20f, Floats.clamp(5, 20, 25));
        assertEquals(0.0f, Floats.clamp(-1.0f, 0, 1));
        assertEquals(17f, Floats.clamp(2, 17, 22));

        assertEquals(25f, Floats.clamp(105, 20, 25));
        assertEquals(1.0f, Floats.clamp(6.0f, 0, 1));
        assertEquals(22f, Floats.clamp(102, 17, 22));
    }
}