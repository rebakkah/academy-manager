package academy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ValidationSecurityTest {

    @ParameterizedTest
    @ValueSource(strings = {"A\nB", "A\rB", "A\tB", "A\u0000B"})
    void requireText_rejectsControlCharsInside(String input) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Validation.requireText(input, "name"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"\u0000", "\u001F", "\u0000\u0000"})
    void requireText_rejectsOnlyControlChars(String input) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Validation.requireText(input, "name"));
    }

    @ParameterizedTest
    @ValueSource(strings = {" Ann\n"})
    void requireText_trimsControlCharsAtEdge(String input) {
        String result = Validation.requireText(input, "name");
        Assertions.assertEquals("Ann", result);
    }

    @Test
    void student_rejectsNewlineInName() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Student(1, "Ann\nEvil", new ContactDetails("ann@example.com", "Brussels")));
    }

    @Test
    void contactDetails_rejectsNewlineInCity() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new ContactDetails("ann@example.com", "Brussels\nGhent"));
    }
}
