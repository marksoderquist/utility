package com.parallelsymmetry.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Counters {

	private static final Map<String, Long> data = new ConcurrentHashMap<String, Long>();

	private static final Map<String, Long> starts = new ConcurrentHashMap<String, Long>();

	public static final long getValue( String name ) {
		Long datum = data.get( name );
		Long start = starts.get( name );
		if( datum != null ) {
			if( start == null ) {
				return datum;
			} else {
				return datum + ( System.currentTimeMillis() - start );
			}
		}
		return 0;
	}

	public static final void startTimer( String name ) {
		Long start = starts.get( name );

		if( start == null ) {
			long time = System.currentTimeMillis();
			starts.put( name, time );
		}
	}

	public static final void stopTimer( String name ) {
		Long start = starts.get( name );

		if( start == null ) return;
		starts.remove( name );
		increment( name, System.currentTimeMillis() - start );
	}

	public static final void increment( String name ) {
		increment( name, 1 );
	}

	public static final void increment( String name, long value ) {
		Long datum = data.get( name );
		if( datum == null ) {
			data.put( name, value );
		} else {
			data.put( name, datum + value );
		}
	}

}
