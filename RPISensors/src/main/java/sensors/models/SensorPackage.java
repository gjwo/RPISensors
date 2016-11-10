package sensors.models;

import java.util.ArrayList;
import sensors.interfaces.SensorUpdateListener;

/**
 * Created by MAWood on 17/07/2016. modified by G.J.Wood 10/11/2016 
 */
public abstract class SensorPackage implements Runnable
{
    private final int sampleRate;
    private boolean paused;
    private ArrayList<SensorUpdateListener> listeners;

    /**
     * SensorPackage		- Constructor
     * @param sampleRate
     */
    SensorPackage(int sampleRate)
    {
        this.sampleRate = sampleRate;

        paused = false;

        listeners = new ArrayList<>();
    }

    /**
     * pause		- Thread will stop processing data until unpaused
     */
    public void pause() {paused = true;}

    /**
     * unpause		- Thread will resume processing data
     */
    public void unpause() {paused = false;}

    /**
     * run		- The main execution loop of the thread
     */
    @Override
    public void run()
    {
        long lastTime;
        final long waitTime = 1000000000L / sampleRate;
        while(!Thread.interrupted())
        {
            if(!paused)
            {
                try
                {
                    lastTime = System.nanoTime();
                    updateData();
                    for(SensorUpdateListener listener:listeners) listener.dataUpdated();

                    while(System.nanoTime() - lastTime < waitTime);
                } catch (Exception ignored)
                {	//do nothing
                }
            }
        }
    }

    /**
     * updateData		- To be implemented by extending classes, will update data based on implementation
     */
    public abstract void updateData();

    /**
     * registerInterest		- add a listener which will be informed when data is updated
     * @param listener
     */
    public void registerInterest(SensorUpdateListener listener)
    {
        listeners.add(listener);
    }
}