package academy;

public final class Main {

    public static void main(String[] args) {
        Academy academy = new Academy();

        // For now students are created in code.
        // File loading comes in step 6.
        academy.addStudent(new Student(
                1,
                "Chitrakalpa",
                new ContactDetails("chitrakalpa@example.com", "Brussels")));

        academy.addStudent(new Student(
                2,
                "Paul",
                new ContactDetails("paul@example.com", "Ghent")));

        academy.addStudent(new Student(
                3,
                "Gayatri",
                new ContactDetails("gayatri@example.com", "Brussels")));

        academy.addStudent(new Student(
                4,
                "Oleh",
                new ContactDetails("oleh@example.com", "Antwerp")));

        // Creation order matters because buildReport() uses LinkedHashMap.
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

        academy.unregister(2, 10);
        academy.register(3, 10);

        academy.register(1, 20);
        academy.register(4, 20);

        // Calling through the concrete type means no checked IOException here.
        new ConsoleReportOutput().write(academy.buildReport());

        System.out.println();
        System.out.println("JUnit by city: " + academy.participantCountsByCity(20));
        System.out.println("All cities: " + academy.allCities());
        System.out.println("Search 'kal': "
                + academy.searchStudents("kal").stream()
                .map(Student::name)
                .toList());
    }
}