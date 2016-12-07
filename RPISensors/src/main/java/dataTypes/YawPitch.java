package dataTypes;

public class YawPitch extends Yaw {
	protected Float pitch;

	public YawPitch() {
		super();
		pitch=0f;
	}

	public YawPitch(Float yaw, Float pitch) {
		super(yaw);
		this.pitch=pitch;
	}
	public YawPitch(YawPitch yp) {
		super.setYaw(yp.getYaw());
		this.pitch = yp.getPitch();
	}

	public Float getPitch() {
		return pitch;
	}

	public void setPitch(Float pitch) {
		this.pitch = pitch;
	}

	@Override
	public String toString() {
        final String format = "%+08.3f";
        return 	super.toString() + " Pitch: " + String.format(format,pitch);
	}

	@Override
	public YawPitch clone() {return new YawPitch(super.get(),pitch);}

	
	public YawPitch multiply(YawPitch b) {
		return new YawPitch(super.get()*b.get(),pitch*b.pitch);
	}
}