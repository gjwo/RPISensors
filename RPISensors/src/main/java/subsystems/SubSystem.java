package subsystems;

import logging.SystemLog;

/**
 * RPISensors - subsystems
 * Created by MAWood on 21/12/2016.
 */
public abstract class SubSystem extends Thread
{
	public enum SubSystemType
	{
		INSTRUMENTS,
		DRIVE_ASSEMBLY,
		LOGGING,
		SUBSYSTEM_MANAGER,
		TELEMETRY,
		TESTING,
		DEVICES
	}

	private final SubSystemType type;
    private SubSystemState state;

	protected void setSubSysState(SubSystemState state)
	{
		SystemLog.log(type,SystemLog.LogLevel.TRACE_MAJOR_STATES, type.name() + " | " + this.state.name() +  "->"+state.name());
		this.state = state;
	}

	protected SubSystem(SubSystemType subSystemType)
    {
		this.type = subSystemType;
        this.state = SubSystemState.IDLE;
    }

    public abstract SubSystemState startup();
    public abstract SubSystemState shutdown();

    public final SubSystemState getSubSysState()
    {
        return this.state;
    }
}