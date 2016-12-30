package dataTypes;

import java.time.Instant;

public class TimeInstantYawPitchRoll extends TimeInstantData <YawPitchRoll> {

	public TimeInstantYawPitchRoll(Data<YawPitchRoll> data, Instant instant) {super(data, instant);}

	public TimeInstantYawPitchRoll(Data<YawPitchRoll> data) {super(data);}

	@Override
	public TimeInstantData <YawPitchRoll> clone() {	return new TimeInstantYawPitchRoll(data,this.time());}

	@Override
	public Data<YawPitchRoll> multiply(Data<YawPitchRoll> b) {return data.multiply(b);}

	@Override
	public YawPitchRoll get() {return data.get();}

	@Override
	public void set(YawPitchRoll x) {data.set(x);}
}