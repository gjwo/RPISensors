package sensors.Implementations.VL53L0X;

import dataTypes.TimestampedData1f;
import devices.I2C.I2CImplementation;
import logging.SystemLog;
import sensors.models.Sensor1D;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import sensors.Implementations.VL53L0X.VL53L0XConstants.Registers;
import subsystems.SubSystem;

/**
 * RPISensors - sensors.Implementations.VL53L0XRanger
 * Created by MAWood on 27/12/2016.
 */
public class VL53L0XRanger extends Sensor1D
{
    private final VL53L0XRegisterOperations registerOperations;

    public VL53L0XRanger(I2CImplementation i2CImplementation, int sampleSize)
    {
        super(sampleSize);
        registerOperations = new VL53L0XRegisterOperations(i2CImplementation);
        try
        {
            init();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void init() throws InterruptedException
    {
        byte HVI2C = registerOperations.readReg(Registers.VHV_CONFIG_PAD_SCL_SDA__EXTSUP_HV);
        registerOperations.writeReg(Registers.VHV_CONFIG_PAD_SCL_SDA__EXTSUP_HV,(HVI2C | (byte)0x01)); // set I2C HIGH to 2.8 V


        SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                "VL53L0XRanger proximity sensor...");
        byte c = registerOperations.readReg(Registers.WHO_AM_I);  // Read WHO_AM_I register for VL53L0XRanger
        SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                "I AM " + c + " I should be " + (byte)0xEE);

        // Get info about the specific device
        byte revID = registerOperations.readReg(Registers.IDENTIFICATION_REVISION_ID);  // Read Revision ID register for VL53L0XRanger
        SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                "Revision ID: " + revID);
        TimeUnit.SECONDS.sleep(1);


        registerOperations.writeReg(Registers.SOFT_RESET_GO2_SOFT_RESET_N,0x01);  // reset device

        TimeUnit.MILLISECONDS.sleep(100);

        HVI2C = registerOperations.readReg(Registers.VHV_CONFIG_PAD_SCL_SDA__EXTSUP_HV);
        registerOperations.writeReg(Registers.VHV_CONFIG_PAD_SCL_SDA__EXTSUP_HV, (HVI2C | (byte)0x01)); // set I2C HIGH to 2.8 V

        // "Set I2C standard mode"
        registerOperations.writeReg(0x88, 0x00);

        registerOperations.writeReg(0x80, 0x01);
        registerOperations.writeReg(0xFF,0x01);
        registerOperations.writeReg(0x00,0x00);
        byte stop_variable = registerOperations.readReg(0x91);
        registerOperations.writeReg(0x00,0x01);
        registerOperations.writeReg(0xFF,0x00);
        registerOperations.writeReg( 0x80,0x00);


// Taken from Pololu Arduino version of ST's API code

        byte spad_count;
        boolean spad_type_is_aperture;
        byte tmp = getSpadInfo();

        spad_count =(byte) (tmp & 0x7f);
        spad_type_is_aperture = ((tmp >> 7) & 0x01)==1;
//  if (!getSpadInfo(&spad_count, &spad_type_is_aperture)) { return false; }

        // The SPAD map (RefGoodSpadMap) is read by VL53L0X_get_info_from_device() in
        // the API, but the same data seems to be more easily readable from
        // GLOBAL_CONFIG_SPAD_ENABLES_REF_0 through _6, so read it from there
        byte ref_spad_map[];
        ref_spad_map = registerOperations.readRegs(Registers.GLOBAL_CONFIG_SPAD_ENABLES_REF_0, 6);

        // -- VL53L0X_set_reference_spads() begin (assume NVM values are valid)

        registerOperations.writeReg(0xFF,0x01);
        registerOperations.writeReg(Registers.DYNAMIC_SPAD_REF_EN_START_OFFSET,0x00);
        registerOperations.writeReg(Registers.DYNAMIC_SPAD_NUM_REQUESTED_REF_SPAD,0x2C);
        registerOperations.writeReg(0xFF,0x00);
        registerOperations.writeReg(Registers.GLOBAL_CONFIG_REF_EN_START_SELECT,0xB4);

