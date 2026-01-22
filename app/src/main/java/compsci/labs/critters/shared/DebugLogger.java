package compsci.labs.critters.shared;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DebugLogger {
    private static final String LOG_FILE = "critters_debug.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    public static synchronized void log(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            out.println("[" + timestamp + "] " + message);
            // Also print to console for convenience, or remove if strictly file only is desired.
            // User said "log to a file", usually implies "instead of console" or "in addition to".
            // I'll keep console for now as it's useful, but the file is the requirement.
            System.out.println("[" + timestamp + "] " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
