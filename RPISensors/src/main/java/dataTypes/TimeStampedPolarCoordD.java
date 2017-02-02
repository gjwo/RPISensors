package dataTypes;

import java.time.Instant;

/**
 * TimeStampedPolarCoordD           -   Time stamped polar coordinates with double values
 * Created by GJWood on 02/02/2017.
 */
public class TimeStampedPolarCoordD extends TimeStampedData<PolarCoordinatesD>
{
    public TimeStampedPolarCoordD(PolarCoordinatesD data)
    {
        super(data);
    }
    public TimeStampedPolarCoordD(PolarCoordinatesD data,Instant time)
    {
        super(data, time);
    }
    @Override
    public TimeStampedData<PolarCoordinatesD> clone()
    {
        return new TimeStampedPolarCoordD(this.getData(),this.time());
    }
}
