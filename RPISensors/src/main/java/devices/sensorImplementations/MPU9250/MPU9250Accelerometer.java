package devices.sensorImplementations.MPU9250;

import devices.sensors.Sensor;
import devices.dataTypes.Data3D;
import devices.dataTypes.TimestampedData3D;
import devices.sensors.interfaces.Accelerometer;

public class MPU9250Accelerometer extends Sensor<TimestampedData3D,Data3D> implements Accelerometer {
	
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
        float x,y,z;
        short registers[];
        //roMPU.readByteRegister(Registers.ACCEL_XOUT_H, 6);  // Read again to trigger
 
        registers = ro.read16BitRegisters(Registers.ACCEL_XOUT_H,3);
        //System.out.println("Accelerometer " + xs + ", " + ys + ", " + zs);

        x = (float) ((float)registers[0]*accScale.getRes()); // transform from raw data to g
        y = (float) ((float)registers[1]*accScale.getRes()); // transform from raw data to g
        z = (float) ((float)registers[2]*accScale.getRes()); // transform from raw data to g

        x -= accBias[0];
        y -= accBias[1];
        z -= accBias[2];

        this.addValue(new TimestampedData3D(x,y,z));
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
