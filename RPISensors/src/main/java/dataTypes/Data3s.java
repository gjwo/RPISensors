package dataTypes;

/**
 * RPISensors.dataTypes
 * Created by G.J.Wood on 06/11/16.
 */
@SuppressWarnings("MalformedFormatString")
public class Data3s extends Data2s{

    private short z;

    @SuppressWarnings("WeakerAccess")
    public Data3s(short x, short y, short z) {
    	super(x,y);
        this.z = z;
    }

    public short getZ() {
        return z;
    }

	public void setZ(short z) {
		this.z = z;
	}

    public void scale(short xScale,short yScale,short zScale)
    {
        super.scale(xScale,yScale);
        z *= zScale;
    }

    public void offset(short xOffset,short yOffset,short zOffset)
    {
        super.offset(xOffset,yOffset);
        z += zOffset;
    }

    public void normalize(){
		short norm;
		// Normalise measurements
		norm = (short)Math.sqrt(x*x + y*y + z*z);
		if (norm == 0.0f)
			throw new ArithmeticException(); // handle NaN
		norm = (short)(1 / norm);
		x *= norm;
		y *= norm;
		z *= norm;
		
	}

	public String toString()
	{
		final String format = "%+04n";
		return 	super.toString() + " z: " + String.format(format,z);
	}
    public Data3s clone()
    {
        return new Data3s(x,y,z);
    }
}
