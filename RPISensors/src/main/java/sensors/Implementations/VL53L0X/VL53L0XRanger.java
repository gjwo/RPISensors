package sensors.Implementations.VL53L0X;

import dataTypes.TimestampedData1f;
import hardwareAbstractionLayer.Device;
import hardwareAbstractionLayer.RegisterOperations;
import logging.SystemLog;
import sensors.models.Sensor1D;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import subsystems.SubSystem;

/**
 * RPISensors - sensors.Implementations.VL53L0XRanger
 * Created by MAWood on 27/12/2016.
 * Based off of kriswiner's work with the sensor in C++
 */
public class VL53L0XRanger extends Sensor1D
{
    private final RegisterOperations registerOperations;
    private final HashMap<Integer,String> errorMap;

    VL53L0XRanger(Device device, int sampleSize)
    {
        super(sampleSize);
        errorMap = new HashMap<>(16);
        errorMap.put(0x00, "Data OK!");// No device error
        errorMap.put(0x01, "VCSEL CONTINUITY TEST FAILURE!");
        errorMap.put(0x02, "VCSEL WATCHDOG TEST FAILURE!");
        errorMap.put(0x03, "NO VHV VALUE FOUND!");
        errorMap.put(0x04, "MSRC NO TARGET!");
        errorMap.put(0x05, "SNR CHECK!");
        errorMap.put(0x06, "RANGE PHASE CHECK!");
        errorMap.put(0x07, "SIGMA THRESHOLD CHECK!");
        errorMap.put(0x08, "TCC!");
        errorMap.put(0x09, "PHASE CONSISTENCY!");
        errorMap.put(0x0A, "MIN CLIP!");
        errorMap.put(0x0B, "RANGE COMPLETE!");
        errorMap.put(0x0C, "ALGO UNDERFLOW!");
        errorMap.put(0x0D, "ALGO OVERFLOW!");
        errorMap.put(0x0E, "RANGE IGNORE THRESHOLD!");

        registerOperations = new RegisterOperations(device);
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
        byte HVI2C = registerOperations.readByte(VL53L0XRegisters.VHV_CONFIG_PAD_SCL_SDA__EXTSUP_HV);
        registerOperations.writeByte(VL53L0XRegisters.VHV_CONFIG_PAD_SCL_SDA__EXTSUP_HV, (byte)(HVI2C | 0x01)); // set device HIGH to 2.8 V


        SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_HW_EVENTS,
                "VL53L0XRanger proximity sensor...");
        byte c = registerOperations.readByte(VL53L0XRegisters.WHO_AM_I);  // Read WHO_AM_I register for VL53L0XRanger
        SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_HW_EVENTS,
                "I AM " + c + " I should be " + (byte) 0xEE);

        // Get info about the specific device
        byte revID = registerOperations.readByte(VL53L0XRegisters.IDENTIFICATION_REVISION_ID);  // Read Revision ID register for VL53L0XRanger
        SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_HW_EVENTS,
                "Revision ID: " + revID);
        TimeUnit.SECONDS.sleep(1);


        registerOperations.writeByte(VL53L0XRegisters.SOFT_RESET_GO2_SOFT_RESET_N, (byte) 0x01);  // reset device

        TimeUnit.MILLISECONDS.sleep(100);

        HVI2C = registerOperations.readByte(VL53L0XRegisters.VHV_CONFIG_PAD_SCL_SDA__EXTSUP_HV);
        registerOperations.writeByte(VL53L0XRegisters.VHV_CONFIG_PAD_SCL_SDA__EXTSUP_HV, (byte)(HVI2C |  0x01)); // set device HIGH to 2.8 V

        // "Set device standard mode"
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x88, (byte) 0x00);

        registerOperations.writeByte(VL53L0XRegisters.POWER_MANAGEMENT_GO1_POWER_FORCE, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.SYSRANGE_START, (byte) 0x00);
        byte stop_variable = registerOperations.readByte(VL53L0XRegisters.RANGE_TYPE_ADDR);
        registerOperations.writeByte(VL53L0XRegisters.SYSRANGE_START, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.POWER_MANAGEMENT_GO1_POWER_FORCE, (byte) 0x00);


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
        ref_spad_map = registerOperations.readBytes(VL53L0XRegisters.GLOBAL_CONFIG_SPAD_ENABLES_REF_0, 6);

        // -- VL53L0X_set_reference_spads() begin (assume NVM values are valid)

        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.DYNAMIC_SPAD_REF_EN_START_OFFSET, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.DYNAMIC_SPAD_NUM_REQUESTED_REF_SPAD, (byte) 0x2C);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.GLOBAL_CONFIG_REF_EN_START_SELECT, (byte) 0xB4);

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

        registerOperations.writeByte(VL53L0XRegisters.GLOBAL_CONFIG_SPAD_ENABLES_REF_0, ref_spad_map[0]);
        registerOperations.writeByte(VL53L0XRegisters.GLOBAL_CONFIG_SPAD_ENABLES_REF_1, ref_spad_map[1]);
        registerOperations.writeByte(VL53L0XRegisters.GLOBAL_CONFIG_SPAD_ENABLES_REF_2, ref_spad_map[2]);
        registerOperations.writeByte(VL53L0XRegisters.GLOBAL_CONFIG_SPAD_ENABLES_REF_3, ref_spad_map[3]);
        registerOperations.writeByte(VL53L0XRegisters.GLOBAL_CONFIG_SPAD_ENABLES_REF_4, ref_spad_map[4]);
        registerOperations.writeByte(VL53L0XRegisters.GLOBAL_CONFIG_SPAD_ENABLES_REF_5, ref_spad_map[5]);

        // -- VL53L0X_set_reference_spads() end
         // -- VL53L0X_load_tuning_settings() begin
        // DefaultTuningSettings from vl53l0x_tuning.h
        /*
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.SYSRANGE_START, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.SYSTEM_RANGE_CONFIG, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x10, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x11, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x24, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x25, (byte) 0xFF);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x75, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.DYNAMIC_SPAD_NUM_REQUESTED_REF_SPAD, (byte) 0x2C);
        registerOperations.writeByte(VL53L0XRegisters.FINAL_RANGE_CONFIG_VALID_PHASE_HIGH, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.PRE_RANGE_CONFIG_VALID_PHASE_HIGH, (byte) 0x20);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.PRE_RANGE_CONFIG_VALID_PHASE_HIGH, (byte) 0x09);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x54, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x31, (byte) 0x04);
        registerOperations.writeByte(VL53L0XRegisters.GLOBAL_CONFIG_VCSEL_WIDTH, (byte) 0x03);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x40, (byte) 0x83);
        registerOperations.writeByte(VL53L0XRegisters.MSRC_CONFIG_TIMEOUT_MACROP, (byte) 0x25);
        registerOperations.writeByte(VL53L0XRegisters.MSRC_CONFIG_CONTROL, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.PRE_RANGE_CONFIG_MIN_SNR, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.PRE_RANGE_CONFIG_VCSEL_PERIOD, (byte) 0x06);
        registerOperations.writeByte(VL53L0XRegisters.PRE_RANGE_CONFIG_TIMEOUT_MACROP_HI, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.PRE_RANGE_CONFIG_TIMEOUT_MACROP_LO, (byte) 0x96);
        registerOperations.writeByte(VL53L0XRegisters.PRE_RANGE_CONFIG_VALID_PHASE_LOW, (byte) 0x08);
        registerOperations.writeByte(VL53L0XRegisters.PRE_RANGE_CONFIG_VALID_PHASE_HIGH, (byte) 0x30);
        registerOperations.writeByte(VL53L0XRegisters.PRE_RANGE_CONFIG_SIGMA_THRESH_HI, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.PRE_RANGE_CONFIG_SIGMA_THRESH_LO, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.PRE_RANGE_MIN_COUNT_RATE_RTN_LIMIT, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x65, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x66, (byte) 0xA0);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x22, (byte) 0x32);
        registerOperations.writeByte(VL53L0XRegisters.FINAL_RANGE_CONFIG_VALID_PHASE_LOW, (byte) 0x14);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x49, (byte) 0xFF);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x4A, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x7A, (byte) 0x0A);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x7B, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x78, (byte) 0x21);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x23, (byte) 0x34);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x42, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT, (byte) 0xFF);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x45, (byte) 0x26);
        registerOperations.writeByte(VL53L0XRegisters.MSRC_CONFIG_TIMEOUT_MACROP, (byte) 0x05);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x40, (byte) 0x40);
        registerOperations.writeByte(VL53L0XRegisters.SYSTEM_THRESH_LOW, (byte) 0x06);
        registerOperations.writeByte(VL53L0XRegisters.CROSSTALK_COMPENSATION_PEAK_RATE_MCPS, (byte) 0x1A);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x43, (byte) 0x40);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x34, (byte) 0x03);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x35, (byte) 0x44);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x31, (byte) 0x04);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x4B, (byte) 0x09);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x4C, (byte) 0x05);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x4D, (byte) 0x04);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x45, (byte) 0x20);
        registerOperations.writeByte(VL53L0XRegisters.FINAL_RANGE_CONFIG_VALID_PHASE_LOW, (byte) 0x08);
        registerOperations.writeByte(VL53L0XRegisters.FINAL_RANGE_CONFIG_VALID_PHASE_HIGH, (byte) 0x28);
        registerOperations.writeByte(VL53L0XRegisters.FINAL_RANGE_CONFIG_MIN_SNR, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.FINAL_RANGE_CONFIG_VCSEL_PERIOD, (byte) 0x04);
        registerOperations.writeByte(VL53L0XRegisters.FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.FINAL_RANGE_CONFIG_TIMEOUT_MACROP_LO, (byte) 0xFE);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x76, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x77, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x0D, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.POWER_MANAGEMENT_GO1_POWER_FORCE, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.SYSTEM_SEQUENCE_CONFIG, (byte) 0xF8);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x8E, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.SYSRANGE_START, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.POWER_MANAGEMENT_GO1_POWER_FORCE, (byte) 0x00);
        // -- VL53L0X_load_tuning_settings() end
        */

        // Configure GPIO1 for interrupt, active LOW
        byte actHIGH = registerOperations.readByte(VL53L0XRegisters.GPIO_HV_MUX_ACTIVE_HIGH);
        registerOperations.writeByte(VL53L0XRegisters.SYSTEM_INTERRUPT_CONFIG_GPIO, (byte) 0x04); // enable data ready interrupt
        registerOperations.writeByte(VL53L0XRegisters.GPIO_HV_MUX_ACTIVE_HIGH, (byte) (actHIGH & ~0x10)); // GPIO1 interrupt active LOW
        registerOperations.writeByte(VL53L0XRegisters.SYSTEM_INTERRUPT_CLEAR, (byte) 0x01); // clear interrupt

        // Get some basic information about the sensor
        byte val1 = registerOperations.readByte(VL53L0XRegisters.PRE_RANGE_CONFIG_VCSEL_PERIOD);
        SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_HW_EVENTS,
                "PRE_RANGE_CONFIG_VCSEL_PERIOD= " + val1 + " decoded: " + VL53L0X_decode_vcsel_period(val1));

        val1 = registerOperations.readByte(VL53L0XRegisters.FINAL_RANGE_CONFIG_VCSEL_PERIOD);
        SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_HW_EVENTS,
                "PRE_RANGE_CONFIG_VCSEL_PERIOD= " + val1 + " decoded: " + VL53L0X_decode_vcsel_period(val1));

        byte[] rawData = registerOperations.readBytes(VL53L0XRegisters.SYSTEM_INTERMEASUREMENT_PERIOD, 4);
        int IMPeriod = (((int) rawData[0]) << 24 | ((int) rawData[1]) << 16 | ((int) rawData[2]) << 8 | rawData[3]);
        SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_HW_EVENTS,
                "System Inter-measurement period = " + IMPeriod + "ms");

        registerOperations.writeByte(VL53L0XRegisters.SYSRANGE_START, (byte) 0x02); // continuous mode and arm next shot
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

        registerOperations.writeByte(VL53L0XRegisters.POWER_MANAGEMENT_GO1_POWER_FORCE, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.SYSRANGE_START, (byte) 0x00);

        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x06);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x83, (byte)(registerOperations.readByte(VL53L0XRegisters.UNKNOWN_ADDR_0x83) | 0x04));
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x07);
        registerOperations.writeByte(VL53L0XRegisters.SYSTEM_HISTOGRAM_BIN, (byte) 0x01);

        registerOperations.writeByte(VL53L0XRegisters.POWER_MANAGEMENT_GO1_POWER_FORCE, (byte) 0x01);

        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x94, (byte) 0x6b);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x83, (byte) 0x00);
        while (registerOperations.readByte(VL53L0XRegisters.UNKNOWN_ADDR_0x83) == 0x00)
        {
            TimeUnit.MILLISECONDS.sleep(10);
            //if (checkTimeoutExpired()) { return false; }
        }
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x83, (byte) 0x01);
        tmp = registerOperations.readByte(VL53L0XRegisters.UNKNOWN_ADDR_0x92);


        registerOperations.writeByte(VL53L0XRegisters.SYSTEM_HISTOGRAM_BIN, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x06);
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0x83, (byte) (registerOperations.readByte(VL53L0XRegisters.UNKNOWN_ADDR_0x83) & ~0x04));
        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x01);
        registerOperations.writeByte(VL53L0XRegisters.SYSRANGE_START, (byte) 0x01);

        registerOperations.writeByte(VL53L0XRegisters.UNKNOWN_ADDR_0xFF, (byte) 0x00);
        registerOperations.writeByte(VL53L0XRegisters.POWER_MANAGEMENT_GO1_POWER_FORCE, (byte) 0x00);

        return tmp;

    }

    @Override
    public void updateData() {

//  byte intStatus = readByte(VL53L0X_ADDRESS, VL53L0X_REG_RESULT_RANGE_STATUS);// Poll for data ready
//  if(intStatus & 0x01) // poll for data ready
//  {

        if (!((registerOperations.readByte(VL53L0XRegisters.RESULT_INTERRUPT_STATUS) & 0x07) == 0)) // wait for data ready interrupt
        {
            registerOperations.writeByte(VL53L0XRegisters.SYSTEM_INTERRUPT_CLEAR, (byte) 0x01); // clear interrupt

            byte[] rangeData = registerOperations.readBytes(VL53L0XRegisters.RESULT_RANGE_STATUS, 14); // continuous ranging


            //for(int i = 1; i<= 14;i++) SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_HW_EVENTS,
            //        "byte " + i + " = " + rangeData[i-1]);

            byte devError = (byte) ((rangeData[0] & 0x78) >> 3); // Check for errors
            //SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_HW_EVENTS, errorMap.get(devError));

            /*SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_HW_EVENTS,
                    "Effective SPAD Return Count = " + ((float) (rangeData[2]) + (float)rangeData[3]/255.));
            SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_HW_EVENTS,
                    "Signal Rate = " + (short) (((short) rangeData[6] << 8) | rangeData[7]) + " mega counts per second");
            SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_HW_EVENTS,
                    "Ambient Rate = " + (short) (((short) rangeData[8] << 8) | rangeData[9]) + " mega counts per second");*/
            int distance = (((short) rangeData[10] << 8) | (rangeData[11] & 0xff));
            if (devError == 0 || devError == 0x0B)
            {
                //SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_LOOPS,
                //        "Distance = " + distance + " mm");
                this.addValue(new TimestampedData1f(distance));
            }
        } else SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_HW_WRITES, "Data not ready");
    }

    public int getRangingTimeBudget(){ return 30;} //milliseconds}
}