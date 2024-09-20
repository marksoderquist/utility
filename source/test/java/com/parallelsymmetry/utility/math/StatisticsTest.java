package com.parallelsymmetry.utility.math;

import com.parallelsymmetry.utility.BaseTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticsTest extends BaseTestCase {

	@Test
	public void testMin() {
		assertEquals( 1.0, Statistics.min( new double[]{ 5.0, 1.0, 3.0, 2.0, 4.0 } ) );
	}

	@Test
	public void testMax() {
		assertEquals( 5.0, Statistics.max( new double[]{ 2.0, 1.0, 3.0, 5.0, 4.0 } ) );
	}

	@Test
	public void testMean() {
		assertEquals( 2.0, Statistics.mean( new double[]{ 1.0, 2.0, 3.0 } ) );
		assertEquals( 2.5, Statistics.mean( new double[]{ 1.0, 2.0, 3.0, 4.0 } ) );
	}

	@Test
	public void testMedian() {
		assertEquals( 2.0, Statistics.median( new double[]{ 1.0, 2.0, 2.0, 3.0, 3.0 } ) );
		assertEquals( 2.5, Statistics.median( new double[]{ 1.0, 2.0, 2.0, 3.0, 3.0, 4.0 } ) );
	}

	@Test
	public void testRange() {
		assertEquals( 4.0, Statistics.range( new double[]{ 2.0, 1.0, 3.0, 5.0, 4.0 } ) );
	}

	@Test
	public void testMidrange() {
		assertEquals( 3.0, Statistics.midrange( new double[]{ 2.0, 1.0, 3.0, 5.0, 4.0 } ) );
	}

	@Test
	public void testVariance() {
		assertEquals( 2.0, Statistics.variance( new double[]{ 2.0, 1.0, 3.0, 5.0, 4.0 } ) );
	}

	@Test
	public void testStandardDeviation() {
		assertEquals( Math.sqrt( 2.0 ), Statistics.standardDeviation( new double[]{ 2.0, 1.0, 3.0, 5.0, 4.0 } ) );
	}

	@Test
	public void testLeastSquaresSlope() {
		double[] x = new double[]{ 1, 2, 4, 5, 6, 6, 8, 9, 11, 12 };
		double[] y = new double[]{ 14, 10, 12, 8, 6, 9, 3, 4, 3, 1 };
		assertEquals( -1.1064189189189184, Statistics.leastSquaresSlope( x, y ) );
	}

	@Test
	public void testLeastSquaresIntercept() {
		double[] x = new double[]{ 1, 2, 4, 5, 6, 6, 8, 9, 11, 12 };
		double[] y = new double[]{ 14, 10, 12, 8, 6, 9, 3, 4, 3, 1 };
		assertEquals( 14.081081081081077, Statistics.leastSquaresIntercept( x, y ) );
	}

}
