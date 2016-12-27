package subsystems;

/**
 * RPISensors - subsystems
 * Created by MAWood on 27/12/2016.
 */
public class TestingSubSystem extends SubSystem
{
    public TestingSubSystem()
    {
        super(SubSystemType.TESTING);
    }

    @Override
    public SubSystemState startup()
    {
        if(getSubSysState() != SubSystemState.IDLE) return getSubSysState();
        setSubSysState(SubSystemState.STARTING);
        setSubSysState(SubSystemState.RUNNING);
        return getSubSysState();
    }

    @Override
    public SubSystemState shutdown()
    {
        if(getSubSysState() != SubSystemState.RUNNING) return getSubSysState();
        setSubSysState(SubSystemState.STOPPING);
        setSubSysState(SubSystemState.IDLE);
        return getSubSysState();
    }
}
