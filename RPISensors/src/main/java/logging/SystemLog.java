package logging;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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
        getLog().addEntry(level, message);
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
}
