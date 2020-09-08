import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.nting.flare.playn.JavaActor;
import org.nting.flare.playn.component.BoxFit;
import org.nting.flare.playn.component.FlareActorRenderObject;
import org.nting.toolkit.Component;
import org.nting.toolkit.app.ToolkitApp;
import org.nting.toolkit.event.KeyEvent;
import org.nting.toolkit.event.KeyListener;
import org.nting.toolkit.event.MouseEvent;
import org.nting.toolkit.event.MouseListener;
import org.nting.toolkit.layout.AbsoluteLayout;

import playn.core.Key;
import playn.core.PlayN;
import playn.swing.JavaGraphics;
import playn.swing.JavaPlatform;

public class FlareJava {

    public static void main(String[] args) throws URISyntaxException, IOException {
        JavaPlatform platform = JavaPlatform.register();
        platform.setTitle("Flare");

        byte[] data = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("Teddy.flr").toURI()));
        JavaActor javaActor = JavaActor.loadFromByteData(data);

        platform.assets().setPathPrefix("org/nting/assets");
        platform.graphics().registerFont("SourceSansPro-Bold", "fonts/SourceSansPro-Bold.ttf");
        platform.graphics().registerFont("SourceSansPro-BoldItalic", "fonts/SourceSansPro-BoldItalic.ttf");
        platform.graphics().registerFont("SourceSansPro-Italic", "fonts/SourceSansPro-Italic.ttf");
        platform.graphics().registerFont("SourceSansPro-Regular", "fonts/SourceSansPro-Regular.ttf");
        platform.graphics().registerFont("IconFont", "fonts/IconFont.ttf");
        ((JavaGraphics) PlayN.graphics()).setSize(1024, 800);

        ToolkitApp.startApp().then(toolkitManager -> toolkitManager.root().addComponent(createContent(javaActor),
                AbsoluteLayout.fillParentConstraint()));
    }

    private static Component createContent(JavaActor javaActor) {
        FlareActorRenderObject flareActorRenderObject = new FlareActorRenderObject(javaActor, null, "idle");
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

        return flareActorRenderObject;
    }
}
