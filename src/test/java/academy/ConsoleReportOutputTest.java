package academy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConsoleReportOutputTest {
    @Test
    void printsEachLineOnItsOwnLine() {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true));
        try {
            new ConsoleReportOutput().write(List.of("a", "", "b"));
        } finally {
            System.setOut(original);
        }
        assertEquals(String.join(System.lineSeparator(), "a", "", "b") + System.lineSeparator(),
                buffer.toString());
    }
}
