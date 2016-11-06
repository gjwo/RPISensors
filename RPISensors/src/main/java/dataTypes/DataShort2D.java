package dataTypes;

/**
 * RPIsensors.dataTypes
 * Created by G.J.Wood 6/11/2016.
 */
public class DataShort2D extends DataShort1D
{
    protected short y;

    public DataShort2D(short x,short y)
    {
        super(x);
        this.y = y;
    }

    public short getY()
    {
        return y;
    }

    public void setY(short y)
    {
        this.y = y;
    }

    public void scale(short xScale,short yScale)
    {
        super.scale(xScale);
        y *= yScale;
    }

    public void offset(short xOffset,short yOffset)
    {
        super.offset(xOffset);
        y += yOffset;
    }

    public String toString()
    {
        final String format = "%+04n";
        return 	super.toString() + " y: " + String.format(format,y);
    }
    public DataShort2D clone()
    {
        return new DataShort2D(x,y);
    }
}
