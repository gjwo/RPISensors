package dataTypes;

import java.time.Instant;

@SuppressWarnings("WeakerAccess")
public interface TimeStamped
{
	long getNano();
	Instant time();
	String getTimeStr();
}