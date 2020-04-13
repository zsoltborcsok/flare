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
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;

public class DartToJava {

    private static final String RELATIVE_PATH = "flare-java/src/main/generated/org/nting/flare/java";

    private static final Logger logger = LoggerFactory.getLogger(DartToJava.class);

    public static void main(String[] args) {
        // renameDartFiles(Paths.get(RELATIVE_PATH));
        // addPackageDefinition(Paths.get(RELATIVE_PATH));
        // removeImports(Paths.get(RELATIVE_PATH));
        // makeClassesPublic(Paths.get(RELATIVE_PATH));
        insertNewKeywords(Paths.get(RELATIVE_PATH));
    }

    private static void insertNewKeywords(Path pathToFiles) {
        Pattern pattern = Pattern.compile("\\s[A-Z]+[a-zA-Z_0-9]*\\(");
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String start = line.substring(0, matcher.start());
                if (start.endsWith("return") || start.endsWith("throw") || start.endsWith("=") || start.endsWith(",")) {
                    return start + " new" + line.substring(matcher.start());
                }
            }
            return line;
        }).collect(Collectors.toList()));
    }

    private static void makeClassesPublic(Path pathToFiles) {
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            if (line.startsWith("class") || line.startsWith("abstract class")) {
                return "public " + line;
            } else {
                return line;
            }
        }).collect(Collectors.toList()));
    }

    private static void removeImports(Path pathToFiles) {
        manipulateJavaFiles(pathToFiles, lines -> {
            lines = lines.stream().filter(line -> !(line.startsWith("import ") && line.endsWith(";"))).collect(Collectors.toList());
            while (2 < lines.size() && isNullOrEmpty(lines.get(2))) {
                lines.remove(2);
            }
            return lines;
        });
    }

    private static void addPackageDefinition(Path pathToFiles) {
        manipulateJavaFiles(pathToFiles, lines -> {
            lines.add(0, "package org.nting.flare.java;");
            lines.add(1, "");
            return lines;
        });
    }

    private static void manipulateJavaFiles(Path pathToFiles, Function<List<String>, List<String>> manipulation) {
        try (Stream<Path> javaFiles = Files.walk(pathToFiles).filter(path -> path.toString().endsWith(".java"))) {
            javaFiles.forEach(path -> {
                try {
                    List<String> lines = Files.readAllLines(path);
                    lines = manipulation.apply(lines);
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
