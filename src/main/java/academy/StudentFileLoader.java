package academy;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class StudentFileLoader {

    private StudentFileLoader() {
    }

    public static List<Student> load(Path path) throws IOException {
        List<Student> students = new ArrayList<>();

        for (String line : SimpleFiles.readLines(path)) {
            if (line.isBlank()) {
                continue;
            }

            students.add(parseLine(line));
        }

        return students;
    }

    // Package-private: tests can call it directly.
    static Student parseLine(String line) {
        String[] fields = line.split(";", -1);

        if (fields.length != 4) {
            throw new IllegalArgumentException("Expected four columns: " + line);
        }

        int id = Integer.parseInt(fields[0].trim());

        // Constructors trim and validate name, email, city.
        return new Student(
                id,
                fields[1],
                new ContactDetails(fields[2], fields[3])
        );
    }
}