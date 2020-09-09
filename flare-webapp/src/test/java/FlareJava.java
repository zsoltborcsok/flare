import java.awt.Image;
import java.awt.Toolkit;

import org.nting.flare.app.core.FlareRenderUtil;
import org.nting.toolkit.app.ToolkitApp;
import org.nting.toolkit.layout.AbsoluteLayout;

import playn.core.PlayN;
import playn.swing.JavaGraphics;
import playn.swing.JavaPlatform;

public class FlareJava {

    public static void main(String[] args) {
        JavaPlatform platform = JavaPlatform.register();
        platform.setTitle("Flare");
        platform.setIcon(getIconImage());

        platform.graphics().registerFont("SourceSansPro-Bold", "text/SourceSansPro-Bold.ttf");
        platform.graphics().registerFont("SourceSansPro-BoldItalic", "text/SourceSansPro-BoldItalic.ttf");
        platform.graphics().registerFont("SourceSansPro-Italic", "text/SourceSansPro-Italic.ttf");
        platform.graphics().registerFont("SourceSansPro-Regular", "text/SourceSansPro-Regular.ttf");
        platform.graphics().registerFont("IconFont", "text/IconFont.ttf");
        ((JavaGraphics) PlayN.graphics()).setSize(1024, 800);

        ToolkitApp.startApp()
                .then(toolkitManager -> toolkitManager.root().addComponent(
                        FlareRenderUtil.createContent("flare/Teddy.flr", null, "idle"),
                        AbsoluteLayout.fillParentConstraint()));
    }

    private static Image getIconImage() {
        return Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemClassLoader().getResource("icon.png"));
    }
}
