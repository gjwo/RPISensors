package devices.sensorImplementations.MPU9250;

import devices.sensors.Sensor;
import devices.dataTypes.TimestampedData3D;
import devices.sensors.interfaces.Accelerometer;

public class MPU9250Accelerometer extends Sensor implements Accelerometer {
	
	MPU9250Accelerometer(int sampleRate, int sampleSize, MPU9250RegisterOperations ro)
	{
		super(sampleSize, sampleSize, ro);
	}

	@Override
	public TimestampedData3D getLatestAcceleration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimestampedData3D getAvgAcceleration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimestampedData3D getAcceleration(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getAccelerometerReadingCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateAccelerometerData() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void calibrateAccelerometer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selfTestAccelerometer() {
		// TODO Auto-generated method stub
		
	}

}
