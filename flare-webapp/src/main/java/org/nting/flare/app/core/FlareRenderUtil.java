package org.nting.flare.app.core;

import org.nting.flare.playn.component.BoxFit;
import org.nting.flare.playn.component.FlareActorRenderObject;
import org.nting.toolkit.Component;
import org.nting.toolkit.event.KeyEvent;
import org.nting.toolkit.event.KeyListener;
import org.nting.toolkit.event.MouseEvent;
import org.nting.toolkit.event.MouseListener;

import playn.core.Key;

public class FlareRenderUtil {

    public static Component createContent(String filePath, String artboardName, String animationName) {
        return FlareActorRenderObject.lazyFlareActorRenderObject(filePath, artboardName, animationName,
                FlareRenderUtil::loaded);
    }

    private static void loaded(FlareActorRenderObject flareActorRenderObject) {
        flareActorRenderObject.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                flareActorRenderObject.paused.adjustValue(p -> !p);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.MouseButton.BUTTON_RIGHT) {
                    BoxFit fit = flareActorRenderObject.fit.getValue();
                    fit = BoxFit.values()[(fit.ordinal() + 1) % BoxFit.values().length];
                    flareActorRenderObject.fit.setValue(fit);
                }
            }
        });
        flareActorRenderObject.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isKeyCode(Key.HOME)) {
                    flareActorRenderObject.rewind();
                } else if (e.isKeyCode(Key.END)) {
                    flareActorRenderObject.fastForward();
                } else if (e.isKeyCode(Key.SPACE)) {
                    flareActorRenderObject.paused.adjustValue(p -> !p);
                }
            }
        });
    }
}
