package academy;

import static academy.TestData.student;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class WorkshopTest {

    private static List<Integer> ids(Workshop workshop) {
        return workshop.getParticipants().stream()
                .map(Student::id)
                .toList();
    }

    @Test
    void constructor_capacityZero_isRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new ClassroomWorkshop(1, "Java", 0, "B12"));
    }

    @Test
    void register_firstStudent_succeeds() {
        Workshop workshop = new ClassroomWorkshop(10, "Java", 2, "B12");

        workshop.register(student(1, "Ann", "Brussels"));

        assertEquals(List.of(1), ids(workshop));
        assertEquals(1, workshop.availablePlaces());
    }

    @Test
    void register_sameStudentTwice_throwsAndListUnchanged() {
        Workshop workshop = new ClassroomWorkshop(10, "Java", 2, "B12");
        Student ann = student(1, "Ann", "Brussels");

        workshop.register(ann);

        assertThrows(IllegalStateException.class, () -> workshop.register(ann));

        assertEquals(List.of(1), ids(workshop));
    }

    @Test
    void register_lastPlaceAccepted_nextRejectedWithoutChangingList() {
        Workshop workshop = new ClassroomWorkshop(10, "Java", 2, "B12");

        workshop.register(student(1, "Ann", "Brussels"));
        workshop.register(student(2, "Bob", "Ghent"));

        assertEquals(0, workshop.availablePlaces());

        assertThrows(IllegalStateException.class,
                () -> workshop.register(student(3, "Cat", "Antwerp")));

        assertEquals(List.of(1, 2), ids(workshop));
    }

    @Test
    void register_duplicateIsCheckedBeforeCapacity() {
        Workshop workshop = new ClassroomWorkshop(10, "Java", 1, "B12");
        Student ann = student(1, "Ann", "Brussels");

        workshop.register(ann);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> workshop.register(ann));

        assertTrue(exception.getMessage().contains("already"));
    }

    @Test
    void register_nullStudent_isRejected() {
        Workshop workshop = new ClassroomWorkshop(10, "Java", 1, "B12");

        assertThrows(IllegalArgumentException.class,
                () -> workshop.register(null));
    }

    @Test
    void unregister_middleParticipant_removedSafely() {
        Workshop workshop = new ClassroomWorkshop(10, "Java", 3, "B12");

        workshop.register(student(1, "Ann", "Brussels"));
        workshop.register(student(2, "Bob", "Ghent"));
        workshop.register(student(3, "Cat", "Antwerp"));

        assertTrue(workshop.unregister(2));

        assertEquals(List.of(1, 3), ids(workshop));
    }

    @Test
    void unregister_absentStudent_returnsFalse() {
        Workshop workshop = new ClassroomWorkshop(10, "Java", 3, "B12");

        assertFalse(workshop.unregister(999));
    }

    @Test
    void unregister_freesPlace_andStudentCanRegisterAgain() {
        Workshop workshop = new ClassroomWorkshop(10, "Java", 2, "B12");
        Student ann = student(1, "Ann", "Brussels");

        workshop.register(ann);
        workshop.register(student(2, "Bob", "Ghent"));

        workshop.unregister(1);

        assertEquals(1, workshop.availablePlaces());

        workshop.register(ann);

        // Registration order changed: Ann is now last.
        assertEquals(List.of(2, 1), ids(workshop));
    }

    @Test
    void getParticipants_returnsCopy() {
        Workshop workshop = new ClassroomWorkshop(10, "Java", 2, "B12");

        assertThrows(UnsupportedOperationException.class,
                () -> workshop.getParticipants().add(student(1, "Ann", "Brussels")));
    }

    @Test
    void joiningInstructions_workThroughBaseType() {
        Workshop classroom = new ClassroomWorkshop(10, "Java", 2, "B12");
        Workshop online = new OnlineWorkshop(20, "JUnit", 3, "https://example.com/junit");

        assertEquals("Go to room B12.", classroom.joiningInstructions());
        assertEquals("Join at https://example.com/junit.", online.joiningInstructions());
    }
}