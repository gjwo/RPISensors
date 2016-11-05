package devices.sensorImplementations.MPU9250;

import devices.sensors.Sensor;

import java.util.Arrays;

import devices.dataTypes.Data3D;
import devices.dataTypes.TimestampedData3D;
import devices.sensors.interfaces.Accelerometer;

public class MPU9250Accelerometer extends Sensor<TimestampedData3D,Data3D>  {
    private static final AccScale accScale = AccScale.AFS_4G;


	MPU9250Accelerometer(int sampleRate, int sampleSize, MPU9250RegisterOperations ro)
	{
		super(sampleSize, sampleSize, ro);
	}
	
	AccScale getAccscale() {
		return accScale;
	}


	@Override
	public void updateData()
	{
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
	public void calibrate()
	{
		// TODO Auto-generated method stub
		
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

        int mask = 0x01; // Define mask for temperature compensation bit 0 of lower byte of accelerometer bias registers
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
        	accelBiasReg[i] -= (accelBiasAvg[i]/8); // Subtract calculated averaged accelerometer bias scaled to 2048 LSB/g (16 g full scale)
        	accelBiasReg[i] *=2; //multiply by two to leave the bottom bit clear and but all the bits in the correct bytes
        }
        System.out.print("(accelBiasReg - biasAvg/8)*2 (16bit): "+Arrays.toString(accelBiasReg));
    	System.out.format(" [0x%X, 0x%X, 0x%X] %n",accelBiasReg[0],accelBiasReg[1],accelBiasReg[2]);

        byte[] buffer = new byte[6];
        
        // XA_OFFSET is a 15 bit quantity with bits 14:7 in the high byte and 6:0 in the low byte with temperature compensation in bit0

        buffer[0] = (byte)((accelBiasReg[0] >> 8) & 0xFF); //Shift down and mask top 8 bits
        buffer[1] = (byte)((accelBiasReg[0])      & 0xFE); //copy bits 7-1 clear bit 0
        buffer[1] = (byte)(buffer[1] | mask_bit[0]); // preserve temperature compensation bit when writing back to accelerometer bias registers
        buffer[2] = (byte)((accelBiasReg[1] >> 8) & 0xFF); //Shift down and mask top 8 bits
        buffer[3] = (byte)((accelBiasReg[1])      & 0xFE); //copy bits 7-1 clear bit 0
        buffer[3] = (byte)(buffer[3] | mask_bit[1]); // preserve temperature compensation bit when writing back to accelerometer bias registers
        buffer[4] = (byte)((accelBiasReg[2] >> 8) & 0xFF); //Shift down and mask top 8 bits
        buffer[5] = (byte)((accelBiasReg[2])      & 0xFE); //copy bits 7-1 clear bit 0
        buffer[5] = (byte)(buffer[5] | mask_bit[2]); // preserve temperature compensation bit when writing back to accelerometer bias registers
        System.out.print("accelBiasReg bytes: "+Arrays.toString(buffer));
    	System.out.format(" [0x%X, 0x%X, 0x%X, 0x%X, 0x%X, 0x%X]%n",buffer[0],buffer[1],buffer[2],buffer[3],buffer[4],buffer[5]);

        // Apparently this is not working for the acceleration biases in the MPU-9250
        // Are we handling the temperature correction bit properly? - see comments above
    	
        // Push accelerometer biases to hardware registers  	
        ro.writeByteRegister(Registers.XA_OFFSET_H, buffer[0]);
        ro.writeByteRegister(Registers.XA_OFFSET_L, buffer[1]);
        ro.writeByteRegister(Registers.YA_OFFSET_H, buffer[2]);
        ro.writeByteRegister(Registers.YA_OFFSET_L, buffer[3]);
        ro.writeByteRegister(Registers.ZA_OFFSET_H, buffer[4]);
        ro.writeByteRegister(Registers.ZA_OFFSET_L, buffer[5]);
        
        // set super class NineDOF variables
        this.setValBias(new Data3D( 	(float)accelBiasAvg[0]/(float)accelSensitivity,
        								(float)accelBiasAvg[1]/(float)accelSensitivity,
        								(float)accelBiasAvg[2]/(float)accelSensitivity));
        System.out.println("accelBias (float): "+Arrays.toString(buffer));
    	System.out.println("End setAccelerometerBiases");
    }

}