package sensors.Implementations.INA219;

import deviceHardwareAbstractionLayer.RegisterSetting;
import utilities.Register;

public enum INA219Registers implements Register
{
    CONFIGURATION     	    (0x00), // Read Write 16 bit MSB First
    SHUNT_VOLTAGE_MEASURE	(0x01),	// Read Only 16 bit MSB First
    BUS_VOLTAGE_MEASURE	    (0x02), // Read Only 16 bit MSB First
    POWER_MEASURE			(0x03), // Read Only 16 bit MSB First
    CURRENT_MEASURE			(0x04), // Read Only 16 bit MSB First
    CALIBRATION			    (0X05); // Read Write 16 bit MSB First

    private final int address;
    
    INA219Registers(int address) {this.address = address;}
    
    @Override
    public int getAddress() {return this.address;}
    
    @Override
    public String getName() {return this.name();}
}

enum Configuration implements RegisterSetting<Short>
{
	RESET_DEFAULTS			((short)0x399F,(short) 0xCFFF),
	
	// Bit 15 Setting this bit to '1' generates a system reset that is the same as power-on reset
	// Resets all registers to default values; this bit self-clears.
	RESET_BIT				((short)0x8000,(short) 0x8000),
															
	BUS_VOLTAGE_RANGE_16V	((short)0x0000,(short) 0x2000), // Bit 13 BRNG 16 Volt Full Scale Reading
	BUS_VOLTAGE_RANGE_32V	((short)0x2000,(short) 0x2000), // Bit 13  BRNG 32 Volt Full Scale Reading (default)
	
	//bits 12-11 Sets PGA maxShunt and range. Note that the PGA defaults to ÷8 (320mV range).
	//Table below shows the maxShunt and range for the various product maxShunt settings.
	PGA_GAIN_DIV_1			((short)0x0000,(short) 0x1800), // bits 12&11 PGA (Shunt Voltage Only) 0 = /1 maxShunt OperatingMode +/- 40mV
	PGA_GAIN_DIV_2			((short)0x0800,(short) 0x1800), // bits 12&11 PGA (Shunt Voltage Only) 0 = /2 maxShunt OperatingMode +/- 80mV
	PGA_GAIN_DIV_4			((short)0x1000,(short) 0x1800), // bits 12&11 PGA (Shunt Voltage Only) 0 = /4 maxShunt OperatingMode +/- 160mV
	PGA_GAIN_DIV_8			((short)0x1800,(short) 0x1800), // bits 12&11 PGA (Shunt Voltage Only) 0 = /8 maxShunt OperatingMode +/- 320mV Default value
	
	// bits 10-07  BADC Bus ADC Resolution/Averaging
	// These bits adjust the Bus ADC resolution (9-, 10-, 11-, or 12-bit) or set the number of samples used when
	// averaging results for the Bus Voltage Register (02h).
	BASDC4_12BIT			((short)0x0400,(short) 0x0780), //bit patterns not yet proven
	BASDC3_11BIT			((short)0x0200,(short) 0x0780),
	BASDC2_10BIT			((short)0x0100,(short) 0x0780), //default
	BASDC1_9BIT				((short)0x0080,(short) 0x0780),

	// bits 6-3 SADC Shunt ADC Resolution/Averaging
	//These bits adjust the Shunt ADC resolution (9-, 10-, 11-, or 12-bit) or set the number of samples used when
	//averaging results for the Shunt Voltage Register (01h).
	//BADC (Bus) and SADC (Shunt) ADC resolution/averaging and conversion time settings are shown below
	SADC_9BIT				((short)0x0000,(short) 0x0078), // Resolution 9 bit 84 μs
	SADC_10BIT				((short)0x0008,(short) 0x0078), // Resolution 10 bit 148 μs
	SADC_11BIT				((short)0x0010,(short) 0x0078), // Resolution 11 bit 276 μs
	SADC_12BIT				((short)0x0018,(short) 0x0078), // Resolution 12 bit 532 μs (default)
	SADC_12BITX				((short)0x0040,(short) 0x0078), // Resolution 12 bit 532 μs
	SADC_2SAMPLES			((short)0x0048,(short) 0x0078), // Averaging 2 samples 1.06ms
	SADC_4SAMPLES			((short)0x0050,(short) 0x0078), // Averaging 4 samples 2.13 ms
	SADC_8SAMPLES			((short)0x0058,(short) 0x0078), // Averaging 8 samples 4.26 ms
	SADC_16SAMPLES			((short)0x0060,(short) 0x0078), // Averaging 16 samples 8.51 ms
	SADC_32SAMPLES			((short)0x0068,(short) 0x0078), // Averaging 32 samples 17.02 ms
	SADC_64SAMPLES			((short)0x0070,(short) 0x0078), // Averaging 64 samples 34.05 ms
	SADC_128SAMPLES			((short)0x0078,(short) 0x0078), // Averaging 128 samples 68.10 ms
	
