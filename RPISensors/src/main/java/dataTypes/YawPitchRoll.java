package dataTypes;

public class YawPitchRoll extends YawPitch {
	Float f3;

	public YawPitchRoll() {
		super();
		f3=0f;
	}

	public YawPitchRoll(Float yaw, Float pitch, Float roll) {
		super(yaw,pitch);
		f3=roll;
	}
	@Override
	public String toString() {
        final String format = "%+08.3f";
        return 	"Yaw: " + String.format(format,f1)+"Pitch: " + String.format(format,f2)+"Roll: " + String.format(format,f3);
	}

	@Override
	public YawPitchRoll clone() {return new YawPitchRoll(f1,f2,f3);}

	
	public YawPitchRoll multiply(YawPitchRoll b) {
		return new YawPitchRoll(f1*b.f1,f2*b.f2,f3*b.f3);
	}
}