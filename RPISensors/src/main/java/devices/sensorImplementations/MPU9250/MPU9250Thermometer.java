package devices.sensorImplementations.MPU9250;

import devices.dataTypes.Data1D;
import devices.dataTypes.TimestampedData1D;
import devices.sensors.Sensor;
import devices.sensors.interfaces.Thermometer;

public class MPU9250Thermometer extends Sensor<TimestampedData1D,Data1D> implements Thermometer {
private MPU9250RegisterOperations roMPU;
	public MPU9250Thermometer(int sampleRate, int sampleSize, MPU9250RegisterOperations ro) {
		super(sampleRate, sampleSize, ro);
		roMPU = ro;
		// TODO Auto-generated constructor stub
	}

	@Override
	public float getLatestTemperature() {
		return getLatestValue().getX();
	}

	@Override
	public float getAvgTemperature() {
		return getAvgValue().getX();
	}

	@Override
	public float getTemperature(int i) {
		return getValue(i).getX();
	}

	@Override
	public int getThermometerReadingCount() {
		return getReadingCount();
	}

	@Override
	public void updateThermometerData() throws Exception {
    	short[] temperature = roMPU.read16BitRegisters(Registers.TEMP_OUT_H,1);
    	temperature = roMPU.read16BitRegisters(Registers.TEMP_OUT_H,1);
    	addValue(new TimestampedData1D((float)temperature[0]));
	}

	@Override
	public void calibrateThermometer() {
		// No Calibration required
	}

	@Override
	public void selfTestThermometer() {
		// No Self Test available
	}

}
