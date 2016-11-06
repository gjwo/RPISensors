package sensors.Implementations.MPU9250;

import java.io.IOException;
import java.util.Arrays;

import dataTypes.DataFloat3D;
import dataTypes.TimestampedDataFloat3D;
import sensors.models.Sensor3D;

public class MPU9250Accelerometer extends Sensor3D  {

	MPU9250Accelerometer(int sampleRate, int sampleSize, MPU9250RegisterOperations ro)
	{
		super(sampleSize, sampleSize, ro);
		this.setValScaling(new DataFloat3D(	(float)AccScale.AFS_4G.getRes(),
										(float)AccScale.AFS_4G.getRes(),
										(float)AccScale.AFS_4G.getRes()));
	}

	@Override
	public void updateData()
	{
         short registers[];
        //roMPU.readByteRegister(Registers.ACCEL_XOUT_H, 6);  // Read again to trigger
        registers = ro.read16BitRegisters(Registers.ACCEL_XOUT_H,3);
        this.addValue(OffsetAndScale(new TimestampedDataFloat3D(registers[0],registers[1],registers[2])));
	}
	
	@Override
	public void calibrate() throws InterruptedException
	{
    	System.out.println("accel.calibrate");
    	
    	// Assumes we are in calibration mode via setCalibrationMode9250();

        // Configure MPU6050 accelerometer for bias calculation
        ro.writeByteRegister(Registers.ACCEL_CONFIG,(byte) AccScale.AFS_2G.getValue()); 		// Set accelerometer full-scale to 2 g, maximum sensitivity


        // Configure FIFO to capture accelerometer data for bias calculation
        ro.writeByteRegister(Registers.USER_CTRL,(byte) 0x40);   // Enable FIFO
        ro.writeByteRegister(Registers.FIFO_EN,(byte) FIFO_MODE.FIFO_MODE_ACC.getValue());     // Enable and accelerometer sensors for FIFO  (max size 512 bytes in MPU-9150)
        Thread.sleep(40); // accumulate 40 samples in 40 milliseconds = 480 bytes

        // At end of sample accumulation, turn off FIFO sensor read
        ro.writeByteRegister(Registers.FIFO_EN,(byte) 0x00);        // Disable gyro and accelerometer sensors for FIFO

        short packetCount = ro.read16BitRegisters( Registers.FIFO_COUNTH, 1)[0];
        int sampleCount =  packetCount / 12; // 12 bytes per sample 6 x 16 bit values

        int[] accelBiasSum = new int[]{0,0,0}; //32 bit to allow for accumulation without overflow
        short[] tempBias;
        System.out.println("Read Fifo packetCount: "+packetCount);
        
        //Read FIFO
        for(int s = 0; s < sampleCount; s++)
        {
            tempBias = ro.read16BitRegisters(Registers.FIFO_R_W,3); //6 bytes
            //System.out.print("bias sample bytes: "+Arrays.toString(tempBias));
        	//System.out.format(" [0x%X, 0x%X, 0x%X, 0x%X, 0x%X, 0x%X]%n",tempBias[0],tempBias[1],tempBias[2],tempBias[3],tempBias[4],tempBias[5]);
            
            accelBiasSum[0] += tempBias[0]; // Sum individual signed 16-bit biases to get accumulated signed 32-bit biases
            accelBiasSum[1] += tempBias[1];
            accelBiasSum[2] += tempBias[2];
        }
        
        //calculate averages
        short[] accelBiasAvg = new short[]{0,0,0}; //16 bit average
        accelBiasAvg[0] = (short)((accelBiasSum[0] / sampleCount) & 0xffff); // Normalise sums to get average count biases
        accelBiasAvg[1] = (short)((accelBiasSum[1] / sampleCount) & 0xffff); 
        accelBiasAvg[2] = (short)((accelBiasSum[2] / sampleCount) & 0xffff); 
        
        System.out.print("Accel Bias average: "+Arrays.toString(accelBiasAvg));
    	System.out.format(" [0x%X, 0x%X, 0x%X]%n",accelBiasAvg[0],accelBiasAvg[1],accelBiasAvg[2]);
    	
        //setAccelerometerBiases(accelBiasAvg);
        
    	System.out.println("End accel.calibrate");
	}

	@Override
	public void configure() throws IOException, InterruptedException
	{
        // Set accelerometer full-scale range configuration
		byte c;
        c = ro.readByteRegister(Registers.ACCEL_CONFIG); // get current ACCEL_CONFIG register value
        c = (byte)(c & ~0xE0); // Clear self-test bits [7:5] ####
        c = (byte)(c & ~0x18);  // Clear AFS bits [4:3]
        c = (byte)(c | AccScale.AFS_2G.getValue() ); // Set full scale range for the accelerometer #### does not require shifting!!!!
        ro.writeByteRegister(Registers.ACCEL_CONFIG, c); // Write new ACCEL_CONFIG register value

        // Set accelerometer sample rate configuration
        // It is possible to get a 4 kHz sample rate from the accelerometer by choosing 1 for
        // accel_fchoice_b bit [3]; in this case the bandwidth is 1.13 kHz
        c = ro.readByteRegister(Registers.ACCEL_CONFIG2); // get current ACCEL_CONFIG2 register value
        c = (byte)(c & ~0x0F); // Clear accel_fchoice_b (bit 3) and A_DLPFG (bits [2:0])
        c = (byte)(c | 0x03);  // Set accelerometer rate to 1 kHz and bandwidth to 41 Hz 
        ro.writeByteRegister(Registers.ACCEL_CONFIG2, c); // Write new ACCEL_CONFIG2 register value

	}
	

