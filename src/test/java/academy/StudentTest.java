package academy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class StudentTest {

    private static final ContactDetails CONTACT =
            new ContactDetails("ann@example.com", "Brussels");

    @Test
    void constructor_blankName_isRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Student(1, "   ", CONTACT));
    }

    @Test
    void constructor_nonPositiveId_isRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Student(0, "Ann", CONTACT));
    }

    @Test
    void constructor_nullContact_isRejectedWithIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> new Student(1, "Ann", null));
    }

    @Test
    void constructor_trimsName() {
        assertEquals("Ann", new Student(1, "  Ann ", CONTACT).name());
    }

    @Test
    void contactDetails_blankCity_isRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new ContactDetails("a@b.c", " "));
    }
}