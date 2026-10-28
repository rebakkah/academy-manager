package academy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class SimpleFiles {

    private SimpleFiles() {
    }

    public static List<String> readLines(Path path) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader =
                     Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    public static void writeLines(Path path, List<String> lines) throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        Files.createDirectories(parent);

        try (BufferedWriter writer =
                     Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}