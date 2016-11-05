package devices.sensorImplementations.MPU9250;

import devices.sensors.Sensor;

import java.util.Arrays;

import devices.dataTypes.Data3D;
import devices.dataTypes.TimestampedData3D;

public class MPU9250Accelerometer extends Sensor<TimestampedData3D,Data3D>  {

	MPU9250Accelerometer(int sampleRate, int sampleSize, MPU9250RegisterOperations ro)
	{
		super(sampleSize, sampleSize, ro);
		this.setValScaling(new Data3D(	(float)AccScale.AFS_4G.getRes(),
										(float)AccScale.AFS_4G.getRes(),
										(float)AccScale.AFS_4G.getRes()));
	}

	@Override
	public void updateData()
	{
         short registers[];
        //roMPU.readByteRegister(Registers.ACCEL_XOUT_H, 6);  // Read again to trigger
        registers = ro.read16BitRegisters(Registers.ACCEL_XOUT_H,3);
        this.addValue(OffsetAndScale(new TimestampedData3D(registers[0],registers[1],registers[2])));
	}
	
	@Override
    public TimestampedData3D OffsetAndScale(TimestampedData3D value)
    {
		TimestampedData3D oSVal = value.clone();
        oSVal.setX(value.getX()*valScaling.getX() -valBias.getX()); // transform from raw data to g
        oSVal.setY(value.getY()*valScaling.getY()-valBias.getY()); // transform from raw data to g
        oSVal.setZ(value.getZ()*valScaling.getZ()-valBias.getZ()); // transform from raw data to g
        return oSVal;
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
        this.setValBias(new Data3D( 	(float)accelBiasAvg[0]/2/(float)accelSensitivity,
        								(float)accelBiasAvg[1]/2/(float)accelSensitivity,
        								(float)accelBiasAvg[2]/2/(float)accelSensitivity));
    }

}