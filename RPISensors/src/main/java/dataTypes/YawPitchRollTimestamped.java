package dataTypes;

import java.time.Instant;

public class YawPitchRollTimestamped extends TimestampedData <YawPitchRoll> {

	public YawPitchRollTimestamped(Data<YawPitchRoll> data, Instant instant) {super(data, instant);}

	public YawPitchRollTimestamped(Data<YawPitchRoll> data) {super(data);}

	@Override
	public TimestampedData <YawPitchRoll> clone() {	return new YawPitchRollTimestamped(data,this.time());}

	@Override
	public Data<YawPitchRoll> multiply(Data<YawPitchRoll> b) {return data.multiply(b);}

	@Override
	public YawPitchRoll get() {return data.get();}

	@Override
	public void set(YawPitchRoll x) {data.set(x);}
}