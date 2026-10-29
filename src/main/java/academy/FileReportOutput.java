package academy;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class FileReportOutput implements ReportOutput {

    private final Path path;

    public FileReportOutput(Path path) {
        this.path = Validation.requireNonNull(path, "path");
    }

    @Override
    public void write(List<String> lines) throws IOException {
        // Do not hide IOException here.
        // Main/application boundary decides what to tell the user.
        SimpleFiles.writeLines(path, lines);
    }
}