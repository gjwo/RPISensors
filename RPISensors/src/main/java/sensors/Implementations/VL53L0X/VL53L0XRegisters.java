package sensors.Implementations.VL53L0X;

import utilities.Register;

/**
 * RPISensors - sensors.Implementations.VL53L0XRanger
 * Created by MAWood on 27/12/2016.
 */
    public enum VL53L0XRegisters implements Register
    {
        VL53L0X_ADDRESS                            (0x29),
        SYSRANGE_START                             (0x00), // mode register 
        														// 1=VL53L0X_REG_SYSRANGE_MODE_START_STOP or VL53L0X_REG_SYSRANGE_MODE_SINGLESHOT
        														// 2=VL53L0X_REG_SYSRANGE_MODE_BACKTOBACK
        														// 4=VL53L0X_REG_SYSRANGE_MODE_TIMED

        SYSTEM_THRESH_HIGH                         (0x0C), // 16 bit MSB?
        SYSTEM_THRESH_LOW                          (0x0E), // 16 bit MSB? val 0x06

        SYSTEM_SEQUENCE_CONFIG                     (0x01), // vals 0xFF, 0xE8, 0x01, 0x02, 0xE8, 0xF8, 0x02 (single ref calibration)
        SYSTEM_RANGE_CONFIG                        (0x09), //vals 0
        SYSTEM_INTERMEASUREMENT_PERIOD             (0x04), // stores 32 bit quantity value is in milliseconds

        SYSTEM_INTERRUPT_CONFIG_GPIO               (0x0A),

        GPIO_HV_MUX_ACTIVE_HIGH                    (0x84),

        SYSTEM_INTERRUPT_CLEAR                     (0x0B),

        RESULT_INTERRUPT_STATUS                    (0x13),
        RESULT_RANGE_STATUS                        (0x14),

        RESULT_CORE_AMBIENT_WINDOW_EVENTS_RTN      (0xBC),
        RESULT_CORE_RANGING_TOTAL_EVENTS_RTN       (0xC0),
        RESULT_CORE_AMBIENT_WINDOW_EVENTS_REF      (0xD0),
        RESULT_CORE_RANGING_TOTAL_EVENTS_REF       (0xD4),
        RESULT_PEAK_SIGNAL_RATE_REF                (0xB6),

        ALGO_PART_TO_PART_RANGE_OFFSET_MM          (0x28),

        I2C_SLAVE_DEVICE_ADDRESS                   (0x8A),

        MSRC_CONFIG_CONTROL                        (0x60), // val 0

        PRE_RANGE_CONFIG_MIN_SNR                   (0x27), // val 0
        PRE_RANGE_CONFIG_VALID_PHASE_LOW           (0x56), // 16 bit LSB val 8
        PRE_RANGE_CONFIG_VALID_PHASE_HIGH          (0x57), // val 30
        PRE_RANGE_MIN_COUNT_RATE_RTN_LIMIT         (0x64), // val 0

        FINAL_RANGE_CONFIG_MIN_SNR                 (0x67), // val 0
        FINAL_RANGE_CONFIG_VALID_PHASE_LOW         (0x47), // 16 bit LSB val 0x14
        FINAL_RANGE_CONFIG_VALID_PHASE_HIGH        (0x48), // val 0
        FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT(0x44), // val 0,FF

        PRE_RANGE_CONFIG_SIGMA_THRESH_HI           (0x61), // val 0
        PRE_RANGE_CONFIG_SIGMA_THRESH_LO           (0x62), // val 0

        PRE_RANGE_CONFIG_VCSEL_PERIOD              (0x50), // val 0x06
        PRE_RANGE_CONFIG_TIMEOUT_MACROP_HI         (0x51), // 16 bit MSB val 0
        PRE_RANGE_CONFIG_TIMEOUT_MACROP_LO         (0x52), // val 0x96

        SYSTEM_HISTOGRAM_BIN                       (0x81),
        HISTOGRAM_CONFIG_INITIAL_PHASE_SELECT      (0x33),
        HISTOGRAM_CONFIG_READOUT_CTRL              (0x55),

        FINAL_RANGE_CONFIG_VCSEL_PERIOD            (0x70), // val 0
        FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI       (0x71), // 16 bit MSB val 1
        FINAL_RANGE_CONFIG_TIMEOUT_MACROP_LO       (0x72), // val 0xFE
        CROSSTALK_COMPENSATION_PEAK_RATE_MCPS      (0x20), // val 0x1A

        MSRC_CONFIG_TIMEOUT_MACROP                 (0x46), //val 0x25, 0x05

        SOFT_RESET_GO2_SOFT_RESET_N                (0xBF),
        IDENTIFICATION_MODEL_ID                    (0xC0), //see WHO_AM_I
        IDENTIFICATION_REVISION_ID                 (0xC2),

        OSC_CALIBRATE_VAL                          (0xF8),

        GLOBAL_CONFIG_VCSEL_WIDTH                  (0x32), //val 3
        GLOBAL_CONFIG_SPAD_ENABLES_REF_0           (0xB0), //6 adjacent bytes ref_spad_map
        GLOBAL_CONFIG_SPAD_ENABLES_REF_1           (0xB1),
        GLOBAL_CONFIG_SPAD_ENABLES_REF_2           (0xB2),
        GLOBAL_CONFIG_SPAD_ENABLES_REF_3           (0xB3),
        GLOBAL_CONFIG_SPAD_ENABLES_REF_4           (0xB4),
        GLOBAL_CONFIG_SPAD_ENABLES_REF_5           (0xB5),

        GLOBAL_CONFIG_REF_EN_START_SELECT          (0xB6),
        DYNAMIC_SPAD_NUM_REQUESTED_REF_SPAD        (0x4E), // val 0x2C
        DYNAMIC_SPAD_REF_EN_START_OFFSET           (0x4F),
        POWER_MANAGEMENT_GO1_POWER_FORCE           (0x80), // Power Management 1 might cause a reset, 0 is normal?

        VHV_CONFIG_PAD_SCL_SDA__EXTSUP_HV          (0x89),

        ALGO_PHASECAL_LIM                          (0x30), // val 0x20 val 0x09
        ALGO_PHASECAL_CONFIG_TIMEOUT               (0x30),

        RANGE_TYPE_ADDR							   (0x91), // single shot = 0
        WHO_AM_I                                   (0xC0), // should be 0x40
        
        UNKNOWN_ADDR_0x0D						   (0x0D), // val 01
        UNKNOWN_ADDR_0x10						   (0x10), // val 0
        UNKNOWN_ADDR_0x11						   (0x11), // val 0
        UNKNOWN_ADDR_0x22						   (0x22), // val 0x32
        UNKNOWN_ADDR_0x23						   (0x23), // val 0x34
        UNKNOWN_ADDR_0x24						   (0x24), // val 0,1
        UNKNOWN_ADDR_0x25						   (0x25), // val 0,FF
        UNKNOWN_ADDR_0x31						   (0x31), // val 4
        UNKNOWN_ADDR_0x34						   (0x34), // val 0x03
        UNKNOWN_ADDR_0x35						   (0x35), // val 0x44
        UNKNOWN_ADDR_0x40						   (0x40), // val 0x83, 0x40
        UNKNOWN_ADDR_0x42						   (0x42), // val 0
        UNKNOWN_ADDR_0x43						   (0x43), // val 0x40
        UNKNOWN_ADDR_0x45						   (0x45), // val 0x26 FINAL_RANGE_CONFIG?
        UNKNOWN_ADDR_0x49						   (0x49), // val FF FINAL_RANGE_CONFIG?
        UNKNOWN_ADDR_0x4A						   (0x4A), // val 0
        UNKNOWN_ADDR_0x4B						   (0x4B), // val 0x09
        UNKNOWN_ADDR_0x4C						   (0x4C), // val 0x05
        UNKNOWN_ADDR_0x4D						   (0x4D), // val 0x04
        UNKNOWN_ADDR_0x54						   (0x54), // val 0
        UNKNOWN_ADDR_0x65						   (0x65), // val 0
        UNKNOWN_ADDR_0x66						   (0x66), // val 0xA0
        UNKNOWN_ADDR_0x75						   (0x75), // val 0
        UNKNOWN_ADDR_0x76						   (0x76), // val 0
        UNKNOWN_ADDR_0x77						   (0x77), // val 0
        UNKNOWN_ADDR_0x78						   (0x78), // val 0x21
        UNKNOWN_ADDR_0x7A						   (0x7A), // val 0xA0
        UNKNOWN_ADDR_0x7B						   (0x7B), // val 0
        UNKNOWN_ADDR_0x83						   (0x83), // mask of 4 used
        UNKNOWN_ADDR_0x88						   (0x88), // val 0
        UNKNOWN_ADDR_0x8E						   (0x83), // val 0x01
        UNKNOWN_ADDR_0x92						   (0x92), // val 0
        UNKNOWN_ADDR_0x94						   (0x94), // val 0
    	UNKNOWN_ADDR_0xFF						   (0xFF); // possibly mode of access is toggled around setting some registers - bank switch?
    															// e.g. ALGO_PHASECAL_LIM, RANGE_TYPE_ADDR, SYSRANGE_START
    															// vals of 0,1,6,7
    	

        private final int address;
        VL53L0XRegisters(int addr)
        {
            this.address = addr;
        }
        public int getAddress()
        {
            return address;
        }
		@Override
		public String getName()
		{
			return this.name();
		}
}
