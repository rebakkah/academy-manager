package academy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StudentFileLoaderTest {

    @Test
    void stripsUtf8Bom(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("students.txt");
        Files.writeString(file, "\uFEFF1;Ann;ann@example.com;Brussels\n");

        List<Student> students = StudentFileLoader.load(file);

        assertEquals(1, students.get(0).id());
    }

    @Test
    void errorNamesTheBadLine(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("students.txt");
        Files.write(file, List.of("1;Ann;ann@example.com;Brussels", "x;Bob;bob@example.com;Ghent"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> StudentFileLoader.load(file));

        assertTrue(e.getMessage().startsWith("line 2:"), e.getMessage());
    }
}
