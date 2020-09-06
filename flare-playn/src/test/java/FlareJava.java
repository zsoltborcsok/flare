import static org.nting.toolkit.ui.style.material.MaterialColorPalette.brown_100;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.nting.flare.playn.FlutterActor;
import org.nting.flare.playn.FlutterActorArtboard;
import org.nting.toolkit.app.ToolkitApp;
import org.nting.toolkit.component.AbstractComponent;
import org.nting.toolkit.layout.AbsoluteLayout;
import org.nting.toolkit.ui.ComponentUI;

import playn.core.Canvas;
import playn.core.PlayN;
import playn.swing.JavaGraphics;
import playn.swing.JavaPlatform;
import pythagoras.f.Dimension;

public class FlareJava {

    public static void main(String[] args) throws URISyntaxException, IOException {
        JavaPlatform platform = JavaPlatform.register();
        platform.setTitle("Flare");

        byte[] data = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("Teddy.flr").toURI()));
        FlutterActor flutterActor = FlutterActor.loadFromByteData(data);

        platform.assets().setPathPrefix("org/nting/assets");
        platform.graphics().registerFont("SourceSansPro-Bold", "fonts/SourceSansPro-Bold.ttf");
        platform.graphics().registerFont("SourceSansPro-BoldItalic", "fonts/SourceSansPro-BoldItalic.ttf");
        platform.graphics().registerFont("SourceSansPro-Italic", "fonts/SourceSansPro-Italic.ttf");
        platform.graphics().registerFont("SourceSansPro-Regular", "fonts/SourceSansPro-Regular.ttf");
        platform.graphics().registerFont("IconFont", "fonts/IconFont.ttf");
        ((JavaGraphics) PlayN.graphics()).setSize(1024, 800);

        ToolkitApp.startApp().then(toolkitManager -> toolkitManager.root().addComponent(
                new FlareActorRenderObject(flutterActor, null, "idle"), AbsoluteLayout.fillParentConstraint()));
    }

    // TODO LayoutManager corresponding for BoxFit + configure the transform accordingly!
    private static class FlareActorRenderObject extends AbstractComponent {

        private FlutterActor flutterActor;
        private FlutterActorArtboard flutterActorArtboard;
        private String animationName;

        private FlareActorRenderObject(FlutterActor flutterActor, String artboardName, String animationName) {
            this.flutterActor = flutterActor;
            flutterActorArtboard = (FlutterActorArtboard) flutterActor.getArtboard(artboardName);
            flutterActorArtboard.initializeGraphics();
            flutterActorArtboard.advance(0.0f);

            this.animationName = animationName;
        }

        @Override
        public void setComponentUI(ComponentUI componentUI) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(flutterActor.artboard().width(), flutterActor.artboard().height());
        }

        @Override
        public void doPaintComponent(Canvas canvas) {
            canvas.setFillColor(brown_100);
            canvas.fillRect(0, 0, width.getValue(), height.getValue());

            flutterActorArtboard.draw(canvas);
        }
    }
}
