/**
 * 
 */
package dataTypes;

/**
 * @author GJWood
 *
 */
public class Yaw extends Data <Float> {
 protected float yaw;
	/**
	 * Yaw	-	Constructor
	 */
	public Yaw() {
		super(0f);
	}

	/**
	 * @param x
	 */
	public Yaw(Float x) {
		super(x);
		yaw = x;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	@Override
	public String toString() {
        final String format = "%+08.3f";
        return 	"Yaw: " + String.format(format,yaw);
	}

	@Override
	public Yaw clone() {return new Yaw(yaw);}

	@Override
	public Yaw multiply(Data<Float> b) {return new Yaw(this.get()*b.get());}

	@Override
	public Float get() {return yaw;}

	@Override
	public void set(Float x) {yaw = x;}
}