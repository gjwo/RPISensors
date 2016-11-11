/**
 * 
 */
package dataTypes;

/**
 * @author GJWood
 *
 */
public class Yaw extends Data <Float> {

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
	}

	@Override
	public String toString() {
        final String format = "%+08.3f";
        return 	"Yaw: " + String.format(format,f1);
	}

	@Override
	public Yaw clone() {return new Yaw(this.getF1());}

	@Override
	public Yaw multiply(Data<Float> b) {return new Yaw(f1*b.f1);}
}