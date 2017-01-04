package dataTypes;

/**
 * dataTypes
 * Created by G.J.Wood 06/11/16
 */
@SuppressWarnings("MalformedFormatString")
public class Data1s
{
    protected short x;

    public Data1s(short x) {this.x = x;}

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
    
    public Data1s clone() {return new Data1s(x);}
    
    public Data1s multiply(Data1s a, Data1s b){
    	
		return new Data1s((short)(a.getX()*b.getX())); 	
    }
}
