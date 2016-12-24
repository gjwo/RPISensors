package subsystems;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * RPISensors - subsystems
 * Created by MAWood on 24/12/2016.
 */
public class LogEntry implements Serializable
{
    public final String message;
    public final SystemLog.LogLevel level;
    public final Instant time;

    LogEntry(SystemLog.LogLevel level, String message)
    {
        this.level = level;
        this.message = message;
        time = Instant.now();
    }

    public String toString()
    {
        final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
        return formatter.format(time) + " -> "+ level.name() + " : " + message;
    }
}
