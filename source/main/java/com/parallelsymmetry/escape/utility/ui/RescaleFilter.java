package com.parallelsymmetry.escape.utility.ui;

import java.awt.image.RGBImageFilter;

public class RescaleFilter extends RGBImageFilter {

	float ascale = 0;

	float rscale = 0;

	float gscale = 0;

	float bscale = 0;

	public RescaleFilter( int mask ) {
		canFilterIndexColorModel = true;

		ascale = ( ( ( mask & 0xff000000 ) >>> 24 ) - 128 ) / 128f;
		rscale = ( ( ( mask & 0x00ff0000 ) >>> 16 ) - 128 ) / 128f;
		gscale = ( ( ( mask & 0x0000ff00 ) >>> 8 ) - 128 ) / 128f;
		bscale = ( ( ( mask & 0x000000ff ) >>> 0 ) - 128 ) / 128f;
	}

	public int filterRGB( int x, int y, int rgb ) {
		int oa = ( rgb & 0xff000000 ) >>> 24;
		int or = ( rgb & 0x00ff0000 ) >>> 16;
		int og = ( rgb & 0x0000ff00 ) >>> 8;
		int ob = ( rgb & 0x000000ff ) >>> 0;

		int a = calc( oa, ascale ) << 24;
		int r = calc( or, rscale ) << 16;
		int g = calc( og, gscale ) << 8;
		int b = calc( ob, bscale ) << 0;

		return a + r + g + b;
	}

	private int calc( int value, float scale ) {
		int delta = value;
		if( scale > 0 ) delta = 255 - value;
		return (int)( value + ( delta * scale ) );
	}
}
