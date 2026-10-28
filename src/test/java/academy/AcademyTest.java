package academy;

import static academy.TestData.student;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AcademyTest {

    private Academy academy;
    private Workshop workshop;

    @BeforeEach
    void setUp() {
        academy = new Academy();
        workshop = new ClassroomWorkshop(10, "Java", 2, "B12");

        academy.addStudent(student(1, "Ann", "Brussels"));
        academy.addWorkshop(workshop);
    }

    @Test
    void addStudent_duplicateId_isRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> academy.addStudent(student(1, "Other", "Ghent")));
    }

    @Test
    void addWorkshop_duplicateId_isRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> academy.addWorkshop(
                        new OnlineWorkshop(10, "Other", 1, "https://example.com/other")));
    }

    @Test
    void find_unknownIds_returnEmptyOptional() {
        assertTrue(academy.findStudent(999).isEmpty());
        assertTrue(academy.findWorkshop(999).isEmpty());
    }

    @Test
    void register_knownIds_addsParticipant() {
        academy.register(1, 10);

        assertEquals(1, workshop.getParticipants().size());
    }

    @Test
    void register_unknownIds_throw() {
        assertThrows(IllegalArgumentException.class,
                () -> academy.register(999, 10));

        assertThrows(IllegalArgumentException.class,
                () -> academy.register(1, 999));
    }

    @Test
    void unregister_unknownIds_throw() {
        assertThrows(IllegalArgumentException.class,
                () -> academy.unregister(999, 10));

        assertThrows(IllegalArgumentException.class,
                () -> academy.unregister(1, 999));
    }

    @Test
    void unregister_registeredStudent_returnsTrueAndFreesPlace() {
        academy.register(1, 10);

        assertTrue(academy.unregister(1, 10));
        assertEquals(2, workshop.availablePlaces());
    }

    @Test
    void unregister_keepsStudentInAcademy() {
        academy.register(1, 10);

        academy.unregister(1, 10);

        assertTrue(academy.findStudent(1).isPresent());
    }

    @Test
    void unregister_notRegistered_returnsFalse() {
        assertFalse(academy.unregister(1, 10));
        assertTrue(academy.findStudent(1).isPresent());
    }
}