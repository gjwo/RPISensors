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
import java.util.Iterator;

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


    private static SystemLog log = null;
    private static String REMOTE_NAME = "Log";

    private final ArrayList<LogEntry> entries;
    private final ArrayList<LogDisplayer> trackers;

    private SystemLog()
    {
        entries = new ArrayList<>();
        trackers = new ArrayList<>();
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
        getLog().addEntry(level, message);
    }

    public void addEntry(LogLevel level, String message)
    {
        entries.add(new LogEntry(level,message));
    }

    public void addEntry(LogEntry entry)
    {
        entries.add(entry);
        Iterator<LogDisplayer> iterator =  trackers.iterator();
        while(iterator.hasNext())
        {
            try
            {
                iterator.next().showEntry(entry.toString());
            } catch (RemoteException e)
            {
                iterator.remove();
            }
        }
        for(LogDisplayer tracker:trackers)
        {
            try
            {
                tracker.showEntry(entry.toString());
            } catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
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

    @Override
    public LogEntry getEntry(int index) throws RemoteException
    {
        return entries.get(index);
    }

    @Override
    public int getEntryCount() throws RemoteException
    {
        return entries.size();
    }

    @Override
    public void registerInterest(LogDisplayer displayer) throws RemoteException
    {
        trackers.add(displayer);
    }
}
