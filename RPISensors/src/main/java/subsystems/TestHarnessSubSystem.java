package subsystems;

/**
 * RPISensors - subsystems
 * Created by MAWood on 27/12/2016.
 */
@SuppressWarnings("WeakerAccess")
public abstract class TestHarnessSubSystem extends SubSystem
{
    private Runnable tester;
    private Thread thread;
    
    protected TestHarnessSubSystem()
    {
        super(SubSystemType.TESTING);
    }
    
    protected void setRunnable(Runnable tester)
    {
    	this.tester = tester;
    }

    @Override
    public SubSystemState startup()
    {
    	if(tester == null) return getSubSysState();
        if(getSubSysState() != SubSystemState.IDLE) return getSubSysState();
        setSubSysState(SubSystemState.STARTING);
        thread = new Thread(tester);
        thread.start();
        setSubSysState(SubSystemState.RUNNING);
        return getSubSysState();
    }

    @Override
    public SubSystemState shutdown()
    {
        if(getSubSysState() != SubSystemState.RUNNING) return getSubSysState();
        setSubSysState(SubSystemState.STOPPING);
        thread.interrupt();
        setSubSysState(SubSystemState.IDLE);
        return getSubSysState();
    }
}