        byte first_spad_to_enable = spad_type_is_aperture ? (byte)12 : 0; // 12 is the first aperture spad
        byte spads_enabled = 0;

        for (byte i = 0; i < 48; i++)
        {
            if (i < first_spad_to_enable || spads_enabled == spad_count)
            {
                // This bit is lower than the first one that should be enabled, or
                // (reference_spad_count) bits have already been enabled, so zero this bit
                ref_spad_map[i / 8] &= ~(1 << (i % 8));
            }
            else if (((ref_spad_map[i / 8] >> (i % 8)) & 0x1)==1)
            {
                spads_enabled++;
            }
        }

        registerOperations.writeReg(Registers.GLOBAL_CONFIG_SPAD_ENABLES_REF_0, ref_spad_map[0]);
        registerOperations.writeReg(Registers.GLOBAL_CONFIG_SPAD_ENABLES_REF_1, ref_spad_map[1]);
        registerOperations.writeReg(Registers.GLOBAL_CONFIG_SPAD_ENABLES_REF_2, ref_spad_map[2]);
        registerOperations.writeReg(Registers.GLOBAL_CONFIG_SPAD_ENABLES_REF_3, ref_spad_map[3]);
        registerOperations.writeReg(Registers.GLOBAL_CONFIG_SPAD_ENABLES_REF_4, ref_spad_map[4]);
        registerOperations.writeReg(Registers.GLOBAL_CONFIG_SPAD_ENABLES_REF_5, ref_spad_map[5]);

        // -- VL53L0X_set_reference_spads() end
/*
  // -- VL53L0X_load_tuning_settings() begin
  // DefaultTuningSettings from vl53l0x_tuning.h
  registerOperations.writeReg(0xFF, 0x01);
  registerOperations.writeReg(0x00, 0x00);
  registerOperations.writeReg(0xFF, 0x00);
  registerOperations.writeReg(0x09, 0x00);
  registerOperations.writeReg(0x10, 0x00);
  registerOperations.writeReg(0x11, 0x00);
  registerOperations.writeReg(0x24, 0x01);
  registerOperations.writeReg(0x25, 0xFF);
  registerOperations.writeReg(0x75, 0x00);
  registerOperations.writeReg(0xFF, 0x01);
  registerOperations.writeReg(0x4E, 0x2C);
  registerOperations.writeReg(0x48, 0x00);
  registerOperations.writeReg(0x30, 0x20);
  registerOperations.writeReg(0xFF, 0x00);
  registerOperations.writeReg(0x30, 0x09);
  registerOperations.writeReg(0x54, 0x00);
  registerOperations.writeReg(0x31, 0x04);
  registerOperations.writeReg(0x32, 0x03);
  registerOperations.writeReg(0x40, 0x83);
  registerOperations.writeReg(0x46, 0x25);
  registerOperations.writeReg(0x60, 0x00);
  registerOperations.writeReg(0x27, 0x00);
  registerOperations.writeReg(0x50, 0x06);
  registerOperations.writeReg(0x51, 0x00);
  registerOperations.writeReg(0x52, 0x96);
  registerOperations.writeReg(0x56, 0x08);
  registerOperations.writeReg(0x57, 0x30);
  registerOperations.writeReg(0x61, 0x00);
  registerOperations.writeReg(0x62, 0x00);
  registerOperations.writeReg(0x64, 0x00);
  registerOperations.writeReg(0x65, 0x00);
  registerOperations.writeReg(0x66, 0xA0);
  registerOperations.writeReg(0xFF, 0x01);
  registerOperations.writeReg(0x22, 0x32);
  registerOperations.writeReg(0x47, 0x14);
  registerOperations.writeReg(0x49, 0xFF);
  registerOperations.writeReg(0x4A, 0x00);
  registerOperations.writeReg(0xFF, 0x00);
  registerOperations.writeReg(0x7A, 0x0A);
  registerOperations.writeReg(0x7B, 0x00);
  registerOperations.writeReg(0x78, 0x21);
  registerOperations.writeReg(0xFF, 0x01);
  registerOperations.writeReg(0x23, 0x34);
  registerOperations.writeReg(0x42, 0x00);
  registerOperations.writeReg(0x44, 0xFF);
  registerOperations.writeReg(0x45, 0x26);
  registerOperations.writeReg(0x46, 0x05);
  registerOperations.writeReg(0x40, 0x40);
  registerOperations.writeReg(0x0E, 0x06);
  registerOperations.writeReg(0x20, 0x1A);
  registerOperations.writeReg(0x43, 0x40);
  registerOperations.writeReg(0xFF, 0x00);
  registerOperations.writeReg(0x34, 0x03);
  registerOperations.writeReg(0x35, 0x44);
  registerOperations.writeReg(0xFF, 0x01);
  registerOperations.writeReg(0x31, 0x04);
  registerOperations.writeReg(0x4B, 0x09);
  registerOperations.writeReg(0x4C, 0x05);
  registerOperations.writeReg(0x4D, 0x04);
  registerOperations.writeReg(0xFF, 0x00);
  registerOperations.writeReg(0x44, 0x00);
  registerOperations.writeReg(0x45, 0x20);
  registerOperations.writeReg(0x47, 0x08);
  registerOperations.writeReg(0x48, 0x28);
  registerOperations.writeReg(0x67, 0x00);
  registerOperations.writeReg(0x70, 0x04);
  registerOperations.writeReg(0x71, 0x01);
  registerOperations.writeReg(0x72, 0xFE);
  registerOperations.writeReg(0x76, 0x00);
  registerOperations.writeReg(0x77, 0x00);
  registerOperations.writeReg(0xFF, 0x01);
  registerOperations.writeReg(0x0D, 0x01);
  registerOperations.writeReg(0xFF, 0x00);
  registerOperations.writeReg(0x80, 0x01);
  registerOperations.writeReg(0x01, 0xF8);
  registerOperations.writeReg(0xFF, 0x01);
  registerOperations.writeReg(0x8E, 0x01);
  registerOperations.writeReg(0x00, 0x01);
  registerOperations.writeReg(0xFF, 0x00);
  registerOperations.writeReg(0x80, 0x00);
// -- VL53L0X_load_tuning_settings() end
 */

