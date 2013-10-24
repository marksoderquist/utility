package com.parallelsymmetry.utility;

import java.util.logging.Level;

import com.parallelsymmetry.utility.log.Log;

public class PerformanceCheck {

	private static final long startTimestamp = System.currentTimeMillis();

	private static long lastTimestamp = startTimestamp;
	
	private static Level level = Log.DETAIL;

	private PerformanceCheck() {}

	public static final String getTimeAfterStart( String message ) {
		long current = System.currentTimeMillis();

		long deltaFromStart = current - startTimestamp;
		long deltaFromLast = current - lastTimestamp;

		lastTimestamp = current;

		return String.format( "* %1$40s:  offset: %2$8d  delta: %3$8d", message, deltaFromStart, deltaFromLast );
	}
	
	public static final void setLogLevel( Level level ) {
		PerformanceCheck.level = level;
	}

	public static final void writeTimeAfterStart( String message ) {
		Log.write( level, getTimeAfterStart( message ) );
	}

}
