package dataTypes;

/**
 * dataTypes
 * Created by G.J.Wood 06/11/16
 */
public class DataShort1D
{
    protected short x;

    public DataShort1D(short x) {this.x = x;}

    public short getX() {return x;}

    public void setX(short x) {this.x = x;}

    public void scale(short xScale)
    {
        x *= xScale;
    }

    public void offset(short xOffset)
    {
        x += xOffset;
    }

    public String toString()
    {
        final String format = "%+04n";
        return 	"x: " + String.format(format,x);
    }
    
    public DataShort1D clone() {return new DataShort1D(x);}
    
    public DataShort1D multiply(DataShort1D a, DataShort1D b){
    	
		return new DataShort1D((short)(a.getX()*b.getX())); 	
    }
}