	// Operating Mode bits 2-0
	// Selects continuous, triggered, or power-down mode of operation.
	// These bits default to continuous shunt and bus measurement mode. The mode settings are shown below
	MODE_POWER_DOWN			((short)0x0000,(short) 0x0007), // Power Down
	MODE_SH_V_TRIGGERED		((short)0x0001,(short) 0x0007), // Shunt voltage, triggered
	MODE_BUS_V_TRIGGERED	((short)0x0002,(short) 0x0007), // Bus voltage, triggered
	MODE_SH_BUS_V_TRIGGERED	((short)0x0003,(short) 0x0007), // Shunt and bus, triggered
	MODE_ADC_OFF			((short)0x0004,(short) 0x0007), // ADC off (disabled)
	MODE_SH_V_CONTINUOUS	((short)0x0005,(short) 0x0007), // Shunt voltage, continuous
	MODE_BUS_V_CONTINUOUS	((short)0x0006,(short) 0x0007), // Bus voltage, continuous
	MODE_SH_BUS_V_CONTINUOUS((short)0x0007,(short) 0x0007); // Shunt and bus voltage, continuous
	
	final short value;
	final short mask;
	
	Configuration( short value,short mask)
	{
		this.value = value;
		this.mask = mask;
	}
	public Short getBits() {return this.value;}
	public Short getMask()	{return this.mask;}
}

enum BusVoltage 
{
	//Read Only Register
	//The Bus Voltage register stores the most recent bus voltage reading, VBUS.
	//At full-scale range = 32 V (decimal = 8000, hex = 1F40), and LSB = 4 mV	
	BUS_VOLTAGE			((short)0x0000,(short) 0xFFF8), //bits 15-3 At full-scale range = 16 V (decimal = 4000, hex = 0FA0), and LSB = 4 mV
	
	//Although the data from the last conversion can be read at any time, the INA219 Conversion Ready bit (CNVR)
	//indicates when data from a conversion is available in the data output registers. The CNVR bit is set after all
	//conversions, averaging, and multiplications are complete. CNVR will clear under the following conditions:
	//1.) Writing a new mode into the Operating Mode bits in the Configuration Register (except for Power-Down or Disable)
	//2.) Reading the Power Register	
	CONVERSION_READY	((short)0x0002,(short) 0x0002), //bit 1
	
	// Math Overflow Flag
	// The Math Overflow Flag (OVF) is set when the Power or Current calculations are out of range.
	// It indicates that current and power data may be meaningless.
	OVERFLOW_FLAG		((short)0x0001,(short) 0x0001); //bit 0

	final short value;
	final short mask;
	
	BusVoltage( short value,short mask)
	{
		this.value = value;
		this.mask = mask;
	}
	short getValue() {return this.value;}
	short getMask()	{return this.mask;}
	static short calcBusVoltage(short rv){return (short)( (rv>>3) & 0x1FFF);} // Shift right and mask out any sign bits
	static boolean isBusVoltageValid(short rv){return ((rv&2)==2) && ((rv&1)==0);} // ready and no overflow
}

/*
 * Calibration Register (address = 05h) [reset = 00h]
 * Current and power calibration are set by bits FS15 to FS1 of the Calibration register. Note that bit FS0 is not
 * used in the calculation. This register sets the current that corresponds to a full-scale drop across the shunt. FullScale
 * range and the LSB of the current and power measurement depend on the value entered in this register.
 * See the Programming the Calibration Register. This register is suitable for use in overall system calibration. Note
 * that the 0 POR values are all default.
 * Figure 27. Calibration Register(1)
 * 15 	14 	13 	12 	11 	10 	9 	8 	7 	6 	5 	4 	3 	2 	1 	0
 * FS15 FS14 FS13 FS12 FS11 FS10 FS9 FS8 FS7 FS6 FS5 FS4 FS3 FS2 FS1 FS0
 * R/W-0 R/W-0 R/W-0 R/W-0 R/W-0 R/W-0 R/W-0 R/W-0 R/W-0 R/W-0 R/W-0 R/W-0 R/W-0 R/W-0 R/W-0 R-0
 * LEGEND: R/W = Read/Write; R = Read only; -n = value after reset
 * (1) FS0 is a void bit and will always be 0. It is not possible to write a 1 to FS0. CALIBRATION is the value stored in FS15:FS1.
 */