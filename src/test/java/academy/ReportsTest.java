package academy;

import static academy.TestData.student;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.TreeMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportsTest {

    private Academy academy;

    @BeforeEach
    void setUp() {
        academy = new Academy();
    }

    private static List<String> names(List<Student> students) {
        return students.stream()
                .map(Student::name)
                .toList();
    }

    private static List<Integer> ids(List<Student> students) {
        return students.stream()
                .map(Student::id)
                .toList();
    }

    // ---- search ----

    @Test
    void search_mixedCaseSubstring_works() {
        academy.addStudent(student(1, "Chitrakalpa", "Brussels"));
        academy.addStudent(student(2, "Paul", "Ghent"));

        assertEquals(List.of("Chitrakalpa"), names(academy.searchStudents("KAL")));
    }

    @Test
    void search_noMatch_returnsEmptyList() {
        academy.addStudent(student(1, "Ann", "Brussels"));

        assertTrue(academy.searchStudents("zzz").isEmpty());
    }

    @Test
    void search_blankQuery_returnsAllSortedByName() {
        academy.addStudent(student(2, "Paul", "Ghent"));
        academy.addStudent(student(1, "Anna", "Brussels"));

        assertEquals(List.of("Anna", "Paul"), names(academy.searchStudents("   ")));
    }

    @Test
    void search_equalNames_keepsBothSortedById() {
        academy.addStudent(student(2, "Anna", "Brussels"));
        academy.addStudent(student(1, "Anna", "Ghent"));

        assertEquals(List.of(1, 2), ids(academy.searchStudents("anna")));
    }

    @Test
    void search_null_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> academy.searchStudents(null));
    }

    // ---- cities, names, counts ----

    @Test
    void allCities_duplicatesOnce_alphabetical() {
        academy.addStudent(student(1, "A", "Brussels"));
        academy.addStudent(student(2, "B", "Ghent"));
        academy.addStudent(student(3, "C", "Brussels"));
        academy.addStudent(student(4, "D", "Antwerp"));

        assertIterableEquals(
                List.of("Antwerp", "Brussels", "Ghent"),
                academy.allCities());
    }

    @Test
    void participantNames_sortedByName() {
        academy.addStudent(student(1, "Zoe", "Brussels"));
        academy.addStudent(student(2, "Amy", "Ghent"));
        academy.addWorkshop(new ClassroomWorkshop(10, "Java", 2, "B12"));

        academy.register(1, 10);
        academy.register(2, 10);

        assertEquals(List.of("Amy", "Zoe"), academy.participantNames(10));
    }

    @Test
    void participantCountsByCity_alphabeticalAndCorrect() {
        academy.addStudent(student(4, "Oleh", "Antwerp"));
        academy.addStudent(student(1, "Chitrakalpa", "Brussels"));
        academy.addStudent(student(3, "Gayatri", "Brussels"));
        academy.addWorkshop(new ClassroomWorkshop(10, "Java", 3, "B12"));

        academy.register(4, 10);
        academy.register(1, 10);
        academy.register(3, 10);

        TreeMap<String, Long> counts = academy.participantCountsByCity(10);

        assertIterableEquals(List.of("Antwerp", "Brussels"), counts.keySet());
        assertEquals(1L, counts.get("Antwerp").longValue());
        assertEquals(2L, counts.get("Brussels").longValue());
    }

    @Test
    void unknownWorkshop_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> academy.participantNames(999));

        assertThrows(IllegalArgumentException.class,
                () -> academy.participantCountsByCity(999));
    }

    @Test
    void noStudents_emptyResults() {
        assertTrue(academy.searchStudents("").isEmpty());
        assertTrue(academy.allCities().isEmpty());
    }

    // ---- report ----

    @Test
    void buildReport_emptyWorkshop_isIncluded() {
        academy.addWorkshop(new ClassroomWorkshop(10, "Empty", 2, "A1"));

        assertEquals(
                List.of("Empty | 0/2 participants", "Go to room A1."),
                academy.buildReport());
    }

    @Test
    void buildReport_sortsNames_butKeepsRegistrationOrder() {
        academy.addStudent(student(1, "Zoe", "Brussels"));
        academy.addStudent(student(2, "Amy", "Ghent"));
        academy.addStudent(student(3, "Bob", "Antwerp"));

        Workshop workshop = new ClassroomWorkshop(10, "Java", 3, "B12");
        academy.addWorkshop(workshop);

        academy.register(1, 10);
        academy.register(2, 10);
        academy.register(3, 10);

        List<String> report = academy.buildReport();

        assertEquals(List.of(
                "Java | 3/3 participants",
                "Go to room B12.",
                "- Amy",
                "- Bob",
                "- Zoe"), report);

        // The report is sorted, but stored registration order is untouched.
        assertEquals(List.of(1, 2, 3), ids(workshop.getParticipants()));
    }

    @Test
    void buildReport_blankLineBetweenSectionsOnly() {
        academy.addWorkshop(new ClassroomWorkshop(10, "First", 1, "A1"));
        academy.addWorkshop(new OnlineWorkshop(20, "Second", 1, "https://example.com/second"));

        assertEquals(List.of(
                "First | 0/1 participants",
                "Go to room A1.",
                "",
                "Second | 0/1 participants",
                "Join at https://example.com/second."), academy.buildReport());
    }
}