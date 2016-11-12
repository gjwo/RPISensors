package dataTypes;

public class YawPitchRollTimestamped extends TimestampedData <YawPitchRoll> {

	public YawPitchRollTimestamped(Data<YawPitchRoll> data, long nanoTime) {
		super(data, nanoTime);
	}

	public YawPitchRollTimestamped(Data<YawPitchRoll> data) {
		super(data);
	}

	@Override
	public TimestampedData <YawPitchRoll> clone() {
		return new YawPitchRollTimestamped(data,nanoTime);
	}

	@Override
	public Data<YawPitchRoll> multiply(Data<YawPitchRoll> b) {
		return data.multiply(b);
	}
}