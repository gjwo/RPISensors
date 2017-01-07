package sensors.models;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import sensors.interfaces.UpdateListener;

/**
 * Created by MAWood on 17/07/2016. modified by G.J.Wood 10/11/2016 
 */
public abstract class SensorPackage implements Runnable
{
    private final int sampleRate;
    private boolean paused;
    private ArrayList<UpdateListener> listeners;

    /**
     * SensorPackage		-   Constructor
     * @param sampleRate    - sample rate in samples per second
     */
    protected SensorPackage(int sampleRate)
    {
        this.sampleRate = sampleRate;
        this.paused = false;
        this.listeners = new ArrayList<>();
    }

    /**
     * pause		- Thread will stop processing data until resumed
     */
    public void pause() {paused = true;}

    /**
     * resume		- Thread will resume processing data
     */
    public void resume() {paused = false;}

    /**
     * run		- The main execution loop of the thread
     */

    @Override
    public void run()
    {
        long lastTime = 0;
        final long waitTime = TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS) / sampleRate;
        long now;
        boolean stop = false;
        while(!Thread.interrupted() && !stop)
        {
            if(!paused)
            {
                try
                {
                    now = System.nanoTime();
                    if( now-lastTime >= waitTime )
                    {
                    	lastTime = now;
                        updateData();
                        for(UpdateListener listener:listeners) listener.dataUpdated();
                    }
                    TimeUnit.NANOSECONDS.sleep(waitTime/10); //empirically this seems to deliver an optimum calculation frequency via navigate of 180+ Hz
                } catch (Exception interrupted)
                {	//close down signal
                	stop = true;
                }
            }
        }
    }

    /**
     * updateData		- To be implemented by extending classes, will update data based on implementation
     */
    protected abstract void updateData();

    /**
     * registerInterest		-   add a listener which will be informed when data is updated
     * @param listener      -   the Method to be called when the data changes
     */
    public void registerInterest(UpdateListener listener)
    {
        listeners.add(listener);
    }
}