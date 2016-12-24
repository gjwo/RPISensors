package subsystems;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * RPISensors - subsystems
 * Created by MAWood on 24/12/2016.
 */
public class SystemLog implements RemoteLog
{

    public enum LogLevel
    {
        ERROR,
        WARNING,
        INFO,
        DEBUG_1,
        DEBUG_2
    }

    class LogEntry implements Serializable
    {
        public final String message;
        public final LogLevel level;
        public final Instant time;

        LogEntry(LogLevel level, String message)
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

    private static SystemLog log = null;
    private static String REMOTE_NAME = "Log";

    private final ArrayList<LogEntry> entries;

    private SystemLog()
    {
        entries = new ArrayList<>();
        Registry reg = null;
        try
        {
            reg = LocateRegistry.getRegistry();
            reg.rebind(REMOTE_NAME, UnicastRemoteObject.exportObject(this,0));
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    public static void log(LogLevel level, String message)
    {
        getLog().log(level, message);
    }

    public void addEntry(LogLevel level, String message)
    {
        entries.add(new LogEntry(level,message));
    }

    public void addEntry(LogEntry entry)
    {
        entries.add(entry);
    }

    private static SystemLog getLog()
    {
        if(log == null) log = new SystemLog();
        return log;
    }

    @Override
    public ArrayList<LogEntry> getEntries() throws RemoteException
    {
        return entries;
    }
}
