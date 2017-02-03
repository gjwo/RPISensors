package logging;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * RPISensors - subsystems
 * Created by MAWood on 24/12/2016.
 */
@SuppressWarnings("WeakerAccess")
public class LogEntry implements Serializable
{
	private static final long serialVersionUID = -24348546989956649L;
	public final String message;
    public final SystemLog.LogLevel level;
    public final Instant time;
    public final Class aClass;

    LogEntry(Class aClass,SystemLog.LogLevel level, String message)
    {
        this.level = level;
        this.message = message;
        this.aClass = aClass;
        time = Instant.now();
        
    }

    public String toString()
    {
        final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
        return formatter.format(time) + " -> ["+ level.getLevel() +"] " + this.aClass.getName() + " : " + message;
    }
    public String toExtendedString()
    {
        final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
        return formatter.format(time) + " -> "+ aClass.getName() +" " + level.name() + " "+ level.getLevel() +  " : " + message;
    }
}