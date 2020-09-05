import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.nting.flare.playn.FlutterActor;

import playn.swing.JavaPlatform;

public class FlareJava {

    public static void main(String[] args) throws URISyntaxException, IOException {
        JavaPlatform.register();
        byte[] data = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("Images.flr").toURI()));
        FlutterActor flutterActor = FlutterActor.loadFromByteData(data);
        flutterActor.artboard().initializeGraphics();
        flutterActor.version();
        System.exit(0);
    }
}
