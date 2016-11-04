package devices.sensorImplementations.MPU9250;

import devices.dataTypes.TimestampedData1D;
import devices.sensors.Sensor;
import devices.sensors.interfaces.Thermometer;

public class MPU9250Thermometer extends Sensor<TimestampedData1D> implements Thermometer {

	public MPU9250Thermometer(int sampleRate, int sampleSize, MPU9250RegisterOperations ro) {
		super(sampleRate, sampleSize, ro);
		// TODO Auto-generated constructor stub
	}

	@Override
	public float getLatestTemperature() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getAvgTemperature() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getTemperature(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getThermometerReadingCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateThermometerData() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void calibrateThermometer() {
		// TODO Auto-generated method stub

	}

	@Override
	public void selfTestThermometer() {
		// TODO Auto-generated method stub

	}

}
