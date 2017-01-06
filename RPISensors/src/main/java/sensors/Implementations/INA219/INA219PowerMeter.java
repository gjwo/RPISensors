package sensors.Implementations.INA219;

import java.io.IOException;

import dataTypes.TimestampedData1f;
import devices.device.RegisterOperations;
import sensors.models.Sensor1D;

public class INA219PowerMeter extends Sensor1D
{
	private final RegisterOperations ro;
	private final INA219Configuration config;

	INA219PowerMeter(RegisterOperations ro, int sampleSize, INA219Configuration config)
	{
		super(sampleSize);
		this.ro = ro;
		this.config = config;
	}

	@Override
	public void updateData() throws IOException
	{
		int raw = ro.readShort(INA219Registers.POWER_MEASURE);
		this.addValue(new TimestampedData1f((float)raw/(float)config.getPowerDivider()));
	}
}
