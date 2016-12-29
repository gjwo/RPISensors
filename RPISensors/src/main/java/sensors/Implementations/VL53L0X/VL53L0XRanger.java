package sensors.Implementations.VL53L0X;

import dataTypes.TimestampedData1f;
import devices.I2C.I2CImplementation;
import logging.SystemLog;
import sensors.models.Sensor1D;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import sensors.Implementations.VL53L0X.VL53L0XRegisters;
import subsystems.SubSystem;

/**
 * RPISensors - sensors.Implementations.VL53L0XRanger
 * Created by MAWood on 27/12/2016.
 * Based off of kriswiner's work with the senson in C++
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
        byte HVI2C = registerOperations.readReg(VL53L0XRegisters.VHV_CONFIG_PAD_SCL_SDA__EXTSUP_HV);
        registerOperations.writeReg(VL53L0XRegisters.VHV_CONFIG_PAD_SCL_SDA__EXTSUP_HV, (HVI2C | (byte) 0x01)); // set I2C HIGH to 2.8 V


        SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                "VL53L0XRanger proximity sensor...");
        byte c = registerOperations.readReg(VL53L0XRegisters.WHO_AM_I);  // Read WHO_AM_I register for VL53L0XRanger
        SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                "I AM " + c + " I should be " + (byte) 0xEE);

        // Get info about the specific device
        byte revID = registerOperations.readReg(VL53L0XRegisters.IDENTIFICATION_REVISION_ID);  // Read Revision ID register for VL53L0XRanger
        SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                "Revision ID: " + revID);
        TimeUnit.SECONDS.sleep(1);


        registerOperations.writeReg(VL53L0XRegisters.SOFT_RESET_GO2_SOFT_RESET_N, 0x01);  // reset device

        TimeUnit.MILLISECONDS.sleep(100);

        HVI2C = registerOperations.readReg(VL53L0XRegisters.VHV_CONFIG_PAD_SCL_SDA__EXTSUP_HV);
        registerOperations.writeReg(VL53L0XRegisters.VHV_CONFIG_PAD_SCL_SDA__EXTSUP_HV, (HVI2C | (byte) 0x01)); // set I2C HIGH to 2.8 V

        // "Set I2C standard mode"
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x88, 0x00);

        registerOperations.writeReg(VL53L0XRegisters.POWER_MANAGEMENT_GO1_POWER_FORCE, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.SYSRANGE_START, 0x00);
        byte stop_variable = registerOperations.readReg(VL53L0XRegisters.RANGE_TYPE_ADDR);
        registerOperations.writeReg(VL53L0XRegisters.SYSRANGE_START, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.POWER_MANAGEMENT_GO1_POWER_FORCE, 0x00);


// Taken from Pololu Arduino version of ST's API code

        byte spad_count;
        boolean spad_type_is_aperture;
        byte tmp = getSpadInfo();

        spad_count = (byte) (tmp & 0x7f);
        spad_type_is_aperture = ((tmp >> 7) & 0x01) == 1;
//  if (!getSpadInfo(&spad_count, &spad_type_is_aperture)) { return false; }

        // The SPAD map (RefGoodSpadMap) is read by VL53L0X_get_info_from_device() in
        // the API, but the same data seems to be more easily readable from
        // GLOBAL_CONFIG_SPAD_ENABLES_REF_0 through _6, so read it from there
        byte ref_spad_map[];
        ref_spad_map = registerOperations.readRegs(VL53L0XRegisters.GLOBAL_CONFIG_SPAD_ENABLES_REF_0, 6);

        // -- VL53L0X_set_reference_spads() begin (assume NVM values are valid)

        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.DYNAMIC_SPAD_REF_EN_START_OFFSET, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.DYNAMIC_SPAD_NUM_REQUESTED_REF_SPAD, 0x2C);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.GLOBAL_CONFIG_REF_EN_START_SELECT, 0xB4);

        byte first_spad_to_enable = spad_type_is_aperture ? (byte) 12 : 0; // 12 is the first aperture spad
        byte spads_enabled = 0;

        for (byte i = 0; i < 48; i++)
        {
            if (i < first_spad_to_enable || spads_enabled == spad_count)
            {
                // This bit is lower than the first one that should be enabled, or
                // (reference_spad_count) bits have already been enabled, so zero this bit
                ref_spad_map[i / 8] &= ~(1 << (i % 8));
            } else if (((ref_spad_map[i / 8] >> (i % 8)) & 0x1) == 1)
            {
                spads_enabled++;
            }
        }

        registerOperations.writeReg(VL53L0XRegisters.GLOBAL_CONFIG_SPAD_ENABLES_REF_0, ref_spad_map[0]);
        registerOperations.writeReg(VL53L0XRegisters.GLOBAL_CONFIG_SPAD_ENABLES_REF_1, ref_spad_map[1]);
        registerOperations.writeReg(VL53L0XRegisters.GLOBAL_CONFIG_SPAD_ENABLES_REF_2, ref_spad_map[2]);
        registerOperations.writeReg(VL53L0XRegisters.GLOBAL_CONFIG_SPAD_ENABLES_REF_3, ref_spad_map[3]);
        registerOperations.writeReg(VL53L0XRegisters.GLOBAL_CONFIG_SPAD_ENABLES_REF_4, ref_spad_map[4]);
        registerOperations.writeReg(VL53L0XRegisters.GLOBAL_CONFIG_SPAD_ENABLES_REF_5, ref_spad_map[5]);

        // -- VL53L0X_set_reference_spads() end
         // -- VL53L0X_load_tuning_settings() begin
        // DefaultTuningSettings from vl53l0x_tuning.h
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.SYSRANGE_START, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.SYSTEM_RANGE_CONFIG, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x10, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x11, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x24, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x25, 0xFF);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x75, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.DYNAMIC_SPAD_NUM_REQUESTED_REF_SPAD, 0x2C);
        registerOperations.writeReg(VL53L0XRegisters.FINAL_RANGE_CONFIG_VALID_PHASE_HIGH, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.PRE_RANGE_CONFIG_VALID_PHASE_HIGH, 0x20);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.PRE_RANGE_CONFIG_VALID_PHASE_HIGH, 0x09);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x54, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x31, 0x04);
        registerOperations.writeReg(VL53L0XRegisters.GLOBAL_CONFIG_VCSEL_WIDTH, 0x03);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x40, 0x83);
        registerOperations.writeReg(VL53L0XRegisters.MSRC_CONFIG_TIMEOUT_MACROP, 0x25);
        registerOperations.writeReg(VL53L0XRegisters.MSRC_CONFIG_CONTROL, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.PRE_RANGE_CONFIG_MIN_SNR, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.PRE_RANGE_CONFIG_VCSEL_PERIOD, 0x06);
        registerOperations.writeReg(VL53L0XRegisters.PRE_RANGE_CONFIG_TIMEOUT_MACROP_HI, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.PRE_RANGE_CONFIG_TIMEOUT_MACROP_LO, 0x96);
        registerOperations.writeReg(VL53L0XRegisters.PRE_RANGE_CONFIG_VALID_PHASE_LOW, 0x08);
        registerOperations.writeReg(VL53L0XRegisters.PRE_RANGE_CONFIG_VALID_PHASE_HIGH, 0x30);
        registerOperations.writeReg(VL53L0XRegisters.PRE_RANGE_CONFIG_SIGMA_THRESH_HI, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.PRE_RANGE_CONFIG_SIGMA_THRESH_LO, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.PRE_RANGE_MIN_COUNT_RATE_RTN_LIMIT, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x65, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x66, 0xA0);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x22, 0x32);
        registerOperations.writeReg(VL53L0XRegisters.FINAL_RANGE_CONFIG_VALID_PHASE_LOW, 0x14);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x49, 0xFF);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x4A, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x7A, 0x0A);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x7B, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x78, 0x21);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x23, 0x34);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x42, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT, 0xFF);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x45, 0x26);
        registerOperations.writeReg(VL53L0XRegisters.MSRC_CONFIG_TIMEOUT_MACROP, 0x05);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x40, 0x40);
        registerOperations.writeReg(VL53L0XRegisters.SYSTEM_THRESH_LOW, 0x06);
        registerOperations.writeReg(VL53L0XRegisters.CROSSTALK_COMPENSATION_PEAK_RATE_MCPS, 0x1A);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x43, 0x40);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x34, 0x03);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x35, 0x44);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x31, 0x04);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x4B, 0x09);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x4C, 0x05);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x4D, 0x04);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x45, 0x20);
        registerOperations.writeReg(VL53L0XRegisters.FINAL_RANGE_CONFIG_VALID_PHASE_LOW, 0x08);
        registerOperations.writeReg(VL53L0XRegisters.FINAL_RANGE_CONFIG_VALID_PHASE_HIGH, 0x28);
        registerOperations.writeReg(VL53L0XRegisters.FINAL_RANGE_CONFIG_MIN_SNR, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.FINAL_RANGE_CONFIG_VCSEL_PERIOD, 0x04);
        registerOperations.writeReg(VL53L0XRegisters.FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.FINAL_RANGE_CONFIG_TIMEOUT_MACROP_LO, 0xFE);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x76, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x77, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x0D, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.POWER_MANAGEMENT_GO1_POWER_FORCE, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.SYSTEM_SEQUENCE_CONFIG, 0xF8);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x8E, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.SYSRANGE_START, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.POWER_MANAGEMENT_GO1_POWER_FORCE, 0x00);
        // -- VL53L0X_load_tuning_settings() end
        

        // Configure GPIO1 for interrupt, active LOW
        byte actHIGH = registerOperations.readReg(VL53L0XRegisters.GPIO_HV_MUX_ACTIVE_HIGH);
        registerOperations.writeReg(VL53L0XRegisters.SYSTEM_INTERRUPT_CONFIG_GPIO, 0x04); // enable data ready interrupt
        registerOperations.writeReg(VL53L0XRegisters.GPIO_HV_MUX_ACTIVE_HIGH, (actHIGH & ~0x10)); // GPIO1 interrupt active LOW
        registerOperations.writeReg(VL53L0XRegisters.SYSTEM_INTERRUPT_CLEAR, 0x01); // clear interrupt

        // Get some basic information about the sensor
        byte val1 = registerOperations.readReg(VL53L0XRegisters.PRE_RANGE_CONFIG_VCSEL_PERIOD);
        SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                "PRE_RANGE_CONFIG_VCSEL_PERIOD= " + val1 + " decoded: " + VL53L0X_decode_vcsel_period(val1));

        val1 = registerOperations.readReg(VL53L0XRegisters.FINAL_RANGE_CONFIG_VCSEL_PERIOD);
        SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                "PRE_RANGE_CONFIG_VCSEL_PERIOD= " + val1 + " decoded: " + VL53L0X_decode_vcsel_period(val1));

        byte[] rawData = registerOperations.readRegs(VL53L0XRegisters.SYSTEM_INTERMEASUREMENT_PERIOD, 4);
        int IMPeriod = (((int) rawData[0]) << 24 | ((int) rawData[1]) << 16 | ((int) rawData[2]) << 8 | rawData[3]);
        SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                "System Inter-measurement period = " + IMPeriod + "ms");

        registerOperations.writeReg(VL53L0XRegisters.SYSRANGE_START, 0x02); // continuous mode and arm next shot
    }

    private short VL53L0X_decode_vcsel_period(short vcsel_period_reg)
    {
        // Converts the encoded VCSEL period register value into the real
        // period in PLL clocks
        return (short) ((vcsel_period_reg + 1) << 1);
    }

    private byte getSpadInfo() throws InterruptedException
    {
        byte tmp;

        registerOperations.writeReg(VL53L0XRegisters.POWER_MANAGEMENT_GO1_POWER_FORCE, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.SYSRANGE_START, 0x00);

        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x06);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x83, registerOperations.readReg(VL53L0XRegisters.UNKNOWN_ADDR_0x83) | 0x04);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x07);
        registerOperations.writeReg(VL53L0XRegisters.SYSTEM_HISTOGRAM_BIN, 0x01);

        registerOperations.writeReg(VL53L0XRegisters.POWER_MANAGEMENT_GO1_POWER_FORCE, 0x01);

        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x94, 0x6b);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x83, 0x00);
        while (registerOperations.readReg(VL53L0XRegisters.UNKNOWN_ADDR_0x83) == 0x00)
        {
            TimeUnit.MILLISECONDS.sleep(10);
            //if (checkTimeoutExpired()) { return false; }
        }
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x83, 0x01);
        tmp = registerOperations.readReg(VL53L0XRegisters.UNKNOWN_ADDR_0x92);


        registerOperations.writeReg(VL53L0XRegisters.SYSTEM_HISTOGRAM_BIN, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x06);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0x83, registerOperations.readReg(VL53L0XRegisters.UNKNOWN_ADDR_0x83) & ~0x04);
        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x01);
        registerOperations.writeReg(VL53L0XRegisters.SYSRANGE_START, 0x01);

        registerOperations.writeReg(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, 0x00);
        registerOperations.writeReg(VL53L0XRegisters.POWER_MANAGEMENT_GO1_POWER_FORCE, 0x00);

        return tmp;

    }

    @Override
    public void updateData() throws IOException
    {

//  byte intStatus = readByte(VL53L0X_ADDRESS, VL53L0X_REG_RESULT_RANGE_STATUS);// Poll for data ready
//  if(intStatus & 0x01) // poll for data ready
//  {

        if (!((registerOperations.readReg(VL53L0XRegisters.RESULT_INTERRUPT_STATUS) & 0x07) == 0)) // wait for data ready interrupt
        {
            registerOperations.writeReg(VL53L0XRegisters.SYSTEM_INTERRUPT_CLEAR, 0x01); // clear interrupt

            byte[] rangeData = registerOperations.readRegs(VL53L0XRegisters.RESULT_RANGE_STATUS, 14); // continuous ranging


            //for(int i = 1; i<= 14;i++) SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
            //        "byte " + i + " = " + rangeData[i-1]);

            byte devError = (byte) ((rangeData[0] & 0x78) >> 3); // Check for errors

            String error = "";
            if (devError == 0) error = "Data OK!";// No device error
            if (devError == 0x01) error = "VCSEL CONTINUITY TEST FAILURE!";
            if (devError == 0x02) error = "VCSEL WATCHDOG TEST FAILURE!";
            if (devError == 0x03) error = "NO VHV VALUE FOUND!";
            if (devError == 0x04) error = "MSRC NO TARGET!";
            if (devError == 0x05) error = "SNR CHECK!";
            if (devError == 0x06) error = "RANGE PHASE CHECK!";
            if (devError == 0x07) error = "SIGMA THRESHOLD CHECK!";
            if (devError == 0x08) error = "TCC!";
            if (devError == 0x09) error = "PHASE CONSISTENCY!";
            if (devError == 0x0A) error = "MIN CLIP!";
            if (devError == 0x0B) error = "RANGE COMPLETE!";
            if (devError == 0x0C) error = "ALGO UNDERFLOW!";
            if (devError == 0x0D) error = "ALGO OVERFLOW!";
            if (devError == 0x0E) error = "RANGE IGNORE THRESHOLD!";

            SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS, error);

            /*SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                    "Effective SPAD Return Count = " + ((float) (rangeData[2]) + (float)rangeData[3]/255.));
            SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                    "Signal Rate = " + (short) (((short) rangeData[6] << 8) | rangeData[7]) + " mega counts per second");
            SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_EVENTS,
                    "Ambient Rate = " + (short) (((short) rangeData[8] << 8) | rangeData[9]) + " mega counts per second");*/
            int distance = (((short) rangeData[10] << 8) | (rangeData[11] & 0xff));
            if (devError == 0 || devError == 0x0B)
            {
                SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_LOOPS,
                        "Distance = " + distance + " mm");
                this.addValue(new TimestampedData1f(distance));
            }
        } else SystemLog.log(SubSystem.SubSystemType.TESTING, SystemLog.LogLevel.TRACE_HW_WRITES, "Data not ready");
    }
}
