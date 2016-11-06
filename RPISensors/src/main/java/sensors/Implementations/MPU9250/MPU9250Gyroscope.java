package sensors.Implementations.MPU9250;

import java.io.IOException;
import java.util.Arrays;

import dataTypes.DataFloat3D;
import dataTypes.TimestampedDataFloat3D;
import sensors.models.Sensor3D;

public class MPU9250Gyroscope extends Sensor3D {
	private GyrScale gyroScale; 
	public MPU9250Gyroscope(int sampleRate, int sampleSize, MPU9250RegisterOperations ro) 
	{
		super(sampleRate, sampleSize, ro);
		gyroScale = GyrScale.GFS_2000DPS;
		this.setValScaling( new DataFloat3D(	(float)gyroScale.getRes(),
										(float)gyroScale.getRes(),
										(float)gyroScale.getRes()));
	}

	@Override
	public void updateData() throws IOException {
        short registers[];
        //roMPU.readByteRegister(Registers.GYRO_XOUT_H, 6);  // Read again to trigger
        registers = ro.read16BitRegisters(Registers.GYRO_XOUT_H,3);
        this.addValue(OffsetAndScale(new TimestampedDataFloat3D(registers[0],registers[1],registers[2])));
	}
	

	@Override
	public void calibrate() throws InterruptedException
	{
    	System.out.println("gyro.calibrate");
    	
    	// Assumes we are in calibration mode via setCalibrationMode9250();

        // Configure MPU6050 gyro for bias calculation
        ro.writeByteRegister(Registers.GYRO_CONFIG,(byte) GyrScale.GFS_250DPS.getValue());  	// Set gyro full-scale to 250 degrees per second, maximum sensitivity

        // Configure FIFO to capture gyro data for bias calculation
        ro.writeByteRegister(Registers.USER_CTRL,(byte) 0x40);   // Enable FIFO
        ro.writeByteRegister(Registers.FIFO_EN,(byte) FIFO_MODE.FIFO_MODE_GYRO.getValue());     // Enable gyro x,y,z sensors for FIFO  (max size 512 bytes in MPU-9150)
        Thread.sleep(40); // accumulate 40 samples in 40 milliseconds = 480 bytes

        // At end of sample accumulation, turn off FIFO sensor read
        ro.writeByteRegister(Registers.FIFO_EN,(byte) 0x00);        // Disable gyro and accelerometer sensors for FIFO

        short packetCount = ro.read16BitRegisters( Registers.FIFO_COUNTH, 1)[0];
        int sampleCount =  packetCount / 12; // 12 bytes per sample 6 x 16 bit values

        int[] gyroBiasSum = new int[]{0,0,0}; //32 bit to allow for accumulation without overflow
        short[] tempBias;
        System.out.println("Read Fifo packetCount: "+packetCount);
        
        //Read FIFO
        for(int s = 0; s < sampleCount; s++)
        {
            tempBias = ro.read16BitRegisters(Registers.FIFO_R_W,3); //6 bytes
            //System.out.print("bias sample bytes: "+Arrays.toString(tempBias));
        	//System.out.format(" [0x%X, 0x%X, 0x%X, 0x%X, 0x%X, 0x%X]%n",tempBias[0],tempBias[1],tempBias[2],tempBias[3],tempBias[4],tempBias[5]);
            
            gyroBiasSum[0] += tempBias[3]; // Sum individual signed 16-bit biases to get accumulated signed 32-bit biases
            gyroBiasSum[1] += tempBias[4];
            gyroBiasSum[2] += tempBias[5];
        }
        
        //calculate averages
        short[] gyroBiasAvg = new short[]{0,0,0}; //16 bit average
        gyroBiasAvg[0] = (short)((gyroBiasSum[0] / sampleCount) & 0xffff);
        gyroBiasAvg[1] = (short)((gyroBiasSum[1] / sampleCount) & 0xffff);
        gyroBiasAvg[2] = (short)((gyroBiasSum[2] / sampleCount) & 0xffff);

        System.out.print("Gyro Bias average: "+Arrays.toString(gyroBiasAvg));
    	System.out.format(" [0x%X, 0x%X, 0x%X]%n",gyroBiasAvg[0],gyroBiasAvg[1],gyroBiasAvg[2]);
    	
        //setGyroBiases(gyroBiasAvg);
        
    	System.out.println("End gyro.calibrate");
	}

	@Override
	public void selfTest() {
		// TODO Auto-generated method stub

	}
    public void setGyroBiases(short[] gyroBiasAvg)
    {
    	System.out.println("setGyroBiases");
        short gyrosensitivity = 131;     // = 131 LSB/degrees/sec
        short[] gyroBiasAvgLSB = new short[] {0,0,0};
        
        // Construct the gyro biases for push to the hardware gyro bias registers, which are reset to zero upon device startup
        // Divide by 4 to get 32.9 LSB per deg/s to conform to expected bias input format
        // Biases are additive, so change sign on calculated average gyro biases
        
        gyroBiasAvgLSB[0] = (short)(-gyroBiasAvg[0]/4);
        gyroBiasAvgLSB[1] = (short)(-gyroBiasAvg[1]/4);
        gyroBiasAvgLSB[2] = (short)(-gyroBiasAvg[2]/4);
        System.out.print("gyroBiasAvgLSB: "+Arrays.toString(gyroBiasAvgLSB));
    	System.out.format(" [0x%X, 0x%X, 0x%X]%n",gyroBiasAvgLSB[0],gyroBiasAvgLSB[1],gyroBiasAvgLSB[2]);
    	
        // Push gyro biases to hardware registers
    	ro.write16bitRegister(Registers.XG_OFFSET_H,gyroBiasAvgLSB[0]);
    	ro.write16bitRegister(Registers.YG_OFFSET_H,gyroBiasAvgLSB[1]);
    	ro.write16bitRegister(Registers.ZG_OFFSET_H,gyroBiasAvgLSB[2]);
         
        // set super class NineDOF variables
        this.setValBias(new DataFloat3D(	(float) gyroBiasAvg[0]/(float) gyrosensitivity,
        							(float) gyroBiasAvg[1]/(float) gyrosensitivity,
        							(float) gyroBiasAvg[2]/(float) gyrosensitivity));
        //System.out.println("gyrBias (float): "+Arrays.toString(gyrBias));
    	System.out.println("End setGyroBiases");
    }
}
