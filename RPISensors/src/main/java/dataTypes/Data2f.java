package dataTypes;

/**
 * RPITank - devices.sensors.dataTypes
 * Created by MAWood on 18/07/2016.
 */
public class Data2f extends Data1f
{
    protected float y;

    public Data2f(float x,float y)
    {
        super(x);
        this.y = y;
    }

    public float getY(){return y;}
    public void setY(float y){this.y = y;}

    public String toString()
    {
        final String format = "%+08.3f";
        return 	super.toString() + " y: " + String.format(format,y);
    }
    
    public Data2f clone(){return new Data2f(x,y);}
    public Data2f multiply(Data2f data){return new Data2f(this.getX()*data.getX(),this.getY()*data.getY());}
    public Data2f add(Data2f data){return new Data2f(this.getX()+data.getX(),this.getY()+data.getY());}
}
