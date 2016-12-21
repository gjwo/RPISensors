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

    public abstract SubSystemState startup();
    public abstract SubSystemState shutdown();

    public final SubSystemState getCurrentState()
    {
        return this.state;
    }
}
