package subsystems;

/**
 * RPISensors - subsystems
 * Created by MAWood on 21/12/2016.
 */
public abstract class SubSystem extends Thread
{
    protected SubSystemState state;

    public SubSystem()
    {
        this.state = SubSystemState.IDLE;
    }

    public abstract void start();
    public abstract void shutdown();

    public final SubSystemState getCurrentState()
    {
        return state;
    }
}
