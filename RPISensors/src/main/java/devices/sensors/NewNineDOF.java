package devices.sensors;

import devices.dataTypes.TimestampedData3D;
import devices.sensorImplementations.MPU9250.MPU9250Magnetometer;

public abstract class NewNineDOF extends SensorPackage {
	Sensor<TimestampedData3D> mag = new MPU9250Magnetometer(1,1,null);
	NewNineDOF(int sampleRate) {
		super(sampleRate);
		// TODO Auto-generated constructor stub
	}

	@Override
	void updateData() {
		// TODO Auto-generated method stub

	}

}