        // Configure GPIO1 for interrupt, active LOW
        byte actHIGH = registerOperations.readReg(Registers.GPIO_HV_MUX_ACTIVE_HIGH);
        registerOperations.writeReg(Registers.SYSTEM_INTERRUPT_CONFIG_GPIO,0x04); // enable data ready interrupt
        registerOperations.writeReg(Registers.GPIO_HV_MUX_ACTIVE_HIGH,(actHIGH & ~0x10)); // GPIO1 interrupt active LOW
        registerOperations.writeReg(Registers.SYSTEM_INTERRUPT_CLEAR,0x01); // clear interrupt

        // Get some basic information about the sensor
        byte val1 = registerOperations.readReg(Registers.PRE_RANGE_CONFIG_VCSEL_PERIOD);
        SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                "PRE_RANGE_CONFIG_VCSEL_PERIOD= " + val1 + " decoded: " + VL53L0X_decode_vcsel_period(val1));

        val1 = registerOperations.readReg(Registers.FINAL_RANGE_CONFIG_VCSEL_PERIOD);
        SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                "PRE_RANGE_CONFIG_VCSEL_PERIOD= " + val1 + " decoded: " + VL53L0X_decode_vcsel_period(val1));

        byte[] rawData = registerOperations.readRegs(Registers.SYSTEM_INTERMEASUREMENT_PERIOD, 4);
        int IMPeriod = (((int) rawData[0]) << 24 | ((int) rawData[1]) << 16 | ((int) rawData[2]) << 8 | rawData[3]);
        SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                "System Inter-measurement period = " + IMPeriod + "ms");

        registerOperations.writeReg(Registers.SYSRANGE_START,0x02); // continuous mode and arm next shot
    }

    private short VL53L0X_decode_vcsel_period(short vcsel_period_reg) {
        // Converts the encoded VCSEL period register value into the real
        // period in PLL clocks
        return (short)((vcsel_period_reg + 1) << 1);
    }

    private byte getSpadInfo() throws InterruptedException
    {
        byte tmp;

        registerOperations.writeReg(0x80,0x01);
        registerOperations.writeReg(0xFF,0x01);
        registerOperations.writeReg(0x00,0x00);

        registerOperations.writeReg(0xFF,0x06);
        registerOperations.writeReg(0x83,(registerOperations.readReg(0x83) | 0x04));
        registerOperations.writeReg(0xFF,0x07);
        registerOperations.writeReg(0x81,0x01);

        registerOperations.writeReg(0x80,0x01);

        registerOperations.writeReg(0x94,0x6b);
        registerOperations.writeReg(0x83,0x00);
        while (registerOperations.readReg(0x83) == 0x00)
        {
            TimeUnit.MILLISECONDS.sleep(10);
            //if (checkTimeoutExpired()) { return false; }
        }
        registerOperations.writeReg(0x83,0x01);
        tmp = registerOperations.readReg(0x92);


        registerOperations.writeReg(0x81,0x00);
        registerOperations.writeReg(0xFF,0x06);
        registerOperations.writeReg(0x83, registerOperations.readReg(0x83  & ~0x04));
        registerOperations.writeReg(0xFF,0x01);
        registerOperations.writeReg(0x00,0x01);

        registerOperations.writeReg(0xFF,0x00);
        registerOperations.writeReg(0x80,0x00);

        return tmp;

    }

    @Override
    public void updateData() throws IOException
    {

//  byte intStatus = readByte(VL53L0X_ADDRESS, VL53L0X_REG_RESULT_RANGE_STATUS);// Poll for data ready
//  if(intStatus & 0x01) // poll for data ready
//  {

        if(!((registerOperations.readReg(Registers.RESULT_INTERRUPT_STATUS) & 0x07) == 0)) // wait for data ready interrupt
        {
            registerOperations.writeReg(Registers.SYSTEM_INTERRUPT_CLEAR, 0x01); // clear interrupt

            byte[] rangeData = registerOperations.readRegs(Registers.RESULT_RANGE_STATUS, 14); // continuous ranging


            //for(int i = 1; i<= 14;i++) SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
            //        "byte " + i + " = " + rangeData[i-1]);

            byte devError = (byte)((rangeData[0] & 0x78) >> 3); // Check for errors

            String error = "";
            if(devError == 0)     error = "Data OK!";// No device error
            if(devError == 0x01)  error = "VCSEL CONTINUITY TEST FAILURE!";
            if(devError == 0x02)  error = "VCSEL WATCHDOG TEST FAILURE!";
            if(devError == 0x03)  error = "NO VHV VALUE FOUND!";
            if(devError == 0x04)  error = "MSRC NO TARGET!";
            if(devError == 0x05)  error = "SNR CHECK!";
            if(devError == 0x06)  error = "RANGE PHASE CHECK!";
            if(devError == 0x07)  error = "SIGMA THRESHOLD CHECK!";
            if(devError == 0x08)  error = "TCC!";
            if(devError == 0x09)  error = "PHASE CONSISTENCY!";
            if(devError == 0x0A)  error = "MIN CLIP!";
            if(devError == 0x0B)  error = "RANGE COMPLETE!";
            if(devError == 0x0C)  error = "ALGO UNDERFLOW!";
            if(devError == 0x0D)  error = "ALGO OVERFLOW!";
            if(devError == 0x0E)  error = "RANGE IGNORE THRESHOLD!";

            SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS, error);

            /*SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                    "Effective SPAD Return Count = " + ((float) (rangeData[2]) + (float)rangeData[3]/255.));
            SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                    "Signal Rate = " + (short) (((short) rangeData[6] << 8) | rangeData[7]) + " mega counts per second");
            SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                    "Ambient Rate = " + (short) (((short) rangeData[8] << 8) | rangeData[9]) + " mega counts per second");*/
            int distance = (((short)rangeData[10] << 8) | (rangeData[11]&0xff));
            if(devError == 0 || devError == 0x0B)
            {
                SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_LOOPS,
                        "Distance = " + distance + " mm");
                this.addValue(new TimestampedData1f(distance));
            }
        } else SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_WRITES, "Data not ready");
    }
}
