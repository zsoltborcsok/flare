import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.base.Strings;
import com.google.common.primitives.Booleans;
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
        Path pathToFiles = Paths.get(RELATIVE_PATH);
        // renameDartFiles(pathToFiles);
        // addPackageDefinition(pathToFiles);
        // removeImports(pathToFiles);
        // makeClassesPublic(pathToFiles);
        // insertNewKeywords(pathToFiles);
        // makeMethodsPublic(pathToFiles);
        // makeAbstractMethodsPublic(pathToFiles);
        // handleOverrideAnnotations(pathToFiles);
        // handleBools(pathToFiles);
        // handleCollectionIterations(pathToFiles);
        // addListImport(pathToFiles);
        // handleListCreation(pathToFiles);
        // replace(pathToFiles, "= <Object, Object>{}", "= new HashMap()");
        // addImport(pathToFiles, "new HashMap\\(", "import java.util.HashMap;");
        // handleConstDeclarations(pathToFiles);
        // addImports(pathToFiles, "org.nting.flare.java.maths", "AABB", "Mat2D", "TransformComponents", "Vec2D");
        // handleInstanceOfs(pathToFiles);
        // handleNotInstanceOfs(pathToFiles);
        // handleGetters(pathToFiles);
        // handleLambdaGetters(pathToFiles);
        // handleAbstractGetters(pathToFiles);
        // addImport(pathToFiles, "min\\(", "import static java.lang.Math.min;");
        // addImport(pathToFiles, "max\\(", "import static java.lang.Math.max;");
        // addImport(pathToFiles, "acos\\(", "import static java.lang.Math.acos;");
        // handleListSizes(pathToFiles);
        // replace(pathToFiles, ".isEmpty", ".isEmpty()");
        // handleIsNotEmpty(pathToFiles);
        // replace(pathToFiles, ".first", ".get(0)", line -> Pattern.compile("\\.first[a-zA-Z_0-9(]").matcher(line).find());
        // replace(pathToFiles, "dynamic", "Object", line -> Pattern.compile("(dynamic[a-zA-Z_0-9(])|([a-zA-Z_0-9]dynamic)").matcher(line).find());
        // replace(pathToFiles, "Uint8List", "byte[]"); // TODO: extra adjustments needed
        // replace(pathToFiles, "Float32List", "float[]", line -> Pattern.compile("Float32List[a-zA-Z_0-9(.]").matcher(line).find());
        // handleAssignOnlyIfTheAssignedToVariableIsNull(pathToFiles);
        // makeFieldsPublicOrPrivate(pathToFiles);
        // replace(pathToFiles, "double.maxFinite", "Double.MAX_VALUE");
        // replace(pathToFiles, " set ", " public void ", line -> Pattern.compile("\\s*(//|public)").matcher(line).find());
        // handleConditionalMemberAccess(pathToFiles);

        // var, operators (e.g. []), constructors, factories, clone, Future, await
    }

    private static void handleConditionalMemberAccess(Path pathToFiles) {
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            int indexOfTheOperator = line.indexOf("?.");
            if (0 <= indexOfTheOperator) {
                int from = line.substring(0, indexOfTheOperator).lastIndexOf(' ') + 1;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(line, 0, from);
                stringBuilder.append("Optional.ofNullable(");
                stringBuilder.append(line, from, indexOfTheOperator);
                stringBuilder.append(").ifPresent(v -> v");
                stringBuilder.append(line, indexOfTheOperator + 1, line.length());
                if (line.endsWith(";")) {
                    stringBuilder.insert(stringBuilder.length() - 1, ")");
                } // TODO closing bracket maybe missing
                line = stringBuilder.toString();
            }
            return line;
        }).collect(Collectors.toList()));
    }

    private static void makeFieldsPublicOrPrivate(Path pathToFiles) {
        Pattern pattern = Pattern.compile("^\\s*(final )?[a-zA-Z_0-9<>\\[\\]]+\\s[a-z_][a-zA-Z_0-9]*(;|\\s=\\s.*)");
        Pattern privatePattern = Pattern.compile("^\\s*(final )?[a-zA-Z_0-9<>\\[\\]]+\\s_.*;");
        manipulateJavaFiles(pathToFiles, lines -> {
            int contexts = 0;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.contains("{")) {
                    contexts++;
                }
                if (line.contains("}")) {
                    contexts--;
                }
                if (contexts == 1) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String lineText = line.replaceAll("^[ ]+", "");
                        int leadingSpaces = line.length() - lineText.length();
                        StringBuilder stringBuilder = new StringBuilder(line.substring(0, leadingSpaces));
                        if (privatePattern.matcher(line).matches()) {
                            stringBuilder.append("private ");
                        } else {
                            stringBuilder.append("public ");
                        }
                        stringBuilder.append(line.substring(leadingSpaces));
                        lines.set(i, stringBuilder.toString());
                    }
                }
            }
            return lines;
        });
    }

    private static void handleAssignOnlyIfTheAssignedToVariableIsNull(Path pathToFiles) {
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            int indexOfTheOperator = line.indexOf("??=");
            if (0 <= indexOfTheOperator) {
                String assignTo = line.substring(0, indexOfTheOperator);
                String variable = assignTo.trim();
                line = assignTo + "= " + variable + " != null ? " + variable + " :" +
                        line.substring(indexOfTheOperator + 3);
            }
            return line;
        }).collect(Collectors.toList()));
    }

    private static void replace(Path pathToFiles, String target, String replacement, Function<String, Boolean> exclude) {
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            if (line.contains(target) && !exclude.apply(line)) {
                line = line.replace(target, replacement);
            }
            return line;
        }).collect(Collectors.toList()));
    }

    private static void handleIsNotEmpty(Path pathToFiles) {
        Pattern pattern = Pattern.compile("\\s[a-zA-Z_0-9]+\\.isNotEmpty");
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                StringBuilder lineString = new StringBuilder(line.replace(".isNotEmpty", ".isEmpty()"));
                lineString.insert(matcher.start() + 1, "!");
                line = lineString.toString();
            }
            return line;
        }).collect(Collectors.toList()));
    }

    private static void replace(Path pathToFiles, String target, String replacement) {
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> line.replace(target, replacement))
                .collect(Collectors.toList()));
    }

    private static void handleListSizes(Path pathToFiles) {
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            if (line.contains(".length") && !Pattern.compile("\\.length[(a-zA-Z]").matcher(line).find()) {
                line = line.replace(".length", ".size()");
            }
            return line;
        }).collect(Collectors.toList()));
    }

    private static void addImport(Path pathToFiles, String regex, String importText) {
        Pattern pattern = Pattern.compile(regex);
        manipulateJavaFiles(pathToFiles, lines -> {
            if (lines.stream().anyMatch(line -> pattern.matcher(line).find())) {
                lines.add(2, importText);
                if (!Strings.isNullOrEmpty(lines.get(3)) && !lines.get(3).contains("import")) {
                    lines.add(3, "");
                }
            }
            return lines;
        });
    }

    private static void handleAbstractGetters(Path pathToFiles) {
        Pattern pattern = Pattern.compile(" get [^=>]*;");
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String lineText = line.replaceAll("^[ ]+", "");
                int leadingSpaces = line.length() - lineText.length();
                line = line.substring(0, leadingSpaces) + "public abstract " + lineText.replace(" get ", " ")
                        .replace(";", "();");
            }
            return line;
        }).collect(Collectors.toList()));
    }

    private static void handleLambdaGetters(Path pathToFiles) {
        Pattern pattern = Pattern.compile(" get .* =>.*;");
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String lineText = line.replaceAll("^[ ]+", "");
                int leadingSpaces = line.length() - lineText.length();
                line = line.substring(0, leadingSpaces) + "public " + lineText.replace(" get ", " ")
                        .replace(" =>", "() { return").replace(";", "; }");
            }
            return line;
        }).collect(Collectors.toList()));
    }

    private static void handleGetters(Path pathToFiles) {
        Pattern pattern = Pattern.compile(" get .* \\{");
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String lineText = line.replaceAll("^[ ]+", "");
                int leadingSpaces = line.length() - lineText.length();
                line = line.substring(0, leadingSpaces) + "public " + lineText.replace(" get ", " ").replace(" {", "() {");
            }
            return line;
        }).collect(Collectors.toList()));
    }

    private static void handleInstanceOfs(Path pathToFiles) {
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            if (line.contains(" is ") && !line.trim().startsWith("//")) {
                line = line.replace(" is ", " instanceof ");
            }
            return line;
        }).collect(Collectors.toList()));
    }

    private static void handleNotInstanceOfs(Path pathToFiles) {
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            if (line.contains(" is! ") && !line.trim().startsWith("//")) {
                line = line.replace(" is! ", " instanceof ");
            } // TODO surround with !(...)
            return line;
        }).collect(Collectors.toList()));
    }

    private static void addImports(Path pathToFiles, String packageName, String... classNames) {
        manipulateJavaFiles(pathToFiles, lines -> {
            boolean[] classUsages = new boolean[classNames.length];
            lines.stream().peek(line -> {
                for (int i = 0; i < classNames.length; i++) {
                    if (line.contains(classNames[i])) {
                        classUsages[i] = true;
                    }
                }
            }).collect(Collectors.toList());

            if (Booleans.asList(classUsages).stream().anyMatch(b -> b)) {
                lines.add(2, "");
            }
            for (int i = classUsages.length - 1; 0 <= i; i--) {
                if (classUsages[i]) {
                    lines.add(2, "import " + packageName + "." + classNames[i] + ";");
                }
            }
            return lines;
        });
    }

    private static void handleConstDeclarations(Path pathToFiles) {
        manipulateJavaFiles(pathToFiles, lines -> {
            lines = lines.stream().map(line -> {
                if (line.contains("static const ")) {
                    line = line.replace("static const ", "public static final ");
                } else if (line.contains("const ")) {
                    line = line.replace("const ", "final ");
                }
                return line;
            }).collect(Collectors.toList());
            return lines;
        });
    }

    private static void handleListCreation(Path pathToFiles) {
        manipulateJavaFiles(pathToFiles, lines -> {
            boolean[] hasArrayListUsage = {false};
            lines = lines.stream().map(line -> {
                if (line.contains("= List<")) {
                    hasArrayListUsage[0] = true;
                    line = line.replace("= List<", "= new ArrayList<");
                } else if (line.contains("= []")) {
                    hasArrayListUsage[0] = true;
                    line = line.replace("= []", "= new ArrayList<>()");
                } else if (line.contains("= <") && line.endsWith("[];")) {
                    hasArrayListUsage[0] = true;
                    line = line.replace("= <", "= new ArrayList<").replace("[]", "()");
                }
                return line;
            }).collect(Collectors.toList());

            if (hasArrayListUsage[0]) {
                lines.add(2, "import java.util.ArrayList;");
            }
            return lines;
        });
    }

    private static void addListImport(Path pathToFiles) {
        manipulateJavaFiles(pathToFiles, lines -> {
            if (lines.stream().anyMatch(line -> line.contains("List<"))) {
                lines.add(2, "import java.util.List;");
                lines.add(3, "");
            }
            return lines;
        });
    }

    private static void handleCollectionIterations(Path pathToFiles) {
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            if (line.contains("for ")) {
                return line.replace(" in ", " : ");
            }
            return line;
        }).collect(Collectors.toList()));
    }

    private static void handleBools(Path pathToFiles) {
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            if (line.contains("bool")) {
                line = line.replace("bool", "boolean");
            }
            if (line.contains("Bool") && !line.contains("Boolean")) {
                line = line.replace("Bool", "Boolean");
            }
            return line;
        }).collect(Collectors.toList()));
    }

    private static void handleOverrideAnnotations(Path pathToFiles) {
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            if (line.contains("@override")) {
                return line.replace("@override", "@Override");
            }
            return line;
        }).collect(Collectors.toList()));
    }

    private static void makeAbstractMethodsPublic(Path pathToFiles) {
        Pattern pattern = Pattern.compile("^\\s*[a-zA-Z_0-9]+\\s[a-z]+[a-zA-Z_0-9]*\\(.*\\);");
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find() && !line.contains("return")) {
                String lineText = line.replaceAll("^[ ]+", "");
                int leadingSpaces = line.length() - lineText.length();
                return line.substring(0, leadingSpaces) + "public abstract " + line.substring(leadingSpaces);
            }
            return line;
        }).collect(Collectors.toList()));
    }

    private static void makeMethodsPublic(Path pathToFiles) {
        Pattern pattern = Pattern.compile("\\s[a-z_0-9]+[a-zA-Z_0-9]*\\(.*\\)\\s\\{");
        manipulateJavaFiles(pathToFiles, lines -> lines.stream().map(line -> {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String lineText = line.replaceAll("^[ ]+", "");
                if (!lineText.startsWith("set") && !lineText.startsWith("//") && !lineText.contains(":")) {
                    int leadingSpaces = line.length() - lineText.length();
                    return line.substring(0, leadingSpaces) + "public " + line.substring(leadingSpaces);
                }
            }
            return line;
        }).collect(Collectors.toList()));
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
