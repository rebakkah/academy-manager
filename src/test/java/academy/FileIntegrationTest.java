package academy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileIntegrationTest {

    @Test
    void load_validFile_returnsStudents_andSkipsBlankLines(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("students.txt");

        Files.write(file, List.of(
                "1;Ann;ann@example.com;Brussels",
                "",
                "2;Bob;bob@example.com;Ghent"
        ));

        List<Student> students = StudentFileLoader.load(file);

        assertEquals(
                List.of("Ann", "Bob"),
                students.stream().map(Student::name).toList());
    }

    @Test
    void load_wrongColumnCount_throwsIllegalArgument(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("students.txt");

        Files.write(file, List.of("1;Ann;ann@example.com"));

        assertThrows(IllegalArgumentException.class,
                () -> StudentFileLoader.load(file));
    }

    @Test
    void load_missingFile_throwsIOException(@TempDir Path dir) {
        assertThrows(IOException.class,
                () -> StudentFileLoader.load(dir.resolve("nope.txt")));
    }

    @Test
    void fileReportOutput_writesLinesThatCanBeReadBack(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out").resolve("report.txt");

        List<String> report = List.of(
                "Java | 0/1 participants",
                "Go to room B12."
        );

        new FileReportOutput(file).write(report);

        assertEquals(report, SimpleFiles.readLines(file));
    }
}