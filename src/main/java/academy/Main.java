package academy;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class Main {

    public static void main(String[] args) {
        Academy academy = new Academy();

        try {
            for (Student student : StudentFileLoader.load(Path.of("data", "students.txt"))) {
                academy.addStudent(student);
            }
        } catch (IOException e) {
            System.err.println("Cannot read students file: " + e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid students file: " + e.getMessage());
            return;
        }

        academy.addWorkshop(new ClassroomWorkshop(
                10,
                "Java Basics",
                2,
                "B12"));

        academy.addWorkshop(new OnlineWorkshop(
                20,
                "JUnit Practice",
                3,
                "https://example.com/junit"));

        academy.register(1, 10);
        academy.register(2, 10);

        try {
            academy.register(3, 10);
            System.err.println("Expected full-workshop rejection, but registration succeeded");
            return;
        } catch (IllegalStateException e) {
            System.out.println("Expected rejection: " + e.getMessage());
        }

        if (!academy.unregister(2, 10)) {
            System.err.println("Expected to remove Paul, but he was not registered");
            return;
        }

        academy.register(3, 10);
        academy.register(1, 20);
        academy.register(4, 20);

        List<ReportOutput> outputs = List.of(
                new ConsoleReportOutput(),
                new FileReportOutput(Path.of("output", "report.txt"))
        );

        List<String> report = academy.buildReport();

        for (ReportOutput output : outputs) {
            try {
                output.write(report);
            } catch (IOException e) {
                System.err.println("Failed to write report: " + e.getMessage());
                return;
            }
        }

        System.out.println("Report written to output/report.txt");
    }
}
