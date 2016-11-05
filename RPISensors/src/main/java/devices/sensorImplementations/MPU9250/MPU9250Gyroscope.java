package devices.sensorImplementations.MPU9250;

import java.io.IOException;
import java.util.Arrays;

import devices.dataTypes.Data3D;
import devices.dataTypes.TimestampedData3D;
import devices.sensors.Sensor;
import devices.sensors.interfaces.Gyroscope;

public class MPU9250Gyroscope extends Sensor<TimestampedData3D,Data3D> {

	public MPU9250Gyroscope(int sampleRate, int sampleSize, MPU9250RegisterOperations ro) {
		super(sampleRate, sampleSize, ro);
		// TODO Auto-generated constructor stub
		this.setValScaling( new Data3D(	(float)GyrScale.GFS_2000DPS.getRes(),
										(float)GyrScale.GFS_2000DPS.getRes(),
										(float)GyrScale.GFS_2000DPS.getRes())
				);
	}


	@Override
	public TimestampedData3D getAvgValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateData() throws IOException {
        float x,y,z;
        short registers[];
        //roMPU.readByteRegister(Registers.GYRO_XOUT_H, 6);  // Read again to trigger
        registers = ro.read16BitRegisters(Registers.GYRO_XOUT_H,3);
        //System.out.println("Gyroscope " + x + ", " + y + ", " + z);

        x = (float) ((float)registers[0]*valScaling.getX()); // transform from raw data to degrees/s
        y = (float) ((float)registers[1]*valScaling.getY()); // transform from raw data to degrees/s
        z = (float) ((float)registers[2]*valScaling.getY()); // transform from raw data to degrees/s

        this.addValue(new TimestampedData3D(x,y,z));
	}

	@Override
	public void calibrate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void selfTest() {
		// TODO Auto-generated method stub

	}
    private void setGyroBiases(short[] gyroBiasAvg)
    {
    	System.out.println("setGyroBiases");
        short gyrosensitivity = 131;     // = 131 LSB/degrees/sec
        byte[] buffer = new byte[6];
        short[] gyroBiasAvgLSB = new short[] {0,0,0};
        
        // Construct the gyro biases for push to the hardware gyro bias registers, which are reset to zero upon device startup
        // Divide by 4 to get 32.9 LSB per deg/s to conform to expected bias input format
        // Biases are additive, so change sign on calculated average gyro biases
        gyroBiasAvgLSB[0] = (short)(-gyroBiasAvg[0]/4);
        gyroBiasAvgLSB[1] = (short)(-gyroBiasAvg[1]/4);
        gyroBiasAvgLSB[2] = (short)(-gyroBiasAvg[2]/4);
        System.out.print("gyroBiasAvgLSB: "+Arrays.toString(gyroBiasAvgLSB));
    	System.out.format(" [0x%X, 0x%X, 0x%X]%n",gyroBiasAvgLSB[0],gyroBiasAvgLSB[1],gyroBiasAvgLSB[2]);
        
        buffer[0] = (byte)(((gyroBiasAvg[0])  >> 8) & 0xFF); //convert to Bytes
        buffer[1] = (byte)((gyroBiasAvg[0])       & 0xFF); 
        buffer[2] = (byte)(((gyroBiasAvg[1])  >> 8) & 0xFF);
        buffer[3] = (byte)(gyroBiasAvg[1]       & 0xFF);
        buffer[4] = (byte)((gyroBiasAvg[2]  >> 8) & 0xFF);
        buffer[5] = (byte)(gyroBiasAvg[2]       & 0xFF);
        System.out.print("Bias bytes: "+Arrays.toString(buffer));
    	System.out.format(" [0x%X, 0x%X, 0x%X, 0x%X, 0x%X, 0x%X]%n",buffer[0],buffer[1],buffer[2],buffer[3],buffer[4],buffer[5]);
        
        // Push gyro biases to hardware registers
        ro.writeByteRegister(Registers.XG_OFFSET_H, buffer[0]);
        ro.writeByteRegister(Registers.XG_OFFSET_L, buffer[1]);
        ro.writeByteRegister(Registers.YG_OFFSET_H, buffer[2]);
        ro.writeByteRegister(Registers.YG_OFFSET_L, buffer[3]);
        ro.writeByteRegister(Registers.ZG_OFFSET_H, buffer[4]);
        ro.writeByteRegister(Registers.ZG_OFFSET_L, buffer[5]);
        
        // set super class NineDOF variables
        this.setValBias(new Data3D(	(float) gyroBiasAvg[0]/(float) gyrosensitivity,
        							(float) gyroBiasAvg[1]/(float) gyrosensitivity,
        							(float) gyroBiasAvg[2]/(float) gyrosensitivity));
        //System.out.println("gyrBias (float): "+Arrays.toString(gyrBias));
    	System.out.println("End setGyroBiases");
    }

}
