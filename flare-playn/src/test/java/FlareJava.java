import org.nting.flare.playn.FlutterActor;
import playn.swing.JavaPlatform;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FlareJava {

    public static void main(String[] args) throws URISyntaxException, IOException {
        JavaPlatform.register();
        byte[] data = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("Izé.flr").toURI()));
        // StreamReader streamReader = StreamReader.createStreamReader(data);
        FlutterActor flutterActor = loadFromByteData(data);
        flutterActor.version();
        System.exit(0);
    }

    private static FlutterActor loadFromByteData(byte[] data) {
        // ByteData data = await context.bundle.load(context.filename);
        FlutterActor actor = new FlutterActor();
        actor.load(data, null);
        return actor;
    }
}
