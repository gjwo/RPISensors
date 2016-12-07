package dataTypes;

public class YawPitchRoll extends YawPitch {
	protected Float roll;

	public YawPitchRoll() {
		super();
		roll=0f;
	}

	public YawPitchRoll(Float yaw, Float pitch, Float roll) {
		super(yaw,pitch);
		this.roll=roll;
	}
	public YawPitchRoll(YawPitch yp,Float roll){
		super(yp);
		this.roll = roll;
	}
	
	public Float getRoll() {
		return roll;
	}

	public void setRoll(Float roll) {
		this.roll = roll;
	}

	@Override
	public String toString() {
        final String format = "%+08.3f";
        return 	super.toString()+" Roll: " + String.format(format,roll);
	}

	@Override
	public YawPitchRoll clone() {return new YawPitchRoll(yaw,pitch,roll);}

	
	public YawPitchRoll multiply(YawPitchRoll b) {
		return new YawPitchRoll(yaw*b.getYaw(),	pitch*b.pitch, roll*b.roll);
	}
}