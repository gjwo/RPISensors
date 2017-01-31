package mapping;

import dataTypes.TimestampedData1f;
import dataTypes.TimestampedData2f;
import devices.motors.AngularPositioner;
import logging.SystemLog;
import main.Main;
import sensors.Implementations.VL53L0X.VL53L0X;
import sensors.interfaces.UpdateListener;
import subsystems.SubSystem;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * RangeScanner -   This class combines a stepper motor and a ranger to generate a 360 degree view
 *                  of the surrounding environment
 *
 * Created by GJWood on 26/01/2017.
 */
public class RangeScanner implements Runnable, RemoteRangeScanner,UpdateListener
{
    private final AngularPositioner angularPositioner;
    private final VL53L0X ranger;
    private final Thread thread;
    private volatile boolean interrupted;
    private volatile boolean finished;
    private final float resolution;
    private final int stepsPerRevolution;
    private final int rangesPerRevolution;
    private volatile TimestampedData2f[] ranges;
    private long delaytime; //in milliseconds;
    private final ArrayList<UpdateListener> listeners;
    private static final String REMOTE_NAME = "RangeScanner";
    private Instant lastUpdated;

    /**
     * RangeScanner -   Constructor
     *
     * @param angularPositioner -   an angular positioner
     * @param ranger            -   a range scanner
     * @param scanRPM           -   Scanning rate in revolutions per minute
     */
    RangeScanner(AngularPositioner angularPositioner, VL53L0X ranger, int scanRPM)
    {
        this.angularPositioner = angularPositioner;
        this.ranger = ranger;
        this.ranger.registerInterest(this);
        this.thread = new Thread(this, "Range Scanner");
        this.interrupted = false;
        this.finished = false;
        this.resolution = angularPositioner.angularPositionResolution();
        int rangesPerSec = 1000/ranger.getRangingTimeBudget(); // rounds down
        this.rangesPerRevolution = (60/scanRPM)*rangesPerSec;
        this.stepsPerRevolution = (int) (360f / resolution);
        if (stepsPerRevolution<rangesPerRevolution)
            SystemLog.log(SubSystem.SubSystemType.MAPPING,SystemLog.LogLevel.ERROR,"positioner resolution too low");
        this.ranges = new TimestampedData2f[rangesPerRevolution];
        this.delaytime = ((long) ranger.getRangingTimeBudget()); //Milliseconds
        this.listeners = new ArrayList<>();
        this.lastUpdated = Main.getMain().getClock().instant();
        try
        {
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind(REMOTE_NAME, UnicastRemoteObject.exportObject(this,0));
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
        SystemLog.log(SubSystem.SubSystemType.MAPPING,SystemLog.LogLevel.TRACE_MAJOR_STATES,"RangeScanner initialised");
        thread.start();
    }
    public void interrupt()
    {
        interrupted = true;
    }
    public boolean isFinished() {return finished;}
    public int getStepsPerRevolution() {return stepsPerRevolution;}
    public Instant lastUpdated() {return lastUpdated;}
    @Override
    public void run()
    {
        SystemLog.log(SubSystem.SubSystemType.MAPPING,SystemLog.LogLevel.TRACE_MAJOR_STATES,"RangeScanner running");
        float[] angles = new float[rangesPerRevolution];
        float angle = 360f / rangesPerRevolution;
        for( int i = 0; i<rangesPerRevolution;i++)
        {
            angles[i] =  i* angle;
        }
        while (!interrupted)
        {
            SystemLog.log(SubSystem.SubSystemType.MAPPING,SystemLog.LogLevel.TRACE_LOOPS,"RangeScanner while at "+ lastUpdated.toString());
            for (int i = 0; i < rangesPerRevolution; i++)
            {
                ranges[i] = new TimestampedData2f(ranger.getLatestRange().getX(),angles[i], ranger.getLatestRange().getInstant());
                try
                {
                    TimeUnit.MILLISECONDS.sleep(delaytime);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                //move positioner
                angularPositioner.setAngularPosition(angles[i]);
            }
            lastUpdated = Main.getMain().getClock().instant();


            //updateData();
        }
        //tidy up
        finished = true;
    }

    /**
     * registerInterest		-   add a listener which will be informed when data is updated
     *
     * @param listener -   the Method to be called when the data changes
     */
    public void registerInterest(UpdateListener listener)
    {
        listeners.add(listener);
    }

    private void updateData()
    {
        for (UpdateListener ul: listeners) {ul.dataUpdated();}
    }

    /**
     * getRanges    -   get a set of timestamped range data
     * @return      -   the latest set of range data (360 sweep after initial scan
     */
    public TimestampedData2f[] getRawRanges(){return ranges.clone();}

    public void unbind()
    {
        try
        {
            Registry reg = LocateRegistry.getRegistry();
            reg.unbind(REMOTE_NAME);
        } catch (RemoteException | NotBoundException e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void dataUpdated()
    {
        SystemLog.log(SubSystem.SubSystemType.MAPPING,SystemLog.LogLevel.ERROR,"Not Implemented");
    }
}
