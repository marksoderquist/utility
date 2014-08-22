package com.parallelsymmetry.utility.math;

import com.parallelsymmetry.utility.math.Statistics;

import junit.framework.TestCase;

public class StatisticsTest extends TestCase {

	public void testMin() {
		assertEquals( 1.0, Statistics.min( new double[] { 5.0, 1.0, 3.0, 2.0, 4.0 } ) );
	}

	public void testMax() {
		assertEquals( 5.0, Statistics.max( new double[] { 2.0, 1.0, 3.0, 5.0, 4.0 } ) );
	}

	public void testMean() {
		assertEquals( 2.0, Statistics.mean( new double[] { 1.0, 2.0, 3.0 } ) );
		assertEquals( 2.5, Statistics.mean( new double[] { 1.0, 2.0, 3.0, 4.0 } ) );
	}

	public void testMedian() {
		assertEquals( 2.0, Statistics.median( new double[] { 1.0, 2.0, 2.0, 3.0, 3.0 } ) );
		assertEquals( 2.5, Statistics.median( new double[] { 1.0, 2.0, 2.0, 3.0, 3.0, 4.0 } ) );
	}

	public void testRange() {
		assertEquals( 4.0, Statistics.range( new double[] { 2.0, 1.0, 3.0, 5.0, 4.0 } ) );
	}

	public void testMidrange() {
		assertEquals( 3.0, Statistics.midrange( new double[] { 2.0, 1.0, 3.0, 5.0, 4.0 } ) );
	}

	public void testVariance() {
		assertEquals( 2.0, Statistics.variance( new double[] { 2.0, 1.0, 3.0, 5.0, 4.0 } ) );
	}

	public void testStandardDeviation() {
		assertEquals( Math.sqrt( 2.0 ), Statistics.standardDeviation( new double[] { 2.0, 1.0, 3.0, 5.0, 4.0 } ) );
	}

}
