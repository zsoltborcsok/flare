import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class DartToJava {

    private static final String RELATIVE_PATH = "flare-java/src/main/generated/org/nting/flare/java";

    private static final Logger logger = LoggerFactory.getLogger(DartToJava.class);

    public static void main(String[] args) {
        // renameDartFiles(Paths.get(RELATIVE_PATH));
        addPackageDefinition(Paths.get(RELATIVE_PATH));
    }

    private static void addPackageDefinition(Path pathToFiles) {
        try (Stream<Path> javaFiles = Files.walk(pathToFiles).filter(path -> path.toString().endsWith(".java"))) {
            javaFiles.forEach(path -> {
                try {
                    List<String> lines = Files.readAllLines(path);
                    lines.add(0, "package org.nting.flare.java;");
                    lines.add(1, "");
                    Files.write(path, lines);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void renameDartFiles(Path pathToFiles) {
        Converter<String, String> fileNameConverter = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL);

        try (Stream<Path> dartFiles = Files.walk(pathToFiles).filter(path -> path.toString().endsWith(".dart"))) {
            dartFiles.forEach(path -> {
                File file = path.toFile();
                String name = file.getName().substring(0, file.getName().length() - 5);
                String javaFileName = fileNameConverter.convert(name) + ".java";
                logger.info(javaFileName);
                file.renameTo(new File(path.getParent().toFile(), javaFileName));
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }
}
