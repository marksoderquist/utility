package com.parallelsymmetry.utility.math;

import java.util.Arrays;

public class Statistics {

	public static final double mean( double[] values ) {
		double sum = 0;
		for( double value : values ) {
			sum += value;
		}
		return sum / values.length;
	}

	public static final double median( double[] values ) {
		Arrays.sort( values );

		double median = Double.NaN;
		int index = values.length / 2;
		if( values.length % 2 == 0 ) {
			median = ( values[index - 1] + values[index] ) / 2;
		} else {
			median = ( values[index] );
		}

		return median;
	}

	public static final double min( double[] values ) {
		double min = Double.MAX_VALUE;

		for( double value : values ) {
			if( value < min ) min = value;
		}

		return min;
	}

	public static final double max( double[] values ) {
		double max = Double.MIN_VALUE;

		for( double value : values ) {
			if( value > max ) max = value;
		}

		return max;
	}

	public static final double range( double[] values ) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for( double value : values ) {
			if( value < min ) min = value;
			if( value > max ) max = value;
		}

		return max - min;
	}

	public static final double midrange( double[] values ) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for( double value : values ) {
			if( value < min ) min = value;
			if( value > max ) max = value;
		}

		return ( min + max ) / 2;
	}

	public static final double variance( double[] values ) {
		double mean = mean( values );

		double sum = 0;
		for( double value : values ) {
			double delta = value - mean;
			sum += delta * delta;
		}

		return sum / values.length;
	}

	public static final double standardDeviation( double[] values ) {
		return Math.sqrt( variance( values ) );
	}

}
