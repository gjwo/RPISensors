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
		// TODO Auto-generated constructor stub
	}
	@Override
    public void scale(Double xScale)
    {
    	System.out.println("Function not implemented");
    }

	@Override
    public void offset(Double xOffset)
    {
    	System.out.println("Function not implemented");
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
        return new Data1D(x);
    }
    public Data1D multiply(Data1D a, Data1D b){
    	System.out.println("Function not implemented");
    	return null;
    }

}
