package dataTypes;

public class YawPitch extends Yaw {
	Float f2;

	public YawPitch() {
		super();
		f2=0f;
	}

	public YawPitch(Float yaw, Float pitch) {
		super(yaw);
		f2=pitch;
	}
	@Override
	public String toString() {
        final String format = "%+08.3f";
        return 	"Yaw: " + String.format(format,f1)+"Pitch: " + String.format(format,f2);
	}

	@Override
	public YawPitch clone() {return new YawPitch(f1,f2);}

	
	public YawPitch multiply(YawPitch b) {
		return new YawPitch(f1*b.f1,f2*b.f2);
	}
}