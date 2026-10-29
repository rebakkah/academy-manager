package academy;

import java.io.IOException;
import java.util.List;

public interface ReportOutput {

    // The interface allows implementations that may fail due to I/O.
    void write(List<String> lines) throws IOException;
}