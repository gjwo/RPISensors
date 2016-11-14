/**
 * 
 */
package dataTypes;

/**
 * @author GJWood
 *
 */
public class Data1D extends Data1 <Double> {

	public Data1D(Double x) {
		super(x);
	}
	@Override
   public String toString()
    {
        final String format = "%+07.3d";
        return 	"x: " + String.format(format,x);
    }
	@Override
    public Data1D clone()
    {
        return new Data1D (x);
    }
	@Override
	public Data1<Double> multiply(Data1<Double> a, Data1<Double> b) {
		return new Data1D (a.get()*b.get());
	}
}