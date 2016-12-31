package dataTypes;

import java.time.Instant;

public interface TimeStamped
{
	public long getNano();
	public Instant time();
	public String getTimeStr();
}