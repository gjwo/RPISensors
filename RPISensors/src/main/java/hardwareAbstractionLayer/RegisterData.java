package hardwareAbstractionLayer;

public class  RegisterData
{
	public enum HWDeviceType{MCU9250,VL53LOX}
	public enum RegisterSize{BITS8, BITS16, BITS32, BITS64}
	
	private final HWDeviceType deviceType;
	private final int deviceNumber;
	private final RegisterSize size;
	private final int address;
	private final String name;

	public RegisterData(HWDeviceType devType, int devNbr,RegisterSize size,String name, int address)
	{
		this.deviceType = devType;
		this.deviceNumber = devNbr;
		this.size = size;
		this.name = name;
		this.address = address;
	}
	
	//getters
	public HWDeviceType getDeviceType(){return deviceType;}
	public int getDeviceNumber(){return deviceNumber;}
	public int getAddress(){return address;}
	public String getName(){return name;}
	public RegisterSize getSize(){return size;}
	
	@SuppressWarnings("SameReturnValue")
	public String toString(int val){return null;}
}