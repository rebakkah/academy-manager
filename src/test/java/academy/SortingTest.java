package academy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class SortingTest {
    @Test
    void searchSortsNamesIgnoringCase() {
        Academy academy = new Academy();
        academy.addStudent(TestData.student(1, "Zed", "Ghent"));
        academy.addStudent(TestData.student(2, "alice", "Ghent"));
        academy.addStudent(TestData.student(3, "Bob", "Ghent"));

        List<String> names = academy.searchStudents("").stream().map(Student::name).toList();

        assertEquals(List.of("alice", "Bob", "Zed"), names);
    }
}
