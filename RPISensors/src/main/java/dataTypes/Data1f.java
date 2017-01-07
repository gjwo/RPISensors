package dataTypes;

import java.io.Serializable;

/**
 * RPITank - devices.sensors.dataTypes
 * Created by MAWood on 18/07/2016.
 */
@SuppressWarnings("MethodDoesntCallSuperMethod")
public class Data1f implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -3533150776805377160L;
	float x;

    public Data1f(float x) {this.x = x;}

    @SuppressWarnings("WeakerAccess")
    public Data1f() {
		x=0;
	}

	public float getX() {return x;}
    public void setX(float x){this.x =x;}

    public String toString()
    {
        final String format = "%+08.3f";
        return 	"x: " + String.format(format,x);
    }
    
    public Data1f clone(){return new Data1f(x); }
    public Data1f multiply(Data1f data){return new Data1f(this.getX()*data.getX());}
    public Data1f add(Data1f data){return new Data1f(this.getX()+data.getX());}
}
