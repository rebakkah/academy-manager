package academy;

import java.util.List;

public final class ConsoleReportOutput implements ReportOutput {

    @Override
    public void write(List<String> lines) {
        // Console printing does not throw IOException,
        // so this implementation may omit the throws clause.
        for (String line : lines) {
            System.out.println(line);
        }
    }
}