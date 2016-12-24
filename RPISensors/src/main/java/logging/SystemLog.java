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
	/*
	 * debugLevelTester settings
	 * 0	-	No diagnostic prints
	 * 1	-	User instructions, substantive output
	 * 2	-	Normal user output, progress etc
	 * 3	-	Main class methods entry & exit
	 * 4	-	Internal methods entry and exit
	 * 5	-	variable changes
	 * 6	-	Device/Sensor register summaries
	 * 7	-
	 * 8	-	Loop internal variables
	 * 9	-	Hardware writes
	 */

    public enum LogLevel
    {
    	USER_INSTRUCTION,
    	USER_INFORMATION,
    	TRACE_MAJOR_STATES,
    	TRACE_INTERFACE_METHODS,
    	TRACE_INTERNAL_METHODS,
    	TRACE_VARIABLES,
    	TRACE_REGISTER_SUMMARIES,
    	TRACE_LOOPS,
    	TRACE_HW_WRITES,
    	TRACE_HW_EVENTS,
        ERROR,
        WARNING,
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
