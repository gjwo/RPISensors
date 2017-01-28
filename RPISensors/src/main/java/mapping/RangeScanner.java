package mapping;

import dataTypes.TimestampedData1f;
import devices.motors.AngularPositioner;
import sensors.Implementations.VL53L0X.VL53L0X;
import sensors.interfaces.UpdateListener;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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
    private int scanRate;
    private final Thread thread;
    private volatile boolean interrupted;
    private volatile boolean finished;
    private final float resolution;
    private final int stepsPerRevolution;
    private volatile TimestampedData1f[] ranges;
    private long delaytime;
    private final ArrayList<UpdateListener> listeners;
    private final HashMap<Float,TimestampedData1f> rangeMap;
    private static final String REMOTE_NAME = "RangeScanner";

    /**
     * RangeScanner -   Constructor
     *
     * @param ap -   an angular positioner
     * @param r  -   a range scanner
     * @param s  -   Scanning rate in revolutions per minute
     */
    RangeScanner(AngularPositioner ap, VL53L0X r, int s)
    {
        this.angularPositioner = ap;
        this.ranger = r;
        ranger.registerInterest(this);
        this.scanRate = s;
        this.thread = new Thread(this, "Range Scanner");
        this.interrupted = false;
        this.finished = false;
        this.resolution = ap.angularPositionResolution();
        this.stepsPerRevolution = (int) (360f / resolution);
        this.ranges = new TimestampedData1f[stepsPerRevolution];
        this.delaytime = ((long) (60f / (float) (scanRate * stepsPerRevolution)) * 1000);
        this.listeners = new ArrayList<>();
        rangeMap = new HashMap<>(stepsPerRevolution);
        try
        {
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind(REMOTE_NAME, UnicastRemoteObject.exportObject(this,0));
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
    public void interrupt()
    {
        interrupted = true;
    }
    public boolean isFinished()
    {
        return finished;
    }
    @Override
    public void run()
    {
        while (!interrupted)
        {
            for (int i = 0; i < stepsPerRevolution; i++)
            {
                ranges[i] = ranger.getLatestRange();
                rangeMap.put(((float)i)*resolution,ranger.getLatestRange());
                try
                {
                    TimeUnit.MILLISECONDS.sleep(delaytime);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            updateData();
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
    public TimestampedData1f[] getRawRanges(){return ranges.clone();}

    public HashMap<Float,TimestampedData1f> getRangeMap() {return rangeMap;}

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

    }
}
