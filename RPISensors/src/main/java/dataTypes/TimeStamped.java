package dataTypes;

import java.time.Instant;

public interface TimeStamped
{
	long getNano();
	Instant time();
	String getTimeStr();
}