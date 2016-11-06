package dataTypes;

/**
 * RPITank - devices.sensors.dataTypes
 * Created by MAWood on 18/07/2016.
 */
public class DataFloat2D extends DataFloat1D
{
    protected float y;

    public DataFloat2D(float x,float y)
    {
        super(x);
        this.y = y;
    }

    public float getY()
    {
        return y;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public void scale(float xScale,float yScale)
    {
        super.scale(xScale);
        y *= yScale;
    }

    public void offset(float xOffset,float yOffset)
    {
        super.offset(xOffset);
        y += yOffset;
    }

    public String toString()
    {
        final String format = "%+04.3f";
        return 	super.toString() + " y: " + String.format(format,y);
    }
    public DataFloat2D clone()
    {
        return new DataFloat2D(x,y);
    }
}
