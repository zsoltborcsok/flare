import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.nting.flare.playn.FlutterActor;

public class FlareJava {

    public static void main(String[] args) throws URISyntaxException, IOException {
        byte[] data = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("Teddy.flr").toURI()));
        // StreamReader streamReader = StreamReader.createStreamReader(data);
        FlutterActor flutterActor = loadFromByteData(data);
    }

    private static FlutterActor loadFromByteData(byte[] data) {
        // ByteData data = await context.bundle.load(context.filename);
        FlutterActor actor = new FlutterActor();
        actor.load(data, null);
        return actor;
    }
}