	@Override
	public void selfTest() 
	{
		// TODO Auto-generated method stub
		
	}
    public void setAccelerometerBiases(short[] accelBiasAvg)
    {
        // Construct the accelerometer biases for push to the hardware accelerometer bias registers. These registers contain
        // factory trim values which must be added to the calculated accelerometer biases; on boot up these registers will hold
        // non-zero values. In addition, bit 0 of the lower byte must be preserved since it is used for temperature
        // compensation calculations. Accelerometer bias registers expect bias input as 2048 LSB per g, so that
        // the accelerometer biases calculated above must be divided by 8.
        // XA_OFFSET is a 15 bit quantity with bits 14:7 in the high byte and 6:0 in the low byte with temperature compensation in bit0
        // so having got it in a 16 bit short, and having preserved the bottom bit, the number must be shifted right by 1 or divide by 2
        // to give the correct value for calculations. After calculations it must be shifted left by 1 or multiplied by 2 to get
        // the bytes correct, then the preserved bit0 can be put back before the bytes are written to registers
    	System.out.println("setAccelerometerBiases");

        short accelSensitivity = 16384;  // = 16384 LSB/g - OK in short max 32,767
        if(accelBiasAvg[2] > 0) {accelBiasAvg[2] -= accelSensitivity;}  // Remove gravity from the z-axis accelerometer bias calculation
        else {accelBiasAvg[2] += accelSensitivity;}
    	System.out.format("z adjusted for gravity %d 0x%X%n",accelBiasAvg[2],accelBiasAvg[2]);
       
        short[] accelBiasReg = ro.read16BitRegisters( Registers.XA_OFFSET_H, 3);
        System.out.print("accelBiasReg with temp compensation bit: "+Arrays.toString(accelBiasReg));
    	System.out.format(" [0x%X, 0x%X, 0x%X] %n",accelBiasReg[0],accelBiasReg[1],accelBiasReg[2]);

        short mask = 0x0001; // Define mask for temperature compensation bit 0 of lower byte of accelerometer bias registers
        byte[] mask_bit = new byte[]{0, 0, 0}; // Define array to hold mask bit for each accelerometer bias axis

        for(int s = 0; s < 3; s++) {
            if((accelBiasReg[s] & mask)==1) mask_bit[s] = 0x01; // If temperature compensation bit is set, record that fact in mask_bit
            //divide accelBiasReg by 2 to remove the bottom bit and preserve any sign (java has no unsigned 16 bit numbers)
            accelBiasReg[s] /=2;
        }
        System.out.print("accelBiasReg without temp compensation bit: "+Arrays.toString(accelBiasReg));
    	System.out.format(" [0x%X, 0x%X, 0x%X] %n",accelBiasReg[0],accelBiasReg[1],accelBiasReg[2]);
        
        // Construct total accelerometer bias, including calculated average accelerometer bias from above
        for (int i = 0; i<3; i++)
        {
        	//Subtract calculated averaged accelerometer bias scaled to 2048 LSB/g (16 g full scale)
        	//multiply by two to leave the bottom bit clear and but all the bits in the correct bytes
        	//Add back the temperature compensation bit
        	accelBiasReg[i] = (short)((accelBiasReg[i] - accelBiasAvg[i]/8)*2+mask_bit[0]);
        }
        System.out.print("(accelBiasReg - biasAvg/8)*2 + TCbit (16bit): "+Arrays.toString(accelBiasReg));
    	System.out.format(" [0x%X, 0x%X, 0x%X] %n",accelBiasReg[0],accelBiasReg[1],accelBiasReg[2]);
    	
        // Push accelerometer biases to hardware registers  	
        ro.write16bitRegister(Registers.XA_OFFSET_H, accelBiasReg[0]);
        ro.write16bitRegister(Registers.YA_OFFSET_H, accelBiasReg[1]);
        ro.write16bitRegister(Registers.ZA_OFFSET_H, accelBiasReg[2]);
        
        // set super class NineDOF variables
        this.setValBias(new DataFloat3D( 	(float)accelBiasAvg[0]/2/(float)accelSensitivity,
        								(float)accelBiasAvg[1]/2/(float)accelSensitivity,
        								(float)accelBiasAvg[2]/2/(float)accelSensitivity));
    }

}