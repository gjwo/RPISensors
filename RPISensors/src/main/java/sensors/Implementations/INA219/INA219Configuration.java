package sensors.Implementations.INA219;

import devices.I2C.RegisterSetting;

/**
 * RPISensors - sensors.Implementations.INA219
 * Created by MAWood on 05/01/2017.
 */
public class INA219Configuration
{
    private BusVoltageRange busVoltageRange;
    private CurrentGain currentGain;
    private AdcResolution adcResolution;
    private AveragingSetting averagingSetting;
    private OperatingMode operatingMode;
    private float maxExpectedCurrent;
    private static final float RSHUNT = 0.1f;

    INA219Configuration()
    {
        this(BusVoltageRange.BUS_VOLTAGE_RANGE_32V, CurrentGain.PGA_GAIN_DIV_8, AdcResolution.BASDC4_12BIT,
                AveragingSetting.SADC_12BIT, OperatingMode.MODE_SH_BUS_V_CONTINUOUS);
    }

    INA219Configuration(BusVoltageRange busVoltageRange, CurrentGain currentGain, AdcResolution adcResolution,
                  AveragingSetting averagingSetting, OperatingMode operatingMode)
    {
        this.busVoltageRange = busVoltageRange;
        this.currentGain = currentGain;
        this.adcResolution = adcResolution;
        this.averagingSetting = averagingSetting;
        this.operatingMode = operatingMode;
        this.maxExpectedCurrent = 3.2f;
    }

    public short getValue()
    {
        short value = 0;
        value |= operatingMode.getValue();
        value |= busVoltageRange.getValue();
        value |= currentGain.getValue();
        value |= adcResolution.getValue();
        value |= averagingSetting.getValue();
        return value;
    }

    public short getCalibrationValue()
    {
        float minLSB = maxExpectedCurrent/32767f;
        float maxLSB = maxExpectedCurrent/4096f;
        float currentLSB = (minLSB + maxLSB)/3f;
        float cal = 0.04096f/(currentLSB*RSHUNT);
        return (short) Math.round(cal);
    }

    public int getCurrentDivider()
    {
        double minLSB = maxExpectedCurrent/32767f;
        double maxLSB = maxExpectedCurrent/4096f;
        double currentLSB = (minLSB + maxLSB)/3f;
        currentLSB *= 1000000;
        return (int)Math.round(1000f/currentLSB);
    }

    public void setBusVoltageRange(BusVoltageRange busVoltageRange)
    {
        this.busVoltageRange = busVoltageRange;
    }

    public void setCurrentGain(CurrentGain currentGain)
    {
        this.currentGain = currentGain;
    }

    public void setAdcResolution(AdcResolution adcResolution)
    {
        this.adcResolution = adcResolution;
    }

    public void setAveragingSetting(AveragingSetting averagingSetting)
    {
        this.averagingSetting = averagingSetting;
    }

    public void setOperatingMode(OperatingMode operatingMode)
    {
        this.operatingMode = operatingMode;
    }

    public BusVoltageRange getBusVoltageRange()
    {
        return busVoltageRange;
    }

    public CurrentGain getCurrentGain()
    {
        return currentGain;
    }

    public AdcResolution getAdcResolution()
    {
        return adcResolution;
    }

    public AveragingSetting getAveragingSetting()
    {
        return averagingSetting;
    }

    public OperatingMode getOperatingMode()
    {
        return operatingMode;
    }

    enum BusVoltageRange implements RegisterSetting<Short>
    {
        BUS_VOLTAGE_RANGE_16V	((short)0x0000,(short) 0x2000), // Bit 13 BRNG 16 Volt Full Scale Reading
        BUS_VOLTAGE_RANGE_32V	((short)0x2000,(short) 0x2000); // Bit 13  BRNG 32 Volt Full Scale Reading (default)

        final short value;
        final short mask;

        BusVoltageRange(short value, short mask)
        {
            this.value = value;
            this.mask = mask;
        }
        public Short getValue() {return this.value;}
        public Short getMask()	{return this.mask;}
    }

    enum CurrentGain implements RegisterSetting<Short>
    {
        //bits 12-11 Sets PGA maxShunt and range. Note that the PGA defaults to ÷8 (320mV range).
        //Table below shows the maxShunt and range for the various product maxShunt settings.
        PGA_GAIN_DIV_1			((short)0x0000,(short) 0x1800, 0.040f), // bits 12&11 PGA (Shunt Voltage Only) 0 = /1 maxShunt BusVoltageRange +/- 40mV
        PGA_GAIN_DIV_2			((short)0x0800,(short) 0x1800, 0.080f), // bits 12&11 PGA (Shunt Voltage Only) 0 = /2 maxShunt BusVoltageRange +/- 80mV
        PGA_GAIN_DIV_4			((short)0x1000,(short) 0x1800, 0.160f), // bits 12&11 PGA (Shunt Voltage Only) 0 = /4 maxShunt BusVoltageRange +/- 160mV
        PGA_GAIN_DIV_8			((short)0x1800,(short) 0x1800, 0.320f); // bits 12&11 PGA (Shunt Voltage Only) 0 = /8 maxShunt BusVoltageRange +/- 320mV Default value

        final short value;
        final short mask;
        final float maxShunt;

        CurrentGain(short value, short mask, float gain)
        {
            this.value = value;
            this.mask = mask;
            this.maxShunt = gain;
        }
        public Short getValue() {return this.value;}
        public Short getMask()	{return this.mask;}
        float getMaxShunt()           {return this.maxShunt;}
    }

    enum AdcResolution implements RegisterSetting<Short>
    {
        // bits 10-07  BADC Bus ADC Resolution/Averaging
        // These bits adjust the Bus ADC resolution (9-, 10-, 11-, or 12-bit) or set the number of samples used when
        // averaging results for the Bus Voltage Register (02h).
        BASDC4_12BIT			((short)0x0400,(short) 0x0780), //bit patterns not yet proven
        BASDC3_11BIT			((short)0x0200,(short) 0x0780),
        BASDC2_10BIT			((short)0x0100,(short) 0x0780), //default
        BASDC1_9BIT				((short)0x0080,(short) 0x0780);

        final short value;
        final short mask;

        AdcResolution(short value, short mask)
        {
            this.value = value;
            this.mask = mask;
        }
        public Short getValue() {return this.value;}
        public Short getMask()	{return this.mask;}
    }

    enum AveragingSetting implements RegisterSetting<Short>
    {
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
        SADC_128SAMPLES			((short)0x0078,(short) 0x0078); // Averaging 128 samples 68.10 ms

        final short value;
        final short mask;

        AveragingSetting(short value, short mask)
        {
            this.value = value;
            this.mask = mask;
        }
        public Short getValue() {return this.value;}
        public Short getMask()	{return this.mask;}
    }

    enum OperatingMode implements RegisterSetting<Short>
    {
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

        OperatingMode(short value, short mask)
        {
            this.value = value;
            this.mask = mask;
        }
        public Short getValue() {return this.value;}
        public Short getMask()	{return this.mask;}
    }
}